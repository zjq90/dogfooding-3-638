package com.library.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.AttendanceRecord;
import com.library.entity.Employee;
import com.library.mapper.AttendanceRecordMapper;
import com.library.mapper.EmployeeMapper;
import com.library.service.AttendanceRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class AttendanceRecordServiceImpl extends ServiceImpl<AttendanceRecordMapper, AttendanceRecord> implements AttendanceRecordService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Page<AttendanceRecord> getRecordPage(Integer page, Integer size, Long employeeId, Long deptId) {
        Page<AttendanceRecord> pageParam = new Page<>(page, size);
        if (employeeId != null) {
            return baseMapper.selectRecordPageByEmployee(pageParam, employeeId);
        } else if (deptId != null) {
            return baseMapper.selectRecordPageByDept(pageParam, deptId);
        }
        return baseMapper.selectRecordPage(pageParam);
    }

    @Override
    public AttendanceRecord getRecordById(Long id) {
        AttendanceRecord record = getById(id);
        if (record != null) {
            record.setStatusName(getStatusName(record.getStatus()));
            record.setLeaveTypeName(getLeaveTypeName(record.getLeaveType()));
        }
        return record;
    }

    @Override
    public AttendanceRecord getRecordByEmployeeAndDate(Long employeeId, LocalDate date) {
        return baseMapper.selectByEmployeeAndDate(employeeId, date);
    }

    @Override
    @Transactional
    public boolean addRecord(AttendanceRecord record) {
        return save(record);
    }

    @Override
    @Transactional
    public boolean updateRecord(AttendanceRecord record) {
        return updateById(record);
    }

    @Override
    @Transactional
    public boolean deleteRecord(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean checkIn(Long employeeId, String deviceNo, String location) {
        LocalDate today = LocalDate.now();
        AttendanceRecord record = baseMapper.selectByEmployeeAndDate(employeeId, today);

        if (record == null) {
            record = new AttendanceRecord();
            Employee employee = employeeMapper.selectById(employeeId);
            if (employee != null) {
                record.setEmployeeId(employeeId);
                record.setEmployeeNo(employee.getEmployeeNo());
                record.setEmployeeName(employee.getName());
                record.setDepartmentId(employee.getDepartmentId());
            }
            record.setAttendanceDate(today);
            record.setCheckInTime(LocalTime.now());
            record.setCheckInDevice(deviceNo);
            record.setCheckInLocation(location);

            LocalTime standardTime = LocalTime.of(9, 0);
            if (record.getCheckInTime().isAfter(standardTime)) {
                record.setStatus(1);
            } else {
                record.setStatus(0);
            }
            return save(record);
        } else if (record.getCheckInTime() == null) {
            record.setCheckInTime(LocalTime.now());
            record.setCheckInDevice(deviceNo);
            record.setCheckInLocation(location);
            return updateById(record);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean checkOut(Long employeeId, String deviceNo, String location) {
        LocalDate today = LocalDate.now();
        AttendanceRecord record = baseMapper.selectByEmployeeAndDate(employeeId, today);

        if (record == null) {
            record = new AttendanceRecord();
            Employee employee = employeeMapper.selectById(employeeId);
            if (employee != null) {
                record.setEmployeeId(employeeId);
                record.setEmployeeNo(employee.getEmployeeNo());
                record.setEmployeeName(employee.getName());
                record.setDepartmentId(employee.getDepartmentId());
            }
            record.setAttendanceDate(today);
            record.setCheckOutTime(LocalTime.now());
            record.setCheckOutDevice(deviceNo);
            record.setCheckOutLocation(location);
            record.setStatus(3);
            return save(record);
        } else {
            record.setCheckOutTime(LocalTime.now());
            record.setCheckOutDevice(deviceNo);
            record.setCheckOutLocation(location);

            if (record.getCheckInTime() != null) {
                Duration duration = Duration.between(record.getCheckInTime(), record.getCheckOutTime());
                double hours = duration.getSeconds() / 3600.0;
                record.setWorkHours(BigDecimal.valueOf(hours).setScale(1, BigDecimal.ROUND_HALF_UP));

                LocalTime standardOutTime = LocalTime.of(18, 0);
                if (record.getCheckOutTime().isBefore(standardOutTime) && record.getStatus() != 1) {
                    record.setStatus(2);
                } else if (record.getCheckOutTime().isAfter(standardOutTime)) {
                    Duration overtime = Duration.between(standardOutTime, record.getCheckOutTime());
                    double overtimeHours = overtime.getSeconds() / 3600.0;
                    record.setOvertimeHours(BigDecimal.valueOf(overtimeHours).setScale(1, BigDecimal.ROUND_HALF_UP));
                }
            }
            return updateById(record);
        }
    }

    @Override
    public List<AttendanceRecord> getRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectByDateRange(startDate, endDate);
    }

    @Override
    @Transactional
    public boolean generateDailyAttendance(LocalDate date) {
        List<Employee> employees = employeeMapper.selectList(null);
        int count = 0;
        for (Employee employee : employees) {
            if (employee.getStatus() != null && employee.getStatus() == 1) {
                AttendanceRecord existing = baseMapper.selectByEmployeeAndDate(employee.getId(), date);
                if (existing == null) {
                    AttendanceRecord record = new AttendanceRecord();
                    record.setEmployeeId(employee.getId());
                    record.setEmployeeNo(employee.getEmployeeNo());
                    record.setEmployeeName(employee.getName());
                    record.setDepartmentId(employee.getDepartmentId());
                    record.setAttendanceDate(date);
                    record.setStatus(3);
                    save(record);
                    count++;
                }
            }
        }
        log.info("生成{}的考勤记录，共{}条", date, count);
        return true;
    }

    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "正常";
            case 1: return "迟到";
            case 2: return "早退";
            case 3: return "缺勤";
            case 4: return "请假";
            case 5: return "加班";
            default: return "未知";
        }
    }

    private String getLeaveTypeName(Integer leaveType) {
        if (leaveType == null) return null;
        switch (leaveType) {
            case 1: return "事假";
            case 2: return "病假";
            case 3: return "年假";
            case 4: return "调休";
            default: return "其他";
        }
    }
}
