-- 添加 ClassIn 配置字典（如果不存在）
INSERT IGNORE INTO `sys_dict` (`id`, `name`, `code`, `description`, `is_system`, `create_user`, `create_time`)
VALUES (20, 'ClassIn配置', 'classin_config', 'ClassIn接口对接相关配置', b'0', 1, NOW());

-- 添加 ClassIn 对接开关字典项（默认开启）
-- 说明：将 value 改为 '0' 可关闭 ClassIn 对接，系统不再调用任何 ClassIn 接口
INSERT IGNORE INTO `sys_dict_item` (`id`, `label`, `value`, `color`, `sort`, `description`, `status`, `dict_id`, `create_user`, `create_time`)
VALUES (200, 'classin_enabled', '1', 'green', 1, '是否启用ClassIn接口对接（1:启用；0:禁用）', 1, 20, 1, NOW());

-- 查询验证
SELECT d.name, d.code, di.label, di.value, di.description
FROM sys_dict d
LEFT JOIN sys_dict_item di ON d.id = di.dict_id
WHERE d.code = 'classin_config';
