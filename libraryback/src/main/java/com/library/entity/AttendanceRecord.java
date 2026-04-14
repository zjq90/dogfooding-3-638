package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("attendance_record")
public class AttendanceRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long employeeId;
    
    private String employeeName;
    
    private String employeeNo;
    
    private Long departmentId;
    
    private String departmentName;
    
    private LocalDate attendanceDate;
    
    private LocalDateTime checkInTime;
    
    private LocalDateTime checkOutTime;
    
    private String checkInDevice;
    
    private String checkOutDevice;
    
    private Integer checkInType;
    
    private Integer checkOutType;
    
    private BigDecimal workHours;
    
    private Integer status;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(select = false)
    private Integer deleted;
}
