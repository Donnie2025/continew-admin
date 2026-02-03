-- 修复 edu_material_lesson 表的 material_name 字段问题
-- 将 material_name 字段从 NOT NULL 改为 DEFAULT NULL

-- 如果表已存在，修改字段约束
ALTER TABLE `edu_material_lesson` 
MODIFY COLUMN `material_name` varchar(150) DEFAULT NULL COMMENT '教材名称（冗余字段，格式：name + level）';

-- 为现有记录填充 material_name 字段（如果为空）
UPDATE `edu_material_lesson` ml
JOIN `edu_material` m ON ml.material_id = m.id
SET ml.material_name = CONCAT(m.name, ' ', m.level)
WHERE ml.material_name IS NULL OR ml.material_name = '';
