package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.BookPurchaseBatch;

import java.util.List;

public interface BookPurchaseBatchService extends IService<BookPurchaseBatch> {

    Page<BookPurchaseBatch> getBatchPage(Integer page, Integer size, Long deptId);

    BookPurchaseBatch getBatchById(Long id);

    boolean addBatch(BookPurchaseBatch batch);

    boolean updateBatch(BookPurchaseBatch batch);

    boolean deleteBatch(Long id);

    boolean auditBatch(Long id, Integer status);

    String generateBatchNo();

    void updateBatchTotal(Long batchId);
}
