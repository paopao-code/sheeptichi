# 数智牧场数据管理平台

这是一个前后端分离的智慧羊场数据分析项目。当前阶段包含 Spring Boot 后端、Vue 3 前端，以及羊体测量数据的查询、新增、删除和基础可视化能力。

## 技术栈

- 前端：Vue 3、Vite、TypeScript、Element Plus、Vue Router、Pinia、Axios、ECharts
- 后端：Spring Boot 3、Java 17、Maven、MyBatis、MySQL、Lombok、Validation
- 数据库：MySQL，数据库名 `sheep_new`，表名 `body_measurement`

## 使用 Navicat 创建数据库和表

1. 打开 Navicat，连接本机 MySQL。
2. 新建数据库，名称填写 `sheep_new`，字符集建议选择 `utf8mb4`，排序规则选择 `utf8mb4_unicode_ci`。
3. 打开查询窗口，选择 `sheep_new` 数据库。
4. 执行 [backend/src/main/resources/db/body_measurement.sql](backend/src/main/resources/db/body_measurement.sql)。

如果当前 MySQL 用户没有 `CREATE DATABASE` 权限，可以先手动创建 `sheep_new`，然后只执行 SQL 文件中的 `CREATE TABLE IF NOT EXISTS body_measurement ...` 部分。

## 配置数据库密码

后端配置文件为：

- 示例文件：[backend/src/main/resources/application-dev.yml.example](backend/src/main/resources/application-dev.yml.example)
- 本地开发文件：[backend/src/main/resources/application-dev.yml](backend/src/main/resources/application-dev.yml)

不要提交真实数据库密码。推荐用环境变量启动：

```powershell
$env:DB_PASSWORD="你的真实MySQL密码"
```

也可以在本机 `application-dev.yml` 中填写密码，但该文件已被 `.gitignore` 忽略。

## 启动后端

```powershell
cd D:\sheep\backend
$env:DB_PASSWORD="你的真实MySQL密码"
mvn spring-boot:run
```

后端默认端口：`8083`。

## 启动前端

```powershell
cd D:\sheep\frontend
npm install
npm run dev
```

前端默认端口：`5173`。开发环境通过 Vite 将 `/api` 代理到 `http://localhost:8083`。

## 测试 /api/health

```powershell
Invoke-RestMethod http://localhost:8083/api/health
```

预期返回：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "status": "ok",
    "service": "sheep-backend"
  }
}
```

## 测试 /api/measurements

查询：

```powershell
Invoke-RestMethod http://localhost:8083/api/measurements
```

新增：

```powershell
Invoke-RestMethod -Method Post http://localhost:8083/api/measurements `
  -ContentType "application/json" `
  -Body '{"penNo":"A01","bodyLength":120.5,"bodyHeight":75.2,"chestWidth":32.1,"chestDepth":40.0,"chestGirth":95.6}'
```

删除：

```powershell
Invoke-RestMethod -Method Delete http://localhost:8083/api/measurements/1
```

说明：新增时不要传 `id` 和 `measuredAt`。`id` 由数据库自增生成，`measuredAt` 由后端自动填充。
