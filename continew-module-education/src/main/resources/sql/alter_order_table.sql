-- 扩展订单表，增加支付渠道相关字段

ALTER TABLE `edu_order`
ADD COLUMN `payment_channel_id` bigint(20) DEFAULT NULL COMMENT '支付渠道ID（关联edu_payment_channel.id）' AFTER `paymentType`,
ADD COLUMN `payment_channel_name` varchar(50) DEFAULT NULL COMMENT '支付渠道名称（快照）' AFTER `payment_channel_id`,
ADD COLUMN `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式（online-在线支付 qrcode-扫码支付 offline-线下支付）' AFTER `payment_channel_name`,
ADD KEY `idx_payment_channel_id` (`payment_channel_id`);
