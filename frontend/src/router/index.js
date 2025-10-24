import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/public/home'
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
  // 前台公开页面
  {
    path: '/public',
    name: 'PublicLayout',
    component: () => import('@/views/public/Layout.vue'),
    children: [
      {
        path: 'home',
        name: 'PublicHome',
        component: () => import('@/views/public/Home.vue')
      },
      {
        path: 'notices',
        name: 'PublicNotices',
        component: () => import('@/views/public/Notices.vue')
      },
      {
        path: 'notices/:id',
        name: 'PublicNoticeDetail',
        component: () => import('@/views/public/NoticeDetail.vue')
      },
      {
        path: 'menu',
        name: 'PublicMenu',
        component: () => import('@/views/public/Menu.vue')
      },
      {
        path: 'teachers',
        name: 'PublicTeachers',
        component: () => import('@/views/public/Teachers.vue')
      },
      {
        path: 'albums',
        name: 'PublicAlbums',
        component: () => import('@/views/public/Albums.vue')
      },
      {
        path: 'enrollment',
        name: 'PublicEnrollment',
        component: () => import('@/views/public/Enrollment.vue')
      }
    ]
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
        path: 'attendance',
        name: 'ParentAttendance',
        component: () => import('@/views/parent/Attendance.vue')
      },
      {
        path: 'albums',
        name: 'ParentAlbums',
        component: () => import('@/views/parent/Albums.vue')
      },
      {
        path: 'health',
        name: 'ParentHealth',
        component: () => import('@/views/parent/Health.vue')
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
      },
      {
        path: 'attendance',
        name: 'AdminAttendance',
        component: () => import('@/views/admin/Attendance.vue')
      },
      {
        path: 'leave',
        name: 'AdminLeave',
        component: () => import('@/views/admin/Leave.vue')
      },
      {
        path: 'albums',
        name: 'AdminAlbums',
        component: () => import('@/views/admin/Albums.vue')
      },
      {
        path: 'health',
        name: 'AdminHealth',
        component: () => import('@/views/admin/Health.vue')
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
