SET @parentId = 1908105687924961280;
-- 教师管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '教师管理', 1000, 2, '/education/teacher', 'Teacher', 'education/teacher/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 教师管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1908105687924961281, '列表', @parentId, 3, 'education:teacher:list', 1, 1, 1, NOW()),
    (1908105687924961282, '详情', @parentId, 3, 'education:teacher:get', 2, 1, 1, NOW()),
    (1908105687924961283, '新增', @parentId, 3, 'education:teacher:create', 3, 1, 1, NOW()),
    (1908105687924961284, '修改', @parentId, 3, 'education:teacher:update', 4, 1, 1, NOW()),
    (1908105687924961285, '删除', @parentId, 3, 'education:teacher:delete', 5, 1, 1, NOW()),
    (1908105687924961286, '导出', @parentId, 3, 'education:teacher:export', 6, 1, 1, NOW());


SELECT * FROM sys_menu;


