package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.entity.BookPurchaseBatch;
import com.library.entity.BookPurchaseDetail;
import com.library.entity.Employee;
import com.library.service.BookPurchaseBatchService;
import com.library.service.BookPurchaseDetailService;
import com.library.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/purchase")
public class BookPurchaseController {

    @Autowired
    private BookPurchaseBatchService batchService;

    @Autowired
    private BookPurchaseDetailService detailService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/batch/list")
    public Result<PageResult<BookPurchaseBatch>> listBatches(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestAttribute(required = false) Long userId,
            @RequestAttribute(required = false) Integer role) {

        Long deptId = null;
        if (role != null && role != 1) {
            Employee employee = employeeService.getById(userId);
            if (employee != null) {
                deptId = employee.getDepartmentId();
            }
        }
        Page<BookPurchaseBatch> batchPage = batchService.getBatchPage(page, size, deptId);
        PageResult<BookPurchaseBatch> pageResult = new PageResult<>(batchPage.getTotal(), batchPage.getRecords(),
                batchPage.getCurrent(), batchPage.getSize());
        return Result.success(pageResult);
    }

    @GetMapping("/batch/{id}")
    public Result<BookPurchaseBatch> getBatch(@PathVariable Long id) {
        BookPurchaseBatch batch = batchService.getBatchById(id);
        if (batch != null) {
            return Result.success(batch);
        }
        return Result.error("批次不存在");
    }

    @PostMapping("/batch/add")
    public Result<Void> addBatch(@RequestBody BookPurchaseBatch batch, @RequestAttribute Long userId) {
        Employee employee = employeeService.getById(userId);
        if (employee != null) {
            batch.setPurchaserId(employee.getId());
            batch.setPurchaserName(employee.getName());
            batch.setDepartmentId(employee.getDepartmentId());
        }
        boolean success = batchService.addBatch(batch);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/batch/update")
    public Result<Void> updateBatch(@RequestBody BookPurchaseBatch batch) {
        boolean success = batchService.updateBatch(batch);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/batch/delete/{id}")
    public Result<Void> deleteBatch(@PathVariable Long id) {
        boolean success = batchService.deleteBatch(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PutMapping("/batch/audit/{id}")
    public Result<Void> auditBatch(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = batchService.auditBatch(id, status);
        return success ? Result.success("审核成功") : Result.error("审核失败");
    }

    @GetMapping("/detail/list/{batchId}")
    public Result<List<BookPurchaseDetail>> listDetails(@PathVariable Long batchId) {
        List<BookPurchaseDetail> details = detailService.getDetailsByBatchId(batchId);
        return Result.success(details);
    }

    @GetMapping("/detail/{id}")
    public Result<BookPurchaseDetail> getDetail(@PathVariable Long id) {
        BookPurchaseDetail detail = detailService.getDetailById(id);
        if (detail != null) {
            return Result.success(detail);
        }
        return Result.error("明细不存在");
    }

    @PostMapping("/detail/add")
    public Result<Void> addDetail(@RequestBody BookPurchaseDetail detail) {
        boolean success = detailService.addDetail(detail);
        if (success) {
            batchService.updateBatchTotal(detail.getBatchId());
        }
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/detail/update")
    public Result<Void> updateDetail(@RequestBody BookPurchaseDetail detail) {
        boolean success = detailService.updateDetail(detail);
        if (success) {
            batchService.updateBatchTotal(detail.getBatchId());
        }
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/detail/delete/{id}")
    public Result<Void> deleteDetail(@PathVariable Long id) {
        BookPurchaseDetail detail = detailService.getById(id);
        if (detail == null) {
            return Result.error("明细不存在");
        }
        Long batchId = detail.getBatchId();
        boolean success = detailService.deleteDetail(id);
        if (success) {
            batchService.updateBatchTotal(batchId);
        }
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
