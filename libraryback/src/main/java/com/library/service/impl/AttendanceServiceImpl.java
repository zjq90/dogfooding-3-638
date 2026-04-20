package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.entity.Attendance;
import com.library.entity.AttendanceStatistics;
import com.library.entity.Department;
import com.library.entity.Employee;
import com.library.mapper.AttendanceMapper;
import com.library.mapper.AttendanceStatisticsMapper;
import com.library.mapper.DepartmentMapper;
import com.library.mapper.EmployeeMapper;
import com.library.service.AttendanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {

    @Autowired
    private AttendanceStatisticsMapper statisticsMapper;
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    @Autowired
    private DepartmentMapper departmentMapper;
    
    private static final LocalTime WORK_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime WORK_END_TIME = LocalTime.of(18, 0);

    @Override
    public PageResult<Attendance> getAttendancePage(Integer page, Integer size, Long employeeId, Long departmentId, LocalDate startDate, LocalDate endDate) {
        Page<Attendance> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        
        if (employeeId != null && employeeId > 0) {
            wrapper.eq(Attendance::getEmployeeId, employeeId);
        }
        
        if (startDate != null) {
            wrapper.ge(Attendance::getAttendanceDate, startDate);
        }
        
        if (endDate != null) {
            wrapper.le(Attendance::getAttendanceDate, endDate);
        }
        
        wrapper.orderByDesc(Attendance::getAttendanceDate);
        Page<Attendance> attendancePage = this.page(pageParam, wrapper);
        
        List<Attendance> records = attendancePage.getRecords();
        fillEmployeeAndDepartmentInfo(records, departmentId);
        
        return new PageResult<>(attendancePage.getTotal(), records, 
                                attendancePage.getCurrent(), attendancePage.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordCheckIn(Long employeeId, String deviceNo) {
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getEmployeeId, employeeId)
               .eq(Attendance::getAttendanceDate, today);
        Attendance existAttendance = this.getOne(wrapper);
        
        LocalDateTime now = LocalDateTime.now();
        int status = 0;
        
        if (now.toLocalTime().isAfter(WORK_START_TIME)) {
            status = 1;
        }
        
        if (existAttendance != null) {
            if (existAttendance.getCheckInTime() != null) {
                throw new BusinessException("今日已签到");
            }
            existAttendance.setCheckInTime(now);
            existAttendance.setStatus(status);
            existAttendance.setDeviceNo(deviceNo);
            return this.updateById(existAttendance);
        }
        
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setAttendanceDate(today);
        attendance.setCheckInTime(now);
        attendance.setStatus(status);
        attendance.setDeviceNo(deviceNo);
        attendance.setRemark("人脸设备签到");
        
        return this.save(attendance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordCheckOut(Long employeeId, String deviceNo) {
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getEmployeeId, employeeId)
               .eq(Attendance::getAttendanceDate, today);
        Attendance existAttendance = this.getOne(wrapper);
        
        if (existAttendance == null || existAttendance.getCheckInTime() == null) {
            throw new BusinessException("今日未签到");
        }
        
        if (existAttendance.getCheckOutTime() != null) {
            throw new BusinessException("今日已签退");
        }
        
        LocalDateTime now = LocalDateTime.now();
        existAttendance.setCheckOutTime(now);
        
        if (now.toLocalTime().isBefore(WORK_END_TIME)) {
            existAttendance.setStatus(2);
        }
        
        existAttendance.setRemark(existAttendance.getRemark() + ",人脸设备签退");
        
        return this.updateById(existAttendance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttendanceStatistics generateStatistics(Long employeeId, Integer year, Integer month) {
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getEmployeeId, employeeId)
               .ge(Attendance::getAttendanceDate, startDate)
               .le(Attendance::getAttendanceDate, endDate);
        List<Attendance> attendances = this.list(wrapper);
        
        int workDays = endDate.getDayOfMonth();
        int presentDays = 0;
        int lateDays = 0;
        int leaveEarlyDays = 0;
        int absentDays = 0;
        
        for (Attendance attendance : attendances) {
            if (attendance.getCheckInTime() != null) {
                presentDays++;
            }
            if (attendance.getStatus() != null) {
                if (attendance.getStatus() == 1) {
                    lateDays++;
                } else if (attendance.getStatus() == 2) {
                    leaveEarlyDays++;
                }
            }
        }
        
        absentDays = workDays - presentDays;
        double attendanceRate = workDays > 0 ? (double) presentDays / workDays * 100 : 0;
        
        LambdaQueryWrapper<AttendanceStatistics> statWrapper = new LambdaQueryWrapper<>();
        statWrapper.eq(AttendanceStatistics::getEmployeeId, employeeId)
                   .eq(AttendanceStatistics::getStatisticsYear, year)
                   .eq(AttendanceStatistics::getStatisticsMonth, month);
        AttendanceStatistics existStat = statisticsMapper.selectOne(statWrapper);
        
        AttendanceStatistics statistics = new AttendanceStatistics();
        statistics.setEmployeeId(employeeId);
        statistics.setStatisticsYear(year);
        statistics.setStatisticsMonth(month);
        statistics.setWorkDays(workDays);
        statistics.setPresentDays(presentDays);
        statistics.setLateDays(lateDays);
        statistics.setLeaveEarlyDays(leaveEarlyDays);
        statistics.setAbsentDays(absentDays);
        statistics.setLeaveDays(0);
        statistics.setAttendanceRate(attendanceRate);
        
        if (existStat != null) {
            statistics.setId(existStat.getId());
            statisticsMapper.updateById(statistics);
        } else {
            statisticsMapper.insert(statistics);
        }
        
        return statistics;
    }

    @Override
    public PageResult<AttendanceStatistics> getStatisticsPage(Integer page, Integer size, Long employeeId, Long departmentId, Integer year, Integer month) {
        Page<AttendanceStatistics> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AttendanceStatistics> wrapper = new LambdaQueryWrapper<>();
        
        if (employeeId != null && employeeId > 0) {
            wrapper.eq(AttendanceStatistics::getEmployeeId, employeeId);
        }
        
        if (year != null) {
            wrapper.eq(AttendanceStatistics::getStatisticsYear, year);
        }
        if (month != null) {
            wrapper.eq(AttendanceStatistics::getStatisticsMonth, month);
        }
        
        wrapper.orderByDesc(AttendanceStatistics::getStatisticsYear)
               .orderByDesc(AttendanceStatistics::getStatisticsMonth);
        Page<AttendanceStatistics> statPage = statisticsMapper.selectPage(pageParam, wrapper);
        
        List<AttendanceStatistics> records = statPage.getRecords();
        fillEmployeeAndDepartmentInfoForStatistics(records, departmentId);
        
        return new PageResult<>(statPage.getTotal(), records, 
                                statPage.getCurrent(), statPage.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateMonthlyStatistics(Integer year, Integer month) {
        List<Employee> employees = employeeMapper.selectAllWithDepartmentName();
        for (Employee employee : employees) {
            if (employee.getStatus() == 1) {
                generateStatistics(employee.getId(), year, month);
            }
        }
    }
    
    private void fillEmployeeAndDepartmentInfo(List<Attendance> attendances, Long filterDepartmentId) {
        if (attendances == null || attendances.isEmpty()) {
            return;
        }
        
        List<Employee> allEmployees = employeeMapper.selectAllWithDepartmentName();
        Map<Long, Employee> empMap = allEmployees.stream()
                .collect(Collectors.toMap(Employee::getId, e -> e));
        
        List<Department> allDepartments = departmentMapper.selectAll();
        Map<Long, String> deptMap = allDepartments.stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
        
        attendances.removeIf(attendance -> {
            Employee emp = empMap.get(attendance.getEmployeeId());
            if (emp != null) {
                attendance.setEmployeeName(emp.getName());
                attendance.setDepartmentId(emp.getDepartmentId());
                attendance.setDepartmentName(deptMap.get(emp.getDepartmentId()));
                
                if (filterDepartmentId != null && filterDepartmentId > 0) {
                    return !filterDepartmentId.equals(emp.getDepartmentId());
                }
            }
            return false;
        });
    }
    
    private void fillEmployeeAndDepartmentInfoForStatistics(List<AttendanceStatistics> statisticsList, Long filterDepartmentId) {
        if (statisticsList == null || statisticsList.isEmpty()) {
            return;
        }
        
        List<Employee> allEmployees = employeeMapper.selectAllWithDepartmentName();
        Map<Long, Employee> empMap = allEmployees.stream()
                .collect(Collectors.toMap(Employee::getId, e -> e));
        
        List<Department> allDepartments = departmentMapper.selectAll();
        Map<Long, String> deptMap = allDepartments.stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
        
        statisticsList.removeIf(statistics -> {
            Employee emp = empMap.get(statistics.getEmployeeId());
            if (emp != null) {
                statistics.setEmployeeName(emp.getName());
                statistics.setDepartmentId(emp.getDepartmentId());
                statistics.setDepartmentName(deptMap.get(emp.getDepartmentId()));
                
                if (filterDepartmentId != null && filterDepartmentId > 0) {
                    return !filterDepartmentId.equals(emp.getDepartmentId());
                }
            }
            return false;
        });
    }
}
