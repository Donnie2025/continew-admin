package top.continew.admin.education.model.resp.classin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassIn 接口通用响应基类
 *
 * @author KAI
 * @since 2024/07/04 17:00
 */
@Data
public class ClassinBaseResp<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务数据
     */
    private T data;

    /**
     * 错误信息
     */
    @JsonProperty("error_info")
    private ClassinErrorInfo errorInfo;
} 