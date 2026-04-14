package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.BookPurchase;
import com.library.service.BookPurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/purchase")
public class BookPurchaseController {

    @Autowired
    private BookPurchaseService bookPurchaseService;

    @GetMapping("/page")
    public Result<PageResult<BookPurchase>> getPurchasePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long purchaserId,
            @RequestAttribute Integer role) {
        
        if (role != 1) {
            return Result.error("无权限查看采购列表");
        }
        
        PageResult<BookPurchase> result = bookPurchaseService.getPurchasePage(page, size, keyword, status, purchaserId);
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<BookPurchase>> getAllPurchases(@RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限查看采购列表");
        }
        
        List<BookPurchase> purchases = bookPurchaseService.list();
        return Result.success(purchases);
    }

    @GetMapping("/{id}")
    public Result<BookPurchase> getPurchaseById(@PathVariable Long id, @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限查看采购详情");
        }
        
        BookPurchase purchase = bookPurchaseService.getPurchaseWithItems(id);
        if (purchase != null) {
            return Result.success(purchase);
        }
        return Result.error("采购记录不存在");
    }

    @GetMapping("/my")
    public Result<List<BookPurchase>> getMyPurchases(@RequestAttribute Long userId, @RequestAttribute Integer role) {
        if (role != 1) {
            return Result.error("无权限查看采购列表");
        }
        
        List<BookPurchase> purchases = bookPurchaseService.getPurchasesByPurchaserId(userId);
        return Result.success(purchases);
    }

    @PostMapping
    public Result<Void> addPurchase(@RequestBody BookPurchase purchase, @RequestAttribute Integer role) {
        log.info("新增采购: {}", purchase.getBatchNo());
        
        if (role != 1) {
            return Result.error("无权限添加采购");
        }
        
        try {
            boolean success = bookPurchaseService.addPurchase(purchase);
            if (success) {
                log.info("采购添加成功: {}", purchase.getBatchNo());
                return Result.success("添加成功");
            }
            return Result.error("添加失败");
        } catch (Exception e) {
            log.error("添加采购失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> updatePurchase(@PathVariable Long id, @RequestBody BookPurchase purchase, @RequestAttribute Integer role) {
        log.info("更新采购: {}", id);
        
        if (role != 1) {
            return Result.error("无权限修改采购信息");
        }
        
        purchase.setId(id);
        
        try {
            boolean success = bookPurchaseService.updatePurchase(purchase);
            if (success) {
                log.info("采购更新成功: {}", id);
                return Result.success("更新成功");
            }
            return Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新采购失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePurchase(@PathVariable Long id, @RequestAttribute Integer role) {
        log.info("删除采购: {}", id);
        
        if (role != 1) {
            return Result.error("无权限删除采购");
        }
        
        try {
            boolean success = bookPurchaseService.deletePurchase(id);
            if (success) {
                log.info("采购删除成功: {}", id);
                return Result.success("删除成功");
            }
            return Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除采购失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status, @RequestAttribute Integer role) {
        log.info("更新采购状态: {}, 状态: {}", id, status);
        
        if (role != 1) {
            return Result.error("无权限修改采购状态");
        }
        
        boolean success = bookPurchaseService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        }
        return Result.error("状态更新失败");
    }
}
