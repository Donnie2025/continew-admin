package top.continew.admin.education.model.resp.classin;

import lombok.Data;

/**
 * ClassIn 创建课堂活动响应对象
 *
 * @author don
 * @since 2025/06/21
 */
@Data
public class ClassinCreateClassResp {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 课堂ID
     */
    private Long classId;

    /**
     * 课堂名称
     */
    private String name;

    /**
     * 课堂直播播放器地址
     */
    private String liveUrl;

    /**
     * 拉流地址信息
     */
    private LiveInfo liveInfo;

    /**
     * 直播拉流地址信息
     */
    @Data
    public static class LiveInfo {
        /**
         * RTMP协议的拉流地址
         */
        private String RTMP;

        /**
         * HLS协议的拉流地址
         */
        private String HLS;

        /**
         * FLV协议的拉流地址
         */
        private String FLV;
    }
} 