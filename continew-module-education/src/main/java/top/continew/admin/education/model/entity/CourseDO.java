package top.continew.admin.education.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;


import java.io.Serial;

/**
 * 班级实体
 *
 * @author don
 * @since 2025/06/21 23:25
 */
@Data
@TableName("edu_course")
public class CourseDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教室名称
     */
    private String name;

    /**
     * 班主任ID
     */
    private Long mainTeacherId;

    /**
     * Classin班主任ID
     */
    private String mainTeacherUid;

    /**
     * 机构课程唯一标识
     */
    private String courseUnique;

    /**
     * classin教室ID
     */
    private Long courseUid;

    /**
     * 教室设置ID
     */
    private Long courseSettingId;

    /**
     * 状态（1：启用；2：禁用）
     */
    private Integer status;

    /**
     * 所属机构ID
     */
    private Long institutionId;
}
