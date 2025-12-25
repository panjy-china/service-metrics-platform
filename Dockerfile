# 使用本地已有的 Alpine + OpenJDK 21 镜像
FROM alpine/java:21-jdk

LABEL maintainer="service-metrics-platform" \
      version="0.0.1-SNAPSHOT" \
      description="Service Metrics Platform - 微信服务指标分析平台"

WORKDIR /app

# 使用国内源加速 apk 安装（可选但推荐）
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

# 不再需要 curl（因为去掉了健康检查），所以可以完全不安装任何额外工具
# 如果后续有其他需求（如调试），可按需添加

# 创建应用用户
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# 设置时区
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

# JVM & Spring 配置
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=9712

# 复制 JAR
COPY target/service-metrics-platform-0.0.1-SNAPSHOT.jar app.jar

# 创建日志目录并授权
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

USER appuser

# 注意：HEALTHCHECK 已被移除！

EXPOSE ${SERVER_PORT}

# 启动应用
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]