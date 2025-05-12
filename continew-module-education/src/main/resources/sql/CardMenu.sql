SET @parentId = 1920872945301639168;
-- 会员卡管理管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (4040, '会员卡管理管理', 4000, 2, '/education/card', 'Card', 'education/card/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 会员卡管理管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (4041, '列表', 4040, 3, 'education:card:list', 1, 1, 1, NOW()),
    (4042, '详情', 4040, 3, 'education:card:get', 2, 1, 1, NOW()),
    (4043, '新增', 4040, 3, 'education:card:create', 3, 1, 1, NOW()),
    (4044, '修改', 4040, 3, 'education:card:update', 4, 1, 1, NOW()),
    (4045, '删除', 4040, 3, 'education:card:delete', 5, 1, 1, NOW()),
    (4046, '导出', 4040, 3, 'education:card:export', 6, 1, 1, NOW());

