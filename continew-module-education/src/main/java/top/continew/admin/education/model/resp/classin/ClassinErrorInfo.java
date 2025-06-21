package top.continew.admin.education.model.resp.classin;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassIn 接口通用响应错误信息
 *
 * @author KAI
 * @since 2024/07/04 17:00
 */
@Data
public class ClassinErrorInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer errno;

    /**
     * 错误信息
     */
    private String error;
} 