-- ============================================================
-- edu_transaction 表重构：引入 Credit/Debit 记账模型
-- 基于实际表结构（含 before_amt / after_amt）
-- 执行前请备份数据！
-- ============================================================

-- Step 1：添加新字段
ALTER TABLE `edu_transaction`
  MODIFY COLUMN `trans_type` varchar(32) NOT NULL
    COMMENT '交易类型（bind:首次购买 recharge:续费充值 consume:消费扣课时 refund:退款 expire:到期清零 adjust:人工调整）',
  ADD COLUMN `direction`   char(1)        NOT NULL DEFAULT 'C'
    COMMENT '借贷方向（C=Credit入账/增加余额, D=Debit出账/减少余额）'
    AFTER `trans_type`,
  ADD COLUMN `balance`     decimal(10,2)  NOT NULL DEFAULT 0
    COMMENT '交易后课时余额快照'
    AFTER `amount`,
  ADD COLUMN `cash_amount` decimal(10,2)  DEFAULT NULL
    COMMENT '实际现金金额（购买/退款时）'
    AFTER `balance`;

-- Step 2：数据迁移 — 根据 trans_type 推断 direction
-- trans_type 枚举值说明：
--   bind     - 首次购买会员卡（C，有 cash_amount）
--   recharge - 续费充值（C，有 cash_amount）
--   consume  - 上课消费扣减课时（D，cash_amount=null）
--   refund   - 退款退课时（C，有 cash_amount）
--   expire   - 到期清零（D，cash_amount=null）
--   adjust   - 人工调整课时（C/D，cash_amount=null）
UPDATE `edu_transaction` SET `direction` = 'C'
  WHERE `trans_type` IN ('bind', 'recharge', 'refund', 'activate');

UPDATE `edu_transaction` SET `direction` = 'D'
  WHERE `trans_type` IN ('consume', 'expire');

-- Step 3：将旧的 after_amt 迁移到新 balance 列
UPDATE `edu_transaction` SET `balance` = `after_amt` WHERE 1 = 1;

-- Step 4：删除旧的 before_amt / after_amt 字段
ALTER TABLE `edu_transaction`
  DROP COLUMN `before_amt`,
  DROP COLUMN `after_amt`;

-- Step 5：验证结果
-- DESCRIBE edu_transaction;
-- SELECT id, trans_type, direction, amount, balance, cash_amount FROM edu_transaction LIMIT 10;
