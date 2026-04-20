import request from '@/utils/request'

export function getProcurementPage(params) {
  return request({
    url: '/procurement/page',
    method: 'get',
    params
  })
}

export function getProcurement(id) {
  return request({
    url: `/procurement/${id}`,
    method: 'get'
  })
}

export function addProcurement(data) {
  return request({
    url: '/procurement',
    method: 'post',
    data
  })
}

export function updateProcurement(id, data) {
  return request({
    url: `/procurement/${id}`,
    method: 'put',
    data
  })
}

export function deleteProcurement(id) {
  return request({
    url: `/procurement/${id}`,
    method: 'delete'
  })
}
