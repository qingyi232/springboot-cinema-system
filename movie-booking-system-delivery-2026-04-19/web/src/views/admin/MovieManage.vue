<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item label="名称" prop="name">
            <el-input v-model="searchForm.name" clearable></el-input>
          </el-form-item>
          <el-form-item label="分类" prop="movieTypeId">
            <el-select v-model="searchForm.movieTypeId" placeholder="请选择" clearable filterable style="width: 150px">
              <el-option :label="item.name" :value="item.id" :key="item.id" v-for="item in searchMovieTypeList"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="电影区域" prop="movieRegionId">
            <el-select v-model="searchForm.movieRegionId" placeholder="请选择" clearable filterable
                       style="width: 150px">
              <el-option :label="item.name" :value="item.id" :key="item.id" v-for="item in searchMovieRegionList"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="primary" @click="add" :icon="Plus">新增</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length<=0">
            批量删除
          </el-button>
        </el-space>
      </el-card>
      <el-card>
        <el-alert
            title="后台已同步前台票价与可售状态：可直接查看每部电影是否已有未来场次和参考票价"
            type="info"
            :closable="false"
            style="margin-bottom: 16px"
        />
        <el-table ref="tableComponents"
                  :data="listData"
                  tooltip-effect="dark"
                  style="width: 100%"
                  @selection-change="selectionChange"
                  border>
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="id" label="ID" width="50"></el-table-column>
          <el-table-column prop="name" label="名称" min-width="160" show-overflow-tooltip></el-table-column>
          <el-table-column prop="mainImg" label="封面图">
            <template #default="scope">
              <el-image v-if="scope.row.mainImg" style="width: 100px; height: 100px" :src="scope.row.mainImg"
                        :preview-src-list="[scope.row.mainImg]" :preview-teleported="true"></el-image>
            </template>
          </el-table-column>
          <el-table-column prop="movieTypeName" label="分类" width="100"></el-table-column>
          <el-table-column prop="movieRegionName" label="区域" width="100"></el-table-column>
          <el-table-column prop="releaseDate" label="上映时间" min-width="160"></el-table-column>
          <el-table-column prop="duration" label="时长" width="100">
            <template #default="scope">
              {{ formatDuration(scope.row.duration) }}
            </template>
          </el-table-column>
          <el-table-column label="参考票价" width="120">
            <template #default="scope">
              <span class="price-text" :class="{ 'price-text--muted': getSaleState(scope.row) !== 'buyable' }">
                {{ getSalePriceText(scope.row) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="售卖状态" width="100">
            <template #default="scope">
              <el-tag :type="getSaleStatusTagType(scope.row)" effect="plain" round>
                {{ getSaleStatusText(scope.row) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="评分" width="80"></el-table-column>
          <el-table-column label="票房" width="110">
            <template #default="scope">
              {{ formatBoxOffice(scope.row.boxOffice) }}
            </template>
          </el-table-column>
          <el-table-column prop="director" label="导演" min-width="120" show-overflow-tooltip></el-table-column>
          <el-table-column prop="protagonist" label="主演" min-width="180" show-overflow-tooltip></el-table-column>
          <el-table-column prop="imgs" label="图集">
            <template #default="scope">
              <el-image v-if="scope.row.imgs" style="width: 100px; height: 100px" :src="scope.row.imgs.split(',')[0]"
                        :preview-src-list="scope.row.imgs.split(',')" :preview-teleported="true"></el-image>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间"></el-table-column>
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
              :total="pageInfo.total">
          </el-pagination>
        </div>
      </el-card>
    </el-space>
    <el-dialog
        v-model="dialogOpen"
        v-if="dialogOpen"
        :title="formData.id?'编辑':'新增'"
        width="800"
    >
      <el-form ref="formRef" :model="formData" label-width="100px" inline>
        <slot name="content">
          <el-form-item label="封面图" prop="mainImg"
                        :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]" style="width: 100%">
            <MyUpLoad type="imageCard" :limit="1" :files="formData.mainImg" @setFiles="formData.mainImg =$event"
                      v-if="dialogOpen"></MyUpLoad>
          </el-form-item>
          <el-form-item label="名称" prop="name"
                        :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input v-model="formData.name"></el-input>
          </el-form-item>

          <el-form-item label="分类" prop="movieTypeId"
                        :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-select v-model="formData.movieTypeId" placeholder="请选择" filterable>
              <el-option :label="item.name" :value="item.id" :key="item.id" v-for="item in movieTypeList"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="时长" prop="duration"
                        :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input-number v-model="formData.duration" :min="1" :step="1" controls-position="right"></el-input-number>
          </el-form-item>
          <el-form-item label="电影区域" prop="movieRegionId"
                        :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-select v-model="formData.movieRegionId" placeholder="请选择" filterable>
              <el-option :label="item.name" :value="item.id" :key="item.id" v-for="item in movieRegionList"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="上映时间" prop="releaseDate"
                        :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-date-picker
                v-model="formData.releaseDate"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择上映时间"
            />
          </el-form-item>
          <el-form-item label="导演" prop="director"
                        :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input v-model="formData.director"></el-input>
          </el-form-item>
          <el-form-item label="主演" prop="protagonist"
                        :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]" style="width: 100%">
            <el-input type="textarea" :rows="5" v-model="formData.protagonist"></el-input>
          </el-form-item>

          <el-form-item label="图集" prop="imgs"
                        :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]" style="width: 100%">
            <MyUpLoad type="image" :limit="10" :files="formData.imgs" @setFiles="formData.imgs =$event"
                      v-if="dialogOpen"></MyUpLoad>
          </el-form-item>
          <el-form-item label="简介" prop="intro"
                        :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <MyEditor :content="formData.intro" @content-change="formData.intro =$event" v-if="dialogOpen"></MyEditor>
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
import {Check, Close, Delete, Edit, Refresh, Plus, Search} from '@element-plus/icons-vue'
import {computed, ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import MyEditor from "@/components/MyEditor.vue";
import MyUpLoad from "@/components/MyUpload.vue";
import {formatBoxOffice, formatTicketPrice} from "@/utils/price.js";
import {getMovieAvailabilityState} from "@/utils/movie.js";

const searchFormComponents = ref();
const tableComponents = ref();
const listData = ref([]);
const pageInfo = ref({
  //当前页
  pageNum: 1,
  //分页大小
  pageSize: 10,
  //总条数
  total: 0
});
const searchForm = ref({
  name: undefined,
  movieTypeId: undefined,
  movieRegionId: undefined,

});
const movieBaseList = ref([]);

const movieTypeList = ref([])
const searchMovieTypeList = computed(() => filterSearchOptions(movieTypeList.value, 'movieTypeId'))

getMovieTypeList()

function getMovieTypeList() {
  request.get("/movieType/list").then(res => {
    movieTypeList.value = res.data;
  })
}

const movieYearsList = ref([])

getMovieYearsList()

function getMovieYearsList() {
  request.get("/movieYears/list").then(res => {
    movieYearsList.value = res.data;
  })
}

const movieRegionList = ref([])
const searchMovieRegionList = computed(() => filterSearchOptions(movieRegionList.value, 'movieRegionId'))

getMovieRegionList()

function getMovieRegionList() {
  request.get("/movieRegion/list").then(res => {
    movieRegionList.value = res.data;
  })
}


getPageList()

/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/movie/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  })
}

