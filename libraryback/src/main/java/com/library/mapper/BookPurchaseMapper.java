package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BookPurchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface BookPurchaseMapper extends BaseMapper<BookPurchase> {
    
    @Select("SELECT * FROM book_purchase WHERE batch_no = #{batchNo} AND deleted = 0")
    BookPurchase selectByBatchNo(@Param("batchNo") String batchNo);
    
    @Select("SELECT * FROM book_purchase WHERE purchaser_id = #{purchaserId} AND deleted = 0 ORDER BY create_time DESC")
    List<BookPurchase> selectByPurchaserId(@Param("purchaserId") Long purchaserId);
}
