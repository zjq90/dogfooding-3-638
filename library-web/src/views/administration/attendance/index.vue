<template>
  <div class="attendance-page">
    <div class="page-header">
      <h2 class="page-title">考勤统计</h2>
      <div class="header-actions">
        <el-button type="success" icon="el-icon-date" @click="showGenerateDialog">生成考勤</el-button>
        <el-button type="primary" icon="el-icon-download" @click="handleExport">导出考勤</el-button>
      </div>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="年份">
          <el-select v-model="filterForm.year" placeholder="选择年份" style="width: 120px">
            <el-option v-for="year in yearOptions" :key="year" :label="year + '年'" :value="year" />
          </el-select>
        </el-form-item>
        <el-form-item label="月份">
          <el-select v-model="filterForm.month" placeholder="选择月份" style="width: 100px">
            <el-option v-for="month in 12" :key="month" :label="month + '月'" :value="month" />
          </el-select>
        </el-form-item>
        <el-form-item label="员工">
          <el-input v-model="filterForm.employeeName" placeholder="员工姓名" style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value">{{ statData.totalEmployees }}</div>
          <div class="stat-label">总人数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #67C23A;">{{ statData.normalCount }}</div>
          <div class="stat-label">正常出勤</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #E6A23C;">{{ statData.lateCount }}</div>
          <div class="stat-label">迟到人次</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #F56C6C;">{{ statData.absentCount }}</div>
          <div class="stat-label">缺勤人次</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #409EFF;">{{ statData.leaveCount }}</div>
          <div class="stat-label">请假人次</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #909399;">{{ statData.avgWorkHours }}</div>
          <div class="stat-label">平均工时</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="table-card">
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="考勤记录" name="records">
          <el-table :data="recordList" v-loading="loading" border stripe size="small">
            <el-table-column type="index" label="序号" width="50" align="center" />
            <el-table-column prop="employeeNo" label="工号" width="100" />
            <el-table-column prop="employeeName" label="姓名" width="100" />
            <el-table-column prop="departmentName" label="部门" width="120" />
            <el-table-column prop="attendanceDate" label="日期" width="110" />
            <el-table-column prop="checkInTime" label="上班时间" width="90" />
            <el-table-column prop="checkOutTime" label="下班时间" width="90" />
            <el-table-column prop="workHours" label="工时" width="70" align="center" />
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)" size="mini">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="overtimeHours" label="加班" width="70" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.overtimeHours > 0" style="color: #67C23A;">{{ scope.row.overtimeHours }}h</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
          </el-table>
          <div class="pagination-wrapper">
            <el-pagination
              background
              @current-change="handleRecordPageChange"
              :current-page="recordPage"
              :page-size="recordSize"
              :total="recordTotal"
              layout="total, prev, pager, next"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="月度统计" name="statistics">
          <el-table :data="statisticsList" v-loading="loading" border stripe size="small">
            <el-table-column type="index" label="序号" width="50" align="center" />
            <el-table-column prop="employeeName" label="姓名" width="100" />
            <el-table-column prop="departmentName" label="部门" width="120" />
            <el-table-column prop="statisticsYear" label="年份" width="80" align="center" />
            <el-table-column prop="statisticsMonth" label="月份" width="70" align="center" />
            <el-table-column prop="totalDays" label="应出勤" width="80" align="center" />
            <el-table-column prop="actualDays" label="实出勤" width="80" align="center" />
            <el-table-column prop="normalDays" label="正常" width="70" align="center" />
            <el-table-column prop="lateCount" label="迟到" width="70" align="center" />
            <el-table-column prop="absentCount" label="缺勤" width="70" align="center" />
            <el-table-column prop="leaveDays" label="请假" width="70" align="center" />
            <el-table-column prop="overtimeHours" label="加班时长" width="90" align="center" />
            <el-table-column prop="totalWorkHours" label="总工时" width="90" align="center" />
          </el-table>
          <div class="pagination-wrapper">
            <el-pagination
              background
              @current-change="handleStatisticsPageChange"
              :current-page="statisticsPage"
              :page-size="statisticsSize"
              :total="statisticsTotal"
              layout="total, prev, pager, next"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 生成考勤对话框 -->
    <el-dialog title="生成考勤数据" :visible.sync="generateDialogVisible" width="400px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="生成类型">
          <el-radio-group v-model="generateForm.type">
            <el-radio label="daily">每日考勤</el-radio>
            <el-radio label="monthly">月度统计</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选择日期" v-if="generateForm.type === 'daily'">
          <el-date-picker
            v-model="generateForm.date"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          />
        </el-form-item>
        <template v-else>
          <el-form-item label="年份">
            <el-select v-model="generateForm.year" placeholder="选择年份" style="width: 100%">
              <el-option v-for="year in yearOptions" :key="year" :label="year + '年'" :value="year" />
            </el-select>
          </el-form-item>
          <el-form-item label="月份">
            <el-select v-model="generateForm.month" placeholder="选择月份" style="width: 100%">
              <el-option v-for="month in 12" :key="month" :label="month + '月'" :value="month" />
            </el-select>
          </el-form-item>
        </template>
      </el-form>
      <div slot="footer">
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleGenerate">确定生成</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getRecordList, getStatisticsList, generateDailyAttendance, generateMonthlyStatistics } from '@/api/attendance'

