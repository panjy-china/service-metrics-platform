# Service Metrics Platform 部署指南

## 概述

本文档介绍如何使用Docker将Service Metrics Platform部署到服务器环境。

## 前置要求

### 服务器环境要求
- **操作系统**: Linux (推荐 Ubuntu 20.04+ 或 CentOS 7+)
- **CPU**: 最少2核，推荐4核
- **内存**: 最少4GB，推荐8GB
- **存储**: 最少20GB可用空间
- **网络**: 确保服务器可以访问外网下载依赖

### 软件要求
- **Docker**: 版本20.10+
- **Docker Compose**: 版本2.0+
- **Java**: JDK 21 (仅构建时需要)
- **Maven**: 3.6+ (仅构建时需要)

## 部署方式

### 方式一：使用预构建镜像（推荐）

如果您已经在本地构建好了镜像，可以直接部署：

```bash
# 1. 上传项目文件到服务器
scp -r service-metrics-platform/ user@your-server:/opt/

# 2. 登录服务器
ssh user@your-server

# 3. 进入项目目录
cd /opt/service-metrics-platform

# 4. 启动服务
docker-compose up -d
```

### 方式二：完整构建部署

如果需要在服务器上完整构建：

```bash
# 1. 克隆或上传代码到服务器
git clone <your-repository> /opt/service-metrics-platform
# 或者
scp -r service-metrics-platform/ user@your-server:/opt/

# 2. 登录服务器
ssh user@your-server

# 3. 进入项目目录
cd /opt/service-metrics-platform

# 4. 使用部署脚本
chmod +x build-and-deploy.sh
./build-and-deploy.sh prod
```

## 配置说明

### 环境变量配置

在部署前，需要修改 `docker-compose.yml` 中的环境变量：

```yaml
environment:
  # 数据库配置
  - MYSQL_URL=jdbc:mysql://mysql:3306/service_metrics?useSSL=false&serverTimezone=Asia/Shanghai
  - MYSQL_USERNAME=your_mysql_user
  - MYSQL_PASSWORD=your_mysql_password
  
  # ClickHouse配置
  - CLICKHOUSE_URL=jdbc:clickhouse://clickhouse:8123/default
  - CLICKHOUSE_USERNAME=default
  - CLICKHOUSE_PASSWORD=your_clickhouse_password
  
  # LLM API配置
  - DASHSCOPE_API_KEY=your_dashscope_api_key
```

### 数据库初始化

项目包含SQL初始化脚本，位于 `sql/` 目录：

```bash
sql/
├── create_wechat_message_analyze_address_table.sql
└── fix_wechat_message_analyze_address_table.sql
```

这些脚本会在MySQL容器启动时自动执行。

## 部署步骤详解

### 1. 准备工作

```bash
# 创建项目目录
sudo mkdir -p /opt/service-metrics-platform
cd /opt/service-metrics-platform

# 创建日志目录
mkdir -p logs
sudo chown -R $USER:$USER logs
```

### 2. 配置文件准备

确保以下文件已正确配置：
- `Dockerfile`
- `docker-compose.yml`
- `src/main/resources/application-prod.yml`
- `.dockerignore`

### 3. 构建和启动

#### 使用自动化脚本（推荐）
```bash
chmod +x build-and-deploy.sh
./build-and-deploy.sh prod
```

#### 手动部署
```bash
# 构建项目
mvn clean package -DskipTests

# 构建Docker镜像
docker build -t service-metrics-platform:0.0.1-SNAPSHOT .

# 启动所有服务
docker-compose up -d
```

### 4. 验证部署

```bash
# 检查所有容器状态
docker-compose ps

# 检查应用健康状态
curl http://localhost:8080/api/order-metrics/health

# 查看应用日志
docker-compose logs -f service-metrics-platform
```

## 服务管理

### 常用命令

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 重启应用服务
docker-compose restart service-metrics-platform

# 查看服务状态
docker-compose ps