getMovieBaseList()

function getMovieBaseList() {
  request.get("/movie/list").then(res => {
    movieBaseList.value = res.data || []
  })
}

function getSaleState(row) {
  return getMovieAvailabilityState(row)
}

function getSalePriceText(row) {
  const saleState = getSaleState(row)
  if (saleState === 'upcoming') {
    return '即将上映'
  }
  return formatTicketPrice(row.lowestPrice, {fallback: '待排片'})
}

function getSaleStatusText(row) {
  const saleState = getSaleState(row)
  if (saleState === 'buyable') return '可购票'
  if (saleState === 'upcoming') return '想看中'
  return '待排片'
}

function getSaleStatusTagType(row) {
  const saleState = getSaleState(row)
  if (saleState === 'buyable') return 'success'
  if (saleState === 'upcoming') return 'warning'
  return 'info'
}

/**
 * 选择分页
 * @param e
 */
function currentChange(e) {
  pageInfo.value.pageNum = e
  getPageList()
}

/**
 * 改变分页数量
 * @param e
 */
function sizeChange(e) {
  pageInfo.value.pageSize = e
  pageInfo.value.pageNum = 1
  getPageList()
}

/**
 * 搜索
 */
function search() {
  pageInfo.value.pageNum = 1
  getPageList()
}

