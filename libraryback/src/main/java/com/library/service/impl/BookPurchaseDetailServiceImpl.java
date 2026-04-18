package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BookPurchaseDetail;
import com.library.mapper.BookPurchaseDetailMapper;
import com.library.service.BookPurchaseDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class BookPurchaseDetailServiceImpl extends ServiceImpl<BookPurchaseDetailMapper, BookPurchaseDetail> implements BookPurchaseDetailService {

    @Override
    public List<BookPurchaseDetail> getDetailsByBatchId(Long batchId) {
        return baseMapper.selectByBatchId(batchId);
    }

    @Override
    public BookPurchaseDetail getDetailById(Long id) {
        return baseMapper.selectDetailById(id);
    }

    @Override
    @Transactional
    public boolean addDetail(BookPurchaseDetail detail) {
        detail.setTotalPrice(detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity())));
        return save(detail);
    }

    @Override
    @Transactional
    public boolean updateDetail(BookPurchaseDetail detail) {
        detail.setTotalPrice(detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity())));
        return updateById(detail);
    }

    @Override
    @Transactional
    public boolean deleteDetail(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean deleteByBatchId(Long batchId) {
        return lambdaUpdate().eq(BookPurchaseDetail::getBatchId, batchId).remove();
    }
}
