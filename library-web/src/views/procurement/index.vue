<template>
  <div class="procurement-container">
    <el-card>
      <div slot="header" class="header">
        <span>图书采购管理</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增采购</el-button>
      </div>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="批次号/采购人" clearable></el-input>
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="searchForm.departmentId" placeholder="请选择部门" clearable>
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="batchNo" label="批次号" width="180"></el-table-column>
        <el-table-column prop="purchaser" label="采购人" width="120"></el-table-column>
        <el-table-column prop="departmentName" label="所属部门" width="150"></el-table-column>
        <el-table-column prop="totalPrice" label="批次总价" width="120">
          <template slot-scope="scope">
            ¥{{ scope.row.totalPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已完成' : '进行中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleView(scope.row)">查看</el-button>
            <el-button size="mini" type="warning" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageInfo.page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageInfo.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pageInfo.total"
        class="pagination">
      </el-pagination>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="800px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="采购人" prop="employeeId">
          <el-select v-model="form.employeeId" placeholder="请选择采购人" style="width: 100%">
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">进行中</el-radio>
            <el-radio :label="1">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="form.remark" rows="2"></el-input>
        </el-form-item>
        <el-form-item label="采购明细">
          <el-button size="mini" type="success" @click="addItem" icon="el-icon-plus">添加图书</el-button>
          <el-table :data="form.items" border stripe>
            <el-table-column prop="bookCategory" label="图书种类" width="120">
              <template slot-scope="scope">
                <el-select v-model="scope.row.bookCategory" placeholder="请选择" size="mini">
                  <el-option label="文学小说" value="文学小说"></el-option>
                  <el-option label="科技技术" value="科技技术"></el-option>
                  <el-option label="历史人文" value="历史人文"></el-option>
                  <el-option label="经济管理" value="经济管理"></el-option>
                  <el-option label="教育教材" value="教育教材"></el-option>
                  <el-option label="其他" value="其他"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="bookName" label="图书名称">
              <template slot-scope="scope">
                <el-input v-model="scope.row.bookName" size="mini" placeholder="请输入图书名称"></el-input>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="90">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.quantity" :min="1" size="mini"></el-input-number>
              </template>
            </el-table-column>
            <el-table-column prop="unitPrice" label="单价" width="100">
              <template slot-scope="scope">
                <el-input v-model="scope.row.unitPrice" size="mini" placeholder="单价"></el-input>
              </template>
            </el-table-column>
            <el-table-column prop="totalPrice" label="小计" width="100">
              <template slot-scope="scope">
                {{ (scope.row.unitPrice * scope.row.quantity).toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template slot-scope="scope">
                <el-button size="mini" type="danger" icon="el-icon-delete" @click="removeItem(scope.$index)"></el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getProcurementPage, getProcurement, addProcurement, updateProcurement, deleteProcurement } from '@/api/procurement'
import { getAllDepartments } from '@/api/department'
import { getAllEmployees } from '@/api/employee'

export default {
  name: 'Procurement',
  data() {
    return {
      searchForm: {
        keyword: '',
        departmentId: ''
      },
      tableData: [],
      pageInfo: {
        page: 1,
        size: 10,
        total: 0
      },
      departments: [],
      employees: [],
      dialogVisible: false,
      dialogTitle: '新增采购',
      isEdit: false,
      form: {
        employeeId: '',
        status: 0,
        remark: '',
        items: []
      },
      rules: {
        employeeId: [
          { required: true, message: '请选择采购人', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.loadData()
    this.loadDepartments()
    this.loadEmployees()
  },
  methods: {
    loadData() {
      const params = {
        page: this.pageInfo.page,
        size: this.pageInfo.size,
        keyword: this.searchForm.keyword,
        departmentId: this.searchForm.departmentId
      }
      getProcurementPage(params).then(res => {
        if (res.code === 0) {
          this.tableData = res.data.records
          this.pageInfo.total = res.data.total
        }
      })
    },
    loadDepartments() {
      getAllDepartments().then(res => {
        if (res.code === 0) {
          this.departments = res.data
        }
      })
    },
    loadEmployees() {
      getAllEmployees().then(res => {
        if (res.code === 0) {
          this.employees = res.data
        }
      })
    },
    handleSearch() {
      this.pageInfo.page = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = {
        keyword: '',
        departmentId: ''
      }
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.pageInfo.size = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.pageInfo.page = val
      this.loadData()
    },
    handleAdd() {
      this.dialogTitle = '新增采购'
      this.isEdit = false
      this.form = {
        employeeId: '',
        status: 0,
        remark: '',
        items: []
      }
      this.dialogVisible = true
    },
    handleView(row) {
      getProcurement(row.id).then(res => {
        if (res.code === 0) {
          this.dialogTitle = '查看采购'
          this.isEdit = true
          this.form = res.data
          this.dialogVisible = true
        }
      })
    },
    handleEdit(row) {
      getProcurement(row.id).then(res => {
        if (res.code === 0) {
          this.dialogTitle = '编辑采购'
          this.isEdit = true
          this.form = res.data
          this.dialogVisible = true
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该采购记录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteProcurement(row.id).then(res => {
          if (res.code === 0) {
            this.$message.success('删除成功')
            this.loadData()
          } else {
            this.$message.error(res.msg)
          }
        })
      })
    },
    addItem() {
      this.form.items.push({
        bookCategory: '',
        bookName: '',
        quantity: 1,
        unitPrice: 0
      })
    },
    removeItem(index) {
      this.form.items.splice(index, 1)
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.isEdit) {
            updateProcurement(this.form.id, this.form).then(res => {
              if (res.code === 0) {
                this.$message.success('更新成功')
                this.dialogVisible = false
                this.loadData()
              } else {
                this.$message.error(res.msg)
              }
            })
          } else {
            addProcurement(this.form).then(res => {
              if (res.code === 0) {
                this.$message.success('添加成功')
                this.dialogVisible = false
                this.loadData()
              } else {
                this.$message.error(res.msg)
              }
            })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.procurement-container {
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
