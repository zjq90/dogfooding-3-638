package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.common.PageResult;
import com.library.entity.Department;
import com.library.entity.Employee;
import com.library.entity.Procurement;
import com.library.entity.ProcurementItem;
import com.library.mapper.DepartmentMapper;
import com.library.mapper.EmployeeMapper;
import com.library.mapper.ProcurementItemMapper;
import com.library.mapper.ProcurementMapper;
import com.library.service.ProcurementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProcurementServiceImpl extends ServiceImpl<ProcurementMapper, Procurement> implements ProcurementService {

    @Autowired
    private ProcurementItemMapper procurementItemMapper;
    
    @Autowired
    private DepartmentMapper departmentMapper;
    
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public PageResult<Procurement> getProcurementPage(Integer page, Integer size, String keyword, Long departmentId, Long employeeId) {
        Page<Procurement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Procurement> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Procurement::getBatchNo, keyword)
                    .or()
                    .like(Procurement::getPurchaser, keyword));
        }
        
        if (departmentId != null && departmentId > 0) {
            wrapper.eq(Procurement::getDepartmentId, departmentId);
        }
        
        if (employeeId != null && employeeId > 0) {
            wrapper.eq(Procurement::getEmployeeId, employeeId);
        }
        
        wrapper.orderByDesc(Procurement::getCreateTime);
        Page<Procurement> procurementPage = this.page(pageParam, wrapper);
        
        List<Procurement> records = procurementPage.getRecords();
        fillDepartmentAndEmployeeName(records);
        
        return new PageResult<>(procurementPage.getTotal(), records, 
                                procurementPage.getCurrent(), procurementPage.getSize());
    }

    @Override
    public Procurement getProcurementWithItems(Long id) {
        Procurement procurement = this.getById(id);
        if (procurement == null) {
            return null;
        }
        
        LambdaQueryWrapper<ProcurementItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcurementItem::getProcurementId, id);
        List<ProcurementItem> items = procurementItemMapper.selectList(wrapper);
        procurement.setItems(items);
        
        fillDepartmentAndEmployeeName(List.of(procurement));
        
        return procurement;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addProcurement(Procurement procurement) {
        if (procurement.getEmployeeId() == null) {
            throw new BusinessException("采购人不能为空");
        }
        
        Employee employee = employeeMapper.selectById(procurement.getEmployeeId());
        if (employee == null) {
            throw new BusinessException("采购人不存在");
        }
        
        if (procurement.getDepartmentId() == null) {
            procurement.setDepartmentId(employee.getDepartmentId());
        }
        
        procurement.setBatchNo("CG" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        procurement.setPurchaser(employee.getName());
        
        if (procurement.getStatus() == null) {
            procurement.setStatus(1);
        }
        
        BigDecimal totalPrice = BigDecimal.ZERO;
        if (procurement.getItems() != null && !procurement.getItems().isEmpty()) {
            for (ProcurementItem item : procurement.getItems()) {
                if (item.getQuantity() == null || item.getUnitPrice() == null) {
                    throw new BusinessException("图书数量和单价不能为空");
                }
                item.setTotalPrice(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
                totalPrice = totalPrice.add(item.getTotalPrice());
            }
        }
        procurement.setTotalPrice(totalPrice);
        
        boolean success = this.save(procurement);
        
        if (success && procurement.getItems() != null && !procurement.getItems().isEmpty()) {
            for (ProcurementItem item : procurement.getItems()) {
                item.setProcurementId(procurement.getId());
                procurementItemMapper.insert(item);
            }
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProcurement(Procurement procurement) {
        if (procurement.getId() == null) {
            throw new BusinessException("采购ID不能为空");
        }
        
        Procurement existProcurement = this.getById(procurement.getId());
        if (existProcurement == null) {
            throw new BusinessException("采购记录不存在");
        }
        
        if (procurement.getEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(procurement.getEmployeeId());
            if (employee == null) {
                throw new BusinessException("采购人不存在");
            }
            procurement.setPurchaser(employee.getName());
        }
        
        if (procurement.getItems() != null && !procurement.getItems().isEmpty()) {
            LambdaQueryWrapper<ProcurementItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcurementItem::getProcurementId, procurement.getId());
            procurementItemMapper.delete(wrapper);
            
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (ProcurementItem item : procurement.getItems()) {
                if (item.getQuantity() == null || item.getUnitPrice() == null) {
                    throw new BusinessException("图书数量和单价不能为空");
                }
                item.setProcurementId(procurement.getId());
                item.setTotalPrice(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
                totalPrice = totalPrice.add(item.getTotalPrice());
                procurementItemMapper.insert(item);
            }
            procurement.setTotalPrice(totalPrice);
        }
        
        return this.updateById(procurement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProcurement(Long id) {
        LambdaQueryWrapper<ProcurementItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcurementItem::getProcurementId, id);
        procurementItemMapper.delete(wrapper);
        
        return this.removeById(id);
    }
    
    private void fillDepartmentAndEmployeeName(List<Procurement> procurements) {
        if (procurements == null || procurements.isEmpty()) {
            return;
        }
        
        List<Department> allDepartments = departmentMapper.selectAll();
        Map<Long, String> deptMap = allDepartments.stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
        
        List<Employee> allEmployees = employeeMapper.selectAllWithDepartmentName();
        Map<Long, String> empMap = allEmployees.stream()
                .collect(Collectors.toMap(Employee::getId, Employee::getName));
        
        for (Procurement procurement : procurements) {
            if (procurement.getDepartmentId() != null) {
                procurement.setDepartmentName(deptMap.get(procurement.getDepartmentId()));
            }
            if (procurement.getEmployeeId() != null) {
                procurement.setEmployeeName(empMap.get(procurement.getEmployeeId()));
            }
        }
    }
}
