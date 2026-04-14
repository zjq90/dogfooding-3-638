import request from '@/utils/request'

export function getPurchaseList(params) {
  return request({
    url: '/purchase/page',
    method: 'get',
    params
  })
}

export function getPurchaseDetail(id) {
  return request({
    url: `/purchase/${id}`,
    method: 'get'
  })
}

export function addPurchase(data) {
  return request({
    url: '/purchase',
    method: 'post',
    data
  })
}

export function updatePurchase(id, data) {
  return request({
    url: `/purchase/${id}`,
    method: 'put',
    data
  })
}

export function deletePurchase(id) {
  return request({
    url: `/purchase/${id}`,
    method: 'delete'
  })
}

export function updatePurchaseStatus(id, status) {
  return request({
    url: `/purchase/${id}/status`,
    method: 'put',
    params: { status }
  })
}
