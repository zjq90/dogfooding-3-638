import request from '@/utils/request'

// 获取采购批次列表
export function getBatchList(params) {
  return request({
    url: '/purchase/batch/list',
    method: 'get',
    params
  })
}

// 获取采购批次详情
export function getBatchById(id) {
  return request({
    url: `/purchase/batch/${id}`,
    method: 'get'
  })
}

// 添加采购批次
export function addBatch(data) {
  return request({
    url: '/purchase/batch/add',
    method: 'post',
    data
  })
}

// 更新采购批次
export function updateBatch(data) {
  return request({
    url: '/purchase/batch/update',
    method: 'put',
    data
  })
}

// 删除采购批次
export function deleteBatch(id) {
  return request({
    url: `/purchase/batch/delete/${id}`,
    method: 'delete'
  })
}

// 审核采购批次
export function auditBatch(id, status) {
  return request({
    url: `/purchase/batch/audit/${id}`,
    method: 'put',
    params: { status }
  })
}

// 获取采购明细列表
export function getDetailList(batchId) {
  return request({
    url: `/purchase/detail/list/${batchId}`,
    method: 'get'
  })
}

// 获取采购明细详情
export function getDetailById(id) {
  return request({
    url: `/purchase/detail/${id}`,
    method: 'get'
  })
}

// 添加采购明细
export function addDetail(data) {
  return request({
    url: '/purchase/detail/add',
    method: 'post',
    data
  })
}

// 更新采购明细
export function updateDetail(data) {
  return request({
    url: '/purchase/detail/update',
    method: 'put',
    data
  })
}

// 删除采购明细
export function deleteDetail(id) {
  return request({
    url: `/purchase/detail/delete/${id}`,
    method: 'delete'
  })
}
