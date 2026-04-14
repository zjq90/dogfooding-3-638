import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '数据概览', icon: 'el-icon-s-data' }
      },
      {
        path: 'books',
        name: 'Books',
        component: () => import('@/views/book/index.vue'),
        meta: { title: '图书管理', icon: 'el-icon-reading' }
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('@/views/category/index.vue'),
        meta: { title: '分类管理', icon: 'el-icon-folder-opened' }
      },
      {
        path: 'borrow',
        name: 'Borrow',
        component: () => import('@/views/borrow/index.vue'),
        meta: { title: '借阅管理', icon: 'el-icon-document' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'el-icon-user' }
      },
      {
        path: 'departments',
        name: 'Departments',
        component: () => import('@/views/department/index.vue'),
        meta: { title: '部门管理', icon: 'el-icon-office-building' }
      },
      {
        path: 'employees',
        name: 'Employees',
        component: () => import('@/views/employee/index.vue'),
        meta: { title: '人员管理', icon: 'el-icon-s-custom' }
      },
      {
        path: 'purchase/batch',
        name: 'PurchaseBatch',
        component: () => import('@/views/purchase/batch/index.vue'),
        meta: { title: '采购批次', icon: 'el-icon-s-order' }
      },
      {
        path: 'purchase/detail',
        name: 'PurchaseDetail',
        component: () => import('@/views/purchase/detail/index.vue'),
        meta: { title: '采购明细', icon: 'el-icon-s-goods' }
      },
      {
        path: 'administration/attendance',
        name: 'Attendance',
        component: () => import('@/views/administration/attendance/index.vue'),
        meta: { title: '考勤统计', icon: 'el-icon-time' }
      },
      {
        path: 'administration/employee',
        name: 'AdminEmployee',
        component: () => import('@/views/administration/employee/index.vue'),
        meta: { title: '人员管理', icon: 'el-icon-user-solid' }
      }
    ]
  },
  {
    path: '*',
    redirect: '/'
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = store.getters.token
  
  if (to.meta && to.meta.public) {
    // 公开页面直接放行
    next()
  } else if (!token) {
    // 需要登录但未登录，跳转到登录页
    next('/login')
  } else {
    // 已登录，放行
    next()
  }
})

export default router
