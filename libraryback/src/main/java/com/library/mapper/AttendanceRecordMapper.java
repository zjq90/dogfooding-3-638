package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.AttendanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceRecordMapper extends BaseMapper<AttendanceRecord> {
    
    @Select("SELECT * FROM attendance_record WHERE employee_id = #{employeeId} AND attendance_date = #{date} AND deleted = 0")
    AttendanceRecord selectByEmployeeAndDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);
    
    @Select("SELECT * FROM attendance_record WHERE employee_id = #{employeeId} AND attendance_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY attendance_date")
    List<AttendanceRecord> selectByEmployeeAndDateRange(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Select("SELECT * FROM attendance_record WHERE department_id = #{departmentId} AND attendance_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY attendance_date, employee_id")
    List<AttendanceRecord> selectByDepartmentAndDateRange(@Param("departmentId") Long departmentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
