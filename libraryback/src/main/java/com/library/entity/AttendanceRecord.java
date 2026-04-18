package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("attendance_record")
public class AttendanceRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;

    private String employeeNo;

    private String employeeName;

    private Long departmentId;

    private String departmentName;

    private LocalDate attendanceDate;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    private String checkInDevice;

    private String checkOutDevice;

    private String checkInLocation;

    private String checkOutLocation;

    private BigDecimal workHours;

    private Integer status;

    private BigDecimal overtimeHours;

    private Integer leaveType;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(select = false)
    private Integer deleted;

    @TableField(exist = false)
    private String statusName;

    @TableField(exist = false)
    private String leaveTypeName;
}
