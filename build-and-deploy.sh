#!/bin/bash

# Service Metrics Platform 构建和部署脚本
# 使用方法: ./build-and-deploy.sh [环境]
# 环境选项: dev, test, prod (默认为prod)

set -e  # 遇到错误立即退出

# 获取环境参数，默认为prod
ENVIRONMENT=${1:-prod}
PROJECT_NAME="service-metrics-platform"
VERSION="0.0.1-SNAPSHOT"
IMAGE_NAME="${PROJECT_NAME}:${VERSION}"

echo "=================================="
echo "Service Metrics Platform 部署脚本"
echo "环境: ${ENVIRONMENT}"
echo "镜像: ${IMAGE_NAME}"
echo "=================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Docker是否运行
check_docker() {
    log_info "检查Docker状态..."
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker未运行，请启动Docker后重试"
        exit 1
    fi
    log_info "Docker运行正常"
}

# 检查Java环境
check_java() {
    log_info "检查Java环境..."
    if ! java -version > /dev/null 2>&1; then
        log_error "Java未安装或未配置PATH"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
    log_info "Java版本: ${JAVA_VERSION}"
    
    if [[ ! "$JAVA_VERSION" =~ ^21\. ]]; then
        log_warn "建议使用Java 21，当前版本: ${JAVA_VERSION}"
    fi
}

# 检查Maven环境
check_maven() {
    log_info "检查Maven环境..."
    if ! mvn -version > /dev/null 2>&1; then
        log_error "Maven未安装或未配置PATH"
        exit 1
    fi
    
    MVN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    log_info "Maven版本: ${MVN_VERSION}"
}

# 清理旧的构建产物
clean_build() {
    log_info "清理旧的构建产物..."
    mvn clean
    
    # 清理Docker镜像（可选）
    if [[ "$2" == "--clean-docker" ]]; then
        log_info "清理旧的Docker镜像..."
        docker rmi ${IMAGE_NAME} 2>/dev/null || true
    fi
}

# 运行测试
run_tests() {
    if [[ "$ENVIRONMENT" != "prod" ]]; then
        log_info "运行单元测试..."
        mvn test
    else
        log_info "生产环境构建，跳过测试"
    fi
}

# Maven构建
build_jar() {
    log_info "开始Maven构建..."
    
    if [[ "$ENVIRONMENT" == "prod" ]]; then
        mvn clean package -DskipTests -Pprod
    else
        mvn clean package
    fi
    
    # 检查jar文件是否生成
    JAR_FILE="target/${PROJECT_NAME}-${VERSION}.jar"
    if [[ ! -f "$JAR_FILE" ]]; then
        log_error "JAR文件构建失败: $JAR_FILE"
        exit 1
    fi
    
    log_info "JAR文件构建成功: $JAR_FILE"
}

# 构建Docker镜像
build_docker_image() {
    log_info "构建Docker镜像..."
    
    # 设置构建参数
    BUILD_ARGS=""
    if [[ "$ENVIRONMENT" == "prod" ]]; then
        BUILD_ARGS="--build-arg SPRING_PROFILES_ACTIVE=prod"
    fi
    
    # 构建镜像
    docker build ${BUILD_ARGS} -t ${IMAGE_NAME} .
    
    # 验证镜像是否构建成功
    if docker images | grep -q "${PROJECT_NAME}"; then
        log_info "Docker镜像构建成功"
        docker images | grep "${PROJECT_NAME}"
    else
        log_error "Docker镜像构建失败"
        exit 1
    fi
}

# 运行容器（开发/测试环境）
run_container() {
    log_info "启动容器..."
    
    # 停止现有容器
    docker stop ${PROJECT_NAME} 2>/dev/null || true
    docker rm ${PROJECT_NAME} 2>/dev/null || true
    
    # 创建日志目录
    mkdir -p ./logs
    
    # 运行新容器
    docker run -d \
        --name ${PROJECT_NAME} \
        -p 8080:8080 \
        -e SPRING_PROFILES_ACTIVE=${ENVIRONMENT} \
        -v $(pwd)/logs:/app/logs \
        ${IMAGE_NAME}
    
    log_info "容器启动成功，容器名称: ${PROJECT_NAME}"
    log_info "应用访问地址: http://localhost:8080"
    log_info "健康检查地址: http://localhost:8080/api/order-metrics/health"
}

# 使用Docker Compose部署（生产环境）
deploy_with_compose() {
    log_info "使用Docker Compose部署..."
    
    # 检查docker-compose.yml是否存在
    if [[ ! -f "docker-compose.yml" ]]; then
        log_error "docker-compose.yml文件不存在"
        exit 1
    fi
    
    # 停止现有服务
    log_info "停止现有服务..."
    docker-compose down
    
    # 启动服务
    log_info "启动所有服务..."
    docker-compose up -d
    
    # 检查服务状态
    log_info "检查服务状态..."
    docker-compose ps
    
    log_info "部署完成！"
    log_info "应用访问地址: http://localhost:8080"
    log_info "查看日志: docker-compose logs -f service-metrics-platform"
}

# 显示部署信息
show_deploy_info() {
    echo ""
    echo "=================================="
    echo "部署完成信息"
    echo "=================================="
    echo "环境: ${ENVIRONMENT}"
    echo "镜像: ${IMAGE_NAME}"
    echo "应用地址: http://localhost:8080"
    echo "健康检查: http://localhost:8080/api/order-metrics/health"
    echo ""
    echo "常用命令:"
    echo "  查看容器状态: docker ps"
    echo "  查看应用日志: docker logs ${PROJECT_NAME}"
    echo "  停止容器: docker stop ${PROJECT_NAME}"
    echo "  删除容器: docker rm ${PROJECT_NAME}"
    echo ""
    if [[ "$ENVIRONMENT" == "prod" ]]; then
        echo "生产环境部署:"
        echo "  查看所有服务: docker-compose ps"
        echo "  查看日志: docker-compose logs -f"
        echo "  停止服务: docker-compose down"
    fi
    echo "=================================="
}

# 主函数
main() {
    # 环境检查
    check_docker
    check_java
    check_maven
    
    # 构建流程
    clean_build
    run_tests
    build_jar
    build_docker_image
    
    # 部署流程
    if [[ "$ENVIRONMENT" == "prod" ]]; then
        deploy_with_compose
    else
        run_container
    fi
    
    # 显示部署信息
    show_deploy_info
}

# 帮助信息
show_help() {
    echo "Service Metrics Platform 构建和部署脚本"
    echo ""
    echo "使用方法:"
    echo "  $0 [环境] [选项]"
    echo ""
    echo "环境选项:"
    echo "  dev     开发环境（默认）"
    echo "  test    测试环境"
    echo "  prod    生产环境"
    echo ""
    echo "选项:"
    echo "  --help          显示帮助信息"
    echo "  --clean-docker  清理旧的Docker镜像"
    echo ""
    echo "示例:"
    echo "  $0 prod                    # 生产环境部署"
    echo "  $0 dev --clean-docker      # 开发环境部署并清理旧镜像"
    echo ""
}

# 处理命令行参数
case "${1}" in
    --help|-h)
        show_help
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac