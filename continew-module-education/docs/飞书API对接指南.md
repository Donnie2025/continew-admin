# 飞书API对接指南

## 📋 概述

本指南详细介绍如何将课节管理系统与飞书云文档进行对接，实现从飞书文件夹批量导入课节的功能。

## 🔧 1. 飞书开发者配置

### 1.1 创建飞书应用

1. 访问 [飞书开放平台](https://open.feishu.cn/)
2. 登录并进入开发者后台
3. 创建企业自建应用或第三方应用
4. 记录应用的 `App ID` 和 `App Secret`

### 1.2 配置应用权限

在应用管理页面，添加以下权限：

**云文档权限**：
- `drive:drive` - 云文档基础权限
- `drive:file` - 文件读取权限
- `drive:folder` - 文件夹访问权限

**API权限**：
- `/open-apis/drive/v1/files` - 获取文件列表
- `/open-apis/drive/v1/files/{file_token}/download` - 下载文件

### 1.3 配置回调地址（可选）

如果需要OAuth授权流程，配置回调地址：
```
https://your-domain.com/api/feishu/callback
```

## 🏗️ 2. 后端集成

### 2.1 配置文件

在 `application.yml` 中添加飞书配置：

```yaml
feishu:
  app-id: cli_xxxxxxxxxxxxxxxxx
  app-secret: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
  base-url: https://open.feishu.cn
```

### 2.2 核心组件

已实现的核心组件：

1. **FeishuConfig** - 飞书配置类
2. **FeishuService** - 飞书服务接口
3. **FeishuServiceImpl** - 飞书服务实现（使用RestTemplate）
4. **MaterialLessonServiceImpl** - 集成飞书导入功能

### 2.3 API调用流程

```
1. 获取访问令牌 (tenant_access_token)
   ↓
2. 解析飞书分享链接，提取文件夹token
   ↓
3. 调用文件列表API获取文件夹下的所有文件
   ↓
4. 过滤PDF文件
   ↓
5. 为每个文件生成下载链接
   ↓
6. 批量创建课节记录
```

## 🔗 3. 飞书API详解

### 3.1 获取访问令牌

**接口**：`POST /open-apis/auth/v3/tenant_access_token/internal`

**请求参数**：
```json
{
  "app_id": "cli_xxxxxxxxxxxxxxxxx",
  "app_secret": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
}
```

**响应示例**：
```json
{
  "code": 0,
  "msg": "success",
  "tenant_access_token": "t-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
  "expire": 7200
}
```

### 3.2 获取文件夹文件列表

**接口**：`GET /open-apis/drive/v1/files`

**请求参数**：
- `folder_token`: 文件夹token
- `page_size`: 每页大小（最大200）
- `page_token`: 分页token（可选）

**请求头**：
```
Authorization: Bearer t-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
Content-Type: application/json
```

**响应示例**：
```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "files": [
      {
        "token": "doccnxxxxxxxxxxxxxxxxxxxxxxxxxx",
        "name": "01, K1U1L1 Hello.pdf",
        "type": "pdf",
        "size": 1024000,
        "url": "https://example.com/file/download"
      }
    ],
    "has_more": false,
    "next_page_token": ""
  }
}
```

### 3.3 获取文件下载链接

**接口**：`GET /open-apis/drive/v1/files/{file_token}/download`

**请求头**：
```
Authorization: Bearer t-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

**响应**：
- 成功：返回文件流或下载链接
- 失败：返回错误信息

## 🎯 4. 使用方法

### 4.1 获取飞书分享链接

1. 在飞书云文档中打开目标文件夹
2. 点击"分享"按钮
3. 选择"复制链接"
4. 获得类似格式的链接：
   ```
   https://ai.feishu.cn/drive/folder/TEV8fJml3lsaj2dLBOUcxovanvc
   ```

### 4.2 在系统中导入

1. 登录课节管理系统
2. 进入课节管理页面
3. 点击"从飞书导入"按钮
4. 选择目标教材
5. 粘贴飞书文件夹链接
6. 配置导入选项（是否覆盖等）
7. 点击确定开始导入
8. 查看导入结果统计

## ⚠️ 5. 注意事项

### 5.1 权限要求

- 飞书应用必须有相应的API权限
- 文件夹必须对应用可见（公开分享或应用有访问权限）
- 确保App ID和App Secret正确配置

### 5.2 文件格式

- 当前只支持PDF文件导入
- 文件名将作为课节名称
- 支持中文文件名

### 5.3 性能考虑

- 大文件夹可能导入时间较长
- 建议分批导入，避免一次性导入过多文件
- 访问令牌有效期为2小时，系统会自动刷新

### 5.4 错误处理

常见错误及解决方案：

1. **权限不足**：检查应用权限配置
2. **链接无效**：确认飞书链接格式正确
3. **文件夹为空**：检查文件夹是否包含PDF文件
4. **网络超时**：检查网络连接和防火墙设置

## 🔧 6. 高级配置

### 6.1 自定义文件存储

可以修改 `generateTempDownloadUrl` 方法，将文件保存到：

- 阿里云OSS
- 腾讯云COS
- 本地文件系统
- 其他云存储服务

### 6.2 支持更多文件格式

在 `getFolderFiles` 方法中修改文件类型过滤条件：

```java
// 支持更多格式
if ("pdf".equalsIgnoreCase(fileType) || 
    "doc".equalsIgnoreCase(fileType) ||
    "docx".equalsIgnoreCase(fileType) ||
    fileName.toLowerCase().matches(".*\\.(pdf|doc|docx|ppt|pptx)$")) {
    // 处理文件
}
```

### 6.3 批量处理优化

对于大量文件的处理，可以考虑：

- 异步处理
- 分页导入
- 进度回调
- 断点续传

## 📞 7. 技术支持

如果在对接过程中遇到问题：

1. 查看系统日志获取详细错误信息
2. 检查飞书开放平台的API文档
3. 验证应用权限和配置
4. 测试网络连接和API可达性

## 🔄 8. 更新日志

- **v1.0.0** - 初始版本，支持基础的文件夹导入功能
- **v1.1.0** - 优化错误处理和日志记录
- **v1.2.0** - 支持文件覆盖和跳过选项

---

**注意**：本指南基于飞书开放平台API v1版本编写，如API有更新请参考最新官方文档。
