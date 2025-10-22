import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/parent',
    name: 'ParentLayout',
    component: () => import('@/views/parent/Layout.vue'),
    meta: { requiresAuth: true, role: 'PARENT' },
    children: [
      {
        path: 'home',
        name: 'ParentHome',
        component: () => import('@/views/parent/Home.vue')
      },
      {
        path: 'children',
        name: 'ParentChildren',
        component: () => import('@/views/parent/Children.vue')
      },
      {
        path: 'notices',
        name: 'ParentNotices',
        component: () => import('@/views/parent/Notices.vue')
      },
      {
        path: 'leave',
        name: 'ParentLeave',
        component: () => import('@/views/parent/Leave.vue')
      },
      {
        path: 'profile',
        name: 'ParentProfile',
        component: () => import('@/views/parent/Profile.vue')
      }
    ]
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { requiresAuth: true, roles: ['PRINCIPAL', 'TEACHER'] },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue')
      },
      {
        path: 'children',
        name: 'AdminChildren',
        component: () => import('@/views/admin/Children.vue')
      },
      {
        path: 'classes',
        name: 'AdminClasses',
        component: () => import('@/views/admin/Classes.vue')
      },
      {
        path: 'notices',
        name: 'AdminNotices',
        component: () => import('@/views/admin/Notices.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 动态导入 userStore，避免循环依赖
  import('@/store/user').then(({ useUserStore }) => {
    const userStore = useUserStore()

    if (to.meta.requiresAuth && !userStore.isLoggedIn) {
      next('/login')
    } else if (to.meta.role === 'PARENT' && !userStore.isParent) {
      next('/login')
    } else if (to.meta.roles && !to.meta.roles.includes(userStore.role)) {
      next('/login')
    } else {
      next()
    }
  })
})

export default router
