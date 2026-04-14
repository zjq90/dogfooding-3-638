package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BookPurchaseItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface BookPurchaseItemMapper extends BaseMapper<BookPurchaseItem> {
    
    @Select("SELECT * FROM book_purchase_item WHERE purchase_id = #{purchaseId} AND deleted = 0 ORDER BY id")
    List<BookPurchaseItem> selectByPurchaseId(@Param("purchaseId") Long purchaseId);
}
