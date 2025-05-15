SET @parentId = 1922301753212239872;
-- 薪资管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '薪资管理', 1000, 2, '/education/salary', 'Salary', 'education/salary/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 薪资管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1922301753212239873, '列表', @parentId, 3, 'education:salary:list', 1, 1, 1, NOW()),
    (1922301753212239874, '详情', @parentId, 3, 'education:salary:get', 2, 1, 1, NOW()),
    (1922301753212239875, '新增', @parentId, 3, 'education:salary:create', 3, 1, 1, NOW()),
    (1922301753212239876, '修改', @parentId, 3, 'education:salary:update', 4, 1, 1, NOW()),
    (1922301753212239877, '删除', @parentId, 3, 'education:salary:delete', 5, 1, 1, NOW()),
    (1922301753212239878, '导出', @parentId, 3, 'education:salary:export', 6, 1, 1, NOW());

