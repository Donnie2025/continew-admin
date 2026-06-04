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

-- ----------------------------
-- Table structure for edu_material
-- 节点类型（type）：
--   CATEGORY  分类（少儿启蒙/青少年/成人教材…）
--   BOOK      课本名字（English for Kids…）
--   LEVEL     课本级别（K1/K2/G1/G2…）
--   UNIT      单元（Unit 1/Unit 2…）
--   LESSON    教材课节（叶节点，对应具体课节文件）
-- ----------------------------
DROP TABLE IF EXISTS `edu_material`;
CREATE TABLE `edu_material` (
  `id`          bigint        NOT NULL AUTO_INCREMENT   COMMENT '主键ID',
  `pid`         bigint        NOT NULL DEFAULT 0        COMMENT '父节点ID（0=根节点）',
  `type`        varchar(20)   NOT NULL                  COMMENT '节点类型（CATEGORY/BOOK/LEVEL/UNIT/LESSON）',
  `name`        varchar(200)  NOT NULL                  COMMENT '节点名称',
  `code`        varchar(50)       DEFAULT NULL          COMMENT '编码（BOOK/LEVEL层使用）',
  `cover_img`   varchar(500)      DEFAULT NULL          COMMENT '封面图片（BOOK层使用）',
  `description` varchar(1000)     DEFAULT NULL          COMMENT '描述',
  `lesson_url`  varchar(500)      DEFAULT NULL          COMMENT '课节资源链接（LESSON层使用）',
  `cloud_id`    varchar(100)      DEFAULT NULL          COMMENT 'ClassIn云盘ID（文件夹节点→文件夹ID，LESSON→文件ID）',
  `cloud_name`  varchar(200)      DEFAULT NULL          COMMENT 'ClassIn云盘名称',
  `is_show`     tinyint(1)    NOT NULL DEFAULT 1        COMMENT '是否前端展示（1:展示 0:不展示）',
  `sort`        int           NOT NULL DEFAULT 999      COMMENT '排序',
  `status`      tinyint(1)    NOT NULL DEFAULT 1        COMMENT '状态（1:启用 0:禁用）',
  `create_user` bigint        NOT NULL                  COMMENT '创建人',
  `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` bigint            DEFAULT NULL          COMMENT '修改人',
  `update_time` datetime          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_pid`         (`pid`),
  KEY `idx_type`        (`type`),
  KEY `idx_code`        (`code`),
  KEY `idx_cloud_id`    (`cloud_id`),
  KEY `idx_is_show`     (`is_show`),
  KEY `idx_sort`        (`sort`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教材统一树形表（CATEGORY/BOOK/LEVEL/UNIT/LESSON）';