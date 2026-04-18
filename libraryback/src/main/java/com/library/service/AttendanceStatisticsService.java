package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.AttendanceStatistics;

import java.util.List;

public interface AttendanceStatisticsService extends IService<AttendanceStatistics> {

    Page<AttendanceStatistics> getStatisticsPage(Integer page, Integer size, Long employeeId, Long deptId);

    AttendanceStatistics getStatisticsById(Long id);

    AttendanceStatistics getStatisticsByEmployeeAndMonth(Long employeeId, Integer year, Integer month);

    boolean generateMonthlyStatistics(Integer year, Integer month);

    boolean generateMonthlyStatisticsByEmployee(Long employeeId, Integer year, Integer month);

    List<AttendanceStatistics> getStatisticsByMonth(Integer year, Integer month);
}
