# edu_account 表改造应用代码修改指南

## 一、改造概述

### 1.1 改造目标
将 `edu_account` 表从仅支持学生扩展为支持**学生、教师、管理员**三种用户类型。

### 1.2 核心变更
| 变更项 | 旧设计 | 新设计 |
|--------|--------|--------|
| 用户标识 | `student_id` | `user_type` + `user_id` |
| 用户名称 | `student_name` | `user_name` |
| 唯一约束 | `(student_id, account_type)` | `(user_type, user_id, account_type)` |

### 1.3 影响范围
- **数据库表**: `edu_account`, `edu_transaction`, `edu_order`
- **Java实体类**: Account, Transaction, Order 相关的 DO/VO/DTO
- **业务逻辑**: 账户创建、查询、交易记录等
- **API接口**: 可能需要调整请求/响应参数

---

## 二、Java 代码修改示例

### 2.1 实体类修改 (DO)

#### 修改前 - AccountDO.java
```java
@Data
@TableName("edu_account")
public class AccountDO extends BaseDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 学生ID */
    private Long studentId;
    
    /** 学生姓名（快照） */
    private String studentName;
    
    /** 账户类型 */
    private String accountType;
    
    /** 当前课时余额 */
    private BigDecimal balance;
    
    /** 到期日期 */
    private LocalDate expireDate;
    
    /** 状态 */
    private Integer status;
    
    /** 备注 */
    private String remark;
}
```

#### 修改后 - AccountDO.java
```java
@Data
@TableName("edu_account")
public class AccountDO extends BaseDO {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 用户类型（student-学生 teacher-教师 admin-管理员） */
    private String userType;
    
    /** 用户ID */
    private Long userId;
    
    /** 用户姓名（快照） */
    private String userName;
    
    /** 账户类型 */
    private String accountType;
    
    /** 当前课时余额 */
    private BigDecimal balance;
    
    /** 到期日期 */
    private LocalDate expireDate;
    
    /** 状态 */
    private Integer status;
    
    /** 备注 */
    private String remark;
}
```

> 旧字段 `studentId`、`studentName` 已直接删除，不保留。

### 2.2 枚举类定义

#### UserTypeEnum.java
```java
@Getter
@AllArgsConstructor
public enum UserTypeEnum {
    
    /** 学生 */
    STUDENT("student", "学生"),
    
    /** 教师 */
    TEACHER("teacher", "教师"),
    
    /** 管理员 */
    ADMIN("admin", "管理员");
    
    private final String code;
    private final String desc;
    
    /**
     * 根据code获取枚举
     */
    public static UserTypeEnum getByCode(String code) {
        for (UserTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 校验code是否有效
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
}
```

### 2.3 Mapper 修改

#### AccountMapper.java
```java
@Mapper
public interface AccountMapper extends BaseMapper<AccountDO> {
    
    /**
     * 根据用户类型和用户ID查询账户列表
     */
    @Select("SELECT * FROM edu_account WHERE user_type = #{userType} AND user_id = #{userId} AND status = 1")
    List<AccountDO> selectByUser(@Param("userType") String userType, @Param("userId") Long userId);
    
    /**
     * 根据用户类型、用户ID和账户类型查询账户
     */
    @Select("SELECT * FROM edu_account WHERE user_type = #{userType} AND user_id = #{userId} AND account_type = #{accountType}")
    AccountDO selectByUserAndType(@Param("userType") String userType, 
                                   @Param("userId") Long userId, 
                                   @Param("accountType") String accountType);
    
    /**
     * 查询即将过期的账户（7天内）
     */
    @Select("SELECT * FROM edu_account WHERE expire_date IS NOT NULL " +
            "AND expire_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY) " +
            "AND status = 1 ORDER BY expire_date ASC")
    List<AccountDO> selectExpiringSoon();
}
```

### 2.4 Service 修改

#### AccountService.java
```java
public interface AccountService extends IService<AccountDO> {
    
    /**
     * 创建账户（新版，支持多用户类型）
     */
    Long createAccount(String userType, Long userId, String userName, 
                       String accountType, BigDecimal balance, LocalDate expireDate);
    
    /**
     * 获取用户的所有账户
     */
    List<AccountDO> getUserAccounts(String userType, Long userId);
    
    /**
     * 获取用户特定类型的账户
     */
    AccountDO getUserAccount(String userType, Long userId, String accountType);
    
    /**
     * 充值/扣费
     */
    boolean updateBalance(Long accountId, BigDecimal amount, String transType, String remark);
    
}
```

