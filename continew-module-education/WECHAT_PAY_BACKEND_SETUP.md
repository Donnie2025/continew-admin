# 微信支付后端配置完成说明

## 已完成的工作

### 1. 依赖配置 ✅
- 在 `continew-module-education/pom.xml` 中添加了：
  - 微信支付SDK：`wechatpay-java` v0.2.12
  - 微信公众号SDK：`weixin-java-mp` v4.6.0

### 2. 配置文件 ✅
- 创建了 `application-wechat.yml` 配置文件
- 在 `application.yml` 中添加了 `wechat` profile

### 3. 配置类 ✅
- `WechatPayConfig.java` - 微信支付配置
- `WechatMpConfig.java` - 微信公众号配置

### 4. 模型类 ✅
- `CreatePaymentReq.java` - 创建支付请求
- `WechatPaymentResp.java` - 支付参数响应

### 5. 服务层 ✅
- `WechatPayService.java` - 支付服务接口
- `WechatPayServiceImpl.java` - 支付服务实现
  - 创建JSAPI支付订单
  - 处理支付回调通知
  - 查询订单支付状态
  - 关闭订单

### 6. 控制器 ✅
- `WechatPaymentController.java` - 支付API接口
  - POST `/api/payment/wechat/jsapi` - 创建支付订单
  - POST `/api/payment/wechat/notify` - 支付回调通知
  - GET `/api/payment/wechat/status/{orderNo}` - 查询支付状态
  - POST `/api/payment/wechat/close/{orderNo}` - 关闭订单

### 7. 证书目录 ✅
- 创建了 `src/main/resources/cert/` 目录

---

## 你需要完成的配置

### 步骤 1：获取微信商户信息

登录微信商户平台：https://pay.weixin.qq.com

1. **获取商户号（mch-id）**
   - 位置：账户中心 -> 商户信息
   - 格式：10位数字，例如：1234567890

2. **获取API密钥（api-v3-key）**
   - 位置：账户中心 -> API安全 -> APIv3密钥
   - 如果没有设置过，点击"设置密钥"
   - 格式：32位字符串

3. **下载商户证书**
   - 位置：账户中心 -> API安全 -> 申请API证书
   - 下载后会得到：
     - `apiclient_cert.pem` (商户证书)
     - `apiclient_key.pem` (商户私钥) **← 重要**
     - `apiclient_cert.p12` (证书包)
   - 将 `apiclient_key.pem` 文件复制到：
     ```
     /Users/wangdong/Workspace/Young/continew-admin/continew-module-education/src/main/resources/cert/apiclient_key.pem
     ```

4. **获取证书序列号（merchant-serial-number）**
   - 位置：账户中心 -> API安全 -> 查看证书
   - 格式：16进制字符串，例如：ABCDEF1234567890

### 步骤 2：获取公众号信息

登录微信公众平台：https://mp.weixin.qq.com

1. **获取AppId**
   - 位置：开发 -> 基本配置 -> 开发者ID(AppID)
   - 格式：以 `wx` 开头，例如：wx1234567890abcdef

2. **获取AppSecret**
   - 位置：开发 -> 基本配置 -> 开发者密码(AppSecret)
   - 如果忘记了，可以点击"重置"（重置后需要重新配置）
   - 格式：32位字符串

### 步骤 3：配置支付授权目录

在微信商户平台：

1. 进入：产品中心 -> 开发配置 -> 支付配置
2. 添加支付授权目录：
   ```
   https://你的域名/pages/
   ```
   注意：必须以 `/` 结尾

### 步骤 4：配置回调域名

在微信商户平台：

1. 进入：产品中心 -> 开发配置 -> 支付配置
2. 配置支付回调URL：
   ```
   https://你的域名/api/payment/wechat/notify
   ```

### 步骤 5：配置OAuth2授权回调域名

在微信公众平台：

1. 进入：设置与开发 -> 公众号设置 -> 功能设置
2. 配置网页授权域名：
   ```
   你的域名（不带https://）
   例如：yourdomain.com
   ```

### 步骤 6：修改配置文件

编辑 `application-wechat.yml`，将占位符替换为实际值：

