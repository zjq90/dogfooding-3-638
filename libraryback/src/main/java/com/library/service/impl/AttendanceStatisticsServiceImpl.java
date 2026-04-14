package com.library.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.AttendanceRecord;
import com.library.entity.AttendanceStatistics;
import com.library.entity.Employee;
import com.library.mapper.AttendanceRecordMapper;
import com.library.mapper.AttendanceStatisticsMapper;
import com.library.mapper.EmployeeMapper;
import com.library.service.AttendanceStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
public class AttendanceStatisticsServiceImpl extends ServiceImpl<AttendanceStatisticsMapper, AttendanceStatistics> implements AttendanceStatisticsService {

    @Autowired
    private AttendanceRecordMapper recordMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Page<AttendanceStatistics> getStatisticsPage(Integer page, Integer size, Long employeeId, Long deptId) {
        Page<AttendanceStatistics> pageParam = new Page<>(page, size);
        if (employeeId != null) {
            return baseMapper.selectStatisticsPageByEmployee(pageParam, employeeId);
        } else if (deptId != null) {
            return baseMapper.selectStatisticsPageByDept(pageParam, deptId);
        }
        return baseMapper.selectStatisticsPage(pageParam);
    }

    @Override
    public AttendanceStatistics getStatisticsById(Long id) {
        return getById(id);
    }

    @Override
    public AttendanceStatistics getStatisticsByEmployeeAndMonth(Long employeeId, Integer year, Integer month) {
        return baseMapper.selectByEmployeeAndMonth(employeeId, year, month);
    }

    @Override
    @Transactional
    public boolean generateMonthlyStatistics(Integer year, Integer month) {
        List<Employee> employees = employeeMapper.selectList(null);
        for (Employee employee : employees) {
            if (employee.getStatus() != null && employee.getStatus() == 1) {
                generateMonthlyStatisticsByEmployee(employee.getId(), year, month);
            }
        }
        log.info("生成{}年{}月考勤统计完成", year, month);
        return true;
    }

    @Override
    @Transactional
    public boolean generateMonthlyStatisticsByEmployee(Long employeeId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            return false;
        }

        AttendanceStatistics statistics = baseMapper.selectByEmployeeAndMonth(employeeId, year, month);
        if (statistics == null) {
            statistics = new AttendanceStatistics();
            statistics.setEmployeeId(employeeId);
            statistics.setEmployeeName(employee.getName());
            statistics.setDepartmentId(employee.getDepartmentId());
            statistics.setStatisticsYear(year);
            statistics.setStatisticsMonth(month);
        }

        int totalDays = yearMonth.lengthOfMonth();
        int workDays = calculateWorkDays(year, month);
        statistics.setTotalDays(workDays);

        List<AttendanceRecord> records = recordMapper.selectByDateRange(startDate, endDate);
        int actualDays = 0;
        int normalDays = 0;
        int lateCount = 0;
        int earlyLeaveCount = 0;
        int absentCount = 0;
        BigDecimal leaveDays = BigDecimal.ZERO;
        BigDecimal overtimeHours = BigDecimal.ZERO;
        BigDecimal totalWorkHours = BigDecimal.ZERO;

        for (AttendanceRecord record : records) {
            if (record.getEmployeeId().equals(employeeId)) {
                if (record.getStatus() != null && record.getStatus() != 3) {
                    actualDays++;
                }
                if (record.getStatus() != null) {
                    switch (record.getStatus()) {
                        case 0: normalDays++; break;
                        case 1: lateCount++; actualDays++; break;
                        case 2: earlyLeaveCount++; actualDays++; break;
                        case 3: absentCount++; break;
                        case 4: leaveDays = leaveDays.add(BigDecimal.ONE); break;
                        case 5: normalDays++; break;
                    }
                }
                if (record.getOvertimeHours() != null) {
                    overtimeHours = overtimeHours.add(record.getOvertimeHours());
                }
                if (record.getWorkHours() != null) {
                    totalWorkHours = totalWorkHours.add(record.getWorkHours());
                }
            }
        }

        statistics.setActualDays(actualDays);
        statistics.setNormalDays(normalDays);
        statistics.setLateCount(lateCount);
        statistics.setEarlyLeaveCount(earlyLeaveCount);
        statistics.setAbsentCount(absentCount);
        statistics.setLeaveDays(leaveDays);
        statistics.setOvertimeHours(overtimeHours);
        statistics.setTotalWorkHours(totalWorkHours);

        if (statistics.getId() != null) {
            return updateById(statistics);
        } else {
            return save(statistics);
        }
    }

    @Override
    public List<AttendanceStatistics> getStatisticsByMonth(Integer year, Integer month) {
        return baseMapper.selectByMonth(year, month);
    }

    private int calculateWorkDays(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        int workDays = 0;
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            int dayOfWeek = date.getDayOfWeek().getValue();
            if (dayOfWeek != 6 && dayOfWeek != 7) {
                workDays++;
            }
        }
        return workDays;
    }
}
