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
- 附近的人：基于地理位置搜索附近的用户（GEO功能）

## 技术栈

### 前端
- Vue 3
- TypeScript
- Vant UI
- Vite
- Geolocation API (HTML5定位)

### 后端
- Spring Boot
- MySQL
- Redis (会话管理、缓存)
- MyBatis-Plus
- Redisson (分布式锁)
- Swagger (API文档)
- GEO 空间索引 (MySQL空间数据处理)

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
│   │   │   └── utils/ (工具类，包含GeoUtils地理计算工具)
│   │   └── resources/ (配置文件)
│   └── sql/ (数据库脚本)
│       ├── create_table.sql (创建基础表)
│       └── add_geo_to_user.sql (添加地理位置字段)
└── juliaoba-frontend/ (前端项目)
    ├── src/
    │   ├── assets/ (静态资源)
    │   ├── components/ (组件)
    │   ├── config/ (配置)
    │   ├── layouts/ (布局)
    │   ├── models/ (数据模型)
    │   ├── pages/ (页面，包含NearbyPage附近用户页面)
    │   ├── plugins/ (插件)
    │   └── services/ (服务)
    └── public/ (公共资源)
```

## 快速开始

### 后端启动

1. 创建MySQL数据库，执行 `sql/create_table.sql` 脚本创建表
2. 执行 `sql/add_geo_to_user.sql` 脚本添加地理位置字段（如需使用GEO功能）
3. 修改 `application.yml` 中的数据库和Redis连接配置
4. 启动后端服务
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

## GEO功能使用说明

1. **功能简介**：通过地理位置信息，帮助用户搜索附近的其他用户，方便线下交流和合作。

2. **如何使用**：
   - 用户登录后，在个人中心页面点击"附近的人"
   - 首次使用时，会请求位置权限，请允许获取位置
   - 系统会自动更新用户位置并搜索附近用户
   - 可以调整搜索范围（默认5公里）查找不同距离内的用户

3. **技术实现**：
   - 前端使用HTML5 Geolocation API获取用户位置
   - 后端使用Haversine公式计算地球表面两点间距离
   - 使用MySQL空间索引优化地理位置查询性能

## 接口文档

启动后端服务后，访问 http://localhost:8080/api/swagger-ui.html 查看API文档。 