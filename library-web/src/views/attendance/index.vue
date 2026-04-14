<template>
  <div class="attendance-container">
    <el-card>
      <div slot="header" class="header">
        <span>考勤管理</span>
        <el-button-group>
          <el-button type="success" icon="el-icon-camera" @click="handleCheckIn">模拟人脸签到</el-button>
          <el-button type="info" icon="el-icon-camera" @click="handleCheckOut">模拟人脸签退</el-button>
          <el-button type="primary" icon="el-icon-data-analysis" @click="handleGenerateStatistics">生成月度统计</el-button>
        </el-button-group>
      </div>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="考勤记录" name="record">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="员工">
              <el-select v-model="searchForm.employeeId" placeholder="请选择员工" clearable filterable>
                <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="部门">
              <el-select v-model="searchForm.departmentId" placeholder="请选择部门" clearable>
                <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="开始日期">
              <el-date-picker v-model="searchForm.startDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd"></el-date-picker>
            </el-form-item>
            <el-form-item label="结束日期">
              <el-date-picker v-model="searchForm.endDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd"></el-date-picker>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadAttendanceData">搜索</el-button>
              <el-button @click="handleResetRecord">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="attendanceData" border stripe>
            <el-table-column prop="employeeName" label="员工姓名" width="120"></el-table-column>
            <el-table-column prop="departmentName" label="所属部门" width="150"></el-table-column>
            <el-table-column prop="attendanceDate" label="考勤日期" width="120"></el-table-column>
            <el-table-column prop="checkInTime" label="签到时间" width="180"></el-table-column>
            <el-table-column prop="checkOutTime" label="签退时间" width="180"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="deviceNo" label="设备编号" width="120"></el-table-column>
          </el-table>

          <el-pagination
            @size-change="handleRecordSizeChange"
            @current-change="handleRecordPageChange"
            :current-page="recordPage.page"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="recordPage.size"
            layout="total, sizes, prev, pager, next, jumper"
            :total="recordPage.total"
            class="pagination">
          </el-pagination>
        </el-tab-pane>

        <el-tab-pane label="考勤统计" name="statistics">
          <el-form :inline="true" :model="statSearchForm" class="search-form">
            <el-form-item label="员工">
              <el-select v-model="statSearchForm.employeeId" placeholder="请选择员工" clearable filterable>
                <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="部门">
              <el-select v-model="statSearchForm.departmentId" placeholder="请选择部门" clearable>
                <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="年份">
              <el-select v-model="statSearchForm.year" placeholder="请选择年份">
                <el-option :label="2024" :value="2024"></el-option>
                <el-option :label="2025" :value="2025"></el-option>
                <el-option :label="2026" :value="2026"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="月份">
              <el-select v-model="statSearchForm.month" placeholder="请选择月份">
                <el-option v-for="m in 12" :key="m" :label="m + '月'" :value="m"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadStatisticsData">搜索</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="statisticsData" border stripe>
            <el-table-column prop="employeeName" label="员工姓名" width="120"></el-table-column>
            <el-table-column prop="departmentName" label="所属部门" width="150"></el-table-column>
            <el-table-column label="统计月份" width="120">
              <template slot-scope="scope">
                {{ scope.row.statisticsYear }}年{{ scope.row.statisticsMonth }}月
              </template>
            </el-table-column>
            <el-table-column prop="workDays" label="工作日数" width="100"></el-table-column>
            <el-table-column prop="presentDays" label="出勤天数" width="100"></el-table-column>
            <el-table-column prop="lateDays" label="迟到天数" width="100"></el-table-column>
            <el-table-column prop="leaveEarlyDays" label="早退天数" width="100"></el-table-column>
            <el-table-column prop="absentDays" label="缺勤天数" width="100"></el-table-column>
            <el-table-column prop="attendanceRate" label="出勤率" width="120">
              <template slot-scope="scope">
                {{ scope.row.attendanceRate.toFixed(2) }}%
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            @size-change="handleStatSizeChange"
            @current-change="handleStatPageChange"
            :current-page="statPage.page"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="statPage.size"
            layout="total, sizes, prev, pager, next, jumper"
            :total="statPage.total"
            class="pagination">
          </el-pagination>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog title="模拟人脸考勤" :visible.sync="checkDialogVisible" width="500px">
      <el-form :model="checkForm" label-width="100px">
        <el-form-item label="选择员工">
          <el-select v-model="checkForm.employeeId" placeholder="请选择员工" style="width: 100%" filterable>
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备编号">
          <el-input v-model="checkForm.deviceNo"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="checkDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitCheck">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAttendancePage, checkIn, checkOut, getStatisticsPage, generateMonthlyStatistics } from '@/api/attendance'
