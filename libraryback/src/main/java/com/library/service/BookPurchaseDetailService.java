package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.BookPurchaseDetail;

import java.util.List;

public interface BookPurchaseDetailService extends IService<BookPurchaseDetail> {

    List<BookPurchaseDetail> getDetailsByBatchId(Long batchId);

    BookPurchaseDetail getDetailById(Long id);

    boolean addDetail(BookPurchaseDetail detail);

    boolean updateDetail(BookPurchaseDetail detail);

    boolean deleteDetail(Long id);

    boolean deleteByBatchId(Long batchId);
}
