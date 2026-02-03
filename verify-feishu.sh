#!/bin/bash

# 飞书导入功能验证脚本
echo "🚀 开始验证飞书导入功能..."

# 检查配置文件
echo "📋 检查配置文件..."
if [ -f "continew-webapi/src/main/resources/config/application-feishu.yml" ]; then
    echo "✅ 飞书配置文件存在"
    grep -q "cli_a9dccc613378dcda" continew-webapi/src/main/resources/config/application-feishu.yml
    if [ $? -eq 0 ]; then
        echo "✅ App ID 配置正确"
    else
        echo "❌ App ID 配置错误"
    fi
else
    echo "❌ 飞书配置文件不存在"
fi

# 检查主配置文件
grep -q "feishu" continew-webapi/src/main/resources/config/application.yml
if [ $? -eq 0 ]; then
    echo "✅ 主配置文件包含飞书配置引用"
else
    echo "❌ 主配置文件缺少飞书配置引用"
fi

# 检查核心文件
echo "🔍 检查核心文件..."
files=(
    "continew-module-education/src/main/java/top/continew/admin/education/config/FeishuConfig.java"
    "continew-module-education/src/main/java/top/continew/admin/education/service/FeishuService.java"
    "continew-module-education/src/main/java/top/continew/admin/education/service/impl/FeishuServiceImpl.java"
    "continew-module-education/src/main/java/top/continew/admin/education/controller/FeishuTestController.java"
)

for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file"
    else
        echo "❌ $file"
    fi
done

# 编译检查
echo "🔨 编译检查..."
mvn clean compile -DskipTests -q
if [ $? -eq 0 ]; then
    echo "✅ 编译成功"
else
    echo "❌ 编译失败"
    exit 1
fi

echo ""
echo "🎉 验证完成！"
echo ""
echo "📝 下一步操作："
echo "1. 启动应用: ./start.sh 或 mvn spring-boot:run"
echo "2. 测试配置: curl http://localhost:8080/api/test/feishu/config"
echo "3. 测试解析: curl 'http://localhost:8080/api/test/feishu/parse-url?url=https://ai.feishu.cn/drive/folder/TEV8fJml3lsaj2dLBOUcxovanvc'"
echo "4. 登录后台管理系统测试导入功能"
echo ""
echo "⚠️  注意: 测试完成后请删除 FeishuTestController.java 文件"
