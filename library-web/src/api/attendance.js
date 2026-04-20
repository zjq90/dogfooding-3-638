import request from '@/utils/request'

export function getAttendancePage(params) {
  return request({
    url: '/attendance/page',
    method: 'get',
    params
  })
}

export function checkIn(employeeId, deviceNo) {
  return request({
    url: '/attendance/checkin',
    method: 'post',
    params: { employeeId, deviceNo }
  })
}

export function checkOut(employeeId, deviceNo) {
  return request({
    url: '/attendance/checkout',
    method: 'post',
    params: { employeeId, deviceNo }
  })
}

export function getStatisticsPage(params) {
  return request({
    url: '/attendance/statistics/page',
    method: 'get',
    params
  })
}

export function generateStatistics(params) {
  return request({
    url: '/attendance/statistics/generate',
    method: 'post',
    params
  })
}

export function generateMonthlyStatistics(params) {
  return request({
    url: '/attendance/statistics/generateMonthly',
    method: 'post',
    params
  })
}
