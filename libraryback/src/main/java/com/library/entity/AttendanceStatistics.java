package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("attendance_statistics")
public class AttendanceStatistics {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employeeId;

    private String employeeName;

    private Long departmentId;

    private String departmentName;

    private Integer statisticsYear;

    private Integer statisticsMonth;

    private Integer totalDays;

    private Integer actualDays;

    private Integer normalDays;

    private Integer lateCount;

    private Integer earlyLeaveCount;

    private Integer absentCount;

    private BigDecimal leaveDays;

    private BigDecimal overtimeHours;

    private BigDecimal totalWorkHours;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(select = false)
    private Integer deleted;
}
