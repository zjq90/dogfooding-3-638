package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.FaceDevice;

import java.util.List;

public interface FaceDeviceService extends IService<FaceDevice> {

    List<FaceDevice> getAllDevices();

    List<FaceDevice> getOnlineDevices();

    FaceDevice getDeviceById(Long id);

    FaceDevice getDeviceByNo(String deviceNo);

    boolean addDevice(FaceDevice device);

    boolean updateDevice(FaceDevice device);

    boolean deleteDevice(Long id);

    boolean updateHeartbeat(Long deviceId);

    boolean simulateCheckIn(String deviceNo, Long employeeId);

    boolean simulateCheckOut(String deviceNo, Long employeeId);
}