/**
 * 重置搜索框
 */
function resetSearch() {
  searchFormComponents.value.resetFields();
  searchForm.value.movieTypeId = undefined
  searchForm.value.movieRegionId = undefined
  pageInfo.value.pageNum = 1
  getPageList()
}

const dialogOpen = ref(false);
const formData = ref({});
const formRef = ref();

/**
 * 新增
 */
function add() {
  formData.value = {
    movieYearsId: getDefaultMovieYearsId()
  }
  dialogOpen.value = true
}

/**
 * 编辑
 * @param index
 * @param row
 */
function edit(index, row) {
  formData.value = Object.assign({}, row)
  if (!formData.value.movieYearsId) {
    formData.value.movieYearsId = getDefaultMovieYearsId()
  }
  dialogOpen.value = true
}

/**
 * 关闭弹框
 */
function closeDialog() {
  dialogOpen.value = false
}

/**
 * 提交数据
 */
function submit() {
  formRef.value.validate((valid) => {
    if (!valid) {
      ElMessage({
        message: "验证失败，请检查表单!",
        type: 'warning'
      });
      return
    }
    if (!formData.value.movieYearsId) {
      const defaultMovieYearsId = getDefaultMovieYearsId()
      if (!defaultMovieYearsId) {
        ElMessage.warning('电影年代基础数据缺失，请先维护一条默认年代数据')
        return
      }
      formData.value.movieYearsId = defaultMovieYearsId
    }
    //新增
    if (!formData.value.id) {
      request.post("/movie/add", formData.value).then(res => {
        if (!res) {
          return
        }
        dialogOpen.value = false
        ElMessage({
          message: "操作成功",
          type: 'success'
        });
        getPageList()
      })
    } else {
      //更新
      request.put("/movie/update", formData.value).then(res => {
        if (!res) {
          return
        }
        dialogOpen.value = false
        ElMessage({
          message: "操作成功",
          type: 'success'
        });
        getPageList()
      })
    }
  })
}

const selectionRows = ref([]);

/**
 * 多选
 * @param rows
 */
function selectionChange(rows) {
  selectionRows.value = rows
}

/**
 * 单个删除
 * @param index
 * @param row
 */
function deleteOne(index, row) {
  batchDelete([row])
}

/**
 * 批量删除
 * @param rows
 */
function batchDelete(rows) {
  if (!rows) {
    rows = selectionRows.value;
  }
  let ids = rows.map(item => item.id);
  ElMessageBox.confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(() => {
    request.delete("/movie/delBatch", {data: ids}).then(res => {
      if (!res) {
        return
      }
      ElMessage({
        message: "操作成功",
        type: 'success'
      });
      getPageList()
    })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消删除'
    });
    tableComponents.value.clearSelection();
  });
}

function formatDuration(duration) {
  return duration ? `${duration}分钟` : '--'
}

function getDefaultMovieYearsId() {
  return movieYearsList.value?.[0]?.id
}

function filterSearchOptions(options, field) {
  const usedIds = new Set(
    (movieBaseList.value || [])
      .map(item => item?.[field])
      .filter(item => item !== null && item !== undefined && item !== '')
      .map(item => String(item))
  )
  return (options || []).filter(item => usedIds.has(String(item.id)))
}
</script>

<style scoped>
.price-text {
  color: #ef4238;
  font-weight: 600;
}

.price-text--muted {
  color: #909399;
}
</style>
