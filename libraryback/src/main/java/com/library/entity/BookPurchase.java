package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("book_purchase")
public class BookPurchase {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String batchNo;
    
    private Long purchaserId;
    
    private String purchaserName;
    
    private BigDecimal totalPrice;
    
    private Integer totalQuantity;
    
    private LocalDate purchaseDate;
    
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
    private List<BookPurchaseItem> items;
}
