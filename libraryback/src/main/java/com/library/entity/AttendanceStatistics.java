package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("attendance_statistics")
public class AttendanceStatistics {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long employeeId;
    
    private Integer statisticsYear;
    
    private Integer statisticsMonth;
    
    private Integer workDays;
    
    private Integer presentDays;
    
    private Integer lateDays;
    
    private Integer leaveEarlyDays;
    
    private Integer absentDays;
    
    private Integer leaveDays;
    
    private Double attendanceRate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(select = false)
    private Integer deleted;
    
    @TableField(exist = false)
    private String employeeName;
    
    @TableField(exist = false)
    private String departmentName;
    
    @TableField(exist = false)
    private Long departmentId;
}
