-- =====================================================
-- 公众号管理目录 + 公众号菜单管理
-- =====================================================

-- 公众号管理目录（一级菜单，id=8000）
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (8000, '公众号管理', 0, 1, '/wechat', 'Wechat', 'Layout', '/wechat/mpMenu', 'wechat', b'0', b'0', b'0', NULL, 10, 1, 1, NOW());

-- 公众号菜单管理页面（父菜单 8000：公众号管理）
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `path`, `name`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `is_hidden`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (8010, '公众号菜单', 8000, 2, '/wechat/mpMenu', 'MpMenu', 'wechat/mpMenu/index', NULL, 'menu', b'0', b'0', b'0', NULL, 1, 1, 1, NOW());

-- 公众号菜单管理按钮
INSERT INTO `sys_menu`
    (`id`, `title`, `parent_id`, `type`, `permission`, `sort`, `status`, `create_user`, `create_time`)
VALUES
    (8011, '查询菜单', 8010, 3, 'wechat:mp:menu:get', 1, 1, 1, NOW()),
    (8012, '保存菜单', 8010, 3, 'wechat:mp:menu:save', 2, 1, 1, NOW()),
    (8013, '删除菜单', 8010, 3, 'wechat:mp:menu:delete', 3, 1, 1, NOW());
