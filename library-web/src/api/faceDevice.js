import request from '@/utils/request'

// 获取设备列表
export function getDeviceList() {
  return request({
    url: '/face-device/list',
    method: 'get'
  })
}

// 获取在线设备列表
export function getOnlineDevices() {
  return request({
    url: '/face-device/online',
    method: 'get'
  })
}

// 获取设备详情
export function getDeviceById(id) {
  return request({
    url: `/face-device/${id}`,
    method: 'get'
  })
}

// 添加设备
export function addDevice(data) {
  return request({
    url: '/face-device/add',
    method: 'post',
    data
  })
}

// 更新设备
export function updateDevice(data) {
  return request({
    url: '/face-device/update',
    method: 'put',
    data
  })
}

// 删除设备
export function deleteDevice(id) {
  return request({
    url: `/face-device/delete/${id}`,
    method: 'delete'
  })
}

// 更新设备心跳
export function updateHeartbeat(id) {
  return request({
    url: `/face-device/heartbeat/${id}`,
    method: 'post'
  })
}

// 模拟上班打卡
export function simulateCheckIn(deviceNo, employeeId) {
  return request({
    url: '/face-device/simulate/checkin',
    method: 'post',
    params: { deviceNo, employeeId }
  })
}

// 模拟下班打卡
export function simulateCheckOut(deviceNo, employeeId) {
  return request({
    url: '/face-device/simulate/checkout',
    method: 'post',
    params: { deviceNo, employeeId }
  })
}