```yaml
wechat:
  pay:
    mch-id: 你的商户号
    api-v3-key: 你的APIv3密钥
    merchant-serial-number: 你的证书序列号
    private-key-path: classpath:cert/apiclient_key.pem
    notify-url: https://你的域名/api/payment/wechat/notify

  mp:
    app-id: 你的公众号AppId
    secret: 你的公众号AppSecret
    oauth-redirect-url: https://你的域名/api/wechat/oauth/callback
```

**安全建议：生产环境使用环境变量**

不要在配置文件中直接写明文密钥，使用环境变量：

```yaml
wechat:
  pay:
    mch-id: ${WECHAT_MCH_ID}
    api-v3-key: ${WECHAT_API_V3_KEY}
    # ...其他配置
```

然后在服务器上设置环境变量：
```bash
export WECHAT_MCH_ID=1234567890
export WECHAT_API_V3_KEY=your-api-v3-key
# ...其他环境变量
```

### 步骤 7：更新订单表结构

确认 `edu_order` 表包含以下字段：

```sql
ALTER TABLE edu_order ADD COLUMN IF NOT EXISTS payment_time DATETIME COMMENT '支付时间';
ALTER TABLE edu_order ADD COLUMN IF NOT EXISTS transaction_id VARCHAR(64) COMMENT '微信支付交易号';
```

可以将微信交易号保存到 `remark` 字段，或者新增一个 `transaction_id` 字段。

---

## 测试流程

### 1. 本地测试准备

由于微信支付回调需要公网地址，本地测试有两种方案：

**方案A：使用内网穿透工具（推荐）**
- 使用 ngrok、花生壳等工具将本地服务映射到公网
- 将临时域名配置到微信商户平台

**方案B：直接部署到测试服务器**
- 将代码部署到有公网IP的测试服务器
- 配置域名和HTTPS证书

### 2. 启动服务

```bash
cd /Users/wangdong/Workspace/Young/continew-admin
mvn clean package
java -jar continew-webapi/target/continew-webapi.jar
```

### 3. 测试API

使用Postman或curl测试支付接口：

```bash
# 创建支付订单
curl -X POST http://localhost:8080/api/payment/wechat/jsapi \
  -H "Content-Type: application/json" \
  -d '{
    "orderNo": "TEST20260605001",
    "openid": "用户的openid",
    "amount": 100,
    "description": "测试订单"
  }'
```

### 4. 查看日志

观察控制台日志，检查：
- 配置是否正确加载
- API调用是否成功
- 回调是否收到并处理

---

## 常见问题排查

### 问题1：启动时报证书路径错误
**原因**：`apiclient_key.pem` 文件不存在
**解决**：检查证书文件是否放在正确位置

### 问题2：创建订单时报"appid与mchid不匹配"
**原因**：公众号AppId和商户号不是关联的
**解决**：在商户平台"产品中心"->"AppID账号管理"中关联公众号

### 问题3：收不到支付回调
**原因**：回调地址不是公网HTTPS地址
**解决**：
1. 检查 `notify-url` 配置是否正确
2. 确保服务器可以被外网访问
3. 查看商户平台"交易中心"->"支付通知"的回调记录

### 问题4：回调验签失败
**原因**：证书配置错误或过期
**解决**：
1. 检查 `merchant-serial-number` 是否正确
2. 检查证书是否过期
3. 重新下载证书并配置

---

## 下一步：前端集成

后端配置完成后，需要继续完成前端集成：

1. 实现获取用户OpenId的流程（OAuth授权）
2. 修改支付页面，调用后端API
3. 处理支付结果
4. 显示支付状态

详细的前端配置请参考：`/Users/wangdong/Workspace/Young/continew-uniapp/WECHAT_PAY_SETUP.md`

---

## 联系支持

如有问题，可以：
1. 查看微信支付官方文档：https://pay.weixin.qq.com/wiki/doc/apiv3/index.shtml
2. 查看SDK文档：https://github.com/wechatpay-apiv3/wechatpay-java
3. 联系微信支付技术支持：https://kf.qq.com/touch/sappfaq/220420yqaU7r220420U73eAR.html
