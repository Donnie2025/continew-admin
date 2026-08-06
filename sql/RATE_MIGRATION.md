# Teacher Registration - Rate Migration to Payment Table

## Overview
Migrated the `rate` field from `edu_teacher` table to `edu_teacher_payment` table, and added `teacher_name` as a redundant field for better data organization.

## Database Changes

### edu_teacher_payment Table Schema (Updated)

```sql
CREATE TABLE IF NOT EXISTS `edu_teacher_payment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `teacher_name` varchar(100) DEFAULT NULL COMMENT '教师姓名（冗余字段）',
  `payment_channel` varchar(20) NOT NULL COMMENT '支付渠道（GCash、Maya、Bank）',
  `account_number` varchar(100) NOT NULL COMMENT '账号',
  `account_name` varchar(100) NOT NULL COMMENT '账户名',
  `qr_code` varchar(512) DEFAULT NULL COMMENT '收款二维码地址',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '银行名称（当payment_channel为Bank时使用）',
  `rate` int DEFAULT '0' COMMENT '单价（从edu_teacher表迁移）',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认收款方式',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_user` bigint DEFAULT NULL,
  `update_user` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_payment_channel` (`payment_channel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### New Fields Added
1. **teacher_name** (varchar(100)): Redundant field storing teacher's name for easier queries
2. **rate** (int): Teacher's rate/price, migrated from edu_teacher table

### edu_teacher Table
- **rate** field can be removed after migration (optional)
- All rate information now stored in edu_teacher_payment

## Migration Strategy

### Step 1: Update Table Schema
Run the table creation script or alter existing table:
```sql
ALTER TABLE `edu_teacher_payment`
ADD COLUMN `teacher_name` varchar(100) DEFAULT NULL COMMENT '教师姓名（冗余字段）' AFTER `teacher_id`,
ADD COLUMN `rate` int DEFAULT '0' COMMENT '单价（从edu_teacher表迁移）' AFTER `bank_name`;
```

### Step 2: Migrate Existing Data
Use the migration script: `sql/migrate_rate_to_payment.sql`
- Creates payment records for teachers with rate but no payment info
- Updates existing payment records with rate and teacher_name
- Includes verification queries

### Step 3: Verify Migration
Check the verification queries in the migration script to ensure:
- All teachers with rate have payment records
- All payment records have rate and teacher_name populated

### Step 4: Update Application (Already Done)
✅ Updated `TeacherPaymentDO` entity with new fields
✅ Updated `TeacherPaymentService` to handle rate and teacher_name
✅ Updated `TeacherServiceImpl` registration to save rate (initially 0)

## Code Changes

### Backend Files Modified

1. **TeacherPaymentDO.java**
   - Added `teacherName` field
   - Added `rate` field

2. **TeacherPaymentService.java**
   - Updated `saveOrUpdate()` method signature to include `teacherName` and `rate`

3. **TeacherPaymentServiceImpl.java**
   - Updated implementation to save/update `teacherName` and `rate`
   - Rate defaults to 0 if not provided

4. **TeacherServiceImpl.java**
   - Updated registration to pass teacher name to payment service
   - Rate set to 0 for new registrations (to be updated by admin)

### SQL Files

1. **edu_teacher_payment.sql** - Updated table creation script
2. **migrate_rate_to_payment.sql** - New migration script for existing data

## Benefits of This Structure

1. **Better Data Organization**: Payment-related data (channel, account, rate) in one table
2. **Redundancy for Performance**: teacher_name in payment table reduces JOIN operations
3. **Flexibility**: Teachers can have different rates for different payment methods (future enhancement)
4. **Cleaner Separation**: Teacher profile info separate from payment/financial info

## Usage After Migration

### Creating New Teacher (Registration)
```java
// Teacher entity - no rate field needed
teacher.setName("John Doe");
teacher.setStatus(1);
// ... other fields

// Payment entity - includes rate
teacherPaymentService.saveOrUpdate(
    teacherId,
    teacherName,    // NEW: redundant name field
    paymentChannel,
    accountNumber,
    accountName,
    qrCode,
    bankName,
    0              // NEW: rate field (starts at 0)
);
```

### Admin Updating Rate
```java
// Get existing payment record
TeacherPaymentDO payment = teacherPaymentService.getByTeacherId(teacherId);

// Update rate
payment.setRate(newRate);
teacherPaymentMapper.updateById(payment);
```

### Querying Teacher with Rate
```java
// No need to join edu_teacher - all info in payment table
TeacherPaymentDO payment = teacherPaymentService.getByTeacherId(teacherId);
String teacherName = payment.getTeacherName();  // Redundant field
Integer rate = payment.getRate();                // Rate from payment table
```

## Migration Checklist

- [x] Update table schema with new fields
- [ ] Run migration script to move existing data
- [ ] Verify all teachers with rate have payment records
- [ ] Verify all payment records have rate and teacher_name
- [x] Update backend entities and services
- [x] Update registration logic to save teacher_name and rate
- [ ] Test new teacher registration
- [ ] Test admin rate updates
- [ ] (Optional) Remove rate field from edu_teacher table

## Notes

- Rate starts at 0 for new teacher registrations
- Admin should update rate through payment management interface
- teacher_name is automatically updated when payment info is saved
- Existing teachers without payment records get auto-created records during migration
- Migration script is idempotent - safe to run multiple times

## Files Modified

### Backend
- ✅ `TeacherPaymentDO.java` - Added teacherName and rate fields
- ✅ `TeacherPaymentService.java` - Updated method signature
- ✅ `TeacherPaymentServiceImpl.java` - Updated implementation
- ✅ `TeacherServiceImpl.java` - Pass teacher name to payment service

### SQL
- ✅ `edu_teacher_payment.sql` - Updated schema
- ✅ `migrate_rate_to_payment.sql` - New migration script

### Documentation
- ✅ `RATE_MIGRATION.md` - This file
