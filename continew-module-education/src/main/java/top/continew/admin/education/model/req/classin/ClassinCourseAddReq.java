package top.continew.admin.education.model.req.classin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassIn 新增课程请求
 *
 * @author KAI
 * @since 2024/07/04 17:00
 */
@Data
public class ClassinCourseAddReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 课程名称
     */
    @JsonProperty("courseName")
    private String courseName;

    /**
     * 班主任 UID
     */
    @JsonProperty("mainTeacherUid")
    private String mainTeacherUid;

    /**
     * 唯一标识
     */
    @JsonProperty("courseUniqueIdentity")
    private String courseUniqueIdentity;

    /**
     * 教室设置ID
     */
    @JsonProperty("classroomSettingId")
    private Long classroomSettingId;
} 