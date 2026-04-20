package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.Attendance;
import com.library.entity.AttendanceStatistics;
import com.library.service.AttendanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/page")
    public Result<PageResult<Attendance>> getAttendancePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        PageResult<Attendance> result = attendanceService.getAttendancePage(page, size, employeeId, departmentId, startDate, endDate);
        return Result.success(result);
    }

    @PostMapping("/checkin")
    public Result<Void> checkIn(@RequestParam Long employeeId, 
                                @RequestParam(defaultValue = "FACE001") String deviceNo) {
        log.info("员工签到: {}", employeeId);
        
        try {
            boolean success = attendanceService.recordCheckIn(employeeId, deviceNo);
            if (success) {
                log.info("员工签到成功: {}", employeeId);
                return Result.success("签到成功");
            }
            return Result.error("签到失败");
        } catch (Exception e) {
            log.error("签到失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/checkout")
    public Result<Void> checkOut(@RequestParam Long employeeId,
                                 @RequestParam(defaultValue = "FACE001") String deviceNo) {
        log.info("员工签退: {}", employeeId);
        
        try {
            boolean success = attendanceService.recordCheckOut(employeeId, deviceNo);
            if (success) {
                log.info("员工签退成功: {}", employeeId);
                return Result.success("签退成功");
            }
            return Result.error("签退失败");
        } catch (Exception e) {
            log.error("签退失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/statistics/page")
    public Result<PageResult<AttendanceStatistics>> getStatisticsPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        PageResult<AttendanceStatistics> result = attendanceService.getStatisticsPage(page, size, employeeId, departmentId, year, month);
        return Result.success(result);
    }

    @PostMapping("/statistics/generate")
    public Result<Void> generateStatistics(@RequestParam Long employeeId,
                                           @RequestParam Integer year,
                                           @RequestParam Integer month) {
        log.info("生成员工考勤统计: {}, {}-{}", employeeId, year, month);
        
        try {
            attendanceService.generateStatistics(employeeId, year, month);
            return Result.success("统计生成成功");
        } catch (Exception e) {
            log.error("统计生成失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/statistics/generateMonthly")
    public Result<Void> generateMonthlyStatistics(@RequestParam Integer year,
                                                  @RequestParam Integer month) {
        log.info("生成月度考勤统计: {}-{}", year, month);
        
        try {
            attendanceService.generateMonthlyStatistics(year, month);
            return Result.success("月度统计生成成功");
        } catch (Exception e) {
            log.error("月度统计生成失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
