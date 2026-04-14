package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.PageResult;
import com.library.entity.Procurement;
import java.util.List;

public interface ProcurementService extends IService<Procurement> {
    
    PageResult<Procurement> getProcurementPage(Integer page, Integer size, String keyword, Long departmentId, Long employeeId);
    
    Procurement getProcurementWithItems(Long id);
    
    boolean addProcurement(Procurement procurement);
    
    boolean updateProcurement(Procurement procurement);
    
    boolean deleteProcurement(Long id);
}
