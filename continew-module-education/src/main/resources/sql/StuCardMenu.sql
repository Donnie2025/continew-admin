SET @parentId = 1921206422332452864;
-- 会员绑卡管理菜单
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (@parentId, '会员绑卡管理', 1000, 2, '/education/stuCard', 'StuCard', 'education/stuCard/index', NULL, NULL, b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 会员绑卡管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (1921206422332452865, '列表', @parentId, 3, 'education:stuCard:list', 1, 1, 1, NOW()),
    (1921206422332452866, '详情', @parentId, 3, 'education:stuCard:get', 2, 1, 1, NOW()),
    (1921206422332452867, '新增', @parentId, 3, 'education:stuCard:create', 3, 1, 1, NOW()),
    (1921206422332452868, '修改', @parentId, 3, 'education:stuCard:update', 4, 1, 1, NOW()),
    (1921206422332452869, '删除', @parentId, 3, 'education:stuCard:delete', 5, 1, 1, NOW()),
    (1921206422332452870, '导出', @parentId, 3, 'education:stuCard:export', 6, 1, 1, NOW());

