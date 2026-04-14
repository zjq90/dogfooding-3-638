package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.BookPurchaseItem;
import java.util.List;

public interface BookPurchaseItemService extends IService<BookPurchaseItem> {
    
    List<BookPurchaseItem> getItemsByPurchaseId(Long purchaseId);
    
    boolean deleteByPurchaseId(Long purchaseId);
}
