package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BookPurchaseBatch;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface BookPurchaseBatchMapper extends BaseMapper<BookPurchaseBatch> {

    @Select("SELECT b.*, d.name as departmentName FROM book_purchase_batch b " +
            "LEFT JOIN sys_department d ON b.department_id = d.id " +
            "WHERE b.deleted = 0 ORDER BY b.create_time DESC")
    Page<BookPurchaseBatch> selectBatchPage(Page<BookPurchaseBatch> page);

    @Select("SELECT b.*, d.name as departmentName FROM book_purchase_batch b " +
            "LEFT JOIN sys_department d ON b.department_id = d.id " +
            "WHERE b.deleted = 0 AND b.department_id = #{deptId} ORDER BY b.create_time DESC")
    Page<BookPurchaseBatch> selectBatchPageByDept(Page<BookPurchaseBatch> page, @Param("deptId") Long deptId);

    @Select("SELECT b.*, d.name as departmentName FROM book_purchase_batch b " +
            "LEFT JOIN sys_department d ON b.department_id = d.id " +
            "WHERE b.deleted = 0 AND b.id = #{id}")
    BookPurchaseBatch selectBatchById(@Param("id") Long id);

    @Update("UPDATE book_purchase_batch SET total_amount = #{totalAmount}, " +
            "total_books = #{totalBooks}, total_types = #{totalTypes} WHERE id = #{batchId}")
    int updateBatchTotal(@Param("batchId") Long batchId, @Param("totalAmount") java.math.BigDecimal totalAmount,
                         @Param("totalBooks") Integer totalBooks, @Param("totalTypes") Integer totalTypes);
}
