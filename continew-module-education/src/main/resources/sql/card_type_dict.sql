-- 添加会员卡类型字典
INSERT INTO `sys_dict` (`name`, `code`, `description`, `is_system`, `create_user`, `create_time`)
VALUES ('会员卡类型', 'card_type', '会员卡类型字典', b'1', 1, NOW());

-- 获取刚插入的字典ID（假设为最新的ID）
SET @dict_id = LAST_INSERT_ID();

-- 添加会员卡类型字典项
INSERT INTO `sys_dict_item` (`label`, `value`, `color`, `sort`, `description`, `status`, `dict_id`, `create_user`, `create_time`)
VALUES 
('次卡有限期', 'TL', 'blue', 1, '次卡有限期：有固定次数和有效期限制', 1, @dict_id, 1, NOW()),
('次卡无限期', 'TU', 'green', 2, '次卡无限期：有固定次数但无有效期限制', 1, @dict_id, 1, NOW()),
('储蓄卡有限期', 'BL', 'orange', 3, '储蓄卡有限期：有余额和有效期限制', 1, @dict_id, 1, NOW()),
('储蓄卡无限期', 'BU', 'purple', 4, '储蓄卡无限期：有余额但无有效期限制', 1, @dict_id, 1, NOW());
