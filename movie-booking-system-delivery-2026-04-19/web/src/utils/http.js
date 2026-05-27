import axios from "axios";
import router from "../router";
import {ElMessage} from "element-plus";
import {getApiBaseUrl} from "./runtime";

// 设置 Axios 的默认基础 URL
axios.defaults.baseURL = getApiBaseUrl();

// 创建 Axios 实例
const http = axios.create({
    baseURL: getApiBaseUrl(),
    timeout: 5000, headers: {
        'Content-Type': 'application/json'
    }
});

function extractErrorMessage(error) {
    const responseData = error?.response?.data;
    return responseData?.data || responseData?.msg || error?.message || "请求失败，请稍后重试";
}

function logHttpError(error, message) {
    console.error("[HTTP Error]", {
        url: error?.config?.url,
        method: error?.config?.method,
        status: error?.response?.status,
        message,
        response: error?.response?.data
    });
}

// 请求拦截器
http.interceptors.request.use(config => {
    if (localStorage.getItem("token")) {
        config.headers["token"] = localStorage.getItem("token");
    }
    return config;
})
;

// 响应拦截器
http.interceptors.response.use(response => {
    return response.data;
}, error => {
    const message = extractErrorMessage(error);
    if (!error.response) {
        const networkMessage = "后端服务不可用，请确认后端已启动后再重试";
        logHttpError(error, networkMessage);
        ElMessage({message: networkMessage, type: "error"});
        return Promise.reject(error);
    }

    logHttpError(error, message);

    switch (error.response.status) {
        case 401:
            localStorage.removeItem("token");
            localStorage.removeItem("currentUser");
            ElMessage({message: message || "请先登录", type: "error"});
            if (router.currentRoute.value.path !== "/login") {
                router.push("/login");
            }
            break;
        case 409:
            ElMessage({message, type: "error"});
            break;
        case 404:
            ElMessage({message: message === "请求失败，请稍后重试" ? "接口未找到" : message, type: "error"});
            break;
        case 500:
            ElMessage({message: message === "请求失败，请稍后重试" ? "服务异常，请稍后重试" : message, type: "error"});
            break;
        default:
            ElMessage({message, type: "error"});
            break;
    }

    return Promise.reject(error);
});
export default http;