# 查看应用日志
docker-compose logs -f service-metrics-platform

# 查看所有服务日志
docker-compose logs -f

# 进入应用容器
docker-compose exec service-metrics-platform bash
```

### 更新部署

```bash
# 1. 停止现有服务
docker-compose down

# 2. 拉取最新代码（如果使用Git）
git pull

# 3. 重新构建和启动
./build-and-deploy.sh prod
```

## 监控和运维

### 日志管理

日志文件位置：
- 应用日志: `./logs/service-metrics-platform.log`
- Nginx日志: Docker volume `nginx_logs`
- MySQL日志: Docker容器内
- ClickHouse日志: Docker volume `clickhouse_logs`

### 性能监控

访问以下端点进行监控：
- 健康检查: `http://your-server:8080/api/order-metrics/health`
- 应用指标: `http://your-server:8080/actuator/metrics`
- 健康状态: `http://your-server:8080/actuator/health`

### 备份策略

#### 数据库备份
```bash
# MySQL备份
docker-compose exec mysql mysqldump -u root -p service_metrics > backup_$(date +%Y%m%d_%H%M%S).sql

# ClickHouse备份
docker-compose exec clickhouse clickhouse-client --query "BACKUP DATABASE default TO Disk('default', 'backup_$(date +%Y%m%d_%H%M%S)')"
```

#### 应用配置备份
```bash
# 备份重要配置文件
tar -czf config_backup_$(date +%Y%m%d_%H%M%S).tar.gz \
    docker-compose.yml \
    src/main/resources/application-prod.yml \
    nginx/
```

## 安全配置

### 防火墙设置
```bash
# 只开放必要端口
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw allow 8080/tcp  # 应用端口（如果需要直接访问）
sudo ufw enable
```

### 数据库安全
- 使用强密码
- 定期更新密码
- 限制数据库访问权限
- 启用SSL连接（生产环境）

### 应用安全
- 定期更新依赖版本
- 使用HTTPS（配置SSL证书）
- 设置合适的CORS策略
- 启用访问日志

## 故障排除

### 常见问题

#### 1. 容器启动失败
```bash
# 查看详细错误信息
docker-compose logs service-metrics-platform

# 检查镜像是否存在
docker images | grep service-metrics-platform

# 重新构建镜像
docker-compose build --no-cache service-metrics-platform
```

#### 2. 数据库连接失败
```bash
# 检查数据库容器状态
docker-compose ps mysql

# 测试数据库连接
docker-compose exec mysql mysql -u root -p

# 检查网络连接
docker network ls
docker network inspect service-metrics-platform_service-metrics-network
```

#### 3. 应用无法访问
```bash
# 检查端口映射
docker-compose ps

# 检查防火墙设置
sudo ufw status

# 测试本地访问
curl http://localhost:8080/api/order-metrics/health
```

#### 4. 内存不足
```bash
# 检查系统资源
free -h
df -h

# 调整JVM参数
# 在docker-compose.yml中修改JAVA_OPTS
```

### 日志分析

```bash
# 应用启动日志
docker-compose logs --tail=50 service-metrics-platform

# 数据库日志
docker-compose logs --tail=50 mysql

# 系统资源使用
docker stats
```

## 扩展配置

### 负载均衡配置

如需配置Nginx负载均衡，创建 `nginx/conf.d/default.conf`：

```nginx
upstream service_metrics_backend {
    server service-metrics-platform:8080;
    # 可以添加更多实例
    # server service-metrics-platform-2:8080;
}

server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://service_metrics_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### SSL/HTTPS配置

```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;

    location / {
        proxy_pass http://service_metrics_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
    }
}
```

## 联系支持

如果在部署过程中遇到问题，请：

1. 查看应用日志：`docker-compose logs -f service-metrics-platform`
2. 检查系统资源：`free -h` 和 `df -h`
3. 验证网络连接：`docker network ls`
4. 提供详细的错误信息和环境描述

---

*最后更新时间：2024-01-15*