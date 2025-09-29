# 使用OpenJDK 21作为基础镜像
FROM openjdk:21-jdk-slim

LABEL maintainer="service-metrics-platform" \
      version="0.0.1-SNAPSHOT" \
      description="Service Metrics Platform - 微信服务指标分析平台"

WORKDIR /app

# 安装必要的工具
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl wget vim \
 && rm -rf /var/lib/apt/lists/*

# 创建应用用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# JVM 参数 & Spring 配置
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=9712

# 复制 jar
COPY target/service-metrics-platform-0.0.1-SNAPSHOT.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

USER appuser

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT}/api/order-metrics/health || exit 1

EXPOSE 9712

# 启动应用
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar