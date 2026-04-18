package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.FaceDevice;
import com.library.mapper.FaceDeviceMapper;
import com.library.service.AttendanceRecordService;
import com.library.service.FaceDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class FaceDeviceServiceImpl extends ServiceImpl<FaceDeviceMapper, FaceDevice> implements FaceDeviceService {

    @Autowired
    private AttendanceRecordService attendanceRecordService;

    @Override
    public List<FaceDevice> getAllDevices() {
        return baseMapper.selectAllDevices();
    }

    @Override
    public List<FaceDevice> getOnlineDevices() {
        return baseMapper.selectByStatus(1);
    }

    @Override
    public FaceDevice getDeviceById(Long id) {
        FaceDevice device = getById(id);
        if (device != null) {
            device.setStatusName(getStatusName(device.getStatus()));
        }
        return device;
    }

    @Override
    public FaceDevice getDeviceByNo(String deviceNo) {
        return baseMapper.selectByDeviceNo(deviceNo);
    }

    @Override
    @Transactional
    public boolean addDevice(FaceDevice device) {
        device.setStatus(1);
        device.setLastHeartbeat(LocalDateTime.now());
        return save(device);
    }

    @Override
    @Transactional
    public boolean updateDevice(FaceDevice device) {
        return updateById(device);
    }

    @Override
    @Transactional
    public boolean deleteDevice(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean updateHeartbeat(Long deviceId) {
        return baseMapper.updateHeartbeat(deviceId, LocalDateTime.now()) > 0;
    }

    @Override
    @Transactional
    public boolean simulateCheckIn(String deviceNo, Long employeeId) {
        FaceDevice device = baseMapper.selectByDeviceNo(deviceNo);
        if (device == null) {
            log.warn("设备不存在: {}", deviceNo);
            return false;
        }
        updateHeartbeat(device.getId());
        return attendanceRecordService.checkIn(employeeId, deviceNo, device.getLocation());
    }

    @Override
    @Transactional
    public boolean simulateCheckOut(String deviceNo, Long employeeId) {
        FaceDevice device = baseMapper.selectByDeviceNo(deviceNo);
        if (device == null) {
            log.warn("设备不存在: {}", deviceNo);
            return false;
        }
        updateHeartbeat(device.getId());
        return attendanceRecordService.checkOut(employeeId, deviceNo, device.getLocation());
    }

    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "离线";
            case 1: return "在线";
            case 2: return "故障";
            default: return "未知";
        }
    }
}
