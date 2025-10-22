<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo"><h3>管理后台</h3></div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/admin/dashboard"><el-icon><DataAnalysis /></el-icon><span>数据面板</span></el-menu-item>
        <el-menu-item index="/admin/users"><el-icon><User /></el-icon><span>用户管理</span></el-menu-item>
        <el-menu-item index="/admin/children"><el-icon><User /></el-icon><span>幼儿管理</span></el-menu-item>
        <el-menu-item index="/admin/classes"><el-icon><School /></el-icon><span>班级管理</span></el-menu-item>
        <el-menu-item index="/admin/notices"><el-icon><Bell /></el-icon><span>通知管理</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header><div class="header-content"><span>管理员</span><el-button @click="$router.push('/login')" size="small">退出</el-button></div></el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>
<style scoped>
.layout-container { height: 100vh; }
.el-aside { background-color: #304156; }
.logo { height: 60px; display: flex; align-items: center; justify-content: center; background-color: #2b3a4a; color: #fff; }
.el-header { background-color: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; padding: 0 20px; }
.header-content { display: flex; justify-content: space-between; width: 100%; }
.el-main { background-color: #f0f2f5; padding: 20px; }
</style>
