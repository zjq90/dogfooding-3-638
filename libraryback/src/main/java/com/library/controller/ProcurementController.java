package com.library.controller;

import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.Procurement;
import com.library.service.ProcurementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/procurement")
public class ProcurementController {

    @Autowired
    private ProcurementService procurementService;

    @GetMapping("/page")
    public Result<PageResult<Procurement>> getProcurementPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long employeeId) {
        
        PageResult<Procurement> result = procurementService.getProcurementPage(page, size, keyword, departmentId, employeeId);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Procurement> getProcurementById(@PathVariable Long id) {
        Procurement procurement = procurementService.getProcurementWithItems(id);
        if (procurement != null) {
            return Result.success(procurement);
        }
        return Result.error("采购记录不存在");
    }

    @PostMapping
    public Result<Void> addProcurement(@RequestBody Procurement procurement) {
        log.info("新增采购: {}", procurement.getBatchNo());
        
        try {
            boolean success = procurementService.addProcurement(procurement);
            if (success) {
                log.info("采购添加成功: {}", procurement.getBatchNo());
                return Result.success("添加成功");
            }
            return Result.error("添加失败");
        } catch (Exception e) {
            log.error("添加采购失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> updateProcurement(@PathVariable Long id, @RequestBody Procurement procurement) {
        log.info("更新采购信息: {}", id);
        
        procurement.setId(id);
        
        try {
            boolean success = procurementService.updateProcurement(procurement);
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
    public Result<Void> deleteProcurement(@PathVariable Long id) {
        log.info("删除采购: {}", id);
        
        try {
            boolean success = procurementService.deleteProcurement(id);
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
}
