package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("procurement")
public class Procurement {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String batchNo;
    
    private String purchaser;
    
    private Long employeeId;
    
    private Long departmentId;
    
    private BigDecimal totalPrice;
    
    private Integer status;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(select = false)
    private Integer deleted;
    
    @TableField(exist = false)
    private String departmentName;
    
    @TableField(exist = false)
    private String employeeName;
    
    @TableField(exist = false)
    private List<ProcurementItem> items;
}
