package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.FaceDevice;
import com.library.mapper.FaceDeviceMapper;
import com.library.service.FaceDeviceService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FaceDeviceServiceImpl extends ServiceImpl<FaceDeviceMapper, FaceDevice> implements FaceDeviceService {

    @Override
    public List<FaceDevice> getOnlineDevices() {
        return baseMapper.selectOnlineDevices();
    }

    @Override
    public FaceDevice getByDeviceNo(String deviceNo) {
        return baseMapper.selectByDeviceNo(deviceNo);
    }

    @Override
    public boolean updateDeviceStatus(String deviceNo, Integer status) {
        FaceDevice device = getByDeviceNo(deviceNo);
        if (device == null) {
            return false;
        }
        device.setStatus(status);
        if (status == 1) {
            device.setLastOnlineTime(LocalDateTime.now());
        }
        return this.updateById(device);
    }
}
