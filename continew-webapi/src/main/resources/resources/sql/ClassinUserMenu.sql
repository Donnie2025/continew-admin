SET @parentId = 1911039026192490496;
-- Classin用户管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, 'Classin用户管理', 1000, 2, '/education/classinUser', 'ClassinUser', 'education/classinUser/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- Classin用户管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1911039026192490497, '列表', @parentId, 3, 'education:classinUser:list', 1, 1, 1, NOW()),
    (1911039026192490498, '详情', @parentId, 3, 'education:classinUser:get', 2, 1, 1, NOW()),
    (1911039026192490499, '新增', @parentId, 3, 'education:classinUser:create', 3, 1, 1, NOW()),
    (1911039026192490500, '修改', @parentId, 3, 'education:classinUser:update', 4, 1, 1, NOW()),
    (1911039026192490501, '删除', @parentId, 3, 'education:classinUser:delete', 5, 1, 1, NOW()),
    (1911039026192490502, '导出', @parentId, 3, 'education:classinUser:export', 6, 1, 1, NOW());

