<template>
  <el-container style="height: 100%;">
    <el-aside width="236px" class="my-aside">
      <div class="brand-block">
        <h3 class="title">{{ brandTitle }}</h3>
        <p class="title-sub">{{ brandSubtitle }}</p>
      </div>

      <el-menu
        class="side-menu"
        :default-active="route.path"
        :default-openeds="defaultOpeneds"
        active-text-color="#ffffff"
        text-color="#e7eefc"
        background-color="transparent"
        router
      >
        <template v-for="section in menuSections" :key="section.index">
          <el-menu-item v-if="section.type === 'item'" :index="section.index" class="standalone-item">
            <span>{{ section.label }}</span>
          </el-menu-item>

          <el-sub-menu v-else :index="section.index" class="group-menu">
            <template #title>
              <span>{{ section.label }}</span>
            </template>
            <el-menu-item
              v-for="item in section.children"
              :key="item.index"
              :index="item.index"
            >
              <span>{{ item.label }}</span>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header height="60px">
        <el-row :gutter="5">
          <el-col :span="6" style="margin-top: 20px;">
            <el-space>
              <router-link to="/admin" aria-label="返回后台首页">
                <House />
              </router-link>
            </el-space>
          </el-col>
          <el-col :span="15"></el-col>
          <el-col :span="3">
            <div style="text-align: right;">
              <el-space style="margin-top: 15px;">
                <el-dropdown v-if="isUserLogin">
                  <div>
                    <el-space>
                      <el-avatar
                        style="width: 30px; height: 30px; border-radius: 50%"
                        shape="square"
                        :size="30"
                        :src="currentUser.avatarUrl"
                      />
                      <span style="font-size: 16px">{{ currentUser.username }}</span>
                    </el-space>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item>
                        <span @click="editCurrentUser">{{ profileLabel }}</span>
                      </el-dropdown-item>
                      <el-dropdown-item>
                        <span @click="editPassword">修改密码</span>
                      </el-dropdown-item>
                      <el-dropdown-item divided>
                        <span @click="logout">退出登录</span>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-space>
            </div>
          </el-col>
        </el-row>
      </el-header>

      <el-main style="background-color: RGB(245,245,245)" class="my-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import tools from '@/utils/tools.js'
import { computed, ref } from 'vue'
import router from '@/router/index.js'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import { House } from '@element-plus/icons-vue'

const route = useRoute()
const isUserLogin = ref(tools.isLogin())
const currentUser = ref(tools.getCurrentUser())

if (currentUser.value === null) {
  router.replace('/login')
}
if (currentUser.value && currentUser.value.type === 'USER') {
  router.replace('/')
}

const isAdmin = computed(() => currentUser.value?.type === 'ADMIN')
const brandTitle = computed(() => '电影院管理系统')
const brandSubtitle = computed(() => isAdmin.value ? '管理员中心' : '影院管理中心')
const profileLabel = computed(() => isAdmin.value ? '管理员基本信息' : '影院基本信息')

const adminMenuSections = [
  { type: 'item', index: '/admin/home', label: '管理员中心' },
  { type: 'item', index: '/admin/editCurrentUser', label: '管理员基本信息' },
  { type: 'item', index: '/admin/editPassword', label: '修改密码' },
  { type: 'item', index: '/admin/cinema', label: '电影院管理' },
  { type: 'item', index: '/admin/user', label: '用户管理' },
  {
    type: 'submenu',
    index: 'admin-movie-group',
    label: '影片管理',
    children: [
      { index: '/admin/movie', label: '电影信息管理' },
      { index: '/admin/movieType', label: '电影分类管理' },
      { index: '/admin/movieRegion', label: '电影区域管理' },
      { index: '/admin/movieCollect', label: '电影收藏管理' },
      { index: '/admin/movieBrowsingHistory', label: '电影浏览记录管理' }
    ]
  },
  {
    type: 'submenu',
    index: 'admin-screening-group',
    label: '放映管理',
    children: [
      { index: '/admin/movieRoom', label: '影厅管理' },
      { index: '/admin/screeningPlan', label: '排片管理' }
    ]
  },
  {
    type: 'submenu',
    index: 'admin-order-group',
    label: '订单管理',
    children: [
      { index: '/admin/movieOrder', label: '电影订单管理' },
      { index: '/admin/goodsOrder', label: '卖品订单管理' },
      { index: '/admin/movieOrderEvaluate', label: '影片评价管理' }
    ]
  },
  {
    type: 'submenu',
    index: 'admin-marketing-group',
    label: '营销管理',
    children: [
      { index: '/admin/slideshow', label: '轮播图' }
    ]
  }
]

const cinemaMenuSections = [
  { type: 'item', index: '/admin/home', label: '影院管理中心' },
  { type: 'item', index: '/admin/editCurrentUser', label: '影院基本信息' },
  { type: 'item', index: '/admin/editPassword', label: '修改密码' },
  {
    type: 'submenu',
    index: 'cinema-screening-group',
    label: '放映管理',
    children: [
      { index: '/admin/movieRoom', label: '影厅管理' },
      { index: '/admin/screeningPlan', label: '排片管理' }
    ]
  },
  {
    type: 'submenu',
    index: 'cinema-order-group',
    label: '订单管理',
    children: [
      { index: '/admin/ticketVerify', label: '验票核销' },
      { index: '/admin/movieOrder', label: '电影订单' },
      { index: '/admin/goodsOrder', label: '卖品订单' },
      { index: '/admin/movieOrderEvaluate', label: '影片评价' }
    ]
  }
]

const menuSections = computed(() => isAdmin.value ? adminMenuSections : cinemaMenuSections)
const defaultOpeneds = computed(() => menuSections.value
  .filter(item => item.type === 'submenu')
  .map(item => item.index))

function logout() {
  ElMessage({
    message: '退出登录成功，正在跳转',
    type: 'success'
  })
  localStorage.clear()
  router.push({ path: '/login' })
}

function editCurrentUser() {
  router.push({ path: '/admin/editCurrentUser' })
}

function editPassword() {
  router.push({ path: '/admin/editPassword' })
}
</script>

<style scoped>
.brand-block {
  padding: 20px 18px 14px;
}

.title {
  color: #ffffff;
  width: 100%;
  text-align: left;
  margin: 0;
  line-height: 1.5;
  font-size: 22px;
}

.title-sub {
  margin: 8px 0 0;
  color: rgba(231, 238, 252, 0.78);
  font-size: 13px;
  line-height: 1.6;
}

.my-main::-webkit-scrollbar {
  display: none;
}

.my-aside {
  background: linear-gradient(180deg, #34445f 0%, #4b6386 52%, #6b85ac 100%);
  overflow-x: hidden;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
}

.side-menu {
  border-right: 0;
  padding: 0 10px 18px;
}

.side-menu :deep(.el-menu-item),
.side-menu :deep(.el-sub-menu__title) {
  margin-bottom: 6px;
  border-radius: 12px;
  height: 44px;
  line-height: 44px;
}

.side-menu :deep(.el-menu-item:hover),
.side-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.1);
}

.side-menu :deep(.el-menu-item.is-active) {
  background: rgba(239, 66, 56, 0.92);
  color: #ffffff !important;
  font-weight: 600;
}

.side-menu :deep(.el-sub-menu .el-menu) {
  background: transparent;
}

.side-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 36px !important;
  margin-bottom: 4px;
}

.standalone-item {
  font-weight: 600;
}
</style>
