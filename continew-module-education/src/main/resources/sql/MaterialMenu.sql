SET @parentId = 2005630497273040896;
-- 教材管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (4100, '教材管理', 1000, 2, '/education/material', 'Material', 'education/material/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 教材管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (4101, '列表', 4100, 3, 'education:material:list', 1, 1, 1, NOW()),
    (4102, '详情', 4100, 3, 'education:material:get', 2, 1, 1, NOW()),
    (4103, '新增', 4100, 3, 'education:material:create', 3, 1, 1, NOW()),
    (4104, '修改', 4100, 3, 'education:material:update', 4, 1, 1, NOW()),
    (4105, '删除', 4100, 3, 'education:material:delete', 5, 1, 1, NOW()),
    (4106, '导出', 4100, 3, 'education:material:export', 6, 1, 1, NOW());

