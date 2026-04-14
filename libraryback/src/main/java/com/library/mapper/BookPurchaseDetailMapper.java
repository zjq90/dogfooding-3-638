package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BookPurchaseDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BookPurchaseDetailMapper extends BaseMapper<BookPurchaseDetail> {

    @Select("SELECT d.*, c.name as categoryName, b.batch_no as batchNo FROM book_purchase_detail d " +
            "LEFT JOIN book_category c ON d.category_id = c.id " +
            "LEFT JOIN book_purchase_batch b ON d.batch_id = b.id " +
            "WHERE d.deleted = 0 AND d.batch_id = #{batchId} ORDER BY d.create_time DESC")
    List<BookPurchaseDetail> selectByBatchId(@Param("batchId") Long batchId);

    @Select("SELECT d.*, c.name as categoryName FROM book_purchase_detail d " +
            "LEFT JOIN book_category c ON d.category_id = c.id " +
            "WHERE d.deleted = 0 AND d.id = #{id}")
    BookPurchaseDetail selectDetailById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM book_purchase_detail WHERE batch_id = #{batchId} AND deleted = 0")
    Integer countByBatchId(@Param("batchId") Long batchId);

    @Select("SELECT SUM(total_price) FROM book_purchase_detail WHERE batch_id = #{batchId} AND deleted = 0")
    java.math.BigDecimal sumTotalPriceByBatchId(@Param("batchId") Long batchId);

    @Select("SELECT SUM(quantity) FROM book_purchase_detail WHERE batch_id = #{batchId} AND deleted = 0")
    Integer sumQuantityByBatchId(@Param("batchId") Long batchId);
}
