package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("book_purchase_batch")
public class BookPurchaseBatch {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String batchNo;

    private String batchName;

    private Long purchaserId;

    private String purchaserName;

    private Long departmentId;

    private BigDecimal totalAmount;

    private Integer totalBooks;

    private Integer totalTypes;

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
    private String departmentName;

    @TableField(exist = false)
    private String statusName;

    @TableField(exist = false)
    private List<BookPurchaseDetail> details;
}
