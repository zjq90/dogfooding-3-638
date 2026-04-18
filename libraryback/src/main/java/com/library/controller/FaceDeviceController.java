package com.library.controller;

import com.library.common.Result;
import com.library.entity.FaceDevice;
import com.library.service.FaceDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/face-device")
public class FaceDeviceController {

    @Autowired
    private FaceDeviceService faceDeviceService;

    @GetMapping("/list")
    public Result<List<FaceDevice>> listDevices() {
        List<FaceDevice> devices = faceDeviceService.getAllDevices();
        return Result.success(devices);
    }

    @GetMapping("/online")
    public Result<List<FaceDevice>> listOnlineDevices() {
        List<FaceDevice> devices = faceDeviceService.getOnlineDevices();
        return Result.success(devices);
    }

    @GetMapping("/{id}")
    public Result<FaceDevice> getDevice(@PathVariable Long id) {
        FaceDevice device = faceDeviceService.getDeviceById(id);
        if (device != null) {
            return Result.success(device);
        }
        return Result.error("设备不存在");
    }

    @PostMapping("/add")
    public Result<Void> addDevice(@RequestBody FaceDevice device) {
        boolean success = faceDeviceService.addDevice(device);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/update")
    public Result<Void> updateDevice(@RequestBody FaceDevice device) {
        boolean success = faceDeviceService.updateDevice(device);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteDevice(@PathVariable Long id) {
        boolean success = faceDeviceService.deleteDevice(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/heartbeat/{id}")
    public Result<Void> heartbeat(@PathVariable Long id) {
        boolean success = faceDeviceService.updateHeartbeat(id);
        return success ? Result.success("心跳更新成功") : Result.error("更新失败");
    }

    @PostMapping("/simulate/checkin")
    public Result<Void> simulateCheckIn(@RequestParam String deviceNo, @RequestParam Long employeeId) {
        boolean success = faceDeviceService.simulateCheckIn(deviceNo, employeeId);
        return success ? Result.success("模拟上班打卡成功") : Result.error("打卡失败");
    }

    @PostMapping("/simulate/checkout")
    public Result<Void> simulateCheckOut(@RequestParam String deviceNo, @RequestParam Long employeeId) {
        boolean success = faceDeviceService.simulateCheckOut(deviceNo, employeeId);
        return success ? Result.success("模拟下班打卡成功") : Result.error("打卡失败");
    }
}
