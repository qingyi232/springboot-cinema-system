<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item label="名称" prop="name">
            <el-input v-model="searchForm.name" clearable></el-input>
          </el-form-item>
          <el-form-item label="分类" prop="category">
            <el-input v-model="searchForm.category" clearable placeholder="如：卖品套餐/饮品"></el-input>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 140px">
              <el-option label="上架" value="上架" />
              <el-option label="下架" value="下架" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="primary" :icon="Plus" @click="add">新增</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length <= 0">
            批量删除
          </el-button>
        </el-space>
      </el-card>

      <el-card>
        <el-alert
          title="后台卖品与前台商品页已打通：支持上下架、库存控制和模拟支付下单。"
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
          <el-table-column label="商品图" width="110">
            <template #default="scope">
              <el-image
                v-if="scope.row.mainImg"
                class="goods-thumb"
                :src="scope.row.mainImg"
                :preview-src-list="[scope.row.mainImg]"
                :preview-teleported="true"
                fit="cover"
              />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="商品名称" min-width="180" show-overflow-tooltip />
          <el-table-column prop="category" label="分类" width="120" />
          <el-table-column label="售价" width="110">
            <template #default="scope"><span class="price-text">￥{{ formatPrice(scope.row.price) }}</span></template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="90" />
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '上架' ? 'success' : 'info'" effect="plain" round>
                {{ scope.row.status || '--' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="intro" label="简介" min-width="220" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" min-width="170" />
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

    <el-dialog v-model="dialogOpen" :title="formData.id ? '编辑卖品商品' : '新增卖品商品'" width="760px">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="商品图" prop="mainImg" :rules="[{required: true, message: '请上传商品图', trigger: ['blur', 'change']}]">
          <MyUpload
            v-if="dialogOpen"
            type="imageCard"
            :limit="1"
            :files="formData.mainImg"
            tip="上传新图后会直接替换当前商品图"
            @setFiles="formData.mainImg = $event"
          />
        </el-form-item>
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="name" :rules="[{required: true, message: '请输入商品名称', trigger: 'blur'}]">
              <el-input v-model="formData.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品分类" prop="category" :rules="[{required: true, message: '请输入商品分类', trigger: 'blur'}]">
              <el-input v-model="formData.category" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品价格" prop="price" :rules="[{required: true, message: '请输入商品价格', trigger: 'blur'}]">
              <el-input-number v-model="formData.price" :min="0.01" :precision="2" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品库存" prop="stock" :rules="[{required: true, message: '请输入商品库存', trigger: 'blur'}]">
              <el-input-number v-model="formData.stock" :min="0" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售卖状态" prop="status" :rules="[{required: true, message: '请选择售卖状态', trigger: 'change'}]">
              <el-radio-group v-model="formData.status">
                <el-radio-button label="上架">上架</el-radio-button>
                <el-radio-button label="下架">下架</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品简介" prop="intro" :rules="[{required: true, message: '请输入商品简介', trigger: 'blur'}]">
          <el-input v-model="formData.intro" type="textarea" :rows="5" maxlength="200" show-word-limit />
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
import {Check, Close, Delete, Edit, Plus, Refresh, Search} from '@element-plus/icons-vue';
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import MyUpload from "@/components/MyUpload.vue";
import {formatOrderAmount} from "@/utils/price.js";

const searchFormComponents = ref();
const tableComponents = ref();
const formRef = ref();
const listData = ref([]);
const selectionRows = ref([]);
const dialogOpen = ref(false);
const formData = ref({status: '上架'});
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
});
const searchForm = ref({
  name: undefined,
  category: undefined,
  status: undefined
});

getPageList();

function getPageList() {
  const data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value));
  request.get('/goods/page', {params: data}).then((res) => {
    listData.value = res.data.list || [];
    pageInfo.value.total = res.data.total || 0;
  });
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

function currentChange(pageNum) {
  pageInfo.value.pageNum = pageNum;
  getPageList();
}

function sizeChange(pageSize) {
  pageInfo.value.pageSize = pageSize;
  pageInfo.value.pageNum = 1;
  getPageList();
}

function selectionChange(rows) {
  selectionRows.value = rows;
}

function add() {
  formData.value = {status: '上架', stock: 0, price: 1};
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
      return;
    }
    const payload = Object.assign({}, formData.value);
    const requestApi = payload.id ? request.put('/goods/update', payload) : request.post('/goods/add', payload);
    requestApi.then(() => {
      ElMessage.success(payload.id ? '更新成功' : '新增成功');
      dialogOpen.value = false;
      getPageList();
    });
  });
}

function deleteOne(row) {
  batchDelete([row]);
}

function batchDelete(rows) {
  const targetRows = rows || selectionRows.value;
  const ids = targetRows.map(item => item.id);
  if (!ids.length) {
    return;
  }
  ElMessageBox.confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(() => {
    request.delete('/goods/delBatch', {data: ids}).then(() => {
      ElMessage.success('操作成功');
      getPageList();
    });
  }).catch(() => {
    tableComponents.value?.clearSelection();
  });
}

function formatPrice(value) {
  return formatOrderAmount(value, {digits: 2});
}
</script>

<style scoped>
.goods-thumb {
  width: 72px;
  height: 72px;
  border-radius: 12px;
  background: #f5f7fa;
}

.price-text {
  color: #ef4238;
  font-weight: 700;
}
</style>
