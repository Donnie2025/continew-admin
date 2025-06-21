SET @parentId = 1936445502524014592;
-- 班级管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '班级管理', 1000, 2, '/education/course', 'Course', 'education/course/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 班级管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1936445502524014593, '列表', @parentId, 3, 'education:course:list', 1, 1, 1, NOW()),
    (1936445502524014594, '详情', @parentId, 3, 'education:course:get', 2, 1, 1, NOW()),
    (1936445502524014595, '新增', @parentId, 3, 'education:course:create', 3, 1, 1, NOW()),
    (1936445502524014596, '修改', @parentId, 3, 'education:course:update', 4, 1, 1, NOW()),
    (1936445502524014597, '删除', @parentId, 3, 'education:course:delete', 5, 1, 1, NOW()),
    (1936445502528208896, '导出', @parentId, 3, 'education:course:export', 6, 1, 1, NOW());

