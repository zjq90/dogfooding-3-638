package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.entity.Attendance;
import com.library.entity.AttendanceStatistics;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService extends IService<Attendance> {
    
    PageResult<Attendance> getAttendancePage(Integer page, Integer size, Long employeeId, Long departmentId, LocalDate startDate, LocalDate endDate);
    
    boolean recordCheckIn(Long employeeId, String deviceNo);
    
    boolean recordCheckOut(Long employeeId, String deviceNo);
    
    AttendanceStatistics generateStatistics(Long employeeId, Integer year, Integer month);
    
    PageResult<AttendanceStatistics> getStatisticsPage(Integer page, Integer size, Long employeeId, Long departmentId, Integer year, Integer month);
    
    void generateMonthlyStatistics(Integer year, Integer month);
}