import { getAllDepartments } from '@/api/department'
import { getAllEmployees } from '@/api/employee'

export default {
  name: 'Attendance',
  data() {
    return {
      activeTab: 'record',
      searchForm: {
        employeeId: '',
        departmentId: '',
        startDate: '',
        endDate: ''
      },
      attendanceData: [],
      recordPage: {
        page: 1,
        size: 10,
        total: 0
      },
      statSearchForm: {
        employeeId: '',
        departmentId: '',
        year: new Date().getFullYear(),
        month: new Date().getMonth() + 1
      },
      statisticsData: [],
      statPage: {
        page: 1,
        size: 10,
        total: 0
      },
      departments: [],
      employees: [],
      checkDialogVisible: false,
      checkType: '',
      checkForm: {
        employeeId: '',
        deviceNo: 'FACE001'
      }
    }
  },
  created() {
    this.loadAttendanceData()
    this.loadStatisticsData()
    this.loadDepartments()
    this.loadEmployees()
  },
  methods: {
    loadAttendanceData() {
      const params = {
        page: this.recordPage.page,
        size: this.recordPage.size,
        employeeId: this.searchForm.employeeId,
        departmentId: this.searchForm.departmentId,
        startDate: this.searchForm.startDate,
        endDate: this.searchForm.endDate
      }
      getAttendancePage(params).then(res => {
        if (res.code === 0) {
          this.attendanceData = res.data.records
          this.recordPage.total = res.data.total
        }
      })
    },
    loadStatisticsData() {
      const params = {
        page: this.statPage.page,
        size: this.statPage.size,
        employeeId: this.statSearchForm.employeeId,
        departmentId: this.statSearchForm.departmentId,
        year: this.statSearchForm.year,
        month: this.statSearchForm.month
      }
      getStatisticsPage(params).then(res => {
        if (res.code === 0) {
          this.statisticsData = res.data.records
          this.statPage.total = res.data.total
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
    handleResetRecord() {
      this.searchForm = {
        employeeId: '',
        departmentId: '',
        startDate: '',
        endDate: ''
      }
      this.loadAttendanceData()
    },
    handleRecordSizeChange(val) {
      this.recordPage.size = val
      this.loadAttendanceData()
    },
    handleRecordPageChange(val) {
      this.recordPage.page = val
      this.loadAttendanceData()
    },
    handleStatSizeChange(val) {
      this.statPage.size = val
      this.loadStatisticsData()
    },
    handleStatPageChange(val) {
      this.statPage.page = val
      this.loadStatisticsData()
    },
    handleCheckIn() {
      this.checkType = 'in'
      this.checkDialogVisible = true
    },
    handleCheckOut() {
      this.checkType = 'out'
      this.checkDialogVisible = true
    },
    submitCheck() {
      if (!this.checkForm.employeeId) {
        this.$message.warning('请选择员工')
        return
      }
      if (this.checkType === 'in') {
        checkIn(this.checkForm.employeeId, this.checkForm.deviceNo).then(res => {
          if (res.code === 0) {
            this.$message.success('签到成功')
            this.checkDialogVisible = false
            this.loadAttendanceData()
          } else {
            this.$message.error(res.msg)
          }
        })
      } else {
        checkOut(this.checkForm.employeeId, this.checkForm.deviceNo).then(res => {
          if (res.code === 0) {
            this.$message.success('签退成功')
            this.checkDialogVisible = false
            this.loadAttendanceData()
          } else {
            this.$message.error(res.msg)
          }
        })
      }
    },
    handleGenerateStatistics() {
      this.$confirm('确定要生成本月考勤统计吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        generateMonthlyStatistics({
          year: this.statSearchForm.year,
          month: this.statSearchForm.month
        }).then(res => {
          if (res.code === 0) {
            this.$message.success('统计生成成功')
            this.loadStatisticsData()
          } else {
            this.$message.error(res.msg)
          }
        })
      })
    },
    getStatusType(status) {
      const typeMap = { 0: 'success', 1: 'warning', 2: 'danger' }
      return typeMap[status] || 'info'
    },
    getStatusText(status) {
      const textMap = { 0: '正常', 1: '迟到', 2: '早退' }
      return textMap[status] || '未知'
    }
  }
}
</script>

<style scoped>
.attendance-container {
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
