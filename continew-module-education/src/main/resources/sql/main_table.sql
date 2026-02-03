CREATE TABLE `edu_student` (
   `id`  bigint(20)   NOT NULL AUTO_INCREMENT     COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '学生姓名',
  `gender` varchar(10) DEFAULT 'male' COMMENT '性别（male-男 female-女）',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `register_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `agent_id` bigint(20) DEFAULT NULL COMMENT '所属代理的ID',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像地址',
  `openid` varchar(64) DEFAULT NULL COMMENT '微信openid',
  `unionid` varchar(64) DEFAULT NULL COMMENT '微信unionid',
  `nickname` varchar(50) DEFAULT NULL COMMENT '微信昵称',
  `country` varchar(50) DEFAULT NULL COMMENT '国家',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  `enable_recording` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否允许录课（0：不允许；1：允许）',
  `status`         tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；2：禁用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20)   NOT NULL                    COMMENT '创建人',
  `update_user` bigint(20)   DEFAULT NULL                COMMENT '修改人',
  `institution_id` bigint(20) NOT NULL COMMENT '所属机构ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_unionid` (`unionid`),
  CONSTRAINT `fk_student_institution` FOREIGN KEY (`institution_id`) REFERENCES `edu_institution` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生表';

ALTER TABLE `edu_student` AUTO_INCREMENT = 1;

CREATE TABLE `edu_teacher` (
   `id`  bigint(20)   NOT NULL AUTO_INCREMENT     COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '教师姓名',
   `recv_name` varchar(50) DEFAULT NULL COMMENT '收款人姓名',
   `score`  int NOT NULL DEFAULT 5  COMMENT '评分',
   `tags` varchar(100) DEFAULT NULL COMMENT '标签',
  `gender` varchar(50) DEFAULT 'female' COMMENT '性别（male, female）',
  `is_show` tinyint DEFAULT '1' COMMENT '是否展示（ 1-展示, 2-不展示）',
  `is_fixed` tinyint DEFAULT '1' COMMENT '是否固定（ 1-固定, 2-不固定）',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像地址',
  `audio_url` varchar(512) DEFAULT NULL COMMENT '音频地址',
  `video_url` varchar(512) DEFAULT NULL COMMENT '视频地址',
  `brief_intro` varchar(256) DEFAULT NULL COMMENT '简介',
  `description` varchar(1024) DEFAULT NULL COMMENT '描述',
  `group_name` varchar(100) DEFAULT NULL COMMENT '所属组',
  `show_salary` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否在教师端展示工资（0：不展示；1：展示）',
  `sort`        int          NOT NULL DEFAULT 999        COMMENT '排序',
  `status`         tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；2：禁用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20)   NOT NULL                    COMMENT '创建人',
  `update_user` bigint(20)   DEFAULT NULL                COMMENT '修改人',
  `institution_id` bigint(20) NOT NULL COMMENT '所属机构ID',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_teacher_institution` FOREIGN KEY (`institution_id`) REFERENCES `edu_institution` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师表';

CREATE TABLE `edu_slot` (
   `id`  bigint(20)   NOT NULL AUTO_INCREMENT     COMMENT 'ID',
  `teacher_id` bigint(20) NOT NULL COMMENT '所属教师ID',
  `teacher_name` varchar(50) DEFAULT NULL COMMENT '教师名字',
  `start_date` varchar(8) NOT NULL COMMENT '开课日期（格式：YYYYMMDD）',
  `start_time` varchar(5) NOT NULL COMMENT '开课时间（格式：HH:MM）',
  `weekday` tinyint(1) NOT NULL COMMENT '星期几（1：周一；2：周二；3：周三；4：周四；5：周五；6：周六；7：周日）',
  `duration` int NOT NULL DEFAULT 25 COMMENT '课程时长（单位为分钟）',
  `is_online` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否在线教室（0：否；1：是）',
  `status`         tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；0：禁用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20)   NOT NULL                    COMMENT '创建人',
  `update_user` bigint(20)   DEFAULT NULL                COMMENT '修改人',
  `institution_id` bigint(20) COMMENT '所属机构ID',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`id`),
  CONSTRAINT `fk_course_institution` FOREIGN KEY (`institution_id`) REFERENCES `edu_institution` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='课程表';

CREATE TABLE `edu_booking` (
   `id`  bigint(20)   NOT NULL AUTO_INCREMENT     COMMENT 'ID',
  `slot_id` bigint(20) NOT NULL COMMENT '所属课程ID',
  `slot_date` varchar(8) NOT NULL COMMENT '开课日期（格式：YYYYMMDD）',
  `slot_time` varchar(5) NOT NULL COMMENT '开课时间（格式：HH:MM）',
  `student_id` bigint(20) NOT NULL COMMENT '所属学生ID',
  `student_name` varchar(50) NOT NULL COMMENT '所属学生姓名',
  `student_phone` varchar(20) NOT NULL COMMENT '学生手机号',
  `teacher_id` bigint(20) DEFAULT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) DEFAULT NULL COMMENT '教师姓名',
  `card_id` bigint(20) DEFAULT NULL COMMENT '预约会员卡ID',
  `card_name` varchar(100) DEFAULT NULL COMMENT '预约会员卡名称',
  `material_id` bigint(20) DEFAULT NULL COMMENT '预约教材ID',
  `material_name` varchar(100) DEFAULT NULL COMMENT '预约教材名字',
  `material_code` varchar(50) DEFAULT NULL COMMENT '预约教材编码',
  `material_level` varchar(50) DEFAULT NULL COMMENT '预约教材级别',
  `lesson_id` bigint(20) DEFAULT NULL COMMENT '课节ID',
  `lesson_name` varchar(100) DEFAULT NULL COMMENT '预约课节名字',
  `lesson_url` varchar(500) DEFAULT NULL COMMENT '预约课节链接',
  `remark` varchar(1024) DEFAULT NULL COMMENT '预约备注',
  `status`         tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；0：禁用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20)   NOT NULL                    COMMENT '创建人',
  `update_user` bigint(20)   DEFAULT NULL                COMMENT '修改人',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_booking_slot` FOREIGN KEY (`slot_id`) REFERENCES `edu_slot` (`id`),
  CONSTRAINT `fk_booking_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`id`),
  CONSTRAINT `fk_booking_card` FOREIGN KEY (`card_id`) REFERENCES `edu_card` (`id`),
  CONSTRAINT `fk_booking_lesson` FOREIGN KEY (`lesson_id`) REFERENCES `edu_material_lesson` (`id`),
  CONSTRAINT `fk_booking_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='预约表';

CREATE TABLE `edu_card` (
   `id`  bigint(20)   NOT NULL AUTO_INCREMENT     COMMENT 'ID',
  `title` varchar(100) NOT NULL COMMENT '会员卡标题',
  `sub_title` varchar(200) DEFAULT NULL COMMENT '副标题',
  `description` varchar(500) DEFAULT NULL COMMENT '会员卡描述',
  `type` varchar(10) NOT NULL COMMENT '会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）',
  `init_times` int DEFAULT NULL COMMENT '初始次数',
  `init_days` int DEFAULT NULL COMMENT '初始有效天数',
  `init_balance` decimal(10,2) DEFAULT NULL COMMENT '初始余额',
  `price` decimal(10,2) DEFAULT NULL COMMENT '售卖价格',
  `sort` INT NOT NULL DEFAULT 999 COMMENT '排序字段，值越小排序越靠前',
  `institution_id` bigint(20) DEFAULT NULL COMMENT '所属机构ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status`         tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；0：禁用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20)   NOT NULL                    COMMENT '创建人',
  `update_user` bigint(20)   DEFAULT NULL                COMMENT '修改人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员卡模板表';

CREATE TABLE `edu_institution` (
   `id`  bigint(20)   NOT NULL AUTO_INCREMENT     COMMENT 'ID',
   `code` varchar(20) NOT NULL COMMENT '机构编码',
   `name` varchar(100) NOT NULL COMMENT '机构名称',
   `sid` varchar(100) NOT NULL COMMENT '机构SID（ClassIn AppId）',
   `secret` varchar(100) NOT NULL COMMENT '机构SECRET（ClassIn AppSecret）',
   `is_active` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否激活（0：否；1：是），只能有一个机构处于激活状态',
   `status`         tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；2：禁用）',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_user` bigint(20)   NOT NULL                    COMMENT '创建人',
   `update_user` bigint(20)   DEFAULT NULL                COMMENT '修改人',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='机构表';

CREATE TABLE `edu_course` (
  `id`  bigint(20)   NOT NULL AUTO_INCREMENT     COMMENT 'ID',
  `name` varchar(100) NOT NULL COMMENT '教室名称',
  `main_teacher_id` bigint(20) DEFAULT NULL COMMENT '班主任ID',
  `main_teacher_uid` varchar(50) DEFAULT NULL COMMENT 'Classin班主任ID',
  `course_unique` varchar(32) NOT NULL COMMENT '机构课程唯一标识',
  `course_uid` bigint(20) DEFAULT NULL     COMMENT 'classin教室ID',
  `course_setting_id` bigint(20) DEFAULT NULL  COMMENT '教室设置ID',
  `status`         tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；2：禁用）',
  `institution_id` bigint(20) COMMENT '所属机构ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20)   DEFAULT NULL                    COMMENT '创建人',
  `update_user` bigint(20)   DEFAULT NULL                COMMENT '修改人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='班级表';

-- ----------------------------
-- Table structure for edu_lesson
-- ----------------------------
CREATE TABLE `edu_lesson` (
  `id`  bigint(20)   NOT NULL AUTO_INCREMENT     COMMENT 'ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `course_uid` bigint NOT NULL COMMENT 'ClassIn 课程ID',
  `activity_uid` bigint COMMENT 'ClassIn 活动ID',
  `class_uid` bigint COMMENT 'ClassIn 课堂ID',
  `unit_uid` bigint COMMENT '单元ID',
  `name` varchar(50) NOT NULL COMMENT '课堂活动名称',
  `teacher_id` bigint NOT NULL COMMENT '主讲教师ID',
  `teacher_uid` bigint NOT NULL COMMENT '主讲教师UID',
  `teacher_name` varchar(100) NOT NULL COMMENT '主讲教师名称',
  `start_time` datetime NOT NULL COMMENT '活动开始时间',
  `duration` bigint NOT NULL COMMENT '课时分钟数',
  `seat_num` int DEFAULT 2 COMMENT '上台人数，包括主讲教师，范围是[1,13]',
  `record_state` tinyint DEFAULT 0 COMMENT '录制状态：0-不录制，1-录制',
  `live_state` tinyint COMMENT '直播状态：0-不开启网页直播，1-开启网页直播',
  `open_state` tinyint COMMENT '公开状态：0-不公开，1-公开',
  `unique_identity` varchar(64) COMMENT '课堂唯一标识',
  `live_url` varchar(255) COMMENT '课堂直播播放器地址',
  `rtmp_url` varchar(255) COMMENT 'RTMP协议的拉流地址',
  `hls_url` varchar(255) COMMENT 'HLS协议的拉流地址',
  `flv_url` varchar(255) COMMENT 'FLV协议的拉流地址',
  `status` tinyint DEFAULT 0 COMMENT '状态（1：启用；2：禁用；3：结课）',
  `create_user` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`) COMMENT '课程ID索引',
  KEY `idx_activity_id` (`activity_id`) COMMENT '活动ID索引',
  KEY `idx_start_time` (`start_time`) COMMENT '开始时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='ClassIn课堂表';

CREATE TABLE `classin_user` (
    `id`  bigint(20)   NOT NULL AUTO_INCREMENT     COMMENT 'ID',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `user_type` tinyint NOT NULL DEFAULT 0 COMMENT '成员类型（0：不是成员；1：学生；2：老师）',
    `uid` varchar(50) DEFAULT NULL COMMENT 'Classin唯一映射关系',
    `password` varchar(100) DEFAULT NULL COMMENT '密码',
    `telephone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `student_id` bigint(20) DEFAULT NULL COMMENT '关联学生ID',
    `teacher_id` bigint(20) DEFAULT NULL COMMENT '关联教师ID',
    `classin_institution_id` bigint(20) NOT NULL COMMENT '关联Classin机构ID',
    `status`         tinyint(1)   UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；2：禁用）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` bigint(20)   NOT NULL                    COMMENT '创建人',
    `update_user` bigint(20)   DEFAULT NULL                COMMENT '修改人',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_classin_user_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`id`),
    CONSTRAINT `fk_classin_user_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`id`),
    CONSTRAINT `fk_classin_user_institution` FOREIGN KEY (`classin_institution_id`) REFERENCES `edu_institution` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Classin用户表';

-- 学生会员卡绑定表（学生持卡实例）
CREATE TABLE `edu_stu_card` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stu_id` bigint(20) NOT NULL COMMENT '学生ID',
  `stu_name` varchar(50) COMMENT '学生姓名',
  `card_id` bigint(20) NOT NULL COMMENT '会员卡ID',
  `card_title` varchar(100) NOT NULL COMMENT '会员卡标题',
  `card_type` varchar(10) NOT NULL COMMENT '会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）',
  `remain_times` int DEFAULT 0 COMMENT '剩余次数（用于次卡）',
  `remain_balance` decimal(10,2) DEFAULT 0 COMMENT '剩余余额（用于储蓄卡）',
  `activate_date` date DEFAULT NULL COMMENT '激活日期',
  `expire_date` date DEFAULT NULL COMMENT '到期日期',
  `purchase_price` decimal(10,2) DEFAULT NULL COMMENT '购买价格',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：启用；0：禁用）',
  `card_status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '卡状态（1：启用，学生端可见；0：禁用，学生端不可见，后台管理系统可见）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  INDEX `idx_stu_id` (`stu_id`),
  INDEX `idx_card_id` (`card_id`),
  INDEX `idx_expire_date` (`expire_date`),
  CONSTRAINT `fk_stu_card_student` FOREIGN KEY (`stu_id`) REFERENCES `edu_student` (`id`),
  CONSTRAINT `fk_stu_card_card` FOREIGN KEY (`card_id`) REFERENCES `edu_card` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生会员卡绑定表（学生持卡实例）';

CREATE TABLE `edu_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stu_card_id` bigint(20) NOT NULL COMMENT '学生会员卡ID',
  `stu_id` bigint(20) NOT NULL COMMENT '学生ID',
  `stu_name` varchar(50) COMMENT '学生姓名',
  `card_id` bigint(20) NOT NULL COMMENT '会员卡ID',
  `card_title` varchar(100) COMMENT '会员卡标题',
  `trans_type` varchar(32) NOT NULL COMMENT '交易类型（bind:首次绑卡, recharge:充值, consume:消费, refund:退款, expire:过期, activate:激活）',
  `times_change` int DEFAULT 0 COMMENT '次数变动（正数为增加，负数为减少）',
  `balance_change` decimal(10,2) DEFAULT 0 COMMENT '余额变动（正数为增加，负数为减少）',
  `before_times` int DEFAULT 0 COMMENT '变动前次数',
  `after_times` int DEFAULT 0 COMMENT '变动后次数',
  `before_balance` decimal(10,2) DEFAULT 0 COMMENT '变动前余额',
  `after_balance` decimal(10,2) DEFAULT 0 COMMENT '变动后余额',
  `amount` decimal(10,2) DEFAULT 0 COMMENT '交易金额（实际收支金额）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
  PRIMARY KEY (`id`),
  INDEX `idx_stu_card_id` (`stu_card_id`),
  INDEX `idx_stu_id` (`stu_id`),
  INDEX `idx_create_time` (`create_time`),
  CONSTRAINT `fk_transaction_stu_card` FOREIGN KEY (`stu_card_id`) REFERENCES `edu_stu_card` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员卡交易流水表';

-- 订单表
CREATE TABLE `edu_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单编号',
  `stu_id` bigint(20) NOT NULL COMMENT '学生ID',
  `stu_name` varchar(50) COMMENT '学生姓名',
  `card_id` bigint(20) NOT NULL COMMENT '会员卡ID',
  `card_title` varchar(100) NOT NULL COMMENT '会员卡标题',
  `card_type` varchar(10) NOT NULL COMMENT '会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）',
  `order_price` decimal(10,2) NOT NULL COMMENT '订单金额',
  `payment_type` varchar(20) NOT NULL COMMENT '支付方式（wechat:微信支付, alipay:支付宝）',
  `order_status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态（PENDING:待确认, COMPLETED:已完成, CANCELLED:已取消）',
  `stu_card_id` bigint(20) DEFAULT NULL COMMENT '关联的学生会员卡ID（下单时创建空白卡）',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `institution_id` bigint(20) DEFAULT NULL COMMENT '所属机构ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  INDEX `idx_stu_id` (`stu_id`),
  INDEX `idx_card_id` (`card_id`),
  INDEX `idx_stu_card_id` (`stu_card_id`),
  INDEX `idx_order_status` (`order_status`),
  INDEX `idx_create_time` (`create_time`),
  CONSTRAINT `fk_order_student` FOREIGN KEY (`stu_id`) REFERENCES `edu_student` (`id`),
  CONSTRAINT `fk_order_card` FOREIGN KEY (`card_id`) REFERENCES `edu_card` (`id`),
  CONSTRAINT `fk_order_stu_card` FOREIGN KEY (`stu_card_id`) REFERENCES `edu_stu_card` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员卡订单表';

CREATE TABLE `edu_salary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `teacher_id` bigint(20) NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(50) NOT NULL COMMENT '教师姓名',
  `recv_name` varchar(50) DEFAULT NULL COMMENT '收款人姓名',
  `start_date` date NOT NULL COMMENT '起始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `course_count` int NOT NULL DEFAULT 0 COMMENT '课程总数',
  `course_amount` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '课程总金额',
  `deduction_amount` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '扣款金额',
  `tip_amount` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '小费金额',
  `final_amount` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '最终支付金额',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（0：失效；1：生效）',
  `is_settled` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否结算（0：未结算；1：已结算）',
  `rate` int NOT NULL DEFAULT 0 COMMENT '单价',
  `group_name` varchar(100) DEFAULT NULL COMMENT '所属组',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_teacher_salary_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师工资表';

CREATE TABLE `edu_course_teacher` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `course_id` bigint(20) DEFAULT NULL COMMENT '教室ID',
  `course_name` varchar(100) NOT NULL COMMENT '教室名称',
  `teacher_id` bigint(20) DEFAULT NULL COMMENT '老师ID',
  `teacher_name` varchar(100) NOT NULL COMMENT '老师名称',
  `teacher_uid` varchar(50) DEFAULT NULL COMMENT 'Vendor老师ID',
  `status` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '状态（1：启用；2：禁用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='班级老师关联表';

CREATE TABLE `edu_course_student` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `course_id` bigint(20) DEFAULT NULL COMMENT '教室ID',
  `course_name` varchar(100) NOT NULL COMMENT '教室名称',
  `student_id` bigint(20) DEFAULT NULL COMMENT '学生ID',
  `student_name` varchar(100) NOT NULL COMMENT '学生名称',
  `student_uid` varchar(50) DEFAULT NULL COMMENT 'Vendor学生ID',
  `status` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '状态（1：启用；2：禁用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='班级学生关联表';

-- 学生密码错误记录表
CREATE TABLE `edu_credential` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联edu_teacher.id或edu_student.id）',
  `user_type` varchar(20) NOT NULL COMMENT '用户类型（teacher-教师, student-学生）',
  `phone` varchar(20) NOT NULL COMMENT '手机号码',
  `password` varchar(255) NOT NULL COMMENT '登录密码（BCrypt加密）',
  `credential_type` varchar(20) NOT NULL DEFAULT 'phone' COMMENT '凭证类型（phone，email等）',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用（0-禁用 1-启用）',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `password_updated_time` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `error_count` int NOT NULL DEFAULT '0' COMMENT '密码错误次数',
  `last_error_time` datetime DEFAULT NULL COMMENT '最后错误时间',
  `freeze_until` datetime DEFAULT NULL COMMENT '冻结到期时间',
  `is_frozen` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否冻结（0-否 1-是）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_credential` (`user_id`, `user_type`, `credential_type`),
  UNIQUE KEY `uk_phone_user_type` (`phone`, `user_type`),
  KEY `idx_phone` (`phone`),
  KEY `idx_user_type` (`user_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_credential_type` (`credential_type`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_freeze_until` (`freeze_until`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='凭证表';

-- ================================
-- 固定课功能相关表结构设计
-- 说明：支持老师开设固定课，学生预约固定课的完整功能
-- ================================

-- 1. 固定课主表 (edu_fixed)
CREATE TABLE `edu_fixed` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `teacher_id` bigint NOT NULL COMMENT '教师ID，关联edu_teacher表',
    `teacher_name` varchar(50) NOT NULL COMMENT '教师姓名（冗余字段）',
    `duration_minutes` int NOT NULL DEFAULT '25' COMMENT '课程时长（分钟）',
    `max_students` int NOT NULL DEFAULT '1' COMMENT '最大学生数',
    
    -- 每周重复规则（简化）
    `week_day` tinyint NOT NULL COMMENT '星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日',
    `start_time` varchar(5) NOT NULL COMMENT '开始时间（HH:MM格式）',
    
    -- 状态和管理字段
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `create_user` bigint DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` bigint DEFAULT NULL COMMENT '修改人', 
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    
    PRIMARY KEY (`id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_status` (`status`),
    KEY `idx_week_time` (`week_day`, `start_time`),
    KEY `idx_teacher_name` (`teacher_name`),
    CONSTRAINT `fk_fixed_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='固定课主表';

-- 2. 固定课预约表 (edu_fixed_booking)
CREATE TABLE `edu_fixed_booking` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `fixed_id` bigint NOT NULL COMMENT '固定课ID，关联edu_fixed表',
    `student_id` bigint NOT NULL COMMENT '学生ID，关联edu_student表',
    `student_name` varchar(50) NOT NULL COMMENT '学生姓名（冗余字段）',
    `student_phone` varchar(20) DEFAULT NULL COMMENT '学生手机号（冗余字段）',
    `teacher_id` bigint NOT NULL COMMENT '教师ID，关联edu_teacher表（冗余字段，便于查询）',
    `teacher_name` varchar(50) NOT NULL COMMENT '教师姓名（冗余字段）',
    
    -- 固定课时间信息（冗余字段，便于查询）
    `week_day` tinyint NOT NULL COMMENT '星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日',
    `start_time` varchar(5) NOT NULL COMMENT '开始时间（HH:MM格式）',
    `duration_minutes` int NOT NULL DEFAULT '25' COMMENT '课程时长（分钟）',
    
    -- 状态和管理字段
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    
    -- 管理字段
    `create_user` bigint DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` bigint DEFAULT NULL COMMENT '修改人',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_fixed_student` (`fixed_id`, `student_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_status` (`status`),
    KEY `idx_student_name` (`student_name`),
    KEY `idx_teacher_name` (`teacher_name`),
    KEY `idx_week_time` (`week_day`, `start_time`),
    CONSTRAINT `fk_fixed_booking_fixed` FOREIGN KEY (`fixed_id`) REFERENCES `edu_fixed` (`id`),
    CONSTRAINT `fk_fixed_booking_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`id`),
    CONSTRAINT `fk_fixed_booking_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='固定课预约表';

-- 3. 固定课操作记录表 (edu_fixed_log)
CREATE TABLE `edu_fixed_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `fixed_id` bigint NOT NULL COMMENT '固定课ID',
    `student_id` bigint DEFAULT NULL COMMENT '学生ID',
    `student_name` varchar(50) DEFAULT NULL COMMENT '学生姓名（冗余字段）',
    `teacher_id` bigint NOT NULL COMMENT '教师ID',
    `teacher_name` varchar(50) NOT NULL COMMENT '教师姓名（冗余字段）',
    
    -- 操作信息（简化）
    `op_type` tinyint NOT NULL COMMENT '操作类型：1-预约，2-取消',
    `op_desc` varchar(200) NOT NULL COMMENT '操作描述',
    
    -- 时间信息
    `op_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `op_user` bigint DEFAULT NULL COMMENT '操作人ID',
    `op_user_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名（冗余字段）',
    
    -- 审计字段（BaseDO继承的字段）
    `create_user` bigint DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` bigint DEFAULT NULL COMMENT '修改人',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    
    PRIMARY KEY (`id`),
    KEY `idx_fixed_id` (`fixed_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_op_type` (`op_type`),
    KEY `idx_op_time` (`op_time`),
    KEY `idx_student_name` (`student_name`),
    KEY `idx_teacher_name` (`teacher_name`),
    KEY `idx_op_user_name` (`op_user_name`),
    CONSTRAINT `fk_fixed_log_fixed` FOREIGN KEY (`fixed_id`) REFERENCES `edu_fixed` (`id`),
    CONSTRAINT `fk_fixed_log_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='固定课操作记录表';