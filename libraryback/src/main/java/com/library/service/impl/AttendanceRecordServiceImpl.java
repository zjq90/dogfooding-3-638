package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.PageResult;
import com.library.entity.AttendanceRecord;
import com.library.entity.Department;
import com.library.entity.Employee;
import com.library.entity.FaceDevice;
import com.library.mapper.AttendanceRecordMapper;
import com.library.mapper.DepartmentMapper;
import com.library.mapper.EmployeeMapper;
import com.library.mapper.FaceDeviceMapper;
import com.library.service.AttendanceRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AttendanceRecordServiceImpl extends ServiceImpl<AttendanceRecordMapper, AttendanceRecord> implements AttendanceRecordService {

    @Autowired
    private EmployeeMapper employeeMapper;
    
    @Autowired
    private DepartmentMapper departmentMapper;
    
    @Autowired
    private FaceDeviceMapper faceDeviceMapper;
    
    private static final LocalTime WORK_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime WORK_END_TIME = LocalTime.of(18, 0);

    @Override
    public PageResult<AttendanceRecord> getAttendancePage(Integer page, Integer size, Long employeeId, Long departmentId, LocalDate startDate, LocalDate endDate) {
        Page<AttendanceRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (employeeId != null && employeeId > 0) {
            wrapper.eq(AttendanceRecord::getEmployeeId, employeeId);
        }
        
        if (departmentId != null && departmentId > 0) {
            wrapper.eq(AttendanceRecord::getDepartmentId, departmentId);
        }
        
        if (startDate != null) {
            wrapper.ge(AttendanceRecord::getAttendanceDate, startDate);
        }
        
        if (endDate != null) {
            wrapper.le(AttendanceRecord::getAttendanceDate, endDate);
        }
        
        wrapper.orderByDesc(AttendanceRecord::getAttendanceDate);
        wrapper.orderByAsc(AttendanceRecord::getEmployeeId);
        
        Page<AttendanceRecord> attendancePage = this.page(pageParam, wrapper);
        
        return new PageResult<>(attendancePage.getTotal(), attendancePage.getRecords(),
                                attendancePage.getCurrent(), attendancePage.getSize());
    }

    @Override
    public List<AttendanceRecord> getAttendanceByEmployee(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectByEmployeeAndDateRange(employeeId, startDate, endDate);
    }

    @Override
    public List<AttendanceRecord> getAttendanceByDepartment(Long departmentId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectByDepartmentAndDateRange(departmentId, startDate, endDate);
    }

    @Override
    public Map<String, Object> getAttendanceStatistics(Long employeeId, LocalDate startDate, LocalDate endDate) {
        List<AttendanceRecord> records = getAttendanceByEmployee(employeeId, startDate, endDate);
        
        int totalDays = records.size();
        int normalDays = 0;
        int lateDays = 0;
        int earlyLeaveDays = 0;
        int absentDays = 0;
        BigDecimal totalWorkHours = BigDecimal.ZERO;
        
        for (AttendanceRecord record : records) {
            if (record.getCheckInType() != null && record.getCheckInType() == 1 
                    && record.getCheckOutType() != null && record.getCheckOutType() == 1) {
                normalDays++;
            }
            if (record.getCheckInType() != null && record.getCheckInType() == 2) {
                lateDays++;
            }
            if (record.getCheckOutType() != null && record.getCheckOutType() == 2) {
                earlyLeaveDays++;
            }
            if (record.getStatus() != null && record.getStatus() == 2) {
                absentDays++;
            }
            if (record.getWorkHours() != null) {
                totalWorkHours = totalWorkHours.add(record.getWorkHours());
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalDays", totalDays);
        result.put("normalDays", normalDays);
        result.put("lateDays", lateDays);
        result.put("earlyLeaveDays", earlyLeaveDays);
        result.put("absentDays", absentDays);
        result.put("totalWorkHours", totalWorkHours);
        result.put("avgWorkHours", totalDays > 0 ? totalWorkHours.divide(BigDecimal.valueOf(totalDays), 1, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        
        return result;
    }

    @Override
    public Map<String, Object> getDepartmentAttendanceStatistics(Long departmentId, LocalDate startDate, LocalDate endDate) {
        List<AttendanceRecord> records = getAttendanceByDepartment(departmentId, startDate, endDate);
        
        int totalRecords = records.size();
        int normalRecords = 0;
        int lateRecords = 0;
        int earlyLeaveRecords = 0;
        int absentRecords = 0;
        
        for (AttendanceRecord record : records) {
            if (record.getCheckInType() != null && record.getCheckInType() == 1 
                    && record.getCheckOutType() != null && record.getCheckOutType() == 1) {
                normalRecords++;
            }
            if (record.getCheckInType() != null && record.getCheckInType() == 2) {
                lateRecords++;
            }
            if (record.getCheckOutType() != null && record.getCheckOutType() == 2) {
                earlyLeaveRecords++;
            }
            if (record.getStatus() != null && record.getStatus() == 2) {
                absentRecords++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalRecords", totalRecords);
        result.put("normalRecords", normalRecords);
        result.put("lateRecords", lateRecords);
        result.put("earlyLeaveRecords", earlyLeaveRecords);
        result.put("absentRecords", absentRecords);
        result.put("normalRate", totalRecords > 0 ? BigDecimal.valueOf(normalRecords * 100).divide(BigDecimal.valueOf(totalRecords), 1, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        
        return result;
    }

    @Override
    public boolean generateAttendance(Long employeeId, LocalDate date) {
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            log.warn("员工不存在: {}", employeeId);
            return false;
        }
        
        AttendanceRecord existRecord = baseMapper.selectByEmployeeAndDate(employeeId, date);
        if (existRecord != null) {
            log.info("考勤记录已存在: employeeId={}, date={}", employeeId, date);
            return true;
        }
        
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(employeeId);
        record.setEmployeeName(employee.getName());
        record.setEmployeeNo(employee.getEmployeeNo());
        record.setDepartmentId(employee.getDepartmentId());
        record.setAttendanceDate(date);
        record.setStatus(1);
        
        LocalDateTime checkInTime = generateRandomCheckInTime(date, true);
        LocalDateTime checkOutTime = generateRandomCheckOutTime(date);
        
        record.setCheckInTime(checkInTime);
        record.setCheckOutTime(checkOutTime);
        record.setCheckInDevice("DEV001");
        record.setCheckOutDevice("DEV001");
        
        LocalTime checkInLocalTime = checkInTime.toLocalTime();
        if (checkInLocalTime.isAfter(WORK_START_TIME)) {
            record.setCheckInType(2);
            record.setStatus(2);
        } else {
            record.setCheckInType(1);
        }
        
        LocalTime checkOutLocalTime = checkOutTime.toLocalTime();
        if (checkOutLocalTime.isBefore(WORK_END_TIME)) {
            record.setCheckOutType(2);
            record.setStatus(2);
        } else {
            record.setCheckOutType(1);
        }
        
        long minutes = ChronoUnit.MINUTES.between(checkInTime, checkOutTime);
        BigDecimal workHours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 1, RoundingMode.HALF_UP);
        record.setWorkHours(workHours);
        
        if (employee.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(employee.getDepartmentId());
            if (department != null) {
                record.setDepartmentName(department.getName());
            }
        }
        
        return this.save(record);
    }
    
    private LocalDateTime generateRandomCheckInTime(LocalDate date, boolean isLate) {
        LocalTime baseTime = isLate ? WORK_START_TIME.plusMinutes((long)(Math.random() * 30)) : 
                                       WORK_START_TIME.minusMinutes((long)(Math.random() * 15));
        return LocalDateTime.of(date, baseTime);
    }
    
    private LocalDateTime generateRandomCheckOutTime(LocalDate date) {
        LocalTime baseTime = WORK_END_TIME.plusMinutes((long)(Math.random() * 60 - 30));
        return LocalDateTime.of(date, baseTime);
    }

    @Override
    public boolean checkIn(Long employeeId, String deviceNo) {
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            log.warn("员工不存在: {}", employeeId);
            return false;
        }
        
        FaceDevice device = faceDeviceMapper.selectByDeviceNo(deviceNo);
        if (device == null || device.getStatus() != 1) {
            log.warn("设备不存在或离线: {}", deviceNo);
            return false;
        }
        
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        
        AttendanceRecord record = baseMapper.selectByEmployeeAndDate(employeeId, today);
        if (record == null) {
            record = new AttendanceRecord();
            record.setEmployeeId(employeeId);
            record.setEmployeeName(employee.getName());
            record.setEmployeeNo(employee.getEmployeeNo());
            record.setDepartmentId(employee.getDepartmentId());
            record.setAttendanceDate(today);
            record.setStatus(1);
            
            if (employee.getDepartmentId() != null) {
                Department department = departmentMapper.selectById(employee.getDepartmentId());
                if (department != null) {
                    record.setDepartmentName(department.getName());
                }
            }
        }
        
        record.setCheckInTime(now);
        record.setCheckInDevice(deviceNo);
        
        LocalTime checkInLocalTime = now.toLocalTime();
        if (checkInLocalTime.isAfter(WORK_START_TIME)) {
            record.setCheckInType(2);
            record.setStatus(2);
        } else {
            record.setCheckInType(1);
        }
        
        if (record.getId() == null) {
            return this.save(record);
        } else {
            return this.updateById(record);
        }
    }

    @Override
    public boolean checkOut(Long employeeId, String deviceNo) {
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            log.warn("员工不存在: {}", employeeId);
            return false;
        }
        
        FaceDevice device = faceDeviceMapper.selectByDeviceNo(deviceNo);
        if (device == null || device.getStatus() != 1) {
            log.warn("设备不存在或离线: {}", deviceNo);
            return false;
        }
        
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        
        AttendanceRecord record = baseMapper.selectByEmployeeAndDate(employeeId, today);
        if (record == null) {
            record = new AttendanceRecord();
            record.setEmployeeId(employeeId);
            record.setEmployeeName(employee.getName());
            record.setEmployeeNo(employee.getEmployeeNo());
            record.setDepartmentId(employee.getDepartmentId());
            record.setAttendanceDate(today);
            record.setStatus(1);
            
            if (employee.getDepartmentId() != null) {
                Department department = departmentMapper.selectById(employee.getDepartmentId());
                if (department != null) {
                    record.setDepartmentName(department.getName());
                }
            }
        }
        
        record.setCheckOutTime(now);
        record.setCheckOutDevice(deviceNo);
        
        LocalTime checkOutLocalTime = now.toLocalTime();
        if (checkOutLocalTime.isBefore(WORK_END_TIME)) {
            record.setCheckOutType(2);
            record.setStatus(2);
        } else {
            record.setCheckOutType(1);
        }
        
        if (record.getCheckInTime() != null) {
            long minutes = ChronoUnit.MINUTES.between(record.getCheckInTime(), now);
            BigDecimal workHours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 1, RoundingMode.HALF_UP);
            record.setWorkHours(workHours);
        }
        
        if (record.getId() == null) {
            return this.save(record);
        } else {
            return this.updateById(record);
        }
    }
}
