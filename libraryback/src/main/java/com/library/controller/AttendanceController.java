package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.AttendanceRecord;
import com.library.service.AttendanceRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceRecordService attendanceRecordService;

    @GetMapping("/page")
    public Result<PageResult<AttendanceRecord>> getAttendancePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestAttribute Integer role) {
        
        if (role != 1) {
            return Result.error("无权限查看考勤列表");
        }
        
        PageResult<AttendanceRecord> result = attendanceRecordService.getAttendancePage(page, size, employeeId, departmentId, startDate, endDate);
        return Result.success(result);
    }

    @GetMapping("/employee/{employeeId}")
    public Result<List<AttendanceRecord>> getAttendanceByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestAttribute Integer role) {
        
        if (role != 1) {
            return Result.error("无权限查看考勤记录");
        }
        
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        List<AttendanceRecord> records = attendanceRecordService.getAttendanceByEmployee(employeeId, startDate, endDate);
        return Result.success(records);
    }

    @GetMapping("/department/{departmentId}")
    public Result<List<AttendanceRecord>> getAttendanceByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestAttribute Integer role) {
        
        if (role != 1) {
            return Result.error("无权限查看考勤记录");
        }
        
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        List<AttendanceRecord> records = attendanceRecordService.getAttendanceByDepartment(departmentId, startDate, endDate);
        return Result.success(records);
    }

    @GetMapping("/statistics/employee/{employeeId}")
    public Result<Map<String, Object>> getEmployeeStatistics(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestAttribute Integer role) {
        
        if (role != 1) {
            return Result.error("无权限查看考勤统计");
        }
        
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Map<String, Object> statistics = attendanceRecordService.getAttendanceStatistics(employeeId, startDate, endDate);
        return Result.success(statistics);
    }

    @GetMapping("/statistics/department/{departmentId}")
    public Result<Map<String, Object>> getDepartmentStatistics(
            @PathVariable Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestAttribute Integer role) {
        
        if (role != 1) {
            return Result.error("无权限查看考勤统计");
        }
        
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Map<String, Object> statistics = attendanceRecordService.getDepartmentAttendanceStatistics(departmentId, startDate, endDate);
        return Result.success(statistics);
    }

    @PostMapping("/checkin")
    public Result<Void> checkIn(
            @RequestParam Long employeeId,
            @RequestParam String deviceNo,
            @RequestAttribute Integer role) {
        
        if (role != 1) {
            return Result.error("无权限打卡");
        }
        
        boolean success = attendanceRecordService.checkIn(employeeId, deviceNo);
        if (success) {
            return Result.success("上班打卡成功");
        }
        return Result.error("上班打卡失败");
    }

    @PostMapping("/checkout")
    public Result<Void> checkOut(
            @RequestParam Long employeeId,
            @RequestParam String deviceNo,
            @RequestAttribute Integer role) {
        
        if (role != 1) {
            return Result.error("无权限打卡");
        }
        
        boolean success = attendanceRecordService.checkOut(employeeId, deviceNo);
        if (success) {
            return Result.success("下班打卡成功");
        }
        return Result.error("下班打卡失败");
    }

    @PostMapping("/generate")
    public Result<Void> generateAttendance(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestAttribute Integer role) {
        
        if (role != 1) {
            return Result.error("无权限生成考勤");
        }
        
        boolean success = attendanceRecordService.generateAttendance(employeeId, date);
        if (success) {
            return Result.success("考勤生成成功");
        }
        return Result.error("考勤生成失败");
    }
}
