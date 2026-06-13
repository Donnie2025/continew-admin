#!/bin/bash

# 配置变量
SSH_HOST="tx"
BASE_DIR="/Users/wangdong/Workspace/Young/continew-admin"
REMOTE_BASE="/www/wwwroot/continew-admin"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# 检查文件是否存在
check_file() {
    if [ ! -f "$1" ]; then
        log_error "文件不存在: $1"
        return 1
    fi
    return 0
}

# 检查目录是否存在
check_dir() {
    if [ ! -d "$1" ]; then
        log_error "目录不存在: $1"
        return 1
    fi
    return 0
}

# 上传文件
upload_file() {
    local source=$1
    local dest=$2

    log_info "上传: $source -> ${SSH_HOST}:${dest}"

    if scp "$source" "${SSH_HOST}:${dest}"; then
        log_info "✓ 上传成功"
        return 0
    else
        log_error "✗ 上传失败"
        return 1
    fi
}

# 上传目录下的所有文件
upload_dir() {
    local source=$1
    local dest=$2

    log_info "上传目录: $source -> ${SSH_HOST}:${dest}"

    if scp -r "$source"/* "${SSH_HOST}:${dest}/"; then
        log_info "✓ 目录上传成功"
        return 0
    else
        log_error "✗ 目录上传失败"
        return 1
    fi
}

# 创建远程目录
create_remote_dirs() {
    log_info "确保远程目录存在..."
    ssh "${SSH_HOST}" "mkdir -p ${REMOTE_BASE}/bin ${REMOTE_BASE}/lib ${REMOTE_BASE}/config ${REMOTE_BASE}/cert"
    if [ $? -eq 0 ]; then
        log_info "✓ 远程目录准备完成"
    else
        log_error "✗ 创建远程目录失败"
        exit 1
    fi
}

echo "========================================"
echo "  ContiNew Admin 部署脚本"
echo "========================================"
echo ""

# 定义源文件路径
MAIN_JAR="${BASE_DIR}/continew-webapi/target/app/bin/continew-admin.jar"
COMMON_JAR="${BASE_DIR}/continew-webapi/target/app/lib/continew-common-3.7.0-SNAPSHOT.jar"
SYSTEM_JAR="${BASE_DIR}/continew-webapi/target/app/lib/continew-module-system-3.7.0-SNAPSHOT.jar"
EDUCATION_JAR="${BASE_DIR}/continew-webapi/target/app/lib/continew-module-education-3.7.0-SNAPSHOT.jar"
CONFIG_DIR="${BASE_DIR}/continew-webapi/src/main/resources/config"
CERT_DIR="${BASE_DIR}/continew-webapi/src/main/resources/cert"

# 检查所有文件和目录
log_info "检查本地文件..."
ERRORS=0

check_file "$MAIN_JAR" || ((ERRORS++))
check_file "$COMMON_JAR" || ((ERRORS++))
check_file "$SYSTEM_JAR" || ((ERRORS++))
check_file "$EDUCATION_JAR" || ((ERRORS++))
check_dir "$CONFIG_DIR" || ((ERRORS++))
check_dir "$CERT_DIR" || ((ERRORS++))

if [ $ERRORS -gt 0 ]; then
    log_error "发现 $ERRORS 个文件或目录不存在，请检查路径"
    exit 1
fi

log_info "✓ 所有本地文件检查通过"
echo ""

# 测试 SSH 连接
log_info "测试 SSH 连接..."
if ! ssh -o ConnectTimeout=5 "${SSH_HOST}" "echo 'SSH 连接成功'" > /dev/null 2>&1; then
    log_error "无法连接到 ${SSH_HOST}，请检查 SSH 配置"
    exit 1
fi
log_info "✓ SSH 连接正常"
echo ""

# 创建远程目录
create_remote_dirs
echo ""

# 开始上传
log_info "开始上传文件..."
echo ""

UPLOAD_ERRORS=0

# 1. 上传主 JAR
upload_file "$MAIN_JAR" "${REMOTE_BASE}/bin/" || ((UPLOAD_ERRORS++))
echo ""

# 2. 上传模块 JAR 文件（重要：common必须先上传，因为其他模块依赖它）
upload_file "$COMMON_JAR" "${REMOTE_BASE}/lib/" || ((UPLOAD_ERRORS++))
echo ""

upload_file "$SYSTEM_JAR" "${REMOTE_BASE}/lib/" || ((UPLOAD_ERRORS++))
echo ""

upload_file "$EDUCATION_JAR" "${REMOTE_BASE}/lib/" || ((UPLOAD_ERRORS++))
echo ""

# 3. 上传配置文件
upload_dir "$CONFIG_DIR" "${REMOTE_BASE}/config" || ((UPLOAD_ERRORS++))
echo ""

# 4. 上传证书文件
upload_dir "$CERT_DIR" "${REMOTE_BASE}/cert" || ((UPLOAD_ERRORS++))
echo ""

# 输出结果
echo "========================================"
if [ $UPLOAD_ERRORS -eq 0 ]; then
    log_info "✓ 所有文件上传完成！"
    echo ""
    log_info "远程文件位置："
    echo "  - 主程序: ${REMOTE_BASE}/bin/continew-admin.jar"
    echo "  - 核心模块: ${REMOTE_BASE}/lib/continew-common-3.7.0-SNAPSHOT.jar"
    echo "  - 系统模块: ${REMOTE_BASE}/lib/continew-module-system-3.7.0-SNAPSHOT.jar"
    echo "  - 教育模块: ${REMOTE_BASE}/lib/continew-module-education-3.7.0-SNAPSHOT.jar"
    echo "  - 配置文件: ${REMOTE_BASE}/config/"
    echo "  - 证书文件: ${REMOTE_BASE}/cert/"
    echo ""
    log_info "下一步："
    echo "  1. SSH连接到服务器: ssh ${SSH_HOST}"
    echo "  2. 进入目录: cd ${REMOTE_BASE}"
    echo "  3. 重启服务: ./restart.sh"
else
    log_error "上传过程中发生 $UPLOAD_ERRORS 个错误"
    exit 1
fi
echo "========================================"
