SET @parentId = 1913646786038648832;
-- 学生管理管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '学生管理管理', 1000, 2, '/education/student', 'Student', 'education/student/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 学生管理管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1913646786038648833, '列表', @parentId, 3, 'education:student:list', 1, 1, 1, NOW()),
    (1913646786038648834, '详情', @parentId, 3, 'education:student:get', 2, 1, 1, NOW()),
    (1913646786038648835, '新增', @parentId, 3, 'education:student:create', 3, 1, 1, NOW()),
    (1913646786038648836, '修改', @parentId, 3, 'education:student:update', 4, 1, 1, NOW()),
    (1913646786038648837, '删除', @parentId, 3, 'education:student:delete', 5, 1, 1, NOW()),
    (1913646786038648838, '导出', @parentId, 3, 'education:student:export', 6, 1, 1, NOW());

