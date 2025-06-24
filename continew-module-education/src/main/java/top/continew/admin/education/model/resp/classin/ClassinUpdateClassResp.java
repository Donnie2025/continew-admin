package top.continew.admin.education.model.resp.classin;

import lombok.Data;

/**
 * ClassIn 编辑课堂活动响应对象
 *
 * @author don
 * @since 2025/06/21
 */
@Data
public class ClassinUpdateClassResp {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 课堂名称
     */
    private String name;
} 