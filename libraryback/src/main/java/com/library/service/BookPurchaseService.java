package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.entity.BookPurchase;
import java.util.List;

public interface BookPurchaseService extends IService<BookPurchase> {
    
    PageResult<BookPurchase> getPurchasePage(Integer page, Integer size, String keyword, Integer status, Long purchaserId);
    
    BookPurchase getPurchaseWithItems(Long id);
    
    List<BookPurchase> getPurchasesByPurchaserId(Long purchaserId);
    
    boolean addPurchase(BookPurchase purchase);
    
    boolean updatePurchase(BookPurchase purchase);
    
    boolean deletePurchase(Long id);
    
    boolean updateStatus(Long id, Integer status);
}
