package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("procurement_item")
public class ProcurementItem {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long procurementId;
    
    private String bookCategory;
    
    private String bookName;
    
    private String isbn;
    
    private Integer quantity;
    
    private BigDecimal unitPrice;
    
    private BigDecimal totalPrice;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(select = false)
    private Integer deleted;
}
