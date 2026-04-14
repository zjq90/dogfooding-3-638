package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.AttendanceRecord;
import com.library.entity.AttendanceStatistics;
import com.library.entity.Employee;
import com.library.service.AttendanceRecordService;
import com.library.service.AttendanceStatisticsService;
import com.library.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceRecordService recordService;

    @Autowired
    private AttendanceStatisticsService statisticsService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/record/list")
    public Result<PageResult<AttendanceRecord>> listRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long employeeId,
            @RequestAttribute(required = false) Long userId,
            @RequestAttribute(required = false) Integer role) {

        Long targetEmployeeId = employeeId;
        Long deptId = null;

        if (role != null && role != 1) {
            Employee employee = employeeService.getById(userId);
            if (employee != null) {
                if (targetEmployeeId == null) {
                    deptId = employee.getDepartmentId();
                }
            }
        }

        Page<AttendanceRecord> recordPage = recordService.getRecordPage(page, size, targetEmployeeId, deptId);
        PageResult<AttendanceRecord> pageResult = new PageResult<>(recordPage.getTotal(), recordPage.getRecords(),
                recordPage.getCurrent(), recordPage.getSize());
        return Result.success(pageResult);
    }

    @GetMapping("/record/{id}")
    public Result<AttendanceRecord> getRecord(@PathVariable Long id) {
        AttendanceRecord record = recordService.getRecordById(id);
        if (record != null) {
            return Result.success(record);
        }
        return Result.error("记录不存在");
    }

    @GetMapping("/record/today")
    public Result<AttendanceRecord> getTodayRecord(@RequestAttribute Long userId) {
        AttendanceRecord record = recordService.getRecordByEmployeeAndDate(userId, LocalDate.now());
        if (record != null) {
            return Result.success(record);
        }
        return Result.success(null);
    }

    @PostMapping("/record/add")
    public Result<Void> addRecord(@RequestBody AttendanceRecord record) {
        boolean success = recordService.addRecord(record);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/record/update")
    public Result<Void> updateRecord(@RequestBody AttendanceRecord record) {
        boolean success = recordService.updateRecord(record);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/record/delete/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        boolean success = recordService.deleteRecord(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/record/checkin")
    public Result<Void> checkIn(@RequestAttribute Long userId, @RequestParam String deviceNo) {
        boolean success = recordService.checkIn(userId, deviceNo, null);
        return success ? Result.success("上班打卡成功") : Result.error("打卡失败");
    }

    @PostMapping("/record/checkout")
    public Result<Void> checkOut(@RequestAttribute Long userId, @RequestParam String deviceNo) {
        boolean success = recordService.checkOut(userId, deviceNo, null);
        return success ? Result.success("下班打卡成功") : Result.error("打卡失败");
    }

    @PostMapping("/record/generate")
    public Result<Void> generateDailyAttendance(@RequestParam(required = false) String date) {
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        boolean success = recordService.generateDailyAttendance(targetDate);
        return success ? Result.success("生成成功") : Result.error("生成失败");
    }

    @GetMapping("/statistics/list")
    public Result<PageResult<AttendanceStatistics>> listStatistics(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long employeeId,
            @RequestAttribute(required = false) Long userId,
            @RequestAttribute(required = false) Integer role) {

        Long targetEmployeeId = employeeId;
        Long deptId = null;

        if (role != null && role != 1) {
            Employee employee = employeeService.getById(userId);
            if (employee != null) {
                if (targetEmployeeId == null) {
                    deptId = employee.getDepartmentId();
                }
            }
        }

        Page<AttendanceStatistics> statisticsPage = statisticsService.getStatisticsPage(page, size, targetEmployeeId, deptId);
        PageResult<AttendanceStatistics> pageResult = new PageResult<>(statisticsPage.getTotal(), statisticsPage.getRecords(),
                statisticsPage.getCurrent(), statisticsPage.getSize());
        return Result.success(pageResult);
    }

    @GetMapping("/statistics/{id}")
    public Result<AttendanceStatistics> getStatistics(@PathVariable Long id) {
        AttendanceStatistics statistics = statisticsService.getStatisticsById(id);
        if (statistics != null) {
            return Result.success(statistics);
        }
        return Result.error("统计记录不存在");
    }

    @PostMapping("/statistics/generate")
    public Result<Void> generateStatistics(@RequestParam Integer year, @RequestParam Integer month) {
        boolean success = statisticsService.generateMonthlyStatistics(year, month);
        return success ? Result.success("生成成功") : Result.error("生成失败");
    }

    @PostMapping("/statistics/generate/employee")
    public Result<Void> generateStatisticsByEmployee(@RequestParam Long employeeId,
                                                      @RequestParam Integer year,
                                                      @RequestParam Integer month) {
        boolean success = statisticsService.generateMonthlyStatisticsByEmployee(employeeId, year, month);
        return success ? Result.success("生成成功") : Result.error("生成失败");
    }
}
