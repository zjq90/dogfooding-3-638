<template>
  <div class="attendance-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="员工">
          <el-select v-model="searchForm.employeeId" placeholder="请选择员工" clearable filterable style="width: 180px">
            <el-option v-for="item in employeeList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-cascader
            v-model="searchForm.departmentId"
            :options="departmentOptions"
            :props="{ checkStrictly: true, value: 'id', label: 'name', children: 'children', emitPath: false }"
            clearable
            placeholder="请选择部门"
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon normal">
              <i class="el-icon-success"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.normalDays || 0 }}</div>
              <div class="stat-label">正常出勤(天)</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon late">
              <i class="el-icon-warning"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.lateDays || 0 }}</div>
              <div class="stat-label">迟到(天)</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon early">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.earlyLeaveDays || 0 }}</div>
              <div class="stat-label">早退(天)</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon hours">
              <i class="el-icon-data-line"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.avgWorkHours || 0 }}</div>
              <div class="stat-label">平均工时(小时)</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card class="table-card">
      <div slot="header" class="card-header">
        <span>考勤记录</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleGenerate">生成模拟考勤</el-button>
      </div>
      
      <el-table v-loading="loading" :data="tableData" stripe style="width: 100%" border>
        <el-table-column type="index" width="60" align="center" label="序号" />
        <el-table-column prop="employeeName" label="员工姓名" min-width="100" />
        <el-table-column prop="employeeNo" label="工号" min-width="100" align="center" />
        <el-table-column prop="departmentName" label="所属部门" min-width="120" />
        <el-table-column prop="attendanceDate" label="考勤日期" min-width="120" align="center" />
        <el-table-column prop="checkInTime" label="上班打卡" min-width="150">
          <template slot-scope="scope">
            <span>{{ formatTime(scope.row.checkInTime) }}</span>
            <el-tag v-if="scope.row.checkInType === 2" type="warning" size="mini" style="margin-left: 5px">迟到</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkOutTime" label="下班打卡" min-width="150">
          <template slot-scope="scope">
            <span>{{ formatTime(scope.row.checkOutTime) }}</span>
            <el-tag v-if="scope.row.checkOutType === 2" type="danger" size="mini" style="margin-left: 5px">早退</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="workHours" label="工作时长" min-width="100" align="center">
          <template slot-scope="scope">
            <span :class="{'warning-text': scope.row.workHours && scope.row.workHours < 8}">
              {{ scope.row.workHours ? scope.row.workHours + 'h' : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '正常' : '异常' }}
            </el-tag>
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
    
    <el-dialog title="生成模拟考勤" :visible.sync="generateDialogVisible" width="400px" :close-on-click-modal="false">
      <el-form :model="generateForm" label-width="80px">
        <el-form-item label="员工">
          <el-select v-model="generateForm.employeeId" placeholder="请选择员工" style="width: 100%">
            <el-option v-for="item in employeeList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="generateForm.date" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="generateLoading" @click="submitGenerate">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAttendanceList, getEmployeeStatistics, generateAttendance } from '@/api/attendance'
import { getAllEmployees } from '@/api/employee'
import { getDepartmentTree } from '@/api/department'

export default {
  name: 'AttendanceManagement',
  data() {
    return {
      loading: false,
      tableData: [],
      page: 1,
      size: 10,
      total: 0,
      searchForm: {
        employeeId: null,
        departmentId: null
      },
      dateRange: [],
      statistics: {},
      employeeList: [],
      departmentOptions: [],
      generateDialogVisible: false,
      generateLoading: false,
      generateForm: {
        employeeId: null,
        date: null
      }
    }
  },
  created() {
    this.loadData()
    this.loadEmployees()
    this.loadDepartments()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const params = {
          page: this.page,
          size: this.size,
          employeeId: this.searchForm.employeeId,
          departmentId: this.searchForm.departmentId
        }
        if (this.dateRange && this.dateRange.length === 2) {
          params.startDate = this.dateRange[0]
          params.endDate = this.dateRange[1]
        }
        const res = await getAttendanceList(params)
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
        if (this.searchForm.employeeId) {
          this.loadStatistics(this.searchForm.employeeId)
        }
      } catch (error) {
        console.error('加载考勤失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    async loadStatistics(employeeId) {
      try {
        const params = {}
        if (this.dateRange && this.dateRange.length === 2) {
          params.startDate = this.dateRange[0]
          params.endDate = this.dateRange[1]
        }
        const res = await getEmployeeStatistics(employeeId, params.startDate, params.endDate)
        if (res.code === 200) {
          this.statistics = res.data
        }
      } catch (error) {
        console.error('加载统计失败:', error)
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
    
    async loadDepartments() {
      try {
        const res = await getDepartmentTree()
        if (res.code === 200) {
          this.departmentOptions = res.data
        }
      } catch (error) {
        console.error('加载部门失败:', error)
      }
    },
    
    formatTime(time) {
      if (!time) return '-'
      return time.replace('T', ' ').substring(0, 16)
    },
    
    handleSearch() {
      this.page = 1
      this.loadData()
    },
    
    handleReset() {
      this.searchForm = { employeeId: null, departmentId: null }
      this.dateRange = []
      this.statistics = {}
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
    
    handleGenerate() {
      this.generateForm = { employeeId: null, date: null }
      this.generateDialogVisible = true
    },
    
    async submitGenerate() {
      if (!this.generateForm.employeeId || !this.generateForm.date) {
        this.$message.warning('请选择员工和日期')
        return
      }
      this.generateLoading = true
      try {
        const res = await generateAttendance(this.generateForm.employeeId, this.generateForm.date)
        if (res.code === 200) {
          this.$message.success('生成成功')
          this.generateDialogVisible = false
          this.loadData()
        } else {
          this.$message.error(res.message || '生成失败')
        }
      } catch (error) {
        console.error('生成考勤失败:', error)
      } finally {
        this.generateLoading = false
      }
    }
  }
}
</script>

<style scoped>
.attendance-container {
  padding-bottom: 24px;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon i {
  font-size: 28px;
  color: #fff;
}

.stat-icon.normal {
  background: linear-gradient(135deg, #67c23a, #85ce61);
}

.stat-icon.late {
  background: linear-gradient(135deg, #e6a23c, #f0c78a);
}

.stat-icon.early {
  background: linear-gradient(135deg, #f56c6c, #fab6b6);
}

.stat-icon.hours {
  background: linear-gradient(135deg, #409eff, #79bbff);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #262626;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
  margin-top: 4px;
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

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.warning-text {
  color: #e6a23c;
  font-weight: 500;
}
</style>
