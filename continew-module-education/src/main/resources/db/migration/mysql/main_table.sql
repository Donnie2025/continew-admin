-- ----------------------------
-- Table structure for edu_class
-- ----------------------------
DROP TABLE IF EXISTS `edu_class`;
CREATE TABLE `edu_class` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `activity_id` bigint COMMENT 'ClassIn 活动ID',
  `class_id` bigint COMMENT 'ClassIn 课堂ID',
  `unit_id` bigint COMMENT '单元ID',
  `name` varchar(50) NOT NULL COMMENT '课堂活动名称',
  `teacher_uid` bigint NOT NULL COMMENT '主讲教师UID',
  `start_time` datetime NOT NULL COMMENT '活动开始时间',
  `end_time` datetime NOT NULL COMMENT '活动结束时间',
  `seat_num` int DEFAULT 2 COMMENT '上台人数，包括主讲教师，范围是[1,4]',
  `record_state` tinyint DEFAULT 0 COMMENT '录制状态：0-不录制，1-录制',
  `record_type` tinyint COMMENT '录制类型：0-云端录制，1-本地录制',
  `live_state` tinyint COMMENT '直播状态：0-不开启网页直播，1-开启网页直播',
  `open_state` tinyint COMMENT '公开状态：0-不公开，1-公开',
  `unique_identity` varchar(64) COMMENT '课堂唯一标识',
  `live_url` varchar(255) COMMENT '课堂直播播放器地址',
  `rtmp_url` varchar(255) COMMENT 'RTMP协议的拉流地址',
  `hls_url` varchar(255) COMMENT 'HLS协议的拉流地址',
  `flv_url` varchar(255) COMMENT 'FLV协议的拉流地址',
  `status` tinyint DEFAULT 0 COMMENT '状态：0-未开始，1-进行中，2-已结束',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`) COMMENT '课程ID索引',
  KEY `idx_activity_id` (`activity_id`) COMMENT '活动ID索引',
  KEY `idx_start_time` (`start_time`) COMMENT '开始时间索引',
  CONSTRAINT `fk_class_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='ClassIn课堂表';

-- ----------------------------
-- Table structure for edu_unit
-- ----------------------------
DROP TABLE IF EXISTS `edu_unit`;
CREATE TABLE `edu_unit` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `unit_id` bigint NOT NULL COMMENT 'ClassIn 单元ID',
  `name` varchar(50) NOT NULL COMMENT '单元名称',
  `content` text COMMENT '单元介绍',
  `publish_flag` tinyint DEFAULT 2 COMMENT '发布状态：0-草稿，2-已发布',
  `sort` int DEFAULT 0 COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`) COMMENT '课程ID索引',
  KEY `idx_unit_id` (`unit_id`) COMMENT '单元ID索引',
  CONSTRAINT `fk_unit_course` FOREIGN KEY (`course_id`) REFERENCES `edu_course` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课程单元表'; 