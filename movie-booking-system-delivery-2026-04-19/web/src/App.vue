<template>
    <div id="app">
      <el-config-provider :locale="locale" :size="size">
        <router-view/>
      </el-config-provider>
    </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import en from 'element-plus/dist/locale/en.mjs'

const language = ref('zh-cn')
const locale = computed(() => (
    language.value === 'zh-cn' ? zhCn : en))

const size= ref('default')

</script>
<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

html, body, #app {
    height: 100%;
}

.container-shadow {
    box-shadow: 0 0 20px #ccc;
    /*margin:20px 20px;*/
    padding: 20px;
    box-sizing: border-box;
    border-radius: 15px;
}
:root {
  --el-color-primary: #EF4238; /* 设置主题色 */
}

:root {
  --el-color-primary: #EF4238; /* 设置主题色 */
  --el-button-primary-bg: #EF4238; /* 主按钮背景色 */
  --el-button-primary-border: #EF4238; /* 主按钮边框色 */
  --el-button-primary-text-color: #fff; /* 主按钮文本色 */
}

/* 默认状态 */
.el-button--primary {
  background-color: var(--el-button-primary-bg);
  border-color: var(--el-button-primary-border);
  color: var(--el-button-primary-text-color);
  transition: background-color 0.3s, border-color 0.3s; /* 添加过渡效果 */
}

/* 悬停状态 */
.el-button--primary:hover {
  background-color: #A52A2A; /* 更深的悬停背景色 */
  border-color: #A52A2A; /* 更深的悬停边框色 */
}

/* 按钮被禁用时 */
.el-button--primary.is-disabled {
  background-color: #f5f5f5; /* 禁用状态背景色 */
  border-color: #dcdcdc; /* 禁用状态边框色 */
  color: #b0b0b0; /* 禁用状态文本色 */
}
</style>
