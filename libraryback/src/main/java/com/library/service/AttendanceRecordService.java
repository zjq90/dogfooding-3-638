package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.entity.AttendanceRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceRecordService extends IService<AttendanceRecord> {
    
    PageResult<AttendanceRecord> getAttendancePage(Integer page, Integer size, Long employeeId, Long departmentId, LocalDate startDate, LocalDate endDate);
    
    List<AttendanceRecord> getAttendanceByEmployee(Long employeeId, LocalDate startDate, LocalDate endDate);
    
    List<AttendanceRecord> getAttendanceByDepartment(Long departmentId, LocalDate startDate, LocalDate endDate);
    
    Map<String, Object> getAttendanceStatistics(Long employeeId, LocalDate startDate, LocalDate endDate);
    
    Map<String, Object> getDepartmentAttendanceStatistics(Long departmentId, LocalDate startDate, LocalDate endDate);
    
    boolean generateAttendance(Long employeeId, LocalDate date);
    
    boolean checkIn(Long employeeId, String deviceNo);
    
    boolean checkOut(Long employeeId, String deviceNo);
}
