<template>
    <div class="main-context">
        <el-card class="box-card">
            <el-space direction="vertical" style="width: 100%" size="large">
                <el-space>
                    <img src="../assets/logo.png" width="100%" style="width: 55px">
                    <el-space direction="vertical" style="width: 100%" size="small">
                        <h2>电影院管理系统</h2>
                    </el-space>
                </el-space>
                <el-form :model="formData" label-width="80px" :rules="rules" ref="formRef" style="width: 100%">
                    <el-form-item label="类型" prop="type">
                        <el-select v-model="formData.type" placeholder="请选择用户类型">
                            <el-option label="普通用户" value="USER"></el-option>
                            <el-option label="影院" value="CINEMA"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="手机号" prop="tel"
                                  :rules="[{required:true,message:'请输入手机号',trigger:[ 'blur','change']}]">
                        <el-input
                            style="width: 180px"
                            placeholder="请输入手机号"
                            v-model.trim="formData.tel"
                            clearable
                        >
                        </el-input>
                    </el-form-item>
                    <el-form-item label="验证码" prop="code"
                                  :rules="[{required:true,message:'请输入验证码',trigger:[ 'blur','change']}]">
                        <div class="code-row">
                            <el-input
                                class="code-input"
                                placeholder="请输入验证码"
                                v-model.trim="formData.code"
                                clearable
                            />
                            <el-button
                                class="code-button"
                                :loading="sendingCode"
                                :disabled="sendingCode || countdown > 0"
                                @click="sendCode"
                            >
                                {{ countdown > 0 ? `${countdown}s后重发` : '发送验证码' }}
                            </el-button>
                        </div>
                    </el-form-item>
                    <el-form-item label="新密码" prop="password"
                                  :rules="[{required:true,message:'请输入密码',trigger:[ 'blur','change']}]">
                        <el-input
                            style="width: 180px"
                            placeholder="请输入密码"
                            show-password
                            v-model.trim="formData.password"
                            clearable
                        >
                        </el-input>
                    </el-form-item>
                    <el-form-item label="" style="width: 100%">
                        <el-space direction="vertical" alignment="left" style="width: 100%">
                            <el-button @click="submitForm()" type="success" style="width: 100%">重置密码</el-button>
                            <router-link tag="span" :to="{path:'login'}">
                                <el-button type="text" class="button" style="float: right">返回登录</el-button>
                            </router-link>
                        </el-space>

                    </el-form-item>
                </el-form>
            </el-space>
        </el-card>
    </div>
</template>
<script setup>
import {onBeforeUnmount, ref} from 'vue';
import {ElMessage} from 'element-plus';
import http from "@/utils/http.js";
import router from "@/router/index.js";

const formRef = ref(null);
const sendingCode = ref(false);
const countdown = ref(0);
let countdownTimer = null;

const formData = ref({
    type: 'USER',
    tel: '',
    code: '',
    password: ''
});

const rules = {
    type: [{required: true, message: '请选择用户类型', trigger: ['blur', 'change']}],
    tel: [{required: true, message: '请输入手机号', trigger: ['blur', 'change']}],
    code: [{required: true, message: '请输入验证码', trigger: ['blur', 'change']}],
    password: [{required: true, message: '请输入密码', trigger: ['blur', 'change']}]
};

const startCountdown = (seconds = 60) => {
    countdown.value = seconds;
    countdownTimer = window.setInterval(() => {
        if (countdown.value <= 1) {
            clearCountdown();
            return;
        }
        countdown.value -= 1;
    }, 1000);
};

const clearCountdown = () => {
    if (countdownTimer) {
        window.clearInterval(countdownTimer);
        countdownTimer = null;
    }
    countdown.value = 0;
};

const sendCode = async () => {
    if (!formData.value.type) {
        ElMessage.warning('请先选择用户类型');
        return;
    }
    if (!formData.value.tel) {
        ElMessage.warning('请先输入手机号');
        return;
    }
    sendingCode.value = true;
    try {
        const res = await http.post('/common/sendRetrieveCode', {
            type: formData.value.type,
            tel: formData.value.tel
        });
        if (!res) {
            return;
        }
        const mockCode = res.data?.code || res.data;
        const expireInSeconds = Number(res.data?.expireInSeconds || 60);
        ElMessage.success(`验证码已发送，模拟验证码：${mockCode}`);
        clearCountdown();
        startCountdown(Math.min(expireInSeconds, 60));
    } finally {
        sendingCode.value = false;
    }
};

const submitForm = () => {
    formRef.value.validate((valid) => {
        if (!valid) {
            return
        }
        http.post("/common/retrievePassword", formData.value).then(res => {
            if (!res) {
                return
            }
            ElMessage({
                message: "重置成功，正在跳转",
                type: "success"
            });
            clearCountdown();
            router.push({path: "/login"})
        });
    });
}

onBeforeUnmount(() => {
    clearCountdown();
});
</script>

<style scoped>
.main-context {
  height: 100vh; /* 使 .app 高度为视口高度 */
  background: url("../assets/login.jpeg") no-repeat center center fixed;
  background-size: cover; /* 使用 cover 保持图片比例 */
  display: flex;
  justify-content: right;
  align-items: center;
  color: white; /* 根据背景图片调整文字颜色 */
}

.box-card {
  width: 300px;
  margin: 0 auto;
  text-align: center;
}

.code-row {
  display: flex;
  width: 100%;
  gap: 8px;
}

.code-input {
  flex: 1;
}

.code-button {
  min-width: 112px;
}
</style>
