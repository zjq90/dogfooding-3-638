import request from '@/utils/request'

export function getAttendanceList(params) {
  return request({
    url: '/attendance/page',
    method: 'get',
    params
  })
}

export function getAttendanceByEmployee(employeeId, startDate, endDate) {
  return request({
    url: `/attendance/employee/${employeeId}`,
    method: 'get',
    params: { startDate, endDate }
  })
}

export function getAttendanceByDepartment(departmentId, startDate, endDate) {
  return request({
    url: `/attendance/department/${departmentId}`,
    method: 'get',
    params: { startDate, endDate }
  })
}

export function getEmployeeStatistics(employeeId, startDate, endDate) {
  return request({
    url: `/attendance/statistics/employee/${employeeId}`,
    method: 'get',
    params: { startDate, endDate }
  })
}

export function getDepartmentStatistics(departmentId, startDate, endDate) {
  return request({
    url: `/attendance/statistics/department/${departmentId}`,
    method: 'get',
    params: { startDate, endDate }
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

export function generateAttendance(employeeId, date) {
  return request({
    url: '/attendance/generate',
    method: 'post',
    params: { employeeId, date }
  })
}
