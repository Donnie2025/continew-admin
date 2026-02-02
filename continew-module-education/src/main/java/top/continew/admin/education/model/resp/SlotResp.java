/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.education.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;
import java.io.Serial;
import java.time.*;
import java.util.List;

/**
 * 课程管理信息
 *
 * @author don
 * @since 2025/04/25 23:24
 */
@Data
@Schema(description = "课程管理信息")
public class SlotResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属教师ID
     */
    @Schema(description = "所属教师ID")
    private Long teacherId;

    /**
     * 教师名字
     */
    @Schema(description = "教师名字")
    private String teacherName;

    /**
     * 开课日期（格式：YYYYMMDD）
     */
    @Schema(description = "开课日期（格式：YYYYMMDD）")
    private String startDate;

    /**
     * 开课时间（格式：HH:MM）
     */
    @Schema(description = "开课时间（格式：HH:MM）")
    private String startTime;

    /**
     * 星期几（1：周一；2：周二；3：周三；4：周四；5：周五；6：周六；7：周日）
     */
    @Schema(description = "星期几（1：周一；2：周二；3：周三；4：周四；5：周五；6：周六；7：周日）")
    private Boolean weekday;

    /**
     * 课程时长（单位为分钟）
     */
    @Schema(description = "课程时长（单位为分钟）")
    private Integer duration;

    /**
     * 学生数量
     */
    @Schema(description = "学生数量")
    private Integer studentCount;

    /**
     * 是否在线教室（0：否；1：是）
     */
    @Schema(description = "是否在线教室（0：否；1：是）")
    private Boolean isOnline;

    /**
     * 状态（1：启用；0：禁用）
     */
    @Schema(description = "状态（1：启用；0：禁用）")
    private Integer status;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    private Long institutionId;

    /**
     * 学生姓名（预约者姓名）
     */
    @Schema(description = "学生姓名（预约者姓名）")
    private List<String> studentNameList;

    /**
     * 预约详细信息列表
     */
    @Schema(description = "预约详细信息列表")
    private List<BookingDetailInfo> bookingDetails;

    /**
     * 预约详细信息
     */
    @Schema(description = "预约详细信息")
    public static class BookingDetailInfo {
        /**
         * 学生ID
         */
        @Schema(description = "学生ID")
        private Long studentId;

        /**
         * 学生姓名
         */
        @Schema(description = "学生姓名")
        private String studentName;

        /**
         * 学生手机号
         */
        @Schema(description = "学生手机号")
        private String studentPhone;

        /**
         * 教材ID
         */
        @Schema(description = "教材ID")
        private Long materialId;

        /**
         * 教材名称
         */
        @Schema(description = "教材名称")
        private String materialName;

        /**
         * 教材编码
         */
        @Schema(description = "教材编码")
        private String materialCode;

        /**
         * 教材级别
         */
        @Schema(description = "教材级别")
        private String materialLevel;

        /**
         * 课节ID
         */
        @Schema(description = "课节ID")
        private Long lessonId;

        /**
         * 课节名称
         */
        @Schema(description = "课节名称")
        private String lessonName;

        /**
         * 课节链接
         */
        @Schema(description = "课节链接")
        private String lessonUrl;

        /**
         * 学生会员卡ID
         */
        @Schema(description = "学生会员卡ID")
        private Long stuCardId;

        /**
         * 会员卡名称
         */
        @Schema(description = "会员卡名称")
        private String cardName;

        /**
         * 预约备注
         */
        @Schema(description = "预约备注")
        private String remark;

        // Getters and Setters
        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentPhone() {
            return studentPhone;
        }

        public void setStudentPhone(String studentPhone) {
            this.studentPhone = studentPhone;
        }

        public Long getMaterialId() {
            return materialId;
        }

        public void setMaterialId(Long materialId) {
            this.materialId = materialId;
        }

        public String getMaterialName() {
            return materialName;
        }

        public void setMaterialName(String materialName) {
            this.materialName = materialName;
        }

        public String getMaterialCode() {
            return materialCode;
        }

        public void setMaterialCode(String materialCode) {
            this.materialCode = materialCode;
        }

        public String getMaterialLevel() {
            return materialLevel;
        }

        public void setMaterialLevel(String materialLevel) {
            this.materialLevel = materialLevel;
        }

        public Long getLessonId() {
            return lessonId;
        }

        public void setLessonId(Long lessonId) {
            this.lessonId = lessonId;
        }

        public String getLessonName() {
            return lessonName;
        }

        public void setLessonName(String lessonName) {
            this.lessonName = lessonName;
        }

        public String getLessonUrl() {
            return lessonUrl;
        }

        public void setLessonUrl(String lessonUrl) {
            this.lessonUrl = lessonUrl;
        }

        public Long getStuCardId() {
            return stuCardId;
        }

        public void setStuCardId(Long stuCardId) {
            this.stuCardId = stuCardId;
        }

        public String getCardName() {
            return cardName;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

    // Getter and Setter for bookingDetails
    public List<BookingDetailInfo> getBookingDetails() {
        return bookingDetails;
    }

    public void setBookingDetails(List<BookingDetailInfo> bookingDetails) {
        this.bookingDetails = bookingDetails;
    }
}