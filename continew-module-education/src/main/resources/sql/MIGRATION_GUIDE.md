# Data Migration Guide: edu_teacher to edu_teacher_payment

## Overview
This guide provides step-by-step instructions for migrating existing teacher payment-related data from the `edu_teacher` table to the new `edu_teacher_payment` table.

## Migration Mapping

| Source (edu_teacher) | Destination (edu_teacher_payment) | Notes |
|---------------------|-----------------------------------|-------|
| `id` | `teacher_id` | Foreign key reference |
| `name` | `teacher_name` | Redundant field for performance |
| `rate` | `rate` | Teacher's rate/price |
| `phone` | `account_number` | Used as default payment account |
| `recv_name` | `account_name` | Fallback to `name` if NULL |
| `institution_id` | `institution_id` | Foreign key reference |
| `status` | `status` | Same status as teacher |
| `create_time` | `create_time` | Preserve timestamps |
| `update_time` | `update_time` | Preserve timestamps |
| `create_user` | `create_user` | Preserve audit info |
| `update_user` | `update_user` | Preserve audit info |
| N/A | `payment_channel` | Default: 'GCash' |
| N/A | `qr_code` | NULL (not available in old data) |
| N/A | `bank_name` | NULL (not applicable for GCash) |
| N/A | `is_default` | 1 (set as default) |

## Prerequisites

1. **Backup Database**
   ```sql
   -- Backup edu_teacher table
   CREATE TABLE `edu_teacher_backup_20260617` AS SELECT * FROM `edu_teacher`;
   ```

2. **Verify edu_teacher_payment table exists**
   ```sql
   SHOW TABLES LIKE 'edu_teacher_payment';
   ```

3. **Check current data state**
   ```sql
   SELECT COUNT(*) AS total_teachers FROM edu_teacher;
   SELECT COUNT(*) AS existing_payment_records FROM edu_teacher_payment;
   ```

## Migration Steps

### Step 1: Run Migration Script

Execute the migration script:
```bash
mysql -u [username] -p [database_name] < migrate_teacher_to_payment.sql
```

Or run it directly in your SQL client:
```sql
source /path/to/migrate_teacher_to_payment.sql
```

### Step 2: Verify Migration

Run the verification queries included in the script:

**1. Check record counts:**
```sql
SELECT
    (SELECT COUNT(*) FROM `edu_teacher`) AS total_teachers,
    (SELECT COUNT(DISTINCT teacher_id) FROM `edu_teacher_payment`) AS teachers_with_payment;
```
Expected: Both counts should be equal

**2. Find missing payment records:**
```sql
SELECT t.id, t.name, t.phone, t.rate
FROM `edu_teacher` t
LEFT JOIN `edu_teacher_payment` p ON t.id = p.teacher_id
WHERE p.id IS NULL;
```
Expected: Empty result (no missing records)

**3. Check rate migration:**
```sql
SELECT
    t.id,
    t.name,
    t.rate AS teacher_rate,
    p.rate AS payment_rate
FROM `edu_teacher` t
INNER JOIN `edu_teacher_payment` p ON t.id = p.teacher_id
WHERE COALESCE(t.rate, 0) != COALESCE(p.rate, 0);
```
Expected: Empty result (all rates match)

**4. Verify teacher names:**
```sql
SELECT p.id, p.teacher_id, p.teacher_name, t.name
FROM `edu_teacher_payment` p
INNER JOIN `edu_teacher` t ON p.teacher_id = t.id
WHERE p.teacher_name != t.name;
```
Expected: Empty result (all names match)

### Step 3: Review Data Quality

Check for data that may need manual review:

**Teachers with zero or NULL rate:**
```sql
SELECT p.teacher_id, p.teacher_name, p.rate, p.account_number
FROM `edu_teacher_payment` p
WHERE p.rate IS NULL OR p.rate = 0
ORDER BY p.teacher_id;
```
Action: Admin should set appropriate rates for these teachers

