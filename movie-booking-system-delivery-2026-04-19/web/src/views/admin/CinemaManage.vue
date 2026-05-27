<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%" size="large">
      <el-card shadow="never">
        <div class="panel-head">
          <div>
            <div class="page-title">电影院管理</div>
            <div class="page-subtitle">统一维护影院账号、名称、电话、地址与展示标签，用户端和影院端会同步使用这些信息。</div>
          </div>
        </div>

        <el-form ref="searchFormComponents" :model="searchForm" inline style="margin-top: 16px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="searchForm.username" clearable placeholder="搜索影院账号" />
          </el-form-item>
          <el-form-item label="电话" prop="tel">
            <el-input v-model="searchForm.tel" clearable placeholder="搜索联系电话" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="searchForm.status" clearable placeholder="全部" style="width: 140px">
              <el-option v-for="item in statusList" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>

        <el-space>
          <el-button type="primary" :icon="Plus" @click="add">新增电影院</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete()" :disabled="selectionRows.length <= 0">批量删除</el-button>
        </el-space>
      </el-card>

      <el-card shadow="never">
        <el-alert
          title="客户口径已统一：影院信息以名称、电话、地址、标签为主，不再在前台展示邮箱。"
          type="info"
          :closable="false"
          style="margin-bottom: 16px"
        />

        <el-table
          ref="tableComponents"
          :data="listData"
          border
          tooltip-effect="dark"
          style="width: 100%"
          @selection-change="selectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="username" label="影院账号" width="120" />
          <el-table-column prop="nickname" label="影院名称" min-width="160" show-overflow-tooltip />
          <el-table-column label="影院图片" width="120">
            <template #default="scope">
              <el-image
                v-if="scope.row.avatarUrl"
                class="cinema-thumb"
                :src="scope.row.avatarUrl"
                :preview-src-list="[scope.row.avatarUrl]"
                :preview-teleported="true"
                fit="cover"
              />
            </template>
          </el-table-column>
          <el-table-column prop="tel" label="联系电话" width="150" />
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '启用' ? 'success' : 'info'" effect="plain" round>
                {{ scope.row.status || '--' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="address" label="地址" min-width="220" show-overflow-tooltip />
          <el-table-column prop="label" label="标签" min-width="220" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" min-width="170" />
          <el-table-column fixed="right" label="高级操作" width="120">
            <template #default="scope">
              <el-button type="warning" plain :icon="RefreshLeft" @click="resetPassword(scope.row)">重置密码</el-button>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="180">
            <template #default="scope">
              <el-button :icon="Edit" @click="edit(scope.row)">编辑</el-button>
              <el-button :icon="Delete" type="danger" @click="deleteOne(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="margin-top: 20px">
          <el-pagination
            @current-change="currentChange"
            @size-change="sizeChange"
            :page-size="pageInfo.pageSize"
            :current-page="pageInfo.pageNum"
            background
            layout="total,sizes, prev, pager, next"
            :total="pageInfo.total"
          />
        </div>
      </el-card>
    </el-space>

    <el-dialog v-model="dialogOpen" :title="formData.id ? '编辑电影院' : '新增电影院'" width="620px">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="影院账号" prop="username" :rules="[{ required: true, message: '请输入影院账号', trigger: ['blur', 'change'] }]">
          <el-input v-model="formData.username" :disabled="!!formData.id" placeholder="例如：c4" />
        </el-form-item>
        <el-form-item label="影院名称" prop="nickname" :rules="[{ required: true, message: '请输入影院名称', trigger: ['blur', 'change'] }]">
          <el-input v-model="formData.nickname" placeholder="请输入电影院名称" />
        </el-form-item>
        <el-form-item label="影院图片" prop="avatarUrl" :rules="[{ required: true, message: '请上传影院图片', trigger: ['blur', 'change'] }]">
          <MyUpLoad type="imageCard" :limit="1" :files="formData.avatarUrl" @setFiles="formData.avatarUrl = $event" v-if="dialogOpen" />
        </el-form-item>
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="tel">
              <el-input v-model="formData.tel" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status" :rules="[{ required: true, message: '请选择状态', trigger: ['blur', 'change'] }]">
              <el-radio-group v-model="formData.status">
                <el-radio v-for="item in statusList" :key="item" :label="item">{{ item }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address" :rules="[{ required: true, message: '请输入影院地址', trigger: ['blur', 'change'] }]">
          <el-input v-model="formData.address" type="textarea" :rows="4" maxlength="120" show-word-limit placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="标签" prop="label" :rules="[{ required: true, message: '请输入标签', trigger: ['blur', 'change'] }]">
          <el-input v-model="formData.label" placeholder="多个标签使用英文逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" :icon="Check" @click="submit">提交</el-button>
        <el-button :icon="Close" @click="closeDialog">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Plus, Refresh, RefreshLeft, Search} from '@element-plus/icons-vue';
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import MyUpLoad from "@/components/MyUpload.vue";

const searchFormComponents = ref();
const tableComponents = ref();
const formRef = ref();
const listData = ref([]);
const selectionRows = ref([]);
const dialogOpen = ref(false);
const formData = ref({status: '启用'});
const statusList = ref(['启用', '禁用']);
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
});
const searchForm = ref({
  username: undefined,
  tel: undefined,
  status: undefined
});

getPageList();

function getPageList() {
  const data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value));
  request.get('/cinema/page', {params: data}).then((res) => {
    listData.value = res.data.list || [];
    pageInfo.value.total = res.data.total || 0;
  });
}

function currentChange(pageNum) {
  pageInfo.value.pageNum = pageNum;
  getPageList();
}

function sizeChange(pageSize) {
  pageInfo.value.pageSize = pageSize;
  pageInfo.value.pageNum = 1;
  getPageList();
}

function search() {
  pageInfo.value.pageNum = 1;
  getPageList();
}

function resetSearch() {
  searchFormComponents.value?.resetFields();
  pageInfo.value.pageNum = 1;
  getPageList();
}

function add() {
  formData.value = {
    username: '',
    nickname: '',
    avatarUrl: '',
    tel: '',
    address: '',
    label: '',
    status: '启用'
  };
  dialogOpen.value = true;
}

function edit(row) {
  formData.value = Object.assign({}, row);
  dialogOpen.value = true;
}

function closeDialog() {
  dialogOpen.value = false;
}

function submit() {
  formRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.warning('请先完成表单校验');
      return;
    }
    const payload = Object.assign({}, formData.value);
    const api = payload.id ? request.put('/cinema/update', payload) : request.post('/cinema/add', payload);
    api.then(() => {
      ElMessage.success(payload.id ? '更新成功' : '新增成功');
      dialogOpen.value = false;
      getPageList();
    });
  });
}

function selectionChange(rows) {
  selectionRows.value = rows;
}

function deleteOne(row) {
  batchDelete([row]);
}

function batchDelete(rows = selectionRows.value) {
  const ids = rows.map(item => item.id);
  if (!ids.length) {
    return;
  }
  ElMessageBox.confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(() => {
    request.delete('/cinema/delBatch', {data: ids}).then(() => {
      ElMessage.success('操作成功');
      getPageList();
    });
  }).catch(() => {
    tableComponents.value?.clearSelection();
  });
}

function resetPassword(row) {
  request.post('/common/resetPassword?type=CINEMA&id=' + row.id).then(() => {
    ElMessage.success('密码已重置');
  });
}
</script>

<style scoped>
.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.page-subtitle {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
}

.cinema-thumb {
  width: 72px;
  height: 72px;
  border-radius: 14px;
  background: #f5f7fa;
}
</style>
