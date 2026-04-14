package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.AttendanceRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRecordMapper extends BaseMapper<AttendanceRecord> {

    @Select("SELECT a.* FROM attendance_record a " +
            "WHERE a.deleted = 0 ORDER BY a.attendance_date DESC, a.check_in_time DESC")
    Page<AttendanceRecord> selectRecordPage(Page<AttendanceRecord> page);

    @Select("SELECT a.* FROM attendance_record a " +
            "WHERE a.deleted = 0 AND a.employee_id = #{employeeId} " +
            "ORDER BY a.attendance_date DESC")
    Page<AttendanceRecord> selectRecordPageByEmployee(Page<AttendanceRecord> page, @Param("employeeId") Long employeeId);

    @Select("SELECT a.* FROM attendance_record a " +
            "WHERE a.deleted = 0 AND a.department_id = #{deptId} " +
            "ORDER BY a.attendance_date DESC")
    Page<AttendanceRecord> selectRecordPageByDept(Page<AttendanceRecord> page, @Param("deptId") Long deptId);

    @Select("SELECT * FROM attendance_record WHERE employee_id = #{employeeId} AND attendance_date = #{date} AND deleted = 0")
    AttendanceRecord selectByEmployeeAndDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);

    @Select("SELECT * FROM attendance_record WHERE deleted = 0 AND attendance_date BETWEEN #{startDate} AND #{endDate}")
    List<AttendanceRecord> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("SELECT COUNT(*) FROM attendance_record WHERE employee_id = #{employeeId} " +
            "AND attendance_date BETWEEN #{startDate} AND #{endDate} AND status = #{status} AND deleted = 0")
    Integer countByEmployeeAndStatus(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate, @Param("status") Integer status);
}