#### AccountServiceImpl.java
```java
@Service
@Slf4j
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDO> implements AccountService {
    
    @Autowired
    private TransactionService transactionService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAccount(String userType, Long userId, String userName,
                              String accountType, BigDecimal balance, LocalDate expireDate) {
        
        // 参数校验
        if (!UserTypeEnum.isValid(userType)) {
            throw new BusinessException("无效的用户类型: " + userType);
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 检查账户是否已存在
        AccountDO existing = baseMapper.selectByUserAndType(userType, userId, accountType);
        if (existing != null) {
            throw new BusinessException("该用户的此类型账户已存在");
        }
        
        // 创建账户
        AccountDO account = new AccountDO();
        account.setUserType(userType);
        account.setUserId(userId);
        account.setUserName(userName);
        account.setAccountType(accountType);
        account.setBalance(balance);
        account.setExpireDate(expireDate);
        account.setStatus(1);
        
        baseMapper.insert(account);
        
        // 如果初始余额大于0，记录交易流水
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            transactionService.recordTransaction(
                account.getId(), userType, userId, userName,
                "bind", "C", balance, balance, null, "账户创建"
            );
        }
        
        return account.getId();
    }
    
    @Override
    public List<AccountDO> getUserAccounts(String userType, Long userId) {
        if (!UserTypeEnum.isValid(userType)) {
            throw new BusinessException("无效的用户类型: " + userType);
        }
        return baseMapper.selectByUser(userType, userId);
    }
    
    @Override
    public AccountDO getUserAccount(String userType, Long userId, String accountType) {
        if (!UserTypeEnum.isValid(userType)) {
            throw new BusinessException("无效的用户类型: " + userType);
        }
        return baseMapper.selectByUserAndType(userType, userId, accountType);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBalance(Long accountId, BigDecimal amount, String transType, String remark) {
        AccountDO account = baseMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException("账户不存在");
        }
        
        // 计算新余额
        BigDecimal newBalance = account.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("账户余额不足");
        }
        
        // 更新余额
        account.setBalance(newBalance);
        int rows = baseMapper.updateById(account);
        
        // 记录交易流水
        if (rows > 0) {
            String direction = amount.compareTo(BigDecimal.ZERO) >= 0 ? "C" : "D";
            transactionService.recordTransaction(
                accountId, account.getUserType(), account.getUserId(), account.getUserName(),
                transType, direction, amount.abs(), newBalance, null, remark
            );
        }
        
        return rows > 0;
    }
}
```

### 2.5 Controller 修改

#### AccountController.java
```java
@RestController
@RequestMapping("/api/account")
@Tag(name = "账户管理")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    /**
     * 创建账户（新版）
     */
    @PostMapping("/create")
    @Operation(summary = "创建账户")
    public R<Long> createAccount(@RequestBody @Validated AccountCreateReq req) {
        Long accountId = accountService.createAccount(
            req.getUserType(),
            req.getUserId(),
            req.getUserName(),
            req.getAccountType(),
            req.getBalance(),
            req.getExpireDate()
        );
        return R.ok(accountId);
    }
    
    /**
     * 查询用户账户列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询用户账户列表")
    public R<List<AccountVO>> getUserAccounts(
            @RequestParam @Schema(description = "用户类型") String userType,
            @RequestParam @Schema(description = "用户ID") Long userId) {
        
        List<AccountDO> accounts = accountService.getUserAccounts(userType, userId);
        List<AccountVO> voList = BeanUtil.copyToList(accounts, AccountVO.class);
        return R.ok(voList);
    }
    
    /**
     * 充值
     */
    @PostMapping("/recharge")
    @Operation(summary = "账户充值")
    public R<Void> recharge(@RequestBody @Validated AccountRechargeReq req) {
        accountService.updateBalance(
            req.getAccountId(),
            req.getAmount(),
            "recharge",
            req.getRemark()
        );
        return R.ok();
    }
    
    /**
     * 扣费
     */
    @PostMapping("/consume")
    @Operation(summary = "账户扣费")
    public R<Void> consume(@RequestBody @Validated AccountConsumeReq req) {
        accountService.updateBalance(
            req.getAccountId(),
            req.getAmount().negate(), // 扣费为负数
            "consume",
            req.getRemark()
        );
        return R.ok();
    }
}
```

