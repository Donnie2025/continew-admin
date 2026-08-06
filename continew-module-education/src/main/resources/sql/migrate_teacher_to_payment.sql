-- ============================================================================
-- Data Migration Script: edu_teacher to edu_teacher_payment
-- ============================================================================
-- Purpose: Migrate teacher payment-related data from edu_teacher to edu_teacher_payment
-- Fields to migrate:
--   - name -> teacher_name (redundant field)
--   - rate -> rate
--   - phone -> account_number (as default payment account)
--   - recv_name -> account_name (if exists, otherwise use name)
--   - brief_intro -> extract GCash info if stored there
-- ============================================================================

-- Step 1: Ensure edu_teacher_payment table exists with correct schema
-- (This should already be created via main_table.sql, but checking here)

-- Step 2: Migrate existing teachers to edu_teacher_payment
-- This will create payment records for all teachers who don't have one yet

INSERT INTO `edu_teacher_payment` (
    `teacher_id`,
    `teacher_name`,
    `payment_channel`,
    `account_number`,
    `account_name`,
    `qr_code`,
    `bank_name`,
    `rate`,
    `is_default`,
    `status`,
    `create_time`,
    `update_time`,
    `create_user`,
    `update_user`,
    `institution_id`
)
SELECT
    t.id AS teacher_id,
    t.name AS teacher_name,
    -- Default to GCash as payment channel
    'GCash' AS payment_channel,
    -- Use phone as account number (fallback to empty string if NULL)
    COALESCE(t.phone, '') AS account_number,
    -- Use recv_name if exists, otherwise use teacher name
    COALESCE(t.recv_name, t.name) AS account_name,
    -- QR code not available in old data
    NULL AS qr_code,
    -- Bank name not applicable for GCash
    NULL AS bank_name,
    -- Migrate rate (default to 0 if NULL)
    COALESCE(t.rate, 0) AS rate,
    -- Set as default payment method
    1 AS is_default,
    -- Use same status as teacher
    t.status AS status,
    -- Preserve timestamps
    t.create_time AS create_time,
    t.update_time AS update_time,
    t.create_user AS create_user,
    t.update_user AS update_user,
    t.institution_id AS institution_id
FROM `edu_teacher` t
WHERE NOT EXISTS (
    -- Only migrate if payment record doesn't exist yet
    SELECT 1 FROM `edu_teacher_payment` p WHERE p.teacher_id = t.id
);

-- Step 3: Update existing payment records with missing data from edu_teacher
-- (In case payment records exist but are incomplete)

UPDATE `edu_teacher_payment` p
INNER JOIN `edu_teacher` t ON p.teacher_id = t.id
SET
    p.teacher_name = t.name,
    p.rate = COALESCE(p.rate, t.rate, 0),
    p.account_name = COALESCE(p.account_name, t.recv_name, t.name),
    p.account_number = COALESCE(NULLIF(p.account_number, ''), t.phone, ''),
    p.institution_id = t.institution_id
WHERE
    p.teacher_name IS NULL
    OR p.rate IS NULL
    OR p.rate = 0
    OR p.account_name IS NULL
    OR p.account_number = ''
    OR p.institution_id IS NULL;

-- ============================================================================
-- Verification Queries
-- ============================================================================

-- 1. Check total teacher count vs payment records count
SELECT
    (SELECT COUNT(*) FROM `edu_teacher`) AS total_teachers,
    (SELECT COUNT(DISTINCT teacher_id) FROM `edu_teacher_payment`) AS teachers_with_payment,
    (SELECT COUNT(*) FROM `edu_teacher`) - (SELECT COUNT(DISTINCT teacher_id) FROM `edu_teacher_payment`) AS missing_payment_records;

-- 2. Find teachers without payment records
SELECT
    t.id,
    t.name,
    t.phone,
    t.rate,
    t.status,
    'Missing payment record' AS issue
FROM `edu_teacher` t
LEFT JOIN `edu_teacher_payment` p ON t.id = p.teacher_id
WHERE p.id IS NULL;

-- 3. Check payment records with missing or zero rate
SELECT
    p.id,
    p.teacher_id,
    p.teacher_name,
    p.rate,
    p.payment_channel,
    p.account_number,
    'Rate is NULL or 0' AS issue
FROM `edu_teacher_payment` p
WHERE p.rate IS NULL OR p.rate = 0;

-- 4. Check payment records with missing teacher_name
SELECT
    p.id,
    p.teacher_id,
    p.teacher_name,
    t.name AS actual_teacher_name,
    'Missing teacher_name' AS issue
FROM `edu_teacher_payment` p
INNER JOIN `edu_teacher` t ON p.teacher_id = t.id
WHERE p.teacher_name IS NULL OR p.teacher_name = '';

-- 5. Compare rate values between tables
SELECT
    t.id,
    t.name,
    t.rate AS teacher_rate,
    p.rate AS payment_rate,
    'Rate mismatch' AS issue
FROM `edu_teacher` t
INNER JOIN `edu_teacher_payment` p ON t.id = p.teacher_id
WHERE COALESCE(t.rate, 0) != COALESCE(p.rate, 0);

-- 6. Summary statistics
SELECT
    'Teachers with rate > 0' AS category,
    COUNT(*) AS count
FROM `edu_teacher`
WHERE rate > 0
UNION ALL
SELECT
    'Payment records with rate > 0' AS category,
    COUNT(*) AS count
FROM `edu_teacher_payment`
WHERE rate > 0
UNION ALL
SELECT
    'Payment records with GCash' AS category,
    COUNT(*) AS count
FROM `edu_teacher_payment`
WHERE payment_channel = 'GCash'
UNION ALL
SELECT
    'Payment records with Maya' AS category,
    COUNT(*) AS count
FROM `edu_teacher_payment`
WHERE payment_channel = 'Maya'
UNION ALL
SELECT
    'Payment records with Bank' AS category,
    COUNT(*) AS count
FROM `edu_teacher_payment`
WHERE payment_channel = 'Bank';

-- ============================================================================
-- Optional: Backup queries (run BEFORE migration)
-- ============================================================================

-- Backup edu_teacher table
-- CREATE TABLE `edu_teacher_backup_20260617` AS SELECT * FROM `edu_teacher`;

-- ============================================================================
-- Optional: Cleanup after successful migration and verification
-- ============================================================================

-- If rate field exists in edu_teacher and needs to be removed:
-- ALTER TABLE `edu_teacher` DROP COLUMN `rate`;

-- If recv_name is no longer needed (data moved to payment table):
-- ALTER TABLE `edu_teacher` DROP COLUMN `recv_name`;

-- ============================================================================
-- Notes:
-- ============================================================================
-- 1. This script is idempotent - safe to run multiple times
-- 2. Default payment channel is set to 'GCash' for all existing teachers
-- 3. Phone number is used as default account_number
-- 4. recv_name is used as account_name, fallback to teacher name
-- 5. Rate is migrated from edu_teacher.rate field
-- 6. QR code and bank_name are not migrated (not available in old structure)
-- 7. Verify all data before dropping columns from edu_teacher table
-- ============================================================================
