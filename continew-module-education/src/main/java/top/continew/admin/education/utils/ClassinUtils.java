package top.continew.admin.education.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import top.continew.admin.education.config.ClassinProperties;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.model.resp.classin.ClassinBaseResp;
import top.continew.admin.education.model.resp.classin.ClassinErrorInfo;
import top.continew.starter.core.exception.BusinessException;

/**
 * ClassIn 工具类
 *
 * @author don
 * @since 2025/06/21
 */
@Slf4j
public class ClassinUtils {

    /**
     * 构建公共请求参数
     *
     * @param properties ClassIn配置
     * @return 包含公共参数的JSONObject
     */
    public static JSONObject buildCommonParams(ClassinProperties properties) {
        long timeStamp = System.currentTimeMillis() / 1000;
        String safeKey = DigestUtils.md5Hex(properties.getAppSecret() + timeStamp);

        JSONObject params = new JSONObject();
        params.set("SID", properties.getAppId());
        params.set("safeKey", safeKey);
        params.set("timeStamp", timeStamp);
        return params;
    }

    /**
     * 校验注册参数
     *
     * @param req 注册请求
     */
    public static void validateRegisterParams(ClassinUserReq req) {
        // 手机号和邮箱二选一，且需要按照格式要求处理
        if (StrUtil.isNotBlank(req.getTelephone())) {
            // 处理手机号格式
            String telephone = req.getTelephone();
            // 如果是中国大陆手机号，直接使用
            if (telephone.startsWith("1") && telephone.length() == 11) {
                // 符合要求
            } else {
                // 其他国家手机号，需要添加国家代码前缀
                // 这里需要根据实际需求处理其他国家的手机号格式
                throw new BusinessException("暂不支持非中国大陆手机号注册");
            }
        } else if (StrUtil.isNotBlank(req.getEmail())) {
            // 符合要求
        } else {
            throw new BusinessException("手机号和邮箱必须填写一个");
        }

        // 设置密码（必填）
        if (StrUtil.isBlank(req.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        if (req.getPassword().length() < 6 || req.getPassword().length() > 20) {
            throw new BusinessException("密码长度必须在6-20位之间");
        }
    }

    /**
     * 执行 POST 请求并处理通用响应
     *
     * @param apiUrl 接口URL
     * @param params 请求参数
     * @param dataType 返回数据类型
     * @param <T> 泛型
     * @return 响应对象
     */
    public static <T> ClassinBaseResp<T> executePost(String apiUrl, JSONObject params, Class<T> dataType) {
        try {
            log.debug("调用 ClassIn 接口: url={}, params={}", apiUrl, params);
            HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .form(params)
                .timeout(10000)
                .execute();

            String responseBody = response.body();
            log.debug("ClassIn 接口响应: {}", responseBody);

            if (StrUtil.isBlank(responseBody)) {
                throw new BusinessException("接口响应为空");
            }
            
            // 由于泛型嵌套，需要手动解析
            JSONObject result = JSONUtil.parseObj(responseBody);
            ClassinBaseResp<T> baseResp = new ClassinBaseResp<>();
            baseResp.setErrorInfo(result.get("error_info", ClassinErrorInfo.class));
            baseResp.setData(result.get("data", dataType));

            if (baseResp.getErrorInfo() == null) {
                throw new BusinessException("接口响应格式错误，无法解析error_info");
            }
            return baseResp;
        } catch (Exception e) {
            log.error("调用ClassIn接口异常: {}", e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            }
            throw new BusinessException("调用ClassIn接口失败：" + e.getMessage());
        }
    }

    /**
     * 执行通用的 POST 请求
     *
     * @param apiUrl 接口URL
     * @param params 请求参数
     * @param description 接口描述（用于日志和异常信息）
     * @return 响应JSONObject
     */
    public static JSONObject executePostRequest(String apiUrl, JSONObject params, String description) {
        try {
            log.debug("调用 ClassIn {} 接口: url={}, params={}", description, apiUrl, params);
            
            HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .form(params)
                .timeout(10000)
                .execute();

            String responseBody = response.body();
            log.debug("ClassIn {} 接口响应: {}", description, responseBody);

            if (StrUtil.isBlank(responseBody)) {
                throw new BusinessException(description + "失败：接口响应为空");
            }

            JSONObject result = JSONUtil.parseObj(responseBody);
            if (result.getInt("code") != 1) {
                String errorMsg = result.getStr("msg");
                int errorCode = result.getInt("code");
                log.error("ClassIn {} 接口调用失败: code={}, error={}", description, errorCode, errorMsg);
                throw new BusinessException(String.format("%s失败（错误码：%d）：%s", description, errorCode, errorMsg));
            }

            return result;
        } catch (Exception e) {
            log.error("调用ClassIn {} 接口异常: {}", description, e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            }
            throw new BusinessException(description + "失败：" + e.getMessage());
        }
    }
} 