**Teachers with missing account numbers:**
```sql
SELECT p.teacher_id, p.teacher_name, p.account_number
FROM `edu_teacher_payment` p
WHERE p.account_number IS NULL OR p.account_number = ''
ORDER BY p.teacher_id;
```
Action: Teachers should be notified to update payment information

### Step 4: Optional Cleanup

After successful migration and verification, you can optionally clean up the `edu_teacher` table:

**Remove rate field from edu_teacher:**
```sql
-- WARNING: Only run after confirming all data is correctly migrated!
ALTER TABLE `edu_teacher` DROP COLUMN `rate`;
```

**Remove recv_name field (now in payment table):**
```sql
-- WARNING: Only run after confirming all data is correctly migrated!
ALTER TABLE `edu_teacher` DROP COLUMN `recv_name`;
```

## Rollback Procedure

If migration fails or issues are detected:

1. **Delete migrated records:**
   ```sql
   DELETE FROM `edu_teacher_payment` WHERE create_time >= '[migration_start_time]';
   ```

2. **Restore from backup if needed:**
   ```sql
   -- If you dropped columns from edu_teacher
   DROP TABLE `edu_teacher`;
   RENAME TABLE `edu_teacher_backup_20260617` TO `edu_teacher`;
   ```

## Post-Migration Tasks

1. **Update Admin Interface**
   - Ensure admin can view/edit payment information in `edu_teacher_payment`
   - Update teacher management screens to show rate from payment table

2. **Notify Teachers**
   - Send notification about new payment management system
   - Ask teachers to verify and update payment information
   - Explain how to add QR codes for easier payment processing

3. **Monitor System**
   - Check logs for any errors accessing payment data
   - Verify salary calculations still work correctly
   - Test payment processing workflows

4. **Documentation Update**
   - Update API documentation to reflect new payment table
   - Update database schema documentation
   - Update developer guides

## Common Issues and Solutions

### Issue 1: Migration script fails with foreign key constraint error
**Solution:** Ensure `edu_teacher_payment` table is created with proper foreign key constraints to `edu_teacher` and `edu_institution` tables.

### Issue 2: Some teachers have NULL institution_id
**Solution:** Update teachers with missing institution_id before migration:
```sql
UPDATE `edu_teacher` 
SET institution_id = (SELECT MIN(id) FROM `edu_institution`) 
WHERE institution_id IS NULL;
```

### Issue 3: Rate values are incorrect after migration
**Solution:** Check if rate field exists in your actual `edu_teacher` table:
```sql
DESCRIBE `edu_teacher`;
```
If rate field doesn't exist, the script will default to 0, which is correct for new registrations.

### Issue 4: Duplicate payment records
**Solution:** The migration script uses `NOT EXISTS` to prevent duplicates. If duplicates occur:
```sql
DELETE p1 FROM `edu_teacher_payment` p1
INNER JOIN `edu_teacher_payment` p2 
WHERE p1.teacher_id = p2.teacher_id AND p1.id > p2.id;
```

## Verification Checklist

- [ ] Backup created successfully
- [ ] Migration script executed without errors
- [ ] All teachers have payment records (counts match)
- [ ] Rate values correctly migrated
- [ ] Teacher names correctly populated
- [ ] Account numbers and names populated
- [ ] Institution IDs correctly set
- [ ] Timestamps and audit fields preserved
- [ ] No duplicate payment records
- [ ] Application can read from new payment table
- [ ] Admin interface updated
- [ ] Testing completed successfully

## Script Location

Migration script: `/continew-admin/continew-module-education/src/main/resources/sql/migrate_teacher_to_payment.sql`

## Support

If you encounter issues during migration:
1. Check the verification queries output
2. Review database logs for errors
3. Ensure all foreign key references are valid
4. Contact the development team with specific error messages

## Migration Statistics Template

After migration, document the results:

```
Migration Date: [Date]
Total Teachers: [Count]
Payment Records Created: [Count]
Teachers with Rate > 0: [Count]
Teachers with Rate = 0: [Count]
Migration Duration: [Time]
Issues Encountered: [None/List]
Rollback Required: [Yes/No]
```
