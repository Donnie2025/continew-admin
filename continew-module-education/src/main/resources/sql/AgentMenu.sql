-- =====================================================
-- 机构管理目录及机构课堂管理菜单
-- =====================================================

-- 机构管理目录（一级菜单）
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (6000, '机构管理', 0, 1, '/agent', 'Agent', 'Layout', '/agent/course', 'organization', b'0', b'0', b'0', NULL, 4, 1, 1, NOW());

-- 机构课堂管理菜单（二级菜单）
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (6010, '机构课堂管理', 6000, 2, '/agent/course', 'AgentCourse', 'agent/course/index', NULL, 'group', b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 机构课堂管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (6011, '分页查询', 6010, 3, 'agent:course:page', 1, 1, 1, NOW()),
    (6012, '详情', 6010, 3, 'agent:course:get', 2, 1, 1, NOW()),
    (6013, '新增', 6010, 3, 'agent:course:create', 3, 1, 1, NOW()),
    (6014, '修改', 6010, 3, 'agent:course:update', 4, 1, 1, NOW()),
    (6015, '删除', 6010, 3, 'agent:course:delete', 5, 1, 1, NOW()),
    (6016, '导出', 6010, 3, 'agent:course:export', 6, 1, 1, NOW());
