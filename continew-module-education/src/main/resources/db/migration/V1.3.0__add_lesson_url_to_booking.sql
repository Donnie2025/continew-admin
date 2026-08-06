-- 添加 lesson_url 字段到预约表
ALTER TABLE edu_booking ADD COLUMN lesson_url VARCHAR(500) COMMENT '预约课节链接';
