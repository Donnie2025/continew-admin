-- 为 edu_material 表添加飞书文件夹token字段
-- 用于存储飞书云空间文件夹token，支持上传文件时定位目标文件夹

ALTER TABLE edu_material
ADD COLUMN feishu_folder_token VARCHAR(100) DEFAULT NULL
COMMENT '飞书云空间文件夹token（文件夹节点使用，LESSON节点为NULL）'
AFTER cloud_name;

-- 添加索引以提高查询性能
CREATE INDEX idx_feishu_folder_token ON edu_material(feishu_folder_token);
