import request from '@/utils/request'

export function employeeLogin(username, password) {
  const params = new URLSearchParams()
  params.append('username', username)
  params.append('password', password)
  
  return request({
    url: '/employee/auth/login',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function getEmployeeInfo() {
  return request({
    url: '/employee/auth/info',
    method: 'get'
  })
}

export function getEmployeePermissions() {
  return request({
    url: '/employee/auth/permissions',
    method: 'get'
  })
}

export function employeeLogout() {
  return request({
    url: '/employee/auth/logout',
    method: 'post'
  })
}
