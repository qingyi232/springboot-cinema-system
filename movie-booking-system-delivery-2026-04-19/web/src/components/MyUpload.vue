<template>
  <div class="upload-wrapper">
    <el-upload
        ref="upload"
        :action="uploadUrl"
        :list-type="listType"
        :show-file-list="!isSingleImageMode"
        :on-preview="handlePreview"
        :file-list="fileList"
        :limit="limit"
        :accept="accept"
        :headers="uploadHeaders"
        :on-success="handleFileSuccess"
        :on-error="handleUploadError"
        :on-exceed="handleExceed"
        :on-remove="handleRemove">
      <el-button size="small" type="primary"> {{
          limit === 1 && fileList.length > 0 ? '点击替换' : '点击上传'
        }}
      </el-button>
      <template #tip>
        <div class="el-upload__tip">{{ tip }}</div>
      </template>
    </el-upload>
    <div v-if="isSingleImageMode && fileList.length" class="single-preview">
      <el-image
        class="single-preview__image"
        :src="fileList[0].url"
        :preview-src-list="[fileList[0].url]"
        :preview-teleported="true"
        fit="cover"
      />
      <div class="single-preview__meta">
        <div class="single-preview__name">{{ fileList[0].name }}</div>
        <div class="single-preview__actions">
          <el-button size="small" type="primary" @click="triggerReplace">更换图片</el-button>
          <el-button size="small" @click="handlePreview(fileList[0])">预览</el-button>
          <el-button size="small" type="danger" plain @click="removeSingleFile">移除图片</el-button>
        </div>
      </div>
    </div>
    <el-dialog v-model="dialogVisible" v-if="dialogVisible">
      <div>
        <img v-if="type==='image'||type==='imageCard'" width="100%" :src="previewFile.url" alt="">
        <video v-if="type==='video'" width="100%" :src="previewFile.url" controls></video>
        <audio v-if="type==='audio'" width="100%" :src="previewFile.url" controls></audio>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, nextTick, reactive, ref, watch} from 'vue';
import utils from "@/utils/tools.js";
import {ElMessage, genFileId} from "element-plus";
import {joinApiUrl} from "@/utils/runtime.js";

const props = defineProps({
  /**
   * 文件类型
   */
  type: {
    type: String,
    default: "file"
  },
  /**
   * 文件列表
   */
  files: {
    type: String,
    default: ""
  },
  /**
   * 提示信息
   */
  tip: {
    type: String,
    default: ""
  },
  /**
   * 上传文件数量限制
   */
  limit: {
    type: Number,
    default: 100
  }
});
//组件对象
const upload = ref()
//上传请求头，需要带token
const uploadHeaders = reactive({
  //设置token
  token: utils.getToken()
});
//服务器上传的URL
const uploadUrl = ref('');
//上传文件列表
const fileList = ref([]);
//允许上传的文件类型
const accept = ref('');
//默认文件类型
const listType = ref('picture-card');
//预览弹框
const dialogVisible = ref(false);
//预览文件信息
const previewFile = ref('');
const isSingleImageMode = computed(() => props.limit === 1 && (props.type === "image" || props.type === "imageCard"));

uploadUrl.value = joinApiUrl("/file/upload");
applyTypeSettings();

watch(
  () => props.files,
  (value) => {
    fileList.value = buildFileList(value);
  },
  {immediate: true}
);

watch(
  () => props.type,
  () => {
    applyTypeSettings();
  },
  {immediate: true}
);

function buildFileList(value) {
  if (!value) {
    return [];
  }
  return String(value)
    .split(",")
    .map(item => item.trim())
    .filter(Boolean)
    .map((fileUrl) => {
      const strings = fileUrl.split("/");
      return {
        name: strings[strings.length - 1],
        url: fileUrl
      };
    });
}

function applyTypeSettings() {
  switch (props.type) {
      /**
       * 图片卡片类型
       */
    case "imageCard":
      listType.value = "picture-card";
      accept.value = "image/*";
      break;
      /**
       *图片
       */
    case "image":
      listType.value = "picture";
      accept.value = "image/*";
      break;
      /**
       * 视频
       */
    case "video":
      accept.value = "video/*";
      listType.value = "text";
      break;
      /**
       * 音频
       */
    case "audio":
      accept.value = "audio/*";
      listType.value = "text";
      break;
      /**
       * 文件 附件
       */
    case "file":
      listType.value = "text";
      accept.value = "";
      break;
  }
}

function normalizeUploadFiles(files = []) {
  return files
    .map((item) => {
      const responseData = item?.response?.data || {};
      const url = responseData.url || item.url || item?.raw?.url || '';
      if (!url) {
        return null;
      }
      return {
        name: responseData.name || item.name || url.split('/').pop(),
        url
      };
    })
    .filter(Boolean);
}

//回调父组件，设置文件数据
const emit = defineEmits(['setFiles']);

/**
 * 通知父组件文件改变
 */
function setFiles() {
  let files = fileList.value.map((item) => {
    return item.url;
  });
  emit("setFiles", files.join(","));
}

/**
 * 文件上传成功后的处理
 * @param response
 * @param file
 * @param fileListRes
 */
function handleFileSuccess(response, file, fileListRes) {
  if (!response || response.code !== 200 || !response?.data?.url) {
    handleUploadError(response);
    fileList.value = normalizeUploadFiles(fileListRes.filter((item) => item.uid !== file.uid));
    setFiles();
    return;
  }
  fileList.value = normalizeUploadFiles(fileListRes);
  //通知父组件文件改变
  setFiles();
}

/**
 * 删除文件
 * @param file
 * @param fileListRes
 */
function handleRemove(file, fileListRes) {
  fileList.value = normalizeUploadFiles(fileListRes);
  //通知父组件文件改变
  setFiles();
}

/**
 * 预览文件
 * @param file
 */
function handlePreview(file) {
  //设置预览对象
  previewFile.value = file;
  if (props.type === "file") {
    //文件类型直接下载
    downloadFile();
    return;
  }
  //打开预览弹框
  dialogVisible.value = true;
}

/**
 * 处理文件超出限制的情况
 * @param files
 */
function handleExceed(files) {
  //如果只有一个就进行替换
  if (props.limit === 1) {
    fileList.value = []
    upload.value.clearFiles()
    const file = files[0];
    file.uid = genFileId()
    upload.value.handleStart(file)
    upload.value.submit()
  } else {
    ElMessage.warning(`最多只允许上传${props.limit}张图片`);
  }
}

function handleUploadError(error) {
  const message =
    error?.response?.data?.data ||
    error?.response?.data?.msg ||
    error?.msg ||
    error?.message ||
    '上传失败，请稍后重试';
  ElMessage.error(message);
}

async function triggerReplace() {
  await nextTick();
  const input = upload.value?.$el?.querySelector('input[type="file"]');
  input?.click();
}

function removeSingleFile() {
  fileList.value = [];
  upload.value?.clearFiles();
  setFiles();
}

/**
 * 下载文件
 */
function downloadFile() {
  const link = document.createElement('a');
  link.style.display = 'none';
  document.body.appendChild(link);
  link.href = previewFile.value.url;
  link.setAttribute('download', previewFile.value.name); // 你可以自定义下载时的文件名
  link.click();
  link.remove();
}
</script>


<style scoped>
.upload-wrapper {
  width: 100%;
}

.single-preview {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 12px;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 14px;
  background: #fafafa;
}

.single-preview__image {
  width: 88px;
  height: 88px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.single-preview__meta {
  flex: 1;
  min-width: 0;
}

.single-preview__name {
  color: #303133;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-all;
}

.single-preview__actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 10px;
}
</style>
