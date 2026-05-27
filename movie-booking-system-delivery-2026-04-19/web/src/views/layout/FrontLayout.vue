<template>
  <el-container class="my-container">
    <el-header class="my-header">
      <el-row class="nav">
        <el-col :span="6" class="logo">
          <div class="brand-entry" @click="router.push('/')">
            <el-space>
              <img src="@/assets/logo.png" width="100%" style="width: 50px" alt="电影院管理系统" />
              <el-space direction="vertical" style="width: 100%" size="small">
                <h3 style="color: black">电影院管理系统</h3>
              </el-space>
            </el-space>
          </div>
        </el-col>
        <el-col :span="12">
          <div style="text-align: center">
            <el-menu
              :default-active="route.path"
              mode="horizontal"
              router
              style="float: left; min-width: 540px"
              background-color="#F5F5F5"
              active-text-color="#EF4238"
              @select="handleSelect"
            >
              <el-menu-item index="/index">首页</el-menu-item>
              <el-menu-item index="/movieList">电影列表</el-menu-item>
              <el-menu-item index="/cinemaList">电影院</el-menu-item>
              <el-menu-item index="/goodsList">卖品商品</el-menu-item>
              <el-menu-item index="/movieOrder">我的订单</el-menu-item>
              <el-menu-item index="/personalCenter">个人中心</el-menu-item>
            </el-menu>
          </div>
        </el-col>
        <el-col :span="6">
          <div style="text-align: left;">
            <el-space style="margin-top: 10px;">
              <el-dropdown v-if="isUserLogin">
                <div>
                  <el-space>
                    <el-avatar
                      style="width: 35px; height: 35px; border-radius: 50%"
                      shape="square"
                      :size="35"
                      :src="currentUser.avatarUrl"
                    ></el-avatar>
                    <span style="font-size: 16px">{{ currentUser.username }}</span>
                  </el-space>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item><span @click="editCurrentUser">个人信息</span></el-dropdown-item>
                    <el-dropdown-item><span @click="editPassword">修改密码</span></el-dropdown-item>
                    <el-dropdown-item><span @click="balanceInfo">余额/充值</span></el-dropdown-item>
                    <el-dropdown-item divided><span @click="logout">退出登录</span></el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </el-space>
          </div>
        </el-col>
      </el-row>
    </el-header>
    <el-main class="my-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import tools from '@/utils/tools.js'
import { ref } from 'vue'
import router from '@/router/index.js'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

const route = useRoute()
const isUserLogin = ref(tools.isLogin())
const currentUser = ref(tools.getCurrentUser())

if (currentUser.value === null) {
  router.replace('/login')
}
if (currentUser.value && currentUser.value.type !== 'USER') {
  router.replace('/admin')
}

function handleSelect() {}

function logout() {
  ElMessage({
    message: '退出登录成功，正在跳转',
    type: 'success'
  })
  localStorage.clear()
  router.push({ path: '/login' })
}

function editCurrentUser() {
  router.push({ path: '/editCurrentUser' })
}

function editPassword() {
  router.push({ path: '/editCurrentUser', query: { tab: 'password' } })
}

function balanceInfo() {
  router.push({ path: '/balanceInfo' })
}
</script>

<style scoped>
.el-menu--horizontal {
  --el-menu-horizontal-height: 50px;
}

.my-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.my-header {
  height: 60px;
  background: #f5f5f5;
  border-bottom: solid 1px #ddd;
}

.brand-entry {
  text-align: right;
  padding-top: 2px;
  margin-right: 30px;
  cursor: pointer;
}

.el-menu.el-menu--horizontal {
  border-bottom: none;
}

.my-main {
  background-color: #f0f3ef;
  padding-bottom: 20px;
  --el-main-padding: 0px;
}

.my-main::-webkit-scrollbar {
  display: none;
}

.my-footer {
  font-size: 14px;
  padding: 10px;
  color: #999;
  background-color: #eaeaea;
  text-align: center;
}
</style>
