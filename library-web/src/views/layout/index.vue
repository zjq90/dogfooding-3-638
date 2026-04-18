<template>
  <el-container class="layout-container">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-left">
        <div class="logo">
          <i class="el-icon-reading"></i>
          <span class="logo-text">图书借阅管理系统</span>
        </div>
      </div>
      
      <div class="header-center">
        <el-menu
          :default-active="activeMenu"
          class="nav-menu"
          mode="horizontal"
          background-color="transparent"
          text-color="#595959"
          active-text-color="#5B8FF9"
          router
        >
          <template v-for="item in menuList">
            <!-- 没有子菜单的项 -->
            <el-menu-item 
              v-if="!item.children" 
              :key="item.path" 
              :index="item.path"
            >
              <i :class="item.icon"></i>
              <span>{{ item.title }}</span>
            </el-menu-item>
            <!-- 有子菜单的项 -->
            <el-submenu 
              v-else 
              :key="item.path" 
              :index="item.path"
            >
              <template slot="title">
                <i :class="item.icon"></i>
                <span>{{ item.title }}</span>
              </template>
              <el-menu-item 
                v-for="child in item.children" 
                :key="child.path" 
                :index="child.path"
              >
                <i :class="child.icon"></i>
                <span>{{ child.title }}</span>
              </el-menu-item>
            </el-submenu>
          </template>
        </el-menu>
      </div>
      
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="32" :src="userInfo.avatar || defaultAvatar"></el-avatar>
            <span class="username">{{ userInfo.realName || userInfo.username }}</span>
            <i class="el-icon-arrow-down"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="profile">
              <i class="el-icon-user"></i> 个人中心
            </el-dropdown-item>
            <el-dropdown-item command="password">
              <i class="el-icon-lock"></i> 修改密码
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <i class="el-icon-switch-button"></i> 退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    
    <!-- 主内容区 -->
    <el-main class="main-content">
      <router-view />
    </el-main>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex'
import { logout } from '@/api/auth'

export default {
  name: 'Layout',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      menuList: [
        { path: '/dashboard', title: '数据概览', icon: 'el-icon-s-data' },
        { path: '/books', title: '图书管理', icon: 'el-icon-reading' },
        { path: '/categories', title: '分类管理', icon: 'el-icon-folder-opened' },
        { path: '/borrow', title: '借阅管理', icon: 'el-icon-document' },
        { path: '/users', title: '用户管理', icon: 'el-icon-user' },
        { path: '/departments', title: '部门管理', icon: 'el-icon-office-building' },
        { path: '/employees', title: '人员管理', icon: 'el-icon-s-custom' },
        {
          path: '/purchase',
          title: '图书采购',
          icon: 'el-icon-shopping-cart-full',
          children: [
            { path: '/purchase/batch', title: '采购批次', icon: 'el-icon-s-order' },
            { path: '/purchase/detail', title: '采购明细', icon: 'el-icon-s-goods' }
          ]
        },
        {
          path: '/administration',
          title: '行政管理',
          icon: 'el-icon-s-management',
          children: [
            { path: '/administration/attendance', title: '考勤统计', icon: 'el-icon-time' },
            { path: '/administration/employee', title: '人员管理', icon: 'el-icon-user-solid' }
          ]
        }
      ]
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    activeMenu() {
      const path = this.$route.path
      // 如果是子菜单路径，返回完整路径
      if (path.startsWith('/purchase/') || path.startsWith('/administration/')) {
        return path
      }
      return path
    }
  },
  methods: {
    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$message.info('个人中心功能开发中')
          break
        case 'password':
          this.$message.info('修改密码功能开发中')
          break
        case 'logout':
          this.handleLogout()
          break
      }
    },
    async handleLogout() {
      try {
        await this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await logout()
        this.$store.dispatch('logout')
        this.$message.success('退出成功')
        this.$router.push('/login')
      } catch (error) {
        // 用户取消
      }
    }
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  background-color: #F5F7FA;
}

.header {
  height: 64px !important;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo i {
  font-size: 32px;
  color: #5B8FF9;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  letter-spacing: 1px;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu {
  border-bottom: none;
}

.nav-menu :deep(.el-menu-item) {
  font-size: 15px;
  height: 64px;
  line-height: 64px;
  padding: 0 24px;
  margin: 0 4px;
  border-bottom: none !important;
  transition: all 0.3s;
}

.nav-menu :deep(.el-menu-item:hover) {
  background-color: rgba(91, 143, 249, 0.05) !important;
}

.nav-menu :deep(.el-menu-item.is-active) {
  background-color: rgba(91, 143, 249, 0.1) !important;
  font-weight: 500;
}

.nav-menu :deep(.el-menu-item i) {
  margin-right: 6px;
  font-size: 18px;
}

/* 子菜单样式 */
.nav-menu :deep(.el-submenu__title) {
  font-size: 15px;
  height: 64px;
  line-height: 64px;
  padding: 0 24px;
  margin: 0 4px;
  border-bottom: none !important;
  transition: all 0.3s;
}

.nav-menu :deep(.el-submenu__title:hover) {
  background-color: rgba(91, 143, 249, 0.05) !important;
}

.nav-menu :deep(.el-submenu.is-active .el-submenu__title) {
  background-color: rgba(91, 143, 249, 0.1) !important;
  font-weight: 500;
  color: #5B8FF9 !important;
}

.nav-menu :deep(.el-submenu__title i) {
  margin-right: 6px;
  font-size: 18px;
}

/* 下拉菜单样式 */
.nav-menu :deep(.el-menu--horizontal .el-menu .el-menu-item) {
  height: 40px;
  line-height: 40px;
  padding: 0 20px;
  font-size: 14px;
}

.nav-menu :deep(.el-menu--horizontal .el-menu .el-menu-item i) {
  margin-right: 8px;
  font-size: 16px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-info:hover {
  background-color: #F5F7FA;
}

.username {
  font-size: 14px;
  color: #595959;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.main-content {
  margin-top: 64px;
  padding: 24px;
  min-height: calc(100vh - 64px);
}
</style>
