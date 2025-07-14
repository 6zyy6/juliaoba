# JuliaOba - 伙伴匹配系统

JuliaOba 是一个基于兴趣标签匹配的用户伙伴匹配系统，帮助用户寻找志同道合的队友。

## 项目介绍

本项目采用前后端分离架构:
- 前端: Vue 3 + TypeScript + Vant UI
- 后端: Spring Boot + MySQL + Redis + MyBatis-Plus

## 功能特点

- 用户注册、登录、个人信息管理
- 兴趣标签匹配：根据用户的兴趣标签智能推荐伙伴
- 心动模式：智能匹配相似兴趣的伙伴
- 队伍管理：创建、加入、退出、解散队伍
- 队伍搜索：支持按队伍状态(公开/加密)和关键词搜索

## 技术栈

### 前端
- Vue 3
- TypeScript
- Vant UI
- Vite

### 后端
- Spring Boot
- MySQL
- Redis (会话管理、缓存)
- MyBatis-Plus
- Redisson (分布式锁)
- Swagger (API文档)

## 项目结构

```
juliaoba/
├── juliaoba-backend/ (后端项目)
│   ├── src/
│   │   ├── main/java/com/yupi/yupao/
│   │   │   ├── common/ (通用类)
│   │   │   ├── config/ (配置类)
│   │   │   ├── controller/ (控制器)
│   │   │   ├── exception/ (异常处理)
│   │   │   ├── mapper/ (数据库访问层)
│   │   │   ├── model/ (数据模型)
│   │   │   ├── service/ (业务逻辑层)
│   │   │   └── utils/ (工具类)
│   │   └── resources/ (配置文件)
│   └── sql/ (数据库脚本)
└── juliaoba-frontend/ (前端项目)
    ├── src/
    │   ├── assets/ (静态资源)
    │   ├── components/ (组件)
    │   ├── config/ (配置)
    │   ├── layouts/ (布局)
    │   ├── models/ (数据模型)
    │   ├── pages/ (页面)
    │   ├── plugins/ (插件)
    │   └── services/ (服务)
    └── public/ (公共资源)
```

## 快速开始

### 后端启动

1. 创建MySQL数据库，执行 `sql/create_table.sql` 脚本创建表
2. 修改 `application.yml` 中的数据库和Redis连接配置
3. 启动后端服务
   ```
   cd juliaoba-backend
   mvn spring-boot:run
   ```

### 前端启动

1. 安装依赖
   ```
   cd juliaoba-frontend
   npm install
   ```

2. 启动开发服务器
   ```
   npm run dev
   ```

3. 构建生产版本
   ```
   npm run build
   ```

## 部署

### 后端部署
可使用提供的Dockerfile构建Docker镜像:
```
cd juliaoba-backend
docker build -t juliaoba-backend .
docker run -p 8080:8080 juliaoba-backend
```

### 前端部署
构建生产版本后，可将dist目录部署到任意静态文件服务器。

## 接口文档

启动后端服务后，访问 http://localhost:8080/api/swagger-ui.html 查看API文档。 