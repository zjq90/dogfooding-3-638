<template>
  <div class="admin-employee-page">
    <div class="page-header">
      <h2 class="page-title">行政人员管理</h2>
      <div class="header-actions">
        <el-button type="success" icon="el-icon-check" @click="handleBatchCheckIn">模拟批量上班</el-button>
        <el-button type="warning" icon="el-icon-close" @click="handleBatchCheckOut">模拟批量下班</el-button>
      </div>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="员工姓名">
          <el-input v-model="filterForm.name" placeholder="请输入姓名" style="width: 150px" />
        </el-form-item>
        <el-form-item label="工号">
          <el-input v-model="filterForm.employeeNo" placeholder="请输入工号" style="width: 150px" />
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="filterForm.departmentId" placeholder="选择部门" style="width: 150px" clearable>
            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="选择状态" style="width: 120px" clearable>
            <el-option label="在职" :value="1" />
            <el-option label="离职" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="employeeList" v-loading="loading" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="employeeNo" label="工号" width="100" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="departmentName" label="所属部门" width="120" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="mini">
              {{ scope.row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="今日考勤" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="getTodayStatus(scope.row.id) === 0" type="success" size="mini">正常</el-tag>
            <el-tag v-else-if="getTodayStatus(scope.row.id) === 1" type="warning" size="mini">迟到</el-tag>
            <el-tag v-else-if="getTodayStatus(scope.row.id) === 3" type="danger" size="mini">缺勤</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleCheckIn(scope.row)">上班打卡</el-button>
            <el-button type="text" size="small" @click="handleCheckOut(scope.row)">下班打卡</el-button>
            <el-button type="text" size="small" @click="handleViewAttendance(scope.row)">考勤</el-button>
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

    <!-- 人脸设备选择对话框 -->
    <el-dialog title="选择打卡设备" :visible.sync="deviceDialogVisible" width="400px">
      <el-form label-width="100px">
        <el-form-item label="选择设备">
          <el-select v-model="selectedDevice" placeholder="请选择人脸设备" style="width: 100%">
            <el-option 
              v-for="device in deviceList" 
              :key="device.id" 
              :label="device.deviceName + ' (' + device.location + ')'" 
              :value="device.deviceNo" 
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="deviceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCheckInOut">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEmployeeList } from '@/api/employee'
import { getDepartmentList } from '@/api/department'
import { getTodayRecord } from '@/api/attendance'
import { getOnlineDevices, simulateCheckIn, simulateCheckOut } from '@/api/faceDevice'

export default {
  name: 'AdminEmployee',
  data() {
    return {
      loading: false,
      employeeList: [],
      departmentList: [],
      deviceList: [],
      selectedEmployees: [],
      todayRecords: {},
      page: 1,
      size: 10,
      total: 0,
      filterForm: {
        name: '',
        employeeNo: '',
        departmentId: null,
        status: 1
      },
      deviceDialogVisible: false,
      selectedDevice: '',
      currentAction: '',
      currentEmployee: null
    }
  },
  created() {
    this.fetchDepartments()
    this.fetchData()
    this.fetchDevices()
    this.fetchTodayRecords()
  },
  methods: {
    async fetchDepartments() {
      try {
        const res = await getDepartmentList()
        if (res.code === 200) {
          this.departmentList = res.data
        }
      } catch (error) {
        console.error('获取部门列表失败:', error)
      }
    },
    async fetchData() {
      this.loading = true
      try {
        const params = {
          page: this.page,
          size: this.size,
          ...this.filterForm
        }
        const res = await getEmployeeList(params)
        if (res.code === 200) {
          this.employeeList = res.data.list
          this.total = res.data.total
        }
      } catch (error) {
        console.error('获取员工列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    async fetchDevices() {
      try {
        const res = await getOnlineDevices()
        if (res.code === 200) {
          this.deviceList = res.data
        }
      } catch (error) {
        console.error('获取设备列表失败:', error)
      }
    },
    async fetchTodayRecords() {
      try {
        const res = await getTodayRecord()
        if (res.code === 200 && res.data) {
          this.$set(this.todayRecords, res.data.employeeId, res.data.status)
        }
      } catch (error) {
        console.error('获取今日考勤失败:', error)
      }
    },
    getTodayStatus(employeeId) {
      return this.todayRecords[employeeId]
    },
    handleSelectionChange(selection) {
      this.selectedEmployees = selection
    },
    handleCurrentChange(val) {
      this.page = val
      this.fetchData()
    },
    handleSearch() {
      this.page = 1
      this.fetchData()
    },
    handleReset() {
      this.filterForm = {
        name: '',
        employeeNo: '',
        departmentId: null,
        status: 1
      }
      this.handleSearch()
    },
    handleCheckIn(employee) {
      this.currentEmployee = employee
      this.currentAction = 'checkin'
      this.deviceDialogVisible = true
    },
    handleCheckOut(employee) {
      this.currentEmployee = employee
      this.currentAction = 'checkout'
      this.deviceDialogVisible = true
    },
    async confirmCheckInOut() {
      if (!this.selectedDevice) {
        this.$message.warning('请选择打卡设备')
        return
      }
      try {
        if (this.currentAction === 'checkin') {
          await simulateCheckIn(this.selectedDevice, this.currentEmployee.id)
          this.$message.success(`${this.currentEmployee.name} 上班打卡成功`)
        } else {
          await simulateCheckOut(this.selectedDevice, this.currentEmployee.id)
          this.$message.success(`${this.currentEmployee.name} 下班打卡成功`)
        }
        this.deviceDialogVisible = false
        this.fetchTodayRecords()
        this.fetchData()
      } catch (error) {
        this.$message.error('打卡失败')
      }
    },
    handleBatchCheckIn() {
      if (this.selectedEmployees.length === 0) {
        this.$message.warning('请选择要打卡的员工')
        return
      }
      this.currentAction = 'batchCheckin'
      this.deviceDialogVisible = true
    },
    handleBatchCheckOut() {
      if (this.selectedEmployees.length === 0) {
        this.$message.warning('请选择要打卡的员工')
        return
      }
      this.currentAction = 'batchCheckout'
      this.deviceDialogVisible = true
    },
    handleViewAttendance(employee) {
      this.$router.push(`/administration/attendance?employeeId=${employee.id}`)
    }
  }
}
</script>

<style scoped>
.admin-employee-page {
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
.filter-card {
  margin-bottom: 20px;
}
.table-card {
  margin-bottom: 20px;
}
.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}
</style>
