# Windows 一键启动说明

## 1. 这次新增了什么

这次没有改掉原来的启动方式，只是**额外补了 Windows 一键启动脚本**：

- `config-windows.bat`：启动配置
- `check-windows.bat`：检查数据库连接
- `init-db-windows.bat`：初始化数据库
- `start-windows.bat`：一键启动前后端
- `stop-windows.bat`：一键停止前后端

也就是说：

- **原命令行启动方式没废掉**
- **现在多了一个适合发给 Windows 客户的双击入口**

---

## 2. 客户电脑需要的环境

建议客户电脑先准备：

- JDK 17
- Node.js 18+
- MySQL 8.x

并且把这些命令加入系统 `PATH`：

- `java`
- `node`

说明：

- **如果交付包里带了 `target/template.jar`，后端可直接用 JAR 启动，不强依赖 Maven**
- **只有在没有现成 JAR 时，才需要 Maven 3.9+**
- **现在初始化数据库默认走 JAR 内置导入逻辑，不再强依赖 `mysql.exe` 命令行**
- **当前交付包已内置前端生产版 `web/dist`，客户启动时不需要再执行 `npm install` / `vite build`**
- **只有当 `web/dist` 被手动删掉时，才会退回到本地 npm 构建**

---

## 3. 首次运行步骤

### 先放到英文路径

请把整个项目解压到**纯英文路径**下再运行，例如：

- `D:\movie-booking-system`

尽量避免这些情况：

- 路径里有中文
- 路径里有空格
- 路径里有括号或特殊符号

### 第一步：修改配置

打开 `config-windows.bat`，按客户电脑实际情况改这些值：

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `SERVER_PORT`
- `FRONT_PORT`

默认值是：

- 后端：`1000`
- 前端：`5173`
- 数据库：`movie_booking_system`
- 数据库账号：`root`
- 数据库密码：`root`

---

### 第二步：初始化数据库

双击运行：

- `init-db-windows.bat`

说明：

- 会创建数据库 `movie_booking_system`
- 会导入 `sql/sql.sql`
- 会继续执行 `sql/2026-04-feature-upgrade.sql`
- **基础 SQL 会覆盖同名表，首次部署再执行**
- **即使电脑上只有 Navicat、没有配置 `mysql.exe` 到 PATH，也可以初始化**

如果客户不确定电脑上有没有可用的 MySQL 服务，或者不确定账号密码是否正确，可以先运行：

- `check-windows.bat`

它会直接检查：

- MySQL 或 phpStudy MySQL 是否已启动
- 数据库端口是否可连接
- 用户名密码是否正确
- 目标数据库是否存在
- 基础表是否已初始化

---

### 第三步：启动系统

双击运行：

- `start-windows.bat`

启动成功后：

- 后端地址：`http://127.0.0.1:1000`
- 前端地址：`http://127.0.0.1:5173`
- 启动日志目录：`logs\backend.log`、`logs\frontend.log`

说明：

- 脚本现在会先等待后端端口真正启动成功，再启动前端
- 如果后端没起来，会直接提示查看 `logs\backend.log`
- 前端现在默认直接使用打包好的 `web/dist`，不会在客户电脑上重新安装依赖
- 启动时会自动生成 `web\dist\runtime-config.js`，所以改前端端口后**不需要重新构建**
- 如果 `web/dist` 缺失，脚本才会回退到 npm 安装 + 构建，并把日志写入：
  - `logs\frontend-build.log`
  - `logs\frontend.log`

---

## 4. 默认账号

- 管理员：`admin / 123456`
- 普通用户：`user1 / 123456`
- 影院：`c1 / 123456`、`c2 / 123456`、`c3 / 123456`

---

## 5. 图片与上传文件

项目根目录下的 `uploads/` 文件夹需要一并发给客户。

因为电影封面、卖品图片、头像等资源，当前都是通过后端 `/file/*` 从这个目录读取的。

如果只发代码不发 `uploads/`，页面会缺图。

---

## 6. 停止系统

双击运行：

- `stop-windows.bat`

它会按端口尝试关闭：

- 后端端口：`1000`
- 前端端口：`5173`

---

## 7. 交付建议

如果你现在发给客户：

### 可以直接发的内容

- 当前统一截图
- 当前代码
- `uploads/` 资源目录
- `sql/` 目录
- 这 5 个 Windows 启动脚本
- 本说明文档

### 最佳发法

建议打包成一个完整交付包：

1. 项目源码
2. `uploads/`
3. `sql/`
4. `README-Windows.md`
5. 5 个 `.bat` 脚本

这样客户即使是 Windows，也不会再问你怎么逐条敲命令。
