package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BookPurchaseItem;
import com.library.mapper.BookPurchaseItemMapper;
import com.library.service.BookPurchaseItemService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookPurchaseItemServiceImpl extends ServiceImpl<BookPurchaseItemMapper, BookPurchaseItem> implements BookPurchaseItemService {

    @Override
    public List<BookPurchaseItem> getItemsByPurchaseId(Long purchaseId) {
        return baseMapper.selectByPurchaseId(purchaseId);
    }

    @Override
    public boolean deleteByPurchaseId(Long purchaseId) {
        return this.remove(new LambdaQueryWrapper<BookPurchaseItem>()
                .eq(BookPurchaseItem::getPurchaseId, purchaseId));
    }
}