### 2.6 DTO 定义

#### AccountCreateReq.java
```java
@Data
@Schema(description = "创建账户请求")
public class AccountCreateReq {
    
    @NotBlank(message = "用户类型不能为空")
    @Schema(description = "用户类型（student/teacher/admin）")
    private String userType;
    
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;
    
    @NotBlank(message = "用户姓名不能为空")
    @Schema(description = "用户姓名")
    private String userName;
    
    @NotBlank(message = "账户类型不能为空")
    @Schema(description = "账户类型（PAID/GIFT/LEAVE/FREEZE）")
    private String accountType;
    
    @NotNull(message = "初始余额不能为空")
    @DecimalMin(value = "0", message = "初始余额不能为负数")
    @Schema(description = "初始余额")
    private BigDecimal balance;
    
    @Schema(description = "到期日期（null表示无限期）")
    private LocalDate expireDate;
    
    @Schema(description = "备注")
    private String remark;
}
```

#### AccountVO.java
```java
@Data
@Schema(description = "账户信息")
public class AccountVO {
    
    @Schema(description = "账户ID")
    private Long id;
    
    @Schema(description = "用户类型")
    private String userType;
    
    @Schema(description = "用户类型描述")
    private String userTypeDesc;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户姓名")
    private String userName;
    
    @Schema(description = "账户类型")
    private String accountType;
    
    @Schema(description = "当前余额")
    private BigDecimal balance;
    
    @Schema(description = "到期日期")
    private LocalDate expireDate;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
```

---

## 三、迁移步骤

### 3.1 准备阶段
1. ✅ 创建数据库迁移脚本（`account_migration.sql`）
2. ✅ 在测试环境执行迁移脚本
3. ⬜ 创建/更新枚举类 `UserTypeEnum`
4. ⬜ 修改实体类 DO，直接使用新字段

### 3.2 开发阶段
1. ⬜ 修改 Mapper 层，添加新的查询方法
2. ⬜ 修改 Service 层，添加新的业务方法
3. ⬜ 修改 Controller 层，更新 API 接口
4. ⬜ 创建/更新 DTO/VO 类
5. ⬜ 编写单元测试
6. ⬜ 修改相关的前端代码

### 3.3 测试阶段
1. ⬜ 单元测试：测试账户创建、查询、充值、扣费等功能
2. ⬜ 集成测试：测试学生、教师、管理员三种用户类型
3. ⬜ 兼容性测试：确保旧接口仍然可用
4. ⬜ 性能测试：确保查询性能没有下降
5. ⬜ 回归测试：确保原有功能不受影响

### 3.4 上线阶段
1. ⬜ 生产环境数据库备份
2. ⬜ 低峰期执行数据库迁移脚本
3. ⬜ 部署新版本应用代码
4. ⬜ 验证核心功能
5. ⬜ 监控系统运行状态

### 3.5 收尾阶段（确认无问题后）
1. ⬜ 确认所有功能正常
2. ⬜ 更新技术文档

---

## 四、注意事项

### 4.1 数据一致性
- 使用事务确保账户和交易流水的一致性
- 定期校验数据完整性

### 4.2 性能优化
- 为 `(user_type, user_id)` 添加联合索引
- 避免全表扫描，使用索引查询
- 对于大量数据，考虑分批处理

### 4.3 安全考虑
- 校验 `userType` 的合法性
- 防止跨用户类型的非法操作
- 记录操作日志，便于审计

---

## 五、测试用例示例

### 5.1 单元测试

