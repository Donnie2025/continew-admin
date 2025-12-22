-- 添加会员卡类型字典（如果不存在）
INSERT IGNORE INTO `sys_dict` (`id`, `name`, `code`, `description`, `is_system`, `create_user`, `create_time`)
VALUES (4, '会员卡类型', 'card_type', '会员卡类型字典', b'1', 1, NOW());

-- 添加会员卡类型字典项（如果不存在）
INSERT IGNORE INTO `sys_dict_item` (`id`, `label`, `value`, `color`, `sort`, `description`, `status`, `dict_id`, `create_user`, `create_time`)
VALUES 
(9, '次卡有限期', 'TL', 'blue', 1, '次卡有限期：有固定次数和有效期限制', 1, 4, 1, NOW()),
(10, '次卡无限期', 'TU', 'green', 2, '次卡无限期：有固定次数但无有效期限制', 1, 4, 1, NOW()),
(11, '储蓄卡有限期', 'BL', 'orange', 3, '储蓄卡有限期：有余额和有效期限制', 1, 4, 1, NOW()),
(12, '储蓄卡无限期', 'BU', 'purple', 4, '储蓄卡无限期：有余额但无有效期限制', 1, 4, 1, NOW());

-- 查询验证
SELECT d.name, d.code, di.label, di.value, di.color, di.sort, di.description 
FROM sys_dict d 
LEFT JOIN sys_dict_item di ON d.id = di.dict_id 
WHERE d.code = 'card_type' 
ORDER BY di.sort;
