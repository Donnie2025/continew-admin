-- 调试会员卡数据查询问题
-- 检查数据库中的会员卡数据状态

-- 1. 检查 edu_stu_card 表结构
DESCRIBE edu_stu_card;

-- 2. 检查是否有会员卡数据
SELECT COUNT(*) as total_cards FROM edu_stu_card;

-- 3. 检查用户ID为1的会员卡数据
SELECT * FROM edu_stu_card WHERE stu_id = 1;

-- 4. 检查所有会员卡数据（限制10条）
SELECT id, stu_id, stu_name, card_id, card_title, card_type, balance, status, card_status 
FROM edu_stu_card 
ORDER BY id DESC 
LIMIT 10;

-- 5. 检查启用状态的会员卡
SELECT COUNT(*) as enabled_cards 
FROM edu_stu_card 
WHERE status = 1;

-- 6. 检查用户ID为1且启用状态的会员卡
SELECT COUNT(*) as user1_enabled_cards 
FROM edu_stu_card 
WHERE stu_id = 1 AND status = 1;

-- 7. 检查字段名是否正确（如果查询失败，可能是字段名问题）
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'edu_stu_card'
ORDER BY ORDINAL_POSITION;

-- 8. 如果没有数据，插入测试数据
-- INSERT INTO edu_stu_card (stu_id, stu_name, card_id, card_title, card_type, balance, status, card_status, create_time, create_user) 
-- VALUES (1, '测试学生', 1, '体验课程', 'TL', 10.00, 1, 1, NOW(), 1);

-- 注意事项：
-- 1. 如果字段名不匹配，需要执行字段重命名SQL
-- 2. 如果没有测试数据，需要插入一些测试数据
-- 3. 检查 balance 字段是否存在，可能还是 remain_balance