```java
@SpringBootTest
@Transactional
class AccountServiceTest {
    
    @Autowired
    private AccountService accountService;
    
    @Test
    @DisplayName("创建学生账户")
    void testCreateStudentAccount() {
        Long accountId = accountService.createAccount(
            UserTypeEnum.STUDENT.getCode(),
            1001L,
            "张三",
            "PAID",
            new BigDecimal("10.00"),
            LocalDate.of(2027, 12, 31)
        );
        
        assertNotNull(accountId);
        AccountDO account = accountService.getById(accountId);
        assertEquals("student", account.getUserType());
        assertEquals(1001L, account.getUserId());
        assertEquals("张三", account.getUserName());
    }
    
    @Test
    @DisplayName("创建教师账户")
    void testCreateTeacherAccount() {
        Long accountId = accountService.createAccount(
            UserTypeEnum.TEACHER.getCode(),
            2001L,
            "李老师",
            "PAID",
            new BigDecimal("50.00"),
            null
        );
        
        assertNotNull(accountId);
        AccountDO account = accountService.getById(accountId);
        assertEquals("teacher", account.getUserType());
        assertEquals(2001L, account.getUserId());
    }
    
    @Test
    @DisplayName("重复创建账户应抛出异常")
    void testDuplicateAccount() {
        accountService.createAccount(
            UserTypeEnum.STUDENT.getCode(), 1001L, "张三", 
            "PAID", BigDecimal.TEN, null
        );
        
        assertThrows(BusinessException.class, () -> {
            accountService.createAccount(
                UserTypeEnum.STUDENT.getCode(), 1001L, "张三",
                "PAID", BigDecimal.TEN, null
            );
        });
    }
    
    @Test
    @DisplayName("查询用户账户列表")
    void testGetUserAccounts() {
        accountService.createAccount(
            UserTypeEnum.STUDENT.getCode(), 1001L, "张三",
            "PAID", BigDecimal.TEN, null
        );
        accountService.createAccount(
            UserTypeEnum.STUDENT.getCode(), 1001L, "张三",
            "GIFT", BigDecimal.ZERO, null
        );
        
        List<AccountDO> accounts = accountService.getUserAccounts(
            UserTypeEnum.STUDENT.getCode(), 1001L
        );
        
        assertEquals(2, accounts.size());
    }
    
    @Test
    @DisplayName("充值")
    void testRecharge() {
        Long accountId = accountService.createAccount(
            UserTypeEnum.STUDENT.getCode(), 1001L, "张三",
            "PAID", new BigDecimal("10.00"), null
        );
        
        boolean success = accountService.updateBalance(
            accountId,
            new BigDecimal("5.00"),
            "recharge",
            "充值测试"
        );
        
        assertTrue(success);
        AccountDO account = accountService.getById(accountId);
        assertEquals(0, new BigDecimal("15.00").compareTo(account.getBalance()));
    }
    
    @Test
    @DisplayName("扣费")
    void testConsume() {
        Long accountId = accountService.createAccount(
            UserTypeEnum.STUDENT.getCode(), 1001L, "张三",
            "PAID", new BigDecimal("10.00"), null
        );
        
        boolean success = accountService.updateBalance(
            accountId,
            new BigDecimal("-3.00"),
            "consume",
            "扣费测试"
        );
        
        assertTrue(success);
        AccountDO account = accountService.getById(accountId);
        assertEquals(0, new BigDecimal("7.00").compareTo(account.getBalance()));
    }
    
    @Test
    @DisplayName("余额不足应抛出异常")
    void testInsufficientBalance() {
        Long accountId = accountService.createAccount(
            UserTypeEnum.STUDENT.getCode(), 1001L, "张三",
            "PAID", new BigDecimal("5.00"), null
        );
        
        assertThrows(BusinessException.class, () -> {
            accountService.updateBalance(
                accountId,
                new BigDecimal("-10.00"),
                "consume",
                "余额不足测试"
            );
        });
    }
}
```

---

## 六、FAQ

### Q1: 为什么不使用外键约束？
**A**: 因为 `user_id` 可能关联 `edu_student`、`edu_teacher` 或后台管理员表，无法建立单一外键。应在应用层保证数据一致性。

### Q2: 如何处理历史数据？
**A**: 迁移脚本会自动将所有现有记录的 `user_type` 设置为 `'student'`，并将 `student_id` 复制到 `user_id`，迁移完成后旧字段直接删除。

### Q4: 教师和管理员的账户类型有区别吗？
**A**: `account_type` 字段对所有用户类型通用（PAID/GIFT/LEAVE/FREEZE）。如果需要区分，可以扩展该字段的枚举值。

### Q5: 如何防止误操作？
**A**: 
- 在 Service 层进行严格的参数校验
- 使用枚举类限制 `userType` 的取值
- 记录操作日志
- 添加权限控制

---

## 七、相关文档

- 数据库迁移脚本：`account_migration.sql`
- API 文档：待补充
- 测试报告：待补充

---

**文档版本**: v1.0  
**最后更新**: 2026-08-05  
**维护人员**: 开发团队
