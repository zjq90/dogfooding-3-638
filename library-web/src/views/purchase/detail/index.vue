<template>
  <div class="purchase-detail-page">
    <div class="page-header">
      <div class="header-left">
        <el-button icon="el-icon-arrow-left" @click="goBack">返回</el-button>
        <h2 class="page-title">采购明细管理</h2>
      </div>
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增明细</el-button>
    </div>

    <el-card class="info-card" v-if="batchInfo.id">
      <el-descriptions :column="4" border size="small">
        <el-descriptions-item label="批次编号">{{ batchInfo.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="批次名称">{{ batchInfo.batchName }}</el-descriptions-item>
        <el-descriptions-item label="采购人">{{ batchInfo.purchaserName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(batchInfo.status)">
            {{ getStatusText(batchInfo.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="table-card">
      <el-table :data="detailList" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="bookType" label="图书种类" min-width="120" />
        <el-table-column prop="bookName" label="图书名称" min-width="180" />
        <el-table-column prop="isbn" label="ISBN" width="140" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="publisher" label="出版社" min-width="150" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="unitPrice" label="单价" width="100" align="right">
          <template slot-scope="scope">
            <span>¥{{ scope.row.unitPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="小计" width="100" align="right">
          <template slot-scope="scope">
            <span style="color: #F56C6C; font-weight: 500;">¥{{ scope.row.totalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="small" style="color: #F56C6C" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="summary-bar" v-if="detailList.length > 0">
        <span>共 <strong>{{ detailList.length }}</strong> 条明细</span>
        <span>总数量: <strong>{{ totalQuantity }}</strong> 册</span>
        <span>总金额: <strong style="color: #F56C6C;">¥{{ totalAmount }}</strong></span>
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="图书种类" prop="bookType">
          <el-input v-model="form.bookType" placeholder="请输入图书种类/分类" />
        </el-form-item>
        <el-form-item label="图书名称" prop="bookName">
          <el-input v-model="form.bookName" placeholder="请输入图书名称" />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input v-model="form.isbn" placeholder="请输入ISBN编号" />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="form.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单价" prop="unitPrice">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="小计">
          <el-input :value="calculateTotal" disabled style="width: 100%">
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getDetailList, addDetail, updateDetail, deleteDetail } from '@/api/purchase'
import { getBatchById } from '@/api/purchase'

export default {
  name: 'PurchaseDetail',
  data() {
    return {
      loading: false,
      batchId: null,
      batchInfo: {},
      detailList: [],
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        batchId: null,
        bookType: '',
        bookName: '',
        isbn: '',
        author: '',
        publisher: '',
        quantity: 1,
        unitPrice: 0,
        remark: ''
      },
      rules: {
        bookType: [{ required: true, message: '请输入图书种类', trigger: 'blur' }],
        bookName: [{ required: true, message: '请输入图书名称', trigger: 'blur' }],
        quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
        unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }]
      }
    }
  },
  computed: {
    calculateTotal() {
      return (this.form.quantity * this.form.unitPrice).toFixed(2)
    },
    totalQuantity() {
      return this.detailList.reduce((sum, item) => sum + item.quantity, 0)
    },
    totalAmount() {
      return this.detailList.reduce((sum, item) => sum + parseFloat(item.totalPrice), 0).toFixed(2)
    }
  },
  created() {
    this.batchId = this.$route.query.batchId
    if (this.batchId) {
      this.fetchBatchInfo()
      this.fetchData()
    } else {
      this.$message.error('缺少批次ID')
      this.goBack()
    }
  },
  methods: {
    async fetchBatchInfo() {
      try {
        const res = await getBatchById(this.batchId)
        if (res.code === 200) {
          this.batchInfo = res.data
        }
      } catch (error) {
        console.error('获取批次信息失败:', error)
      }
    },
    async fetchData() {
      this.loading = true
      try {
        const res = await getDetailList(this.batchId)
        if (res.code === 200) {
          this.detailList = res.data
        }
      } catch (error) {
        console.error('获取采购明细列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    goBack() {
      this.$router.push('/purchase/batch')
    },
    handleAdd() {
      this.dialogTitle = '新增采购明细'
      this.form = {
        id: null,
        batchId: this.batchId,
        bookType: '',
        bookName: '',
        isbn: '',
        author: '',
        publisher: '',
        quantity: 1,
        unitPrice: 0,
        remark: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑采购明细'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            if (this.form.id) {
              await updateDetail(this.form)
              this.$message.success('更新成功')
            } else {
              await addDetail(this.form)
              this.$message.success('添加成功')
            }
            this.dialogVisible = false
            this.fetchData()
            this.fetchBatchInfo()
          } catch (error) {
            this.$message.error('操作失败')
          }
        }
      })
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该采购明细吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteDetail(row.id)
        this.$message.success('删除成功')
        this.fetchData()
        this.fetchBatchInfo()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    getStatusType(status) {
      const types = ['warning', 'success', 'info', 'danger']
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = ['待审核', '已审核', '已完成', '已取消']
      return texts[status] || '未知'
    }
  }
}
</script>

<style scoped>
.purchase-detail-page {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}
.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 500;
}
.info-card {
  margin-bottom: 20px;
}
.table-card {
  margin-bottom: 20px;
}
.summary-bar {
  margin-top: 15px;
  padding: 15px;
  background-color: #F5F7FA;
  border-radius: 4px;
  display: flex;
  justify-content: flex-end;
  gap: 30px;
  font-size: 14px;
}
.summary-bar strong {
  font-size: 16px;
}
</style>
