package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.entity.BookPurchase;
import com.library.entity.BookPurchaseItem;
import com.library.entity.Employee;
import com.library.mapper.BookPurchaseMapper;
import com.library.mapper.BookPurchaseItemMapper;
import com.library.mapper.EmployeeMapper;
import com.library.service.BookPurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class BookPurchaseServiceImpl extends ServiceImpl<BookPurchaseMapper, BookPurchase> implements BookPurchaseService {

    @Autowired
    private BookPurchaseItemMapper itemMapper;
    
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public PageResult<BookPurchase> getPurchasePage(Integer page, Integer size, String keyword, Integer status, Long purchaserId) {
        Page<BookPurchase> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BookPurchase> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(BookPurchase::getBatchNo, keyword)
                    .or()
                    .like(BookPurchase::getPurchaserName, keyword)
                    .or()
                    .like(BookPurchase::getRemark, keyword));
        }
        
        if (status != null) {
            wrapper.eq(BookPurchase::getStatus, status);
        }
        
        if (purchaserId != null && purchaserId > 0) {
            wrapper.eq(BookPurchase::getPurchaserId, purchaserId);
        }
        
        wrapper.orderByDesc(BookPurchase::getCreateTime);
        Page<BookPurchase> purchasePage = this.page(pageParam, wrapper);
        
        return new PageResult<>(purchasePage.getTotal(), purchasePage.getRecords(), 
                                purchasePage.getCurrent(), purchasePage.getSize());
    }

    @Override
    public BookPurchase getPurchaseWithItems(Long id) {
        BookPurchase purchase = this.getById(id);
        if (purchase != null) {
            List<BookPurchaseItem> items = itemMapper.selectByPurchaseId(id);
            purchase.setItems(items);
        }
        return purchase;
    }

    @Override
    public List<BookPurchase> getPurchasesByPurchaserId(Long purchaserId) {
        return baseMapper.selectByPurchaserId(purchaserId);
    }

    @Override
    @Transactional
    public boolean addPurchase(BookPurchase purchase) {
        if (purchase.getPurchaserId() == null) {
            throw new BusinessException("采购人不能为空");
        }
        
        Employee employee = employeeMapper.selectById(purchase.getPurchaserId());
        if (employee == null) {
            throw new BusinessException("采购人不存在");
        }
        
        purchase.setPurchaserName(employee.getName());
        
        String batchNo = generateBatchNo();
        purchase.setBatchNo(batchNo);
        
        if (purchase.getPurchaseDate() == null) {
            purchase.setPurchaseDate(LocalDate.now());
        }
        
        if (purchase.getStatus() == null) {
            purchase.setStatus(0);
        }
        
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalQuantity = 0;
        
        if (purchase.getItems() != null && !purchase.getItems().isEmpty()) {
            for (BookPurchaseItem item : purchase.getItems()) {
                if (item.getQuantity() == null) {
                    item.setQuantity(1);
                }
                if (item.getUnitPrice() == null) {
                    item.setUnitPrice(BigDecimal.ZERO);
                }
                BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                item.setSubtotal(subtotal);
                totalPrice = totalPrice.add(subtotal);
                totalQuantity += item.getQuantity();
            }
        }
        
        purchase.setTotalPrice(totalPrice);
        purchase.setTotalQuantity(totalQuantity);
        
        boolean success = this.save(purchase);
        
        if (success && purchase.getItems() != null && !purchase.getItems().isEmpty()) {
            for (BookPurchaseItem item : purchase.getItems()) {
                item.setPurchaseId(purchase.getId());
                itemMapper.insert(item);
            }
        }
        
        return success;
    }

    @Override
    @Transactional
    public boolean updatePurchase(BookPurchase purchase) {
        if (purchase.getId() == null) {
            throw new BusinessException("采购ID不能为空");
        }
        
        BookPurchase existPurchase = this.getById(purchase.getId());
        if (existPurchase == null) {
            throw new BusinessException("采购记录不存在");
        }
        
        if (purchase.getPurchaserId() != null) {
            Employee employee = employeeMapper.selectById(purchase.getPurchaserId());
            if (employee == null) {
                throw new BusinessException("采购人不存在");
            }
            purchase.setPurchaserName(employee.getName());
        }
        
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalQuantity = 0;
        
        if (purchase.getItems() != null && !purchase.getItems().isEmpty()) {
            for (BookPurchaseItem item : purchase.getItems()) {
                if (item.getQuantity() == null) {
                    item.setQuantity(1);
                }
                if (item.getUnitPrice() == null) {
                    item.setUnitPrice(BigDecimal.ZERO);
                }
                BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                item.setSubtotal(subtotal);
                totalPrice = totalPrice.add(subtotal);
                totalQuantity += item.getQuantity();
            }
        }
        
        purchase.setTotalPrice(totalPrice);
        purchase.setTotalQuantity(totalQuantity);
        
        boolean success = this.updateById(purchase);
        
        if (success && purchase.getItems() != null) {
            itemMapper.delete(new LambdaQueryWrapper<BookPurchaseItem>()
                    .eq(BookPurchaseItem::getPurchaseId, purchase.getId()));
            
            for (BookPurchaseItem item : purchase.getItems()) {
                item.setId(null);
                item.setPurchaseId(purchase.getId());
                itemMapper.insert(item);
            }
        }
        
        return success;
    }

    @Override
    @Transactional
    public boolean deletePurchase(Long id) {
        itemMapper.delete(new LambdaQueryWrapper<BookPurchaseItem>()
                .eq(BookPurchaseItem::getPurchaseId, id));
        return this.removeById(id);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        BookPurchase purchase = new BookPurchase();
        purchase.setId(id);
        purchase.setStatus(status);
        return this.updateById(purchase);
    }
    
    private String generateBatchNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = this.count(new LambdaQueryWrapper<BookPurchase>()
                .likeRight(BookPurchase::getBatchNo, "PO" + dateStr));
        return String.format("PO%s%04d", dateStr, count + 1);
    }
}
