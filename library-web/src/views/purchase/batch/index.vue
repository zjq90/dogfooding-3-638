<template>
  <div class="purchase-batch-page">
    <div class="page-header">
      <h2 class="page-title">采购批次管理</h2>
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增采购批次</el-button>
    </div>

    <el-card class="table-card">
      <el-table :data="batchList" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="batchNo" label="批次编号" width="150" />
        <el-table-column prop="batchName" label="批次名称" min-width="150" />
        <el-table-column prop="purchaserName" label="采购人" width="100" />
        <el-table-column prop="departmentName" label="采购部门" width="120" />
        <el-table-column prop="totalTypes" label="图书种类" width="90" align="center" />
        <el-table-column prop="totalBooks" label="总册数" width="90" align="center" />
        <el-table-column prop="totalAmount" label="批次总价" width="120" align="right">
          <template slot-scope="scope">
            <span style="color: #F56C6C; font-weight: 500;">¥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="采购日期" width="120" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleViewDetails(scope.row)">明细</el-button>
            <el-button 
              v-if="scope.row.status === 0" 
              type="text" 
              size="small" 
              @click="handleAudit(scope.row, 1)"
            >审核</el-button>
            <el-button type="text" size="small" style="color: #F56C6C" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          background
          @current-change="handleCurrentChange"
          :current-page="page"
          :page-size="size"
          :total="total"
          layout="total, prev, pager, next"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="批次名称" prop="batchName">
          <el-input v-model="form.batchName" placeholder="请输入批次名称" />
        </el-form-item>
        <el-form-item label="采购日期" prop="purchaseDate">
          <el-date-picker
            v-model="form.purchaseDate"
            type="date"
            placeholder="选择采购日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <!-- 批次详情对话框 -->
    <el-dialog title="批次详情" :visible.sync="detailDialogVisible" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="批次编号">{{ currentBatch.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="批次名称">{{ currentBatch.batchName }}</el-descriptions-item>
        <el-descriptions-item label="采购人">{{ currentBatch.purchaserName }}</el-descriptions-item>
        <el-descriptions-item label="采购部门">{{ currentBatch.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="图书种类">{{ currentBatch.totalTypes }} 种</el-descriptions-item>
        <el-descriptions-item label="总册数">{{ currentBatch.totalBooks }} 册</el-descriptions-item>
        <el-descriptions-item label="批次总价">
          <span style="color: #F56C6C; font-weight: 600;">¥{{ currentBatch.totalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="采购日期">{{ currentBatch.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentBatch.status)">
            {{ getStatusText(currentBatch.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentBatch.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="detail-section">
        <h4>采购明细</h4>
        <el-table :data="currentBatch.details" border size="small">
          <el-table-column type="index" label="序号" width="50" />
          <el-table-column prop="bookType" label="图书种类" min-width="120" />
          <el-table-column prop="bookName" label="图书名称" min-width="150" />
          <el-table-column prop="quantity" label="数量" width="80" align="center" />
          <el-table-column prop="unitPrice" label="单价" width="100" align="right">
            <template slot-scope="scope">¥{{ scope.row.unitPrice }}</template>
          </el-table-column>
          <el-table-column prop="totalPrice" label="小计" width="100" align="right">
            <template slot-scope="scope">¥{{ scope.row.totalPrice }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getBatchList, addBatch, updateBatch, deleteBatch, auditBatch, getBatchById } from '@/api/purchase'

export default {
  name: 'PurchaseBatch',
  data() {
    return {
      loading: false,
      batchList: [],
      page: 1,
      size: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      form: {
        id: null,
        batchName: '',
        purchaseDate: '',
        remark: ''
      },
      rules: {
        batchName: [{ required: true, message: '请输入批次名称', trigger: 'blur' }],
        purchaseDate: [{ required: true, message: '请选择采购日期', trigger: 'change' }]
      },
      detailDialogVisible: false,
      currentBatch: {
        details: []
      }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await getBatchList({ page: this.page, size: this.size })
        if (res.code === 200) {
          this.batchList = res.data.list
          this.total = res.data.total
        }
      } catch (error) {
        console.error('获取采购批次列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    handleCurrentChange(val) {
      this.page = val
      this.fetchData()
    },
    handleAdd() {
      this.dialogTitle = '新增采购批次'
      this.form = {
        id: null,
        batchName: '',
        purchaseDate: new Date().toISOString().split('T')[0],
        remark: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑采购批次'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleView(row) {
      try {
        const res = await getBatchById(row.id)
        if (res.code === 200) {
          this.currentBatch = res.data
          this.detailDialogVisible = true
        }
      } catch (error) {
        this.$message.error('获取详情失败')
      }
    },
    handleViewDetails(row) {
      this.$router.push(`/purchase/detail?batchId=${row.id}`)
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            if (this.form.id) {
              await updateBatch(this.form)
              this.$message.success('更新成功')
            } else {
              await addBatch(this.form)
              this.$message.success('添加成功')
            }
            this.dialogVisible = false
            this.fetchData()
          } catch (error) {
            this.$message.error('操作失败')
          }
        }
      })
    },
    async handleAudit(row, status) {
      try {
        await this.$confirm('确定要审核通过该采购批次吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await auditBatch(row.id, status)
        this.$message.success('审核成功')
        this.fetchData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('审核失败')
        }
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该采购批次吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteBatch(row.id)
        this.$message.success('删除成功')
        this.fetchData()
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
.purchase-batch-page {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 500;
}
.table-card {
  margin-bottom: 20px;
}
.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}
.detail-section {
  margin-top: 20px;
}
.detail-section h4 {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #EBEEF5;
}
</style>
