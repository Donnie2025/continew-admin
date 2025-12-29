SET @parentId = 2005630497596002304;
-- 课节管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (4110, '课节管理', 4000, 2, '/education/materialLesson', 'MaterialLesson', 'education/materialLesson/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 课节管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (4111, '列表', 4110, 3, 'education:materialLesson:list', 1, 1, 1, NOW()),
    (4112, '详情', 4110, 3, 'education:materialLesson:get', 2, 1, 1, NOW()),
    (4113, '新增', 4110, 3, 'education:materialLesson:create', 3, 1, 1, NOW()),
    (4114, '修改', 4110, 3, 'education:materialLesson:update', 4, 1, 1, NOW()),
    (4115, '删除', 4110, 3, 'education:materialLesson:delete', 5, 1, 1, NOW()),
    (4116, '导出', 4110, 3, 'education:materialLesson:export', 6, 1, 1, NOW());

