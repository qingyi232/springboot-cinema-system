<template>
  <div class="goods-page">
    <el-space direction="vertical" alignment="left" style="width: 100%" size="large">
      <el-card class="filter-card" shadow="never">
        <div class="header-row">
          <div>
            <div class="page-title">卖品商品</div>
            <div class="page-subtitle">观影前先把零食和饮品一起下单，支付方式支持支付宝、微信、银行卡</div>
          </div>
        </div>

        <el-alert
          title="卖品为模拟支付，下单后会自动生成卖品订单并扣减余额。库存为实时展示，售罄商品不可购买。"
          type="info"
          :closable="false"
          style="margin: 16px 0"
        />

        <el-form :model="searchForm" inline>
          <el-form-item label="关键词">
            <el-input
              v-model="searchForm.name"
              placeholder="搜索卖品名称"
              clearable
              @keyup.enter="search"
              style="width: 240px"
            />
          </el-form-item>
          <el-form-item label="分类">
            <el-radio-group v-model="searchForm.category" @change="search">
              <el-radio-button label="">全部</el-radio-button>
              <el-radio-button v-for="item in categoryList" :key="item" :label="item">
                {{ item }}
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-row v-if="listData.length > 0" :gutter="18">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in listData" :key="item.id" class="goods-col">
          <el-card shadow="hover" class="goods-card" :class="{ 'goods-card--disabled': !canBuy(item) }">
            <el-image :src="item.mainImg" fit="cover" class="goods-cover" />
            <div class="goods-body">
              <div class="goods-top-row">
                <el-tag effect="plain" round>{{ item.category || '卖品' }}</el-tag>
                <el-tag :type="canBuy(item) ? 'success' : 'info'" effect="plain" round>
                  {{ canBuy(item) ? `库存 ${item.stock}` : '已售罄' }}
                </el-tag>
              </div>
              <div class="goods-name">{{ item.name }}</div>
              <div class="goods-intro">{{ item.intro || '暂无商品介绍' }}</div>
              <div class="goods-footer">
                <div>
                  <div class="goods-price-label">售价</div>
                  <div class="goods-price">￥{{ formatPrice(item.price) }}</div>
                </div>
                <el-button type="danger" round :disabled="!canBuy(item)" @click="openPurchase(item)">
                  {{ canBuy(item) ? '立即购买' : '暂不可购' }}
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无卖品商品" />

      <div style="width: 100%">
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
    </el-space>
  </div>

  <el-dialog v-model="dialogVisible" title="确认卖品订单" width="520px">
    <div v-if="currentGoods.id" class="purchase-dialog-body">
      <el-image :src="currentGoods.mainImg" class="dialog-cover" fit="cover" />
      <div class="dialog-content">
        <div class="dialog-name">{{ currentGoods.name }}</div>
        <div class="dialog-intro">{{ currentGoods.intro || '暂无商品介绍' }}</div>
        <div class="dialog-stock">可售库存：{{ currentGoods.stock }}</div>
      </div>
    </div>

    <el-form :model="purchaseForm" label-width="88px" style="margin-top: 18px">
      <el-form-item label="购买数量">
        <el-input-number v-model="purchaseForm.quantity" :min="1" :max="Math.max(1, Number(currentGoods.stock || 1))" />
      </el-form-item>
      <el-form-item label="支付方式">
        <el-radio-group v-model="purchaseForm.paymentMethod">
          <el-radio-button label="支付宝">支付宝</el-radio-button>
          <el-radio-button label="微信">微信</el-radio-button>
          <el-radio-button label="银行卡">银行卡</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="订单金额">
        <div class="dialog-total">￥{{ totalPrice }}</div>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="submitOrder">确认购买</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import request from "@/utils/http.js";
import {computed, ref, toRaw} from "vue";
import {ElMessage} from "element-plus";
import {Refresh, Search} from '@element-plus/icons-vue';
import {formatOrderAmount} from "@/utils/price.js";
import {useRouter} from "vue-router";

const router = useRouter();
const listData = ref([]);
const categoryList = ref([]);
const dialogVisible = ref(false);
const submitLoading = ref(false);
const currentGoods = ref({});
const pageInfo = ref({
  pageNum: 1,
  pageSize: 8,
  total: 0
});
const searchForm = ref({
  name: "",
  category: ""
});
const purchaseForm = ref({
  quantity: 1,
  paymentMethod: "支付宝"
});

getPageList();
getCategoryList();

function getPageList() {
  const data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value));
  request.get('/goods/page', {params: data}).then((res) => {
    listData.value = res.data.list || [];
    pageInfo.value.total = res.data.total || 0;
  });
}

function getCategoryList() {
  request.get('/goods/list').then((res) => {
    const source = Array.isArray(res.data) ? res.data : [];
    categoryList.value = [...new Set(source.map(item => item.category).filter(Boolean))];
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
  searchForm.value = {name: '', category: ''};
  pageInfo.value.pageNum = 1;
  getPageList();
}

function canBuy(item) {
  return Number(item?.stock || 0) > 0 && item?.status === '上架';
}

function formatPrice(value) {
  return formatOrderAmount(value, {digits: 2});
}

function openPurchase(item) {
  currentGoods.value = Object.assign({}, item);
  purchaseForm.value.quantity = 1;
  purchaseForm.value.paymentMethod = '支付宝';
  dialogVisible.value = true;
}

const totalPrice = computed(() => {
  const total = Number(currentGoods.value.price || 0) * Number(purchaseForm.value.quantity || 0);
  return formatPrice(total);
});

function submitOrder() {
  if (!currentGoods.value.id) {
    return;
  }
  submitLoading.value = true;
  request.post('/goodsOrder/submit', {
    goodsId: currentGoods.value.id,
    quantity: purchaseForm.value.quantity,
    paymentMethod: purchaseForm.value.paymentMethod
  }).then(() => {
    ElMessage.success('购买成功，已生成卖品订单');
    dialogVisible.value = false;
    getPageList();
    router.push('/movieOrder?type=goodsOrder');
  }).finally(() => {
    submitLoading.value = false;
  });
}
</script>

<style scoped>
.goods-page {
  width: 75%;
  margin: 20px auto 0;
}

.filter-card {
  border-radius: 18px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.page-subtitle {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
}

.goods-col {
  margin-bottom: 18px;
}

.goods-card {
  height: 100%;
  border-radius: 20px;
  overflow: hidden;
  transition: transform .2s ease, box-shadow .2s ease;
}

.goods-card:hover {
  transform: translateY(-2px);
}

.goods-card--disabled {
  filter: grayscale(0.25);
  opacity: 0.9;
}

.goods-cover {
  width: 100%;
  height: 220px;
  border-radius: 14px;
  background: #f5f7fa;
}

.goods-body {
  padding-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.goods-top-row {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  flex-wrap: wrap;
}

.goods-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.goods-intro {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
  min-height: 46px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.goods-price-label {
  color: #909399;
  font-size: 12px;
}

.goods-price {
  margin-top: 4px;
  font-size: 24px;
  line-height: 1;
  font-weight: 700;
  color: #ef4238;
}

.purchase-dialog-body {
  display: flex;
  gap: 16px;
  align-items: center;
}

.dialog-cover {
  width: 120px;
  height: 120px;
  border-radius: 14px;
  background: #f5f7fa;
  flex-shrink: 0;
}

.dialog-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.dialog-intro {
  margin-top: 10px;
  color: #606266;
  line-height: 1.8;
}

.dialog-stock {
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}

.dialog-total {
  color: #ef4238;
  font-size: 22px;
  font-weight: 700;
}
</style>
