package top.continew.admin.education.model.req.classin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassIn 创建单元请求对象
 *
 * @author don
 * @since 2025/06/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassinCreateUnitReq {

    /**
     * 班级（课程）ID
     * 必填
     */
    private Long courseId;

    /**
     * 单元名称
     * 必填，长度不超过50字
     * 注：课程下不支持创建同名单元
     */
    private String name;

    /**
     * 是否发布
     * 必填
     * 0-草稿，2-已发布（显示）
     */
    private Integer publishFlag;

    /**
     * 单元介绍
     * 非必填，不传默认为空
     */
    private String content;
} 