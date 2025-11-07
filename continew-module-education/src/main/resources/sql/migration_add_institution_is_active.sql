-- =====================================================
-- 迁移脚本：edu_institution 表添加 is_active 字段
-- 说明：用于替代配置文件中的 classin.active 配置，完全使用数据库管理机构配置
-- 执行时间：2025-11-07
-- =====================================================

-- 1. 添加 is_active 字段
ALTER TABLE `edu_institution` 
ADD COLUMN `is_active` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否激活（0：否；1：是），只能有一个机构处于激活状态' 
AFTER `secret`;

-- 2. 更新注释，明确 sid 和 secret 对应 ClassIn 的 AppId 和 AppSecret
ALTER TABLE `edu_institution` 
MODIFY COLUMN `sid` varchar(100) NOT NULL COMMENT '机构SID（ClassIn AppId）',
MODIFY COLUMN `secret` varchar(100) NOT NULL COMMENT '机构SECRET（ClassIn AppSecret）';

-- 3. 插入或更新机构数据
-- 如果表中没有数据，请插入机构信息（示例）：
INSERT INTO `edu_institution` (`code`, `name`, `sid`, `secret`, `is_active`, `status`, `create_user`, `create_time`, `update_time`) 
VALUES 
  ('app138', '元气森林', '52994294', 'tjMHEZBa', 1, 1, 1, NOW(), NOW()),
  ('app158', '备用机构', '67402266', '1YqGfCpd', 0, 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
  `sid` = VALUES(`sid`), 
  `secret` = VALUES(`secret`);

-- 4. 如果表中已有数据，请根据实际情况设置激活的机构
-- 方法1：根据 sid 设置（推荐）
-- UPDATE `edu_institution` SET `is_active` = 0;  -- 先全部设为非激活
-- UPDATE `edu_institution` SET `is_active` = 1 WHERE `sid` = '52994294' LIMIT 1;  -- 设置 app138 为激活

-- 方法2：根据机构名称设置
-- UPDATE `edu_institution` SET `is_active` = 0;  -- 先全部设为非激活
-- UPDATE `edu_institution` SET `is_active` = 1 WHERE `name` = '元气森林' LIMIT 1;

-- 5. 验证配置（可选）
-- SELECT * FROM `edu_institution` WHERE `is_active` = 1;

