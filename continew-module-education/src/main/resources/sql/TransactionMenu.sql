SET @parentId = 1921206354992902144;
-- 订单管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '订单管理', 1000, 2, '/education/transaction', 'Transaction', 'education/transaction/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 订单管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1921206354992902145, '列表', @parentId, 3, 'education:transaction:list', 1, 1, 1, NOW()),
    (1921206354992902146, '详情', @parentId, 3, 'education:transaction:get', 2, 1, 1, NOW()),
    (1921206354992902147, '新增', @parentId, 3, 'education:transaction:create', 3, 1, 1, NOW()),
    (1921206354992902148, '修改', @parentId, 3, 'education:transaction:update', 4, 1, 1, NOW()),
    (1921206354992902149, '删除', @parentId, 3, 'education:transaction:delete', 5, 1, 1, NOW()),
    (1921206354992902150, '导出', @parentId, 3, 'education:transaction:export', 6, 1, 1, NOW());

