SET @parentId = 1915789094777139200;
-- 课程管理管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '课程管理管理', 1000, 2, '/education/slot', 'Slot', 'education/slot/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 课程管理管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1915789094777139201, '列表', @parentId, 3, 'education:slot:list', 1, 1, 1, NOW()),
    (1915789094777139202, '详情', @parentId, 3, 'education:slot:get', 2, 1, 1, NOW()),
    (1915789094777139203, '新增', @parentId, 3, 'education:slot:create', 3, 1, 1, NOW()),
    (1915789094777139204, '修改', @parentId, 3, 'education:slot:update', 4, 1, 1, NOW()),
    (1915789094777139205, '删除', @parentId, 3, 'education:slot:delete', 5, 1, 1, NOW()),
    (1915789094777139206, '导出', @parentId, 3, 'education:slot:export', 6, 1, 1, NOW());

