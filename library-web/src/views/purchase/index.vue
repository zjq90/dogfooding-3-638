<template>
  <div class="purchase-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="批次号/采购人/备注" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="待审核" :value="0" />
            <el-option label="已审核" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="采购人">
          <el-select v-model="searchForm.purchaserId" placeholder="请选择采购人" clearable filterable style="width: 150px">
            <el-option v-for="item in employeeList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="table-card">
      <div slot="header" class="card-header">
        <span>采购批次列表</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增采购</el-button>
      </div>
      
      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%" border>
        <el-table-column type="index" width="60" align="center" label="序号" />
        <el-table-column prop="batchNo" label="批次号" min-width="150" />
        <el-table-column prop="purchaserName" label="采购人" min-width="100" />
        <el-table-column prop="purchaseDate" label="采购日期" min-width="120" align="center" />
        <el-table-column prop="totalQuantity" label="总数量" min-width="100" align="center">
          <template slot-scope="scope">
            <span class="quantity-text">{{ scope.row.totalQuantity }} 册</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总价" min-width="120" align="right">
          <template slot-scope="scope">
            <span class="price-text">¥{{ scope.row.totalPrice ? scope.row.totalPrice.toFixed(2) : '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleView(scope.row)">详情</el-button>
            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button v-if="scope.row.status === 0" type="text" size="small" style="color: #67c23a" @click="handleAudit(scope.row)">审核</el-button>
            <el-button type="text" size="small" style="color: #f56c6c" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-wrapper">
        <el-pagination
          :current-page="page"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="800px" :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="采购人" prop="purchaserId">
              <el-select v-model="form.purchaserId" placeholder="请选择采购人" style="width: 100%">
                <el-option v-for="item in employeeList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购日期" prop="purchaseDate">
              <el-date-picker v-model="form.purchaseDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
        
        <el-divider content-position="left">采购明细</el-divider>
        
        <div class="items-header">
          <el-button type="primary" size="small" icon="el-icon-plus" @click="addItem">添加明细</el-button>
        </div>
        
        <el-table :data="form.items" border style="width: 100%; margin-top: 10px">
          <el-table-column label="图书种类" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.bookType" placeholder="种类" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="图书名称" min-width="180">
            <template slot-scope="scope">
              <el-input v-model="scope.row.bookName" placeholder="名称" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="数量" width="100">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.quantity" :min="1" size="small" controls-position="right" @change="calculateSubtotal(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="120">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.unitPrice" :min="0" :precision="2" size="small" controls-position="right" @change="calculateSubtotal(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120" align="right">
            <template slot-scope="scope">
              ¥{{ scope.row.subtotal ? scope.row.subtotal.toFixed(2) : '0.00' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template slot-scope="scope">
              <el-button type="text" size="small" style="color: #f56c6c" @click="removeItem(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="items-footer">
          <span>合计：{{ form.items.length }} 种图书，共 {{ totalQuantity }} 册，总价 <strong>¥{{ totalPrice.toFixed(2) }}</strong></span>
        </div>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
    
    <el-dialog title="采购详情" :visible.sync="detailDialogVisible" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="批次号">{{ detailData.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="采购人">{{ detailData.purchaserName }}</el-descriptions-item>
        <el-descriptions-item label="采购日期">{{ detailData.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)" size="small">{{ getStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总数量">{{ detailData.totalQuantity }} 册</el-descriptions-item>
        <el-descriptions-item label="总价">¥{{ detailData.totalPrice ? detailData.totalPrice.toFixed(2) : '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider content-position="left">采购明细</el-divider>
      
      <el-table :data="detailData.items" border style="width: 100%">
        <el-table-column type="index" width="50" align="center" label="序号" />
        <el-table-column prop="bookType" label="图书种类" width="120" />
        <el-table-column prop="bookName" label="图书名称" min-width="180" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="unitPrice" label="单价" width="100" align="right">
          <template slot-scope="scope">
            ¥{{ scope.row.unitPrice ? scope.row.unitPrice.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="subtotal" label="小计" width="100" align="right">
          <template slot-scope="scope">
            ¥{{ scope.row.subtotal ? scope.row.subtotal.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { getPurchaseList, getPurchaseDetail, addPurchase, updatePurchase, deletePurchase, updatePurchaseStatus } from '@/api/purchase'
import { getAllEmployees } from '@/api/employee'

export default {
  name: 'PurchaseManagement',
  data() {
    return {
      loading: false,
      tableData: [],
      page: 1,
      size: 10,
      total: 0,
      searchForm: {
        keyword: '',
        status: null,
        purchaserId: null
      },
      dialogVisible: false,
      dialogTitle: '新增采购',
      submitLoading: false,
      form: {
        id: null,
        purchaserId: null,
        purchaseDate: null,
        remark: '',
        items: []
      },
      rules: {
        purchaserId: [{ required: true, message: '请选择采购人', trigger: 'change' }],
        purchaseDate: [{ required: true, message: '请选择采购日期', trigger: 'change' }]
      },
      employeeList: [],
      detailDialogVisible: false,
      detailData: { items: [] }
    }
  },
  computed: {
    totalQuantity() {
      return this.form.items.reduce((sum, item) => sum + (item.quantity || 0), 0)
    },
    totalPrice() {
      return this.form.items.reduce((sum, item) => sum + (item.subtotal || 0), 0)
    }
  },
  created() {
    this.loadData()
    this.loadEmployees()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getPurchaseList({
          page: this.page,
          size: this.size,
          keyword: this.searchForm.keyword,
          status: this.searchForm.status,
          purchaserId: this.searchForm.purchaserId
        })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        console.error('加载采购列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    async loadEmployees() {
      try {
        const res = await getAllEmployees()
        if (res.code === 200) {
          this.employeeList = res.data
        }
      } catch (error) {
        console.error('加载员工失败:', error)
      }
    },
    
    getStatusType(status) {
      const types = { 0: 'info', 1: 'warning', 2: 'success' }
      return types[status] || 'info'
    },
    
    getStatusText(status) {
      const texts = { 0: '待审核', 1: '已审核', 2: '已完成' }
      return texts[status] || '未知'
    },
    
    formatTime(time) {
      if (!time) return '-'
      return time.replace('T', ' ').substring(0, 19)
    },
    
    handleSearch() {
      this.page = 1
      this.loadData()
    },
    
    handleReset() {
      this.searchForm = { keyword: '', status: null, purchaserId: null }
      this.handleSearch()
    },
    
    handleSizeChange(val) {
      this.size = val
      this.loadData()
    },
    
    handleCurrentChange(val) {
      this.page = val
      this.loadData()
    },
    
    handleAdd() {
      this.dialogTitle = '新增采购'
      this.form = {
        id: null,
        purchaserId: null,
        purchaseDate: null,
        remark: '',
        items: []
      }
      this.dialogVisible = true
    },
    
    async handleEdit(row) {
      this.dialogTitle = '编辑采购'
      try {
        const res = await getPurchaseDetail(row.id)
        if (res.code === 200) {
          this.form = {
            id: res.data.id,
            purchaserId: res.data.purchaserId,
            purchaseDate: res.data.purchaseDate,
            remark: res.data.remark,
            items: res.data.items || []
          }
          this.dialogVisible = true
        }
      } catch (error) {
        console.error('获取详情失败:', error)
      }
    },
    
    async handleView(row) {
      try {
        const res = await getPurchaseDetail(row.id)
        if (res.code === 200) {
          this.detailData = res.data
          this.detailDialogVisible = true
        }
      } catch (error) {
        console.error('获取详情失败:', error)
      }
    },
    
    async handleAudit(row) {
      try {
        await this.$confirm('确定审核通过该采购批次吗？', '提示', { type: 'warning' })
        const res = await updatePurchaseStatus(row.id, 1)
        if (res.code === 200) {
          this.$message.success('审核成功')
          this.loadData()
        } else {
          this.$message.error(res.message || '审核失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('审核失败:', error)
        }
      }
    },
    
    async handleDelete(row) {
      try {
        await this.$confirm(`确定要删除采购批次「${row.batchNo}」吗？`, '提示', { type: 'warning' })
        const res = await deletePurchase(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        } else {
          this.$message.error(res.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
        }
      }
    },
    
    addItem() {
      this.form.items.push({
        bookType: '',
        bookName: '',
        quantity: 1,
        unitPrice: 0,
        subtotal: 0
      })
    },
    
    removeItem(index) {
      this.form.items.splice(index, 1)
    },
    
    calculateSubtotal(row) {
      row.subtotal = (row.quantity || 0) * (row.unitPrice || 0)
    },
    
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          if (this.form.items.length === 0) {
            this.$message.warning('请添加采购明细')
            return
          }
          this.submitLoading = true
          try {
            const api = this.form.id ? updatePurchase(this.form.id, this.form) : addPurchase(this.form)
            const res = await api
            if (res.code === 200) {
              this.$message.success(this.form.id ? '更新成功' : '添加成功')
              this.dialogVisible = false
              this.loadData()
            } else {
              this.$message.error(res.message || '操作失败')
            }
          } catch (error) {
            console.error('提交失败:', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.purchase-container {
  padding-bottom: 24px;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.table-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 16px;
  font-weight: 500;
}

.items-header {
  display: flex;
  justify-content: flex-end;
}

.items-footer {
  margin-top: 16px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 4px;
  text-align: right;
}

.items-footer strong {
  color: #f56c6c;
  font-size: 18px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.quantity-text {
  font-weight: 500;
  color: #409eff;
}

.price-text {
  font-weight: 600;
  color: #f56c6c;
}
</style>
