package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("attendance")
public class Attendance {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long employeeId;
    
    private LocalDate attendanceDate;
    
    private LocalDateTime checkInTime;
    
    private LocalDateTime checkOutTime;
    
    private Integer status;
    
    private String deviceNo;
    
    private String remark;
    
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
