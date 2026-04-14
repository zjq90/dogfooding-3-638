package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.AttendanceStatistics;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AttendanceStatisticsMapper extends BaseMapper<AttendanceStatistics> {

    @Select("SELECT s.* FROM attendance_statistics s " +
            "WHERE s.deleted = 0 ORDER BY s.statistics_year DESC, s.statistics_month DESC")
    Page<AttendanceStatistics> selectStatisticsPage(Page<AttendanceStatistics> page);

    @Select("SELECT s.* FROM attendance_statistics s " +
            "WHERE s.deleted = 0 AND s.employee_id = #{employeeId} " +
            "ORDER BY s.statistics_year DESC, s.statistics_month DESC")
    Page<AttendanceStatistics> selectStatisticsPageByEmployee(Page<AttendanceStatistics> page, @Param("employeeId") Long employeeId);

    @Select("SELECT s.* FROM attendance_statistics s " +
            "WHERE s.deleted = 0 AND s.department_id = #{deptId} " +
            "ORDER BY s.statistics_year DESC, s.statistics_month DESC")
    Page<AttendanceStatistics> selectStatisticsPageByDept(Page<AttendanceStatistics> page, @Param("deptId") Long deptId);

    @Select("SELECT * FROM attendance_statistics WHERE employee_id = #{employeeId} " +
            "AND statistics_year = #{year} AND statistics_month = #{month} AND deleted = 0")
    AttendanceStatistics selectByEmployeeAndMonth(@Param("employeeId") Long employeeId, @Param("year") Integer year, @Param("month") Integer month);

    @Select("SELECT * FROM attendance_statistics WHERE deleted = 0 AND statistics_year = #{year} AND statistics_month = #{month}")
    List<AttendanceStatistics> selectByMonth(@Param("year") Integer year, @Param("month") Integer month);
}
