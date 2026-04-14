import request from '@/utils/request'

// 获取考勤记录列表
export function getRecordList(params) {
  return request({
    url: '/attendance/record/list',
    method: 'get',
    params
  })
}

// 获取考勤记录详情
export function getRecordById(id) {
  return request({
    url: `/attendance/record/${id}`,
    method: 'get'
  })
}

// 获取今日考勤记录
export function getTodayRecord() {
  return request({
    url: '/attendance/record/today',
    method: 'get'
  })
}

// 添加考勤记录
export function addRecord(data) {
  return request({
    url: '/attendance/record/add',
    method: 'post',
    data
  })
}

// 更新考勤记录
export function updateRecord(data) {
  return request({
    url: '/attendance/record/update',
    method: 'put',
    data
  })
}

// 删除考勤记录
export function deleteRecord(id) {
  return request({
    url: `/attendance/record/delete/${id}`,
    method: 'delete'
  })
}

// 上班打卡
export function checkIn(deviceNo) {
  return request({
    url: '/attendance/record/checkin',
    method: 'post',
    params: { deviceNo }
  })
}

// 下班打卡
export function checkOut(deviceNo) {
  return request({
    url: '/attendance/record/checkout',
    method: 'post',
    params: { deviceNo }
  })
}

// 生成每日考勤
export function generateDailyAttendance(date) {
  return request({
    url: '/attendance/record/generate',
    method: 'post',
    params: { date }
  })
}

// 获取考勤统计列表
export function getStatisticsList(params) {
  return request({
    url: '/attendance/statistics/list',
    method: 'get',
    params
  })
}

// 获取考勤统计详情
export function getStatisticsById(id) {
  return request({
    url: `/attendance/statistics/${id}`,
    method: 'get'
  })
}

// 生成月度考勤统计
export function generateMonthlyStatistics(year, month) {
  return request({
    url: '/attendance/statistics/generate',
    method: 'post',
    params: { year, month }
  })
}

// 生成指定员工月度考勤统计
export function generateEmployeeStatistics(employeeId, year, month) {
  return request({
    url: '/attendance/statistics/generate/employee',
    method: 'post',
    params: { employeeId, year, month }
  })
}
