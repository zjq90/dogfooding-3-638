package com.library.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BookPurchaseBatch;
import com.library.entity.BookPurchaseDetail;
import com.library.mapper.BookPurchaseBatchMapper;
import com.library.mapper.BookPurchaseDetailMapper;
import com.library.service.BookPurchaseBatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class BookPurchaseBatchServiceImpl extends ServiceImpl<BookPurchaseBatchMapper, BookPurchaseBatch> implements BookPurchaseBatchService {

    @Autowired
    private BookPurchaseDetailMapper detailMapper;

    @Override
    public Page<BookPurchaseBatch> getBatchPage(Integer page, Integer size, Long deptId) {
        Page<BookPurchaseBatch> pageParam = new Page<>(page, size);
        if (deptId != null) {
            return baseMapper.selectBatchPageByDept(pageParam, deptId);
        }
        return baseMapper.selectBatchPage(pageParam);
    }

    @Override
    public BookPurchaseBatch getBatchById(Long id) {
        BookPurchaseBatch batch = baseMapper.selectBatchById(id);
        if (batch != null) {
            batch.setStatusName(getStatusName(batch.getStatus()));
            List<BookPurchaseDetail> details = detailMapper.selectByBatchId(id);
            batch.setDetails(details);
        }
        return batch;
    }

    @Override
    @Transactional
    public boolean addBatch(BookPurchaseBatch batch) {
        batch.setBatchNo(generateBatchNo());
        batch.setStatus(0);
        batch.setTotalAmount(BigDecimal.ZERO);
        batch.setTotalBooks(0);
        batch.setTotalTypes(0);
        if (batch.getPurchaseDate() == null) {
            batch.setPurchaseDate(LocalDate.now());
        }
        return save(batch);
    }

    @Override
    @Transactional
    public boolean updateBatch(BookPurchaseBatch batch) {
        return updateById(batch);
    }

    @Override
    @Transactional
    public boolean deleteBatch(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean auditBatch(Long id, Integer status) {
        BookPurchaseBatch batch = getById(id);
        if (batch == null) {
            return false;
        }
        batch.setStatus(status);
        return updateById(batch);
    }

    @Override
    public String generateBatchNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%04d", (int) (Math.random() * 10000));
        return "CG" + dateStr + randomStr;
    }

    @Override
    public void updateBatchTotal(Long batchId) {
        BigDecimal totalAmount = detailMapper.sumTotalPriceByBatchId(batchId);
        Integer totalBooks = detailMapper.sumQuantityByBatchId(batchId);
        Integer totalTypes = detailMapper.countByBatchId(batchId);

        baseMapper.updateBatchTotal(batchId,
                totalAmount != null ? totalAmount : BigDecimal.ZERO,
                totalBooks != null ? totalBooks : 0,
                totalTypes != null ? totalTypes : 0);
    }

    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待审核";
            case 1: return "已审核";
            case 2: return "已完成";
            case 3: return "已取消";
            default: return "未知";
        }
    }
}
