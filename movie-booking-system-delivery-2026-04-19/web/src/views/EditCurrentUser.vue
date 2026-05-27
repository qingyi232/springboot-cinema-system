<template>
  <div class="profile-page">
    <el-card class="profile-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <div class="card-title">{{ pageTitle }}</div>
            <div class="card-desc">{{ pageDesc }}</div>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="基本信息" name="profile">
          <el-form ref="profileFormRef" :model="profileForm" label-width="100px">
            <el-form-item prop="avatarUrl" label="头像">
              <MyUpLoad
                type="imageCard"
                :limit="1"
                :files="profileForm.avatarUrl"
                tip="支持直接更换头像"
                @setFiles="profileForm.avatarUrl = $event"
              />
            </el-form-item>
            <el-form-item prop="username" label="用户名">
              <el-input
                v-model="profileForm.username"
                type="text"
                auto-complete="off"
                placeholder="用户名"
                disabled
              />
            </el-form-item>
            <el-form-item
              prop="nickname"
              :label="isCinema ? '影院名称' : '昵称'"
              :rules="[{ required: true, message: isCinema ? '请输入影院名称' : '请输入昵称', trigger: ['blur', 'change'] }]"
            >
              <el-input
                v-model="profileForm.nickname"
                type="text"
                auto-complete="off"
                :placeholder="isCinema ? '请输入影院名称' : '请输入昵称'"
              />
            </el-form-item>
            <el-form-item prop="email" label="邮箱">
              <el-input
                v-model="profileForm.email"
                auto-complete="off"
                placeholder="请输入邮箱"
              />
            </el-form-item>
            <el-form-item prop="tel" label="电话">
              <el-input
                v-model="profileForm.tel"
                type="text"
                auto-complete="off"
                placeholder="请输入电话"
              />
            </el-form-item>
            <el-form-item
              v-if="isCinema"
              label="标签"
              prop="label"
              :rules="[{ required: true, message: '请输入影院标签', trigger: ['blur', 'change'] }]"
            >
              <el-input v-model="profileForm.label" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item
              v-if="isCinema"
              label="地址"
              prop="address"
              :rules="[{ required: true, message: '请输入影院地址', trigger: ['blur', 'change'] }]"
            >
              <el-input v-model="profileForm.address" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item class="action-row">
              <el-button type="primary" @click="handleProfileSubmit">保存基本信息</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="修改密码" name="password">
          <el-form ref="passwordFormRef" :model="passwordForm" label-width="100px">
            <el-form-item
              prop="oldPassword"
              label="旧密码"
              :rules="[{ required: true, message: '请输入旧密码', trigger: ['blur', 'change'] }]"
            >
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                show-password
                auto-complete="off"
                placeholder="请输入旧密码"
              />
            </el-form-item>
            <el-form-item
              prop="newPassword"
              label="新密码"
              :rules="[
                { required: true, message: '请输入新密码', trigger: ['blur', 'change'] },
                { min: 6, message: '新密码至少 6 位', trigger: ['blur', 'change'] }
              ]"
            >
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
                auto-complete="off"
                placeholder="请输入新密码"
              />
            </el-form-item>
            <el-form-item
              prop="confirmPassword"
              label="确认密码"
              :rules="[
                { required: true, message: '请再次输入新密码', trigger: ['blur', 'change'] },
                { validator: validateConfirmPassword, trigger: ['blur', 'change'] }
              ]"
            >
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
                auto-complete="off"
                placeholder="请再次输入新密码"
              />
            </el-form-item>
            <el-alert
              title="密码修改成功后需要重新登录。"
              type="warning"
              :closable="false"
              show-icon
              class="password-alert"
            />
            <el-form-item class="action-row">
              <el-button type="primary" @click="handlePasswordSubmit">保存新密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import utils from "@/utils/tools.js";
import MyUpLoad from "@/components/MyUpload.vue";
import { ElMessage } from 'element-plus';
import http from "@/utils/http.js";

const route = useRoute();
const router = useRouter();
const profileFormRef = ref();
const passwordFormRef = ref();
const profileForm = ref({});
const passwordForm = ref(createPasswordForm());
const activeTab = ref(resolveActiveTab());

loadProfile();

watch(
  () => route.fullPath,
  () => {
    activeTab.value = resolveActiveTab();
  }
);

const isCinema = computed(() => profileForm.value.type === 'CINEMA');
const pageTitle = computed(() => {
  if (profileForm.value.type === 'ADMIN') {
    return '管理员基本信息';
  }
  if (profileForm.value.type === 'CINEMA') {
    return '影院基本信息';
  }
  return '个人信息';
});
const pageDesc = computed(() => {
  if (profileForm.value.type === 'CINEMA') {
    return '这里可以统一维护影院资料和登录密码。';
  }
  if (profileForm.value.type === 'ADMIN') {
    return '这里可以统一维护管理员资料和登录密码。';
  }
  return '这里可以统一维护个人资料和登录密码。';
});

function createPasswordForm() {
  return {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
}

function loadProfile() {
  profileForm.value = {
    ...(utils.getCurrentUser() || {})
  };
}

function resolveActiveTab() {
  if (route.query.tab === 'password' || route.path.endsWith('/editPassword')) {
    return 'password';
  }
  return 'profile';
}

function getProfilePath() {
  return route.path.startsWith('/admin') ? '/admin/editCurrentUser' : '/editCurrentUser';
}

function handleTabChange(name) {
  router.replace({
    path: getProfilePath(),
    query: name === 'password' ? { tab: 'password' } : {}
  });
}

function validateConfirmPassword(rule, value, callback) {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的新密码不一致'));
    return;
  }
  callback();
}

function handleProfileSubmit() {
  profileFormRef.value?.validate((valid) => {
    if (!valid) {
      return;
    }
    http.post('/common/updateCurrentUser', profileForm.value).then((res) => {
      if (!res) {
        return;
      }
      http.get("/common/currentUser").then((res1) => {
        const currentUser = res1.data;
        localStorage.setItem("currentUser", JSON.stringify(currentUser));
        profileForm.value = { ...currentUser };
        ElMessage({
          message: '基本信息保存成功',
          type: 'success'
        });
      });
    });
  });
}

function handlePasswordSubmit() {
  passwordFormRef.value?.validate((valid) => {
    if (!valid) {
      return;
    }
    http.post('/common/updatePassword', {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    }).then((res) => {
      if (!res) {
        return;
      }
      localStorage.clear();
      ElMessage({
        message: '密码修改成功，请重新登录',
        type: 'success'
      });
      passwordForm.value = createPasswordForm();
      router.push({ path: '/login' });
    });
  });
}
</script>

<style scoped>
.profile-page {
  max-width: 760px;
  margin: 20px auto 0;
}

.profile-card {
  border-radius: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
}

.card-desc {
  margin-top: 8px;
  color: #6b7280;
  font-size: 13px;
}

.action-row {
  margin-top: 12px;
}

.password-alert {
  margin-bottom: 18px;
}
</style>
