package top.continew.admin.education.model.resp.classin;

import lombok.Data;

/**
 * ClassIn 创建单元响应对象
 *
 * @author don
 * @since 2025/06/21
 */
@Data
public class ClassinCreateUnitResp {

    /**
     * 单元名称
     */
    private String name;

    /**
     * 单元ID
     */
    private Long unitId;
} 