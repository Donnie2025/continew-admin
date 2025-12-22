SET @parentId = 1993682471440060416;
-- 订单管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '订单管理', 5000, 2, '/education/order', 'Order', 'education/order/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 订单管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (5011, '列表', 5010, 3, 'education:order:list', 1, 1, 1, NOW()),
    (5012, '详情', 5010, 3, 'education:order:get', 2, 1, 1, NOW()),
    (5013, '新增', 5010, 3, 'education:order:create', 3, 1, 1, NOW()),
    (5014, '修改', 5010, 3, 'education:order:update', 4, 1, 1, NOW()),
    (5015, '删除', 5010, 3, 'education:order:delete', 5, 1, 1, NOW()),
    (5016, '导出', 5010, 3, 'education:order:export', 6, 1, 1, NOW());

