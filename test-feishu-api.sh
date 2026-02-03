#!/bin/bash

# 飞书API测试脚本
echo "🔍 测试飞书API调用..."

APP_ID="cli_a9dccc613378dcda"
APP_SECRET="86bUOn7UMgkzHpAMpxhkwcqKObR56Uhd"
BASE_URL="https://open.feishu.cn"

echo "📋 配置信息:"
echo "App ID: $APP_ID"
echo "Base URL: $BASE_URL"
echo ""

# 1. 测试获取访问令牌
echo "🔑 测试获取访问令牌..."
TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/open-apis/auth/v3/tenant_access_token/internal" \
  -H "Content-Type: application/json" \
  -d "{\"app_id\":\"$APP_ID\",\"app_secret\":\"$APP_SECRET\"}")

echo "令牌响应: $TOKEN_RESPONSE"

# 解析访问令牌
ACCESS_TOKEN=$(echo $TOKEN_RESPONSE | grep -o '"tenant_access_token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$ACCESS_TOKEN" ]; then
    echo "❌ 获取访问令牌失败"
    exit 1
else
    echo "✅ 访问令牌获取成功: ${ACCESS_TOKEN:0:20}..."
fi

echo ""

# 2. 测试文件夹文件列表API
FOLDER_TOKEN="TEV8fJml3lsaj2dLBOUcxovanvc"
echo "📁 测试获取文件夹文件列表..."
echo "文件夹Token: $FOLDER_TOKEN"

FILES_URL="$BASE_URL/open-apis/drive/v1/files?folder_token=$FOLDER_TOKEN&page_size=200&order_by=EditedTime&direction=DESC"
echo "请求URL: $FILES_URL"

FILES_RESPONSE=$(curl -s -X GET "$FILES_URL" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json")

echo "文件列表响应: $FILES_RESPONSE"

# 检查响应
if echo "$FILES_RESPONSE" | grep -q '"code":0'; then
    echo "✅ 文件列表获取成功"
    
    # 提取文件数量
    FILE_COUNT=$(echo "$FILES_RESPONSE" | grep -o '"files":\[.*\]' | grep -o '"name":' | wc -l)
    echo "📊 文件数量: $FILE_COUNT"
    
    # 提取第一个文件的token（如果有）
    FIRST_FILE_TOKEN=$(echo "$FILES_RESPONSE" | grep -o '"token":"[^"]*"' | head -1 | cut -d'"' -f4)
    if [ ! -z "$FIRST_FILE_TOKEN" ]; then
        echo "📄 第一个文件Token: $FIRST_FILE_TOKEN"
        
        # 3. 测试文件下载链接API
        echo ""
        echo "⬇️ 测试获取文件下载链接..."
        DOWNLOAD_URL="$BASE_URL/open-apis/drive/v1/files/$FIRST_FILE_TOKEN/download"
        echo "下载API URL: $DOWNLOAD_URL"
        
        DOWNLOAD_RESPONSE=$(curl -s -X GET "$DOWNLOAD_URL" \
          -H "Authorization: Bearer $ACCESS_TOKEN")
        
        echo "下载链接响应: $DOWNLOAD_RESPONSE"
        
        if echo "$DOWNLOAD_RESPONSE" | grep -q '"code":0'; then
            echo "✅ 下载链接获取成功"
        else
            echo "❌ 下载链接获取失败"
        fi
    fi
else
    echo "❌ 文件列表获取失败"
    echo "错误信息: $(echo "$FILES_RESPONSE" | grep -o '"msg":"[^"]*"' | cut -d'"' -f4)"
fi

echo ""
echo "🎯 测试完成！"
echo ""
echo "📝 如果测试失败，请检查："
echo "1. 网络连接是否正常"
echo "2. 飞书应用权限是否正确配置"
echo "3. 文件夹链接是否有效"
echo "4. App ID和App Secret是否正确"