export default {
  name: 'Attendance',
  data() {
    return {
      loading: false,
      activeTab: 'records',
      filterForm: {
        year: new Date().getFullYear(),
        month: new Date().getMonth() + 1,
        employeeName: ''
      },
      yearOptions: [],
      recordList: [],
      recordPage: 1,
      recordSize: 10,
      recordTotal: 0,
      statisticsList: [],
      statisticsPage: 1,
      statisticsSize: 10,
      statisticsTotal: 0,
      statData: {
        totalEmployees: 0,
        normalCount: 0,
        lateCount: 0,
        absentCount: 0,
        leaveCount: 0,
        avgWorkHours: 0
      },
      generateDialogVisible: false,
      generateForm: {
        type: 'daily',
        date: new Date().toISOString().split('T')[0],
        year: new Date().getFullYear(),
        month: new Date().getMonth() + 1
      }
    }
  },
  created() {
    this.initYearOptions()
    this.fetchRecordData()
    this.fetchStatisticsData()
  },
  methods: {
    initYearOptions() {
      const currentYear = new Date().getFullYear()
      for (let i = currentYear - 2; i <= currentYear + 1; i++) {
        this.yearOptions.push(i)
      }
    },
    async fetchRecordData() {
      this.loading = true
      try {
        const res = await getRecordList({
          page: this.recordPage,
          size: this.recordSize
        })
        if (res.code === 200) {
          this.recordList = res.data.list
          this.recordTotal = res.data.total
          this.calculateStats()
        }
      } catch (error) {
        console.error('获取考勤记录失败:', error)
      } finally {
        this.loading = false
      }
    },
    async fetchStatisticsData() {
      this.loading = true
      try {
        const res = await getStatisticsList({
          page: this.statisticsPage,
          size: this.statisticsSize
        })
        if (res.code === 200) {
          this.statisticsList = res.data.list
          this.statisticsTotal = res.data.total
        }
      } catch (error) {
        console.error('获取考勤统计失败:', error)
      } finally {
        this.loading = false
      }
    },
    calculateStats() {
      this.statData.totalEmployees = new Set(this.recordList.map(r => r.employeeId)).size
      this.statData.normalCount = this.recordList.filter(r => r.status === 0).length
      this.statData.lateCount = this.recordList.filter(r => r.status === 1).length
      this.statData.absentCount = this.recordList.filter(r => r.status === 3).length
      this.statData.leaveCount = this.recordList.filter(r => r.status === 4).length
      const totalHours = this.recordList.reduce((sum, r) => sum + (r.workHours || 0), 0)
      this.statData.avgWorkHours = this.recordList.length > 0 ? (totalHours / this.recordList.length).toFixed(1) : 0
    },
    handleTabChange() {
      if (this.activeTab === 'records') {
        this.fetchRecordData()
      } else {
        this.fetchStatisticsData()
      }
    },
    handleRecordPageChange(val) {
      this.recordPage = val
      this.fetchRecordData()
    },
    handleStatisticsPageChange(val) {
      this.statisticsPage = val
      this.fetchStatisticsData()
    },
    handleSearch() {
      this.recordPage = 1
      this.statisticsPage = 1
      this.fetchRecordData()
      this.fetchStatisticsData()
    },
    handleReset() {
      this.filterForm = {
        year: new Date().getFullYear(),
        month: new Date().getMonth() + 1,
        employeeName: ''
      }
      this.handleSearch()
    },
    showGenerateDialog() {
      this.generateDialogVisible = true
    },
    async handleGenerate() {
      try {
        if (this.generateForm.type === 'daily') {
          await generateDailyAttendance(this.generateForm.date)
          this.$message.success('每日考勤生成成功')
        } else {
          await generateMonthlyStatistics(this.generateForm.year, this.generateForm.month)
          this.$message.success('月度统计生成成功')
        }
        this.generateDialogVisible = false
        this.handleTabChange()
      } catch (error) {
        this.$message.error('生成失败')
      }
    },
    handleExport() {
      this.$message.info('导出功能开发中')
    },
    getStatusType(status) {
      const types = ['success', 'warning', 'warning', 'danger', 'info', 'success']
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = ['正常', '迟到', '早退', '缺勤', '请假', '加班']
      return texts[status] || '未知'
    }
  }
}
</script>

<style scoped>
.attendance-page {
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
.stat-cards {
  margin-bottom: 20px;
}
.stat-card {
  text-align: center;
}
.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
.table-card {
  margin-bottom: 20px;
}
.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}
</style>
