package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRecordService extends IService<AttendanceRecord> {

    Page<AttendanceRecord> getRecordPage(Integer page, Integer size, Long employeeId, Long deptId);

    AttendanceRecord getRecordById(Long id);

    AttendanceRecord getRecordByEmployeeAndDate(Long employeeId, LocalDate date);

    boolean addRecord(AttendanceRecord record);

    boolean updateRecord(AttendanceRecord record);

    boolean deleteRecord(Long id);

    boolean checkIn(Long employeeId, String deviceNo, String location);

    boolean checkOut(Long employeeId, String deviceNo, String location);

    List<AttendanceRecord> getRecordsByDateRange(LocalDate startDate, LocalDate endDate);

    boolean generateDailyAttendance(LocalDate date);
}
