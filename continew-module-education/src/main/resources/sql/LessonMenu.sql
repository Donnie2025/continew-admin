
-- 课堂管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (4090, '课堂管理', 4000, 2, '/education/lesson', 'Lesson', 'education/lesson/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 课堂管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (4091, '列表', 4090, 3, 'education:lesson:list', 1, 1, 1, NOW()),
    (4092, '详情', 4090, 3, 'education:lesson:get', 2, 1, 1, NOW()),
    (4093, '新增', 4090, 3, 'education:lesson:create', 3, 1, 1, NOW()),
    (4094, '修改', 4090, 3, 'education:lesson:update', 4, 1, 1, NOW()),
    (4095, '删除', 4090, 3, 'education:lesson:delete', 5, 1, 1, NOW()),
    (4096, '导出', 4090, 3, 'education:lesson:export', 6, 1, 1, NOW());

