# 使用OpenJDK 21作为基础镜像
FROM openjdk:21-jdk-slim

# 设置维护者信息
LABEL maintainer="service-metrics-platform"
LABEL version="0.0.1-SNAPSHOT"
LABEL description="Service Metrics Platform - 微信服务指标分析平台"

# 设置工作目录
WORKDIR /app

# 安装必要的系统依赖
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    vim \
    && rm -rf /var/lib/apt/lists/*

# 创建应用用户（安全最佳实践）
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 设置时区为上海时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

# 设置Spring Boot配置
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=9712

# 复制Maven构建的jar文件
COPY target/service-metrics-platform-0.0.1-SNAPSHOT.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# 切换到应用用户
USER appuser

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT}/api/order-metrics/health || exit 1

# 暴露端口
EXPOSE 9712

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]