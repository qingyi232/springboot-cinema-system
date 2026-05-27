<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item label="投放位置" prop="position">
            <el-select v-model="searchForm.position" placeholder="请选择" clearable filterable style="width: 180px">
              <el-option :label="item" :value="item" :key="item" v-for="item in positionList"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="primary" @click="add" :icon="Plus">新增</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length <= 0">
            批量删除
          </el-button>
        </el-space>
      </el-card>

      <el-card>
        <el-alert
          title="广告位页已统一投放信息表达：支持查看投放位置、跳转链接、排序优先级和素材封面，方便运营统一管理。"
          type="info"
          :closable="false"
          style="margin-bottom: 16px"
        />
        <el-table
          ref="tableComponents"
          :data="listData"
          tooltip-effect="dark"
          style="width: 100%"
          @selection-change="selectionChange"
          border
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="id" label="ID" width="60"></el-table-column>
          <el-table-column label="投放信息" min-width="220">
            <template #default="scope">
              <div class="asset-cell">
                <el-image
                  v-if="scope.row.mainImg"
                  class="asset-thumb"
                  :src="scope.row.mainImg"
                  :preview-src-list="[scope.row.mainImg]"
                  :preview-teleported="true"
                />
                <div class="asset-meta">
                  <div class="asset-title">{{ scope.row.title || '--' }}</div>
                  <div class="asset-sub">广告位素材</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="投放位置" width="160">
            <template #default="scope">
              <el-tag type="warning" effect="plain" round>{{ scope.row.position || '--' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="link" label="跳转链接" min-width="220" show-overflow-tooltip>
            <template #default="scope">
              <span class="link-text">{{ scope.row.link || '--' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="排序优先级" width="110">
            <template #default="scope">
              <span class="sort-text">#{{ scope.row.sort ?? 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="160"></el-table-column>
          <el-table-column fixed="right" label="操作" width="200">
            <template #default="scope">
              <el-button :icon="Edit" @click="edit(scope.$index, scope.row)">编辑</el-button>
              <el-button :icon="Delete" type="danger" @click="deleteOne(scope.$index, scope.row)">删除</el-button>
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
          >
          </el-pagination>
        </div>
      </el-card>
    </el-space>

    <el-dialog v-model="dialogOpen" v-if="dialogOpen" :title="formData.id ? '编辑' : '新增'" width="560">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <slot name="content">
          <el-form-item
            label="投放位置"
            prop="position"
            :rules="[{ required: true, message: '不能为空', trigger: ['blur', 'change'] }]"
          >
            <el-select v-model="formData.position" placeholder="请选择" filterable>
              <el-option :label="item" :value="item" :key="item" v-for="item in positionList"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="标题" prop="title" :rules="[{ required: true, message: '不能为空', trigger: ['blur', 'change'] }]">
            <el-input v-model="formData.title"></el-input>
          </el-form-item>
          <el-form-item label="链接" prop="link" :rules="[{ required: true, message: '不能为空', trigger: ['blur', 'change'] }]">
            <el-input v-model="formData.link" placeholder="请输入跳转链接"></el-input>
          </el-form-item>
          <el-form-item
            label="封面图"
            prop="mainImg"
            :rules="[{ required: true, message: '不能为空', trigger: ['blur', 'change'] }]"
          >
            <MyUpLoad type="imageCard" :limit="1" :files="formData.mainImg" @setFiles="formData.mainImg = $event" v-if="dialogOpen"></MyUpLoad>
          </el-form-item>
          <el-form-item
            label="排序"
            prop="sort"
            :rules="[{ required: true, message: '不能为空', trigger: ['blur', 'change'] }]"
          >
            <el-input-number v-model="formData.sort" :min="0" :step="1" controls-position="right" />
          </el-form-item>
        </slot>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit" :icon="Check">提交</el-button>
          <el-button @click="closeDialog" :icon="Close">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import { Check, Close, Delete, Edit, Refresh, Plus, Search } from '@element-plus/icons-vue'
import { ref, toRaw } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import MyUpLoad from "@/components/MyUpload.vue";

const searchFormComponents = ref();
const tableComponents = ref();
const listData = ref([]);
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
});
const searchForm = ref({
  position: undefined,
});

const positionList = ref(['轮播图左下侧', '轮播图右下侧'])

getPageList()

function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/advertising/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  })
}

function currentChange(e) {
  pageInfo.value.pageNum = e
  getPageList()
}

function sizeChange(e) {
  pageInfo.value.pageSize = e
  getPageList()
}

function search() {
  pageInfo.value.pageNum = 1
  getPageList()
}

function resetSearch() {
  searchFormComponents.value.resetFields();
  pageInfo.value.pageNum = 1
  getPageList()
}

const dialogOpen = ref(false);
const formData = ref({});
const formRef = ref();

function add() {
  formData.value = {}
  formData.value.sort = 0
  dialogOpen.value = true
}

function edit(index, row) {
  formData.value = Object.assign({}, row)
  dialogOpen.value = true
}

function closeDialog() {
  dialogOpen.value = false
}

function submit() {
  formRef.value.validate((valid) => {
    if (!valid) {
      ElMessage({ message: "验证失败，请检查表单!", type: 'warning' });
      return
    }
    if (!formData.value.id) {
      request.post("/advertising/add", formData.value).then(res => {
        if (!res) return
        dialogOpen.value = false
        ElMessage({ message: "操作成功", type: 'success' });
        getPageList()
      })
    } else {
      request.put("/advertising/update", formData.value).then(res => {
        if (!res) return
        dialogOpen.value = false
        ElMessage({ message: "操作成功", type: 'success' });
        getPageList()
      })
    }
  })
}

const selectionRows = ref([]);

function selectionChange(rows) {
  selectionRows.value = rows
}

function deleteOne(index, row) {
  batchDelete([row])
}

function batchDelete(rows) {
  if (!rows) rows = selectionRows.value;
  let ids = rows.map(item => item.id);
  ElMessageBox.confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(() => {
    request.delete("/advertising/delBatch", { data: ids }).then(res => {
      if (!res) return
      ElMessage({ message: "操作成功", type: 'success' });
      getPageList()
    })
  }).catch(() => {
    ElMessage({ type: 'info', message: '已取消删除' });
    tableComponents.value.clearSelection();
  });
}
</script>

<style scoped>
.asset-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}
.asset-thumb {
  width: 64px;
  height: 64px;
  border-radius: 14px;
  flex-shrink: 0;
}
.asset-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}
.asset-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}
.asset-sub {
  font-size: 12px;
  color: #909399;
}
.link-text {
  color: #606266;
}
.sort-text {
  font-weight: 600;
  color: #7c3aed;
}
</style>
