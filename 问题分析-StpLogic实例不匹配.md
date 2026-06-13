# 【问题确认】微信登录后支付提示"需要登录"完整分析报告

## 📊 问题现象

**Token**: `ac44251e-fde2-4e00-8cf6-5a3d786778f4`
- 长度：36字符
- 格式：UUID标准格式（Sa-Token默认）
- 失败接口：`/api/wechat/oauth/jssdk-signature` 返回401

## 🎯 根本原因（已确认）

### **StpLogic实例不匹配导致Session隔离**

系统有两个独立的Sa-Token实例：

#### 1. **StpUtil**（默认实例，用于后台管理）
- Type: "login"（默认）
- 用于：后台管理系统的用户认证

#### 2. **StpMiniUtil**（小程序专用实例）
- Type: "mini"（自定义）
- 用于：小程序/H5用户认证

**代码证据**：

**StpMiniUtil.java 第35行**：
```java
stpLogic = new StpLogic("mini");  // 独立实例，type="mini"
```

**SaTokenConfiguration.java 第98行**：
```java
@Bean
public SaInterceptor saInterceptor() {
    return new SaExtensionInterceptor(handle -> SaRouter.match("/**")
        .notMatch(excludes)
        .check(r -> {
            // ⚠️ 问题所在：使用默认的StpUtil验证
            StpUtil.checkLogin();  // 这是type="login"的实例
        }));
}
```

**登录流程（MiniAuthServiceImpl.java 第803-810行）**：
```java
// 微信公众号登录使用StpMiniUtil
StpMiniUtil.login(student.getId(), "mp");  // type="mini"
StpMiniUtil.getSession().set(SaSession.USER, userContext);
String token = StpMiniUtil.getTokenValue();  // 生成UUID token
```

### 问题流程图

```
用户微信登录
    ↓
StpMiniUtil.login()  [type="mini"]
    ↓
保存Session到 Redis:satoken:mini:session:xxx
    ↓
返回Token: ac44251e-fde2-4e00-8cf6-5a3d786778f4
    ↓
用户访问支付接口
    ↓
SaInterceptor 拦截器
    ↓
StpUtil.checkLogin()  [type="login"]  ⚠️
    ↓
查找 Redis:satoken:login:session:xxx  ❌ 找不到
    ↓
返回401错误：未登录
```

**两个实例的Redis Key不同**：
- StpMiniUtil: `satoken:mini:session:token值`
- StpUtil: `satoken:login:session:token值`

## 🔧 解决方案

### 方案1：修改拦截器，区分不同类型的请求（推荐）

**修改文件**：`SaTokenConfiguration.java`

```java
@Bean
public SaInterceptor saInterceptor() {
    return new SaExtensionInterceptor(handle -> SaRouter.match("/**")
        .notMatch(excludes)
        .check(r -> {
            SaRequest saRequest = SaHolder.getRequest();
            String requestPath = saRequest.getRequestPath();
            
            // 如果包含 sign，进行 API 接口参数签名验证
            if (paramNames.stream().anyMatch(SaSignTemplate.sign::equals)) {
                SaSignUtil.checkRequest(saRequest);
                return;
            }
            
            // ✅ 新增：判断请求路径，使用对应的StpLogic
            if (requestPath.startsWith("/api/mini/") || 
                requestPath.startsWith("/api/wechat/") || 
                requestPath.startsWith("/api/payment/")) {
                // 小程序/H5相关接口，使用StpMiniUtil
                StpMiniUtil.checkLogin();
            } else {
                // 后台管理接口，使用默认StpUtil
                StpUtil.checkLogin();
            }
            
            if (SaRouter.isMatchCurrURI(loginPasswordProperties.getExcludes())) {
                return;
            }
            UserContext userContext = UserContextHolder.getContext();
            CheckUtils.throwIf(userContext.isPasswordExpired(), "密码已过期，请修改密码");
        }));
}
```

### 方案2：在WechatOAuthController中添加@SaCheckLogin注解

**修改文件**：`WechatOAuthController.java`

```java
@GetMapping("/jssdk-signature")
@SaCheckLogin(type = "mini")  // ✅ 指定使用mini类型的StpLogic
public R<Object> getJsSdkSignature(@RequestParam String url) {
    log.info("获取JS-SDK签名，url: {}", url);
    // ... 现有代码
}
```

但这个方法需要每个接口都添加注解，不够优雅。

### 方案3：修改全局拦截器，自动识别Token类型

**创建新的拦截器**：`SmartSaInterceptor.java`

```java
@Component
public class SmartSaInterceptor extends SaInterceptor {
    
    @Override
    public void check(SaRequest request) {
        String token = request.getHeader("Authorization");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            
            // 尝试用StpMiniUtil验证
            try {
                if (StpMiniUtil.getStpLogic().getLoginIdByToken(token) != null) {
                    // Token属于mini类型，使用StpMiniUtil验证
                    StpMiniUtil.checkLogin();
                    return;
                }
            } catch (Exception ignored) {
                // Token不属于mini类型，继续用默认验证
            }
        }
        
        // 使用默认StpUtil验证
        StpUtil.checkLogin();
    }
}
```

## 📋 推荐实施步骤

### 立即修复（方案1）

1. **修改 `SaTokenConfiguration.java`**
   ```java
   // 在第98行修改
   if (requestPath.startsWith("/api/mini/") || 
       requestPath.startsWith("/api/wechat/") || 
       requestPath.startsWith("/api/payment/")) {
       StpMiniUtil.checkLogin();
   } else {
       StpUtil.checkLogin();
   }
   ```

2. **重启后端服务**

3. **测试验证**
   - 微信登录
   - 点击调试按钮查看Token
   - 进入支付页面
   - 点击支付按钮

### 验证清单

- [ ] 微信登录成功，获取36字符UUID token
- [ ] Token保存到前端Storage
- [ ] 访问 `/api/wechat/oauth/jssdk-signature` 不再返回401
- [ ] 访问 `/api/payment/wechat/jsapi` 不再返回401
- [ ] 支付流程正常

## 💡 为什么Token是36字符？

**这是Sa-Token的正常行为**：

- Sa-Token默认使用UUID作为Token
- UUID标准格式：`xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`（36字符）
- **这不是Bug！**

虽然配置文件中有：
```yaml
sa-token:
  extension:
    enableJwt: true  # 启用JWT
```

但实际上Sa-Token的JWT功能需要额外配置才会生效，当前仍然使用UUID模式。

## 📝 总结

**问题**：StpLogic实例不匹配
- 登录用：`StpMiniUtil` (type="mini")
- 验证用：`StpUtil` (type="login")
- 结果：Session隔离，验证失败

**解决**：修改拦截器，根据请求路径使用对应的StpLogic实例

**不是问题**：
- ✅ Token格式正确（UUID是Sa-Token默认格式）
- ✅ UserContext正确保存（包含openid）
- ✅ Session正确存储到Redis

---
**更新时间**: 2026-06-13
**严重程度**: 🔴 高（阻塞支付功能）
**预计修复时间**: 15分钟（修改一处代码）
**测试时间**: 10分钟
