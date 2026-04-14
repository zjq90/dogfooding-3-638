package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.FaceDevice;
import java.util.List;

public interface FaceDeviceService extends IService<FaceDevice> {
    
    List<FaceDevice> getOnlineDevices();
    
    FaceDevice getByDeviceNo(String deviceNo);
    
    boolean updateDeviceStatus(String deviceNo, Integer status);
}
