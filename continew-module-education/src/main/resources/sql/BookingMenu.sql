
-- 预约管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (4070, '预约管理', 4000, 2, '/education/booking', 'Booking', 'education/booking/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 预约管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (4071, '列表', 4070, 3, 'education:booking:list', 1, 1, 1, NOW()),
    (4072, '详情', 4070, 3, 'education:booking:get', 2, 1, 1, NOW()),
    (4073, '新增', 4070, 3, 'education:booking:create', 3, 1, 1, NOW()),
    (4074, '修改', 4070, 3, 'education:booking:update', 4, 1, 1, NOW()),
    (4075, '删除', 4070, 3, 'education:booking:delete', 5, 1, 1, NOW()),
    (4076, '导出', 4070, 3, 'education:booking:export', 6, 1, 1, NOW());

