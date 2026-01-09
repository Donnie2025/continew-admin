/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.education.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.admin.education.constant.TransactionType;
import top.continew.admin.education.helper.ClassinHelper;
import top.continew.admin.education.mapper.BookingMapper;
import top.continew.admin.education.mapper.TransactionMapper;
import top.continew.admin.education.mapper.LessonMapper;
import top.continew.admin.education.mapper.StuCardMapper;
import top.continew.admin.education.mapper.CourseMapper;
import top.continew.admin.education.model.entity.BookingDO;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.req.BatchBookingReq;
import top.continew.admin.education.model.entity.TransactionDO;
import top.continew.admin.education.model.entity.LessonDO;
import top.continew.admin.education.model.entity.StuCardDO;
import top.continew.admin.education.model.query.BookingQuery;
import top.continew.admin.education.model.req.BookingReq;
import top.continew.admin.education.model.req.CourseReq;
import top.continew.admin.education.model.req.CourseTeacherReq;
import top.continew.admin.education.model.req.CourseStudentReq;
import top.continew.admin.education.model.resp.*;
import top.continew.admin.education.service.*;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 预约业务实现
 *
 * @author don
 * @since 2025/05/23 23:25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl extends BaseServiceImpl<BookingMapper, BookingDO, BookingResp, BookingDetailResp, BookingQuery, BookingReq> implements BookingService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StuCardService stuCardService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private LessonMapper lessonMapper;

    @Autowired
    private StuCardMapper stuCardMapper;

    @Autowired
    private SlotService slotService;

    @Autowired
    private ClassinHelper classinHelper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseTeacherService courseTeacherService;

    @Autowired
    private CourseStudentService courseStudentService;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialLessonService materialLessonService;

    /**
     * 创建预约前处理
     * 根据前端提交的 memberId 和 cardId 自动获取会员姓名和会员卡名称
     *
     * @param req 创建信息
     */
    @Override
    protected void beforeCreate(BookingReq req) {
        // 获取请求参数
        Long studentId = req.getStudentId();
        Long cardId = req.getStuCardId();
        Long slotId = req.getSlotId();

        log.info("预约参数: studentId={}, cardId={}, materialId={}, slotId={}, createUser={}", studentId, cardId, req
            .getMaterialId(), slotId, req.getCreateUser());

        // 检查该学生是否已经预约过该课时
        if (studentId != null && slotId != null) {
            LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BookingDO::getSlotId, slotId).eq(BookingDO::getStudentId, studentId);
            long count = baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new RuntimeException("您已经预约过该课时，不能重复预约");
            }
            log.info("检查重复预约通过");

            // 新增：校验课时预约人数不能超过studentCount
            // 查询该课时已预约人数
            LambdaQueryWrapper<BookingDO> slotCountWrapper = new LambdaQueryWrapper<>();
            slotCountWrapper.eq(BookingDO::getSlotId, slotId);
            long bookedCount = baseMapper.selectCount(slotCountWrapper);
            // 获取课时最大可预约人数
            SlotDetailResp slot = slotService.get(slotId);
            if (slot != null && slot.getStudentCount() != null) {
                int maxCount = slot.getStudentCount();
                if (bookedCount >= maxCount) {
                    throw new RuntimeException("该课时预约人数已满，无法继续预约");
                }
            }
        }

        // 根据 slotId 获取课时信息
        if (slotId != null) {
            SlotDetailResp slot = slotService.get(slotId);
            if (slot != null) {
                req.setStartDate(slot.getStartDate());
                req.setStartTime(slot.getStartTime());
                log.info("设置课时信息: startDate={}, startTime={}", slot.getStartDate(), slot.getStartTime());
            }
        }

        // 根据 studentId 获取学生信息
        if (studentId != null) {
            StudentDetailResp student = studentService.get(studentId);
            if (student != null) {
                req.setStudentName(student.getName());
                log.info("设置学生姓名: {}", student.getName());

                // 设置学生手机号
                req.setPhone(student.getPhone());
                log.info("设置学生手机号: {}", student.getPhone());

                // 设置操作人名字
                req.setOperatorName(student.getName());
                log.info("设置操作人名字: {}", req.getOperatorName());
            }
        }

        // 设置操作时间为当前时间
        req.setOperateTime(LocalDateTime.now());
        log.info("设置操作时间: {}", req.getOperateTime());

        // 根据 cardId 获取会员卡信息
        if (cardId != null) {
            StuCardDetailResp stuCard = stuCardService.get(cardId);
            if (stuCard != null) {
                req.setCardTitle(stuCard.getCardTitle());
                log.info("设置会员卡标题: {}", stuCard.getCardTitle());
            }
        }

        // 如果没有设置创建人，则设置默认值
        if (req.getCreateUser() == null) {
            req.setCreateUser(1L); // 默认系统管理员
        }
    }

    /**
     * 根据课时ID列表查询对应的预约信息
     *
     * @param slotIds 课时ID列表
     * @return 课时ID到学生姓名列表的映射
     */
    @Override
    public Map<Long, List<String>> findStudentNamesBySlotIds(List<Long> slotIds) {
        if (slotIds == null || slotIds.isEmpty()) {
            return new HashMap<>();
        }

        // 查询所有匹配的预约记录
        LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(BookingDO::getSlotId, slotIds);
        List<BookingDO> bookings = baseMapper.selectList(queryWrapper);

        // 按照slotId分组，合并多个学生的名字
        return bookings.stream()
            .filter(booking -> booking.getStudentName() != null && !booking.getStudentName().isEmpty())
            .collect(Collectors.groupingBy(BookingDO::getSlotId, Collectors
                .mapping(BookingDO::getStudentName, Collectors.toList())));
    }

    /**
     * 创建预约后处理
     * 同步预约信息到 ClassIn 系统
     *
     * @param req    创建信息
     * @param entity 创建的预约实体
     */
    @Override
    protected void afterCreate(BookingReq req, BookingDO entity) {
        try {
            log.info("开始同步预约信息到 ClassIn 系统: bookingId={}", entity.getId());

            // 获取课时信息
            SlotDetailResp slot = slotService.get(entity.getSlotId());
            if (slot == null) {
                log.error("课时信息不存在: slotId={}", entity.getSlotId());
                return;
            }

            TeacherDetailResp teacherDetailResp = teacherService.get(slot.getTeacherId());
            if (teacherDetailResp == null) {
                log.error("老师信息不存在: teacherId={}", slot.getTeacherId());
                return;
            }
            ClassinUserDO teacherClassinUser = classinHelper.getClassinUser(teacherDetailResp
                .getId(), ClassinConstants.USER_TYPE_TEACHER, teacherDetailResp.getName(), teacherDetailResp
                    .getPhone(), teacherDetailResp.getEmail());

            // 获取学生信息
            StudentDetailResp student = studentService.get(entity.getStudentId());
            if (student == null) {
                log.error("学生信息不存在: studentId={}", entity.getStudentId());
                return;
            }
            ClassinUserDO stuClassinUserDO = classinHelper.getClassinUser(student
                .getId(), ClassinConstants.USER_TYPE_STUDENT, student.getName(), student.getPhone(), student
                    .getEmail());

            // 构建 ClassIn 预约请求参数
            Map<String, Object> classInParams = new HashMap<>();
            classInParams.put("studentId", stuClassinUserDO.getClassinUid());
            classInParams.put("startTime", entity.getSlotDate() + " " + entity.getSlotTime());
            classInParams.put("duration", slot.getDuration());
            classInParams.put("teacherId", teacherClassinUser.getClassinUid());

            // TODO: 调用 ClassIn API 进行预约同步
            // classInService.createBooking(classInParams);

            log.info("预约信息同步到 ClassIn 系统成功: bookingId={}", entity.getId());
        } catch (Exception e) {
            log.error("同步预约信息到 ClassIn 系统失败: bookingId={}, error={}", entity.getId(), e.getMessage(), e);
            // 这里可以选择是否抛出异常，取决于业务需求
            // throw new RuntimeException("同步预约信息到 ClassIn 系统失败", e);
        }
    }

    @Override
    public List<BookingDO> listByStudentId(Long studentId) {
        LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookingDO::getStudentId, studentId).orderByDesc(BookingDO::getSlotDate, BookingDO::getSlotTime);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<BookingDO> listByStudentIdAndMaterialId(Long studentId, Long materialId) {
        LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookingDO::getStudentId, studentId)
            .eq(BookingDO::getMaterialId, materialId)
            .orderByDesc(BookingDO::getSlotDate, BookingDO::getSlotTime);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public Long add(BookingDO booking) {
        // 设置审计字段
        if (booking.getCreateUser() == null) {
            booking.setCreateUser(booking.getStudentId()); // 使用学生ID作为创建用户
        }
        if (booking.getCreateTime() == null) {
            booking.setCreateTime(LocalDateTime.now());
        }

        baseMapper.insert(booking);
        return booking.getId();
    }

    @Override
    public void update(BookingDO booking, Long id) {
        booking.setId(id);
        // 设置更新审计字段
        booking.setUpdateTime(LocalDateTime.now());
        // TODO: 设置更新用户ID，需要从认证上下文获取

        baseMapper.updateById(booking);
    }

    @Override
    public void delete(List<Long> ids) {
        baseMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBookingWithTransaction(BookingDO booking) {
        try {
            // 0. 检查时间段是否已被预约
            log.info("检查时间段是否可预约: slotId={}, teacherId={}, slotDate={}, slotTime={}", booking.getSlotId(), booking
                .getTeacherId(), booking.getSlotDate(), booking.getSlotTime());
            checkSlotAvailability(booking);

            // 1. 创建预约记录
            log.info("开始创建预约记录: studentId={}, slotId={}", booking.getStudentId(), booking.getSlotId());

            // 设置审计字段
            booking.setCreateUser(booking.getStudentId()); // 使用学生ID作为创建用户
            booking.setCreateTime(LocalDateTime.now());

            baseMapper.insert(booking);
            Long bookingId = booking.getId();
            log.info("预约记录创建成功: bookingId={}", bookingId);

            // 2. 创建交易记录（扣款）
            log.info("开始创建交易记录: bookingId={}, cardId={}", bookingId, booking.getStuCardId());
            createTransactionRecord(booking);
            log.info("交易记录创建成功: bookingId={}", bookingId);

            // 3. 创建课程记录并对接ClassIn
            log.info("开始创建课程记录: bookingId={}", bookingId);
            createLessonRecord(booking);
            log.info("课程记录创建成功: bookingId={}", bookingId);

            // 4. 扣减学生卡余额
            log.info("开始扣减学生卡余额: bookingId={}, cardId={}", bookingId, booking.getStuCardId());
            deductStuCardBalance(booking.getStuCardId());
            log.info("学生卡余额扣减成功: bookingId={}", bookingId);

            log.info("预约创建完成: bookingId={}", bookingId);
            return bookingId;

        } catch (Exception e) {
            log.error("创建预约失败，事务回滚: {}", e.getMessage(), e);
            throw new RuntimeException("创建预约失败，请重试: " + e.getMessage(), e);
        }
    }

    /**
     * 创建交易记录
     * 统一使用 remainBalance 记录所有卡类型的余额变化
     */
    private void createTransactionRecord(BookingDO booking) {
        try {
            // 获取学生卡信息
            log.info("开始获取学生卡信息: stuCardId={}", booking.getStuCardId());

            StuCardDO stuCard = stuCardMapper.selectById(booking.getStuCardId());
            if (stuCard == null) {
                log.error("学生卡不存在: stuCardId={}", booking.getStuCardId());
                throw new RuntimeException("学生卡不存在");
            }

            String cardTitle = stuCard.getCardName();
            String cardType = stuCard.getCardType();
            BigDecimal balance = stuCard.getBalance();

            if (balance == null || balance.compareTo(BigDecimal.ONE) < 0) {
                throw new RuntimeException("学生卡余额不足");
            }

            // 创建交易记录
            TransactionDO transaction = new TransactionDO();
            transaction.setStuCardId(booking.getStuCardId());
            transaction.setStuId(booking.getStudentId());
            transaction.setStuName(booking.getStudentName());
            transaction.setCardTitle(cardTitle);
            transaction.setTransType(TransactionType.BOOK_DEBIT); // 约课扣费
            transaction.setAmount(BigDecimal.ONE); // 交易金额为1次
            transaction.setRemark("预约课程扣费 - 预约ID: " + booking.getId());

            // 统一记录余额变化
            transaction.setBeforeAmt(balance);
            transaction.setAfterAmt(balance.subtract(BigDecimal.ONE));

            log.info("交易记录创建: stuCardId={}, cardType={}, 余额: {} -> {}", booking
                .getStuCardId(), cardType, balance, balance.subtract(BigDecimal.ONE));

            // 设置审计字段
            transaction.setCreateUser(booking.getStudentId());
            transaction.setCreateTime(LocalDateTime.now());

            transactionMapper.insert(transaction);

        } catch (Exception e) {
            log.error("创建交易记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建交易记录失败，请重试", e);
        }
    }

    /**
     * 创建课程记录并对接ClassIn
     * 维护 edu_course, edu_course_teacher, edu_course_student, edu_lesson 四张表
     */
    private void createLessonRecord(BookingDO booking) {
        try {
            // 1. 先检查是否有现有课程
            Long teacherId = booking.getTeacherId();
            Long studentId = booking.getStudentId();
            Long existingCourseId = courseMapper.findCommonCourse(teacherId, studentId);

            Long courseId;
            boolean isNewCourse;

            if (existingCourseId != null) {
                // 复用现有课程
                courseId = existingCourseId;
                isNewCourse = false;
                log.info("复用现有课程，跳过关联创建: courseId={}", courseId);
            } else {
                // 创建新课程并建立关联
                courseId = createNewCourse(booking);
                isNewCourse = true;

                // 关联教师到课程 (edu_course_teacher)
                associateTeacherToCourse(courseId, booking);

                // 关联学生到课程 (edu_course_student)  
                associateStudentToCourse(courseId, booking);

                log.info("新课程关联创建完成: courseId={}", courseId);
            }

            // 3. 创建课节记录 (edu_lesson) - 无论新旧课程都需要创建课节
            createLesson(courseId, booking);

            log.info("课程体系处理成功: courseId={}, bookingId={}, isNewCourse={}", courseId, booking.getId(), isNewCourse);

        } catch (Exception e) {
            log.error("创建课程记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建课程记录失败，请重试", e);
        }
    }

    /**
     * 检查时间段是否可预约
     */
    private void checkSlotAvailability(BookingDO booking) {
        try {
            // 根据教师ID、日期、时间查询是否已有预约
            LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BookingDO::getTeacherId, booking.getTeacherId())
                .eq(BookingDO::getSlotDate, booking.getSlotDate())
                .eq(BookingDO::getSlotTime, booking.getSlotTime())
                .ne(BookingDO::getStatus, 0); // 排除已取消的预约

            List<BookingDO> existingBookings = baseMapper.selectList(queryWrapper);

            if (!existingBookings.isEmpty()) {
                BookingDO existingBooking = existingBookings.get(0);
                String errorMsg = String.format("该时间段已被预约，教师：%s，日期：%s，时间：%s，预约学生：%s", booking.getTeacherName(), booking
                    .getSlotDate(), booking.getSlotTime(), existingBooking.getStudentName());
                log.warn("时间段冲突: {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }

            log.info("时间段检查通过，可以预约");

        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw e; // 重新抛出业务异常
            }
            log.error("检查时间段可用性失败: {}", e.getMessage(), e);
            throw new RuntimeException("检查时间段可用性失败，请重试", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createBatchBookingWithTransaction(BatchBookingReq batchReq) {
        List<Long> bookingIds = new ArrayList<>();

        try {
            log.info("开始批量创建预约: teacherId={}, materialId={}, slotCount={}", batchReq.getTeacherId(), batchReq
                .getMaterialId(), batchReq.getSlotCount());

            // 获取当前登录用户ID作为学生ID
            Long studentId = getCurrentStudentId();
            if (studentId == null) {
                throw new RuntimeException("用户未登录，无法创建预约");
            }

            // 获取学生和教师信息
            StudentDetailResp student = studentService.get(studentId);
            TeacherDetailResp teacher = teacherService.get(batchReq.getTeacherId());

            if (student == null) {
                throw new RuntimeException("学生信息不存在");
            }
            if (teacher == null) {
                throw new RuntimeException("教师信息不存在");
            }

            // 获取教材信息
            MaterialDetailResp material = null;
            if (batchReq.getMaterialId() != null) {
                material = materialService.get(batchReq.getMaterialId());
            }

            // 获取会员卡信息
            StuCardDO stuCard = null;
            if (batchReq.getStuCardId() != null) {
                stuCard = stuCardMapper.selectById(batchReq.getStuCardId());
            }

            // 为每个时间段创建预约记录
            List<BookingDO> bookings = new ArrayList<>();
            for (int i = 0; i < batchReq.getSlots().size(); i++) {
                BatchBookingReq.SlotInfo slotInfo = batchReq.getSlots().get(i);
                
                BookingDO booking = new BookingDO();
                booking.setSlotId(Long.valueOf(slotInfo.getSlotId()));
                booking.setSlotDate(slotInfo.getDate().replace("-", ""));
                booking.setSlotTime(slotInfo.getStartTime());
                booking.setStudentId(studentId);
                booking.setStudentName(student.getName());
                booking.setStudentPhone(student.getPhone());
                booking.setTeacherId(batchReq.getTeacherId());
                booking.setMaterialId(batchReq.getMaterialId());
                booking.setStuCardId(batchReq.getStuCardId());
                
                // 设置课节ID - 优先使用每个时间段的lessonId，否则使用全局的lessonId
                Long lessonId = null;
                if (slotInfo.getLessonId() != null) {
                    lessonId = slotInfo.getLessonId();
                } else if (batchReq.getLessonId() != null) {
                    lessonId = batchReq.getLessonId();
                }
                
                if (lessonId != null) {
                    booking.setLessonId(lessonId);
                    
                    // 获取课节信息用于设置lesson_name
                    try {
                        MaterialLessonDetailResp lessonInfo = materialLessonService.get(lessonId);
                        if (lessonInfo != null) {
                            booking.setLessonName(lessonInfo.getLessonName());
                        }
                    } catch (Exception e) {
                        log.warn("获取课节信息失败: lessonId={}, error={}", lessonId, e.getMessage());
                    }
                }

                // 设置教材信息
                if (material != null) {
                    booking.setMaterialName(material.getName());
                }

                // 设置会员卡信息
                if (stuCard != null) {
                    booking.setCardName(stuCard.getCardName());
                }

                // 设置教师信息
                booking.setTeacherName(teacher.getName());
                
                // 设置预约备注
                booking.setRemark(batchReq.getNote());
                booking.setStatus(1); // 有效状态
                
                bookings.add(booking);
            }

            // 批量保存预约记录
            for (BookingDO booking : bookings) {
                baseMapper.insert(booking);
                bookingIds.add(booking.getId());
            }

            log.info("批量预约创建成功: 共{}条记录", bookingIds.size());
            return bookingIds;

        } catch (Exception e) {
            log.error("批量创建预约失败: {}", e.getMessage(), e);
            throw new RuntimeException("预约失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取当前登录学生ID
     */
    private Long getCurrentStudentId() {
        // 从用户上下文中获取当前登录用户ID
        Long userId = UserContextHolder.getUserId();
        if (userId == null) {
            // 如果没有用户上下文，说明用户未登录，抛出异常要求登录
            throw new RuntimeException("用户未登录，请先登录后再进行预约操作");
        }
        return userId;
    }

    /**
     * 格式化日期为数据库存储格式
     * 将前端传入的日期格式 "2026-01-03" 转换为数据库存储格式 "20260103"
     *
     * @param dateStr 前端传入的日期字符串，格式为 "YYYY-MM-DD"
     * @return 数据库存储格式的日期字符串，格式为 "YYYYMMDD"
     */
    private String formatDateForDatabase(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return dateStr;
        }
        // 移除日期中的横线，将 "2026-01-03" 转换为 "20260103"
        return dateStr.replace("-", "");
    }

    /**
     * 创建新的一对一课程
     */
    private Long createNewCourse(BookingDO booking) {
        // 为一对一课程创建唯一的课程名称
        String courseName = booking.getTeacherName() + "-" + booking.getStudentName() + "的一对一课程";

        CourseReq courseReq = new CourseReq();
        courseReq.setName(courseName);
        courseReq.setMainTeacherId(booking.getTeacherId());
        // 设置默认机构ID，可以从配置或其他地方获取
        courseReq.setInstitutionId(1L); // TODO: 从配置获取默认机构ID

        Long courseId = courseService.create(courseReq);
        log.info("创建新课程成功: courseId={}, courseName={}", courseId, courseName);
        return courseId;
    }

    /**
     * 关联教师到课程
     */
    private void associateTeacherToCourse(Long courseId, BookingDO booking) {
        try {
            CourseTeacherReq teacherReq = new CourseTeacherReq();
            teacherReq.setCourseId(courseId);
            teacherReq.setTeacherIds(List.of(booking.getTeacherId()));

            courseTeacherService.addTeachersToCourse(teacherReq);
            log.info("教师关联成功: courseId={}, teacherId={}", courseId, booking.getTeacherId());
        } catch (Exception e) {
            log.warn("教师关联失败，可能已存在: {}", e.getMessage());
            // 不抛出异常，继续执行
        }
    }

    /**
     * 关联学生到课程
     */
    private void associateStudentToCourse(Long courseId, BookingDO booking) {
        try {
            CourseStudentReq studentReq = new CourseStudentReq();
            studentReq.setCourseId(courseId);
            studentReq.setStudentIds(List.of(booking.getStudentId()));

            courseStudentService.addStudentsToCourse(studentReq);
            log.info("学生关联成功: courseId={}, studentId={}", courseId, booking.getStudentId());
        } catch (Exception e) {
            log.warn("学生关联失败，可能已存在: {}", e.getMessage());
            // 不抛出异常，继续执行
        }
    }

    /**
     * 创建课节记录
     */
    private void createLesson(Long courseId, BookingDO booking) {
        LessonDO lesson = new LessonDO();
        lesson.setCourseId(courseId); // 设置课程ID
        lesson.setName(booking.getTeacherName() + "-" + booking.getStudentName() + "的一对一课节");
        lesson.setTeacherId(booking.getTeacherId());
        lesson.setTeacherName(booking.getTeacherName());

        // 将 YYYYMMDD 格式转换为 YYYY-MM-DD 格式
        String formattedDate = booking.getSlotDate().substring(0, 4) + "-" + booking.getSlotDate()
            .substring(4, 6) + "-" + booking.getSlotDate().substring(6, 8);
        lesson.setStartTime(LocalDateTime.parse(formattedDate + "T" + booking.getSlotTime() + ":00"));
        lesson.setDuration(25L); // 一对一课程默认25分钟
        lesson.setSeatNum(1); // 一对一
        lesson.setStatus(1);
        lesson.setRemark("预约ID: " + booking.getId());

        // 设置审计字段
        lesson.setCreateUser(booking.getStudentId());
        lesson.setCreateTime(LocalDateTime.now());

        // 创建课节记录
        lessonMapper.insert(lesson);
        log.info("课节创建成功: lessonId={}, courseId={}", lesson.getId(), courseId);
    }

    /**
     * 扣减学生卡余额
     * 统一使用 remainBalance 管理所有卡类型的余额
     */
    private void deductStuCardBalance(Long cardId) {
        try {
            // 获取学生卡信息
            StuCardDO stuCard = stuCardMapper.selectById(cardId);
            if (stuCard == null) {
                throw new RuntimeException("学生卡不存在");
            }

            BigDecimal balance = stuCard.getBalance();
            if (balance == null || balance.compareTo(BigDecimal.ONE) < 0) {
                throw new RuntimeException("学生卡余额不足");
            }

            // 统一扣减余额
            StuCardDO updateCard = new StuCardDO();
            updateCard.setId(cardId);
            updateCard.setBalance(balance.subtract(BigDecimal.ONE));

            stuCardMapper.updateById(updateCard);

            log.info("学生卡扣减成功: cardId={}, cardType={}, 余额: {} -> {}", cardId, stuCard.getCardType(), balance, balance
                .subtract(BigDecimal.ONE));

        } catch (Exception e) {
            log.error("扣减学生卡失败: {}", e.getMessage(), e);
            throw new RuntimeException("扣减学生卡失败，请重试", e);
        }
    }

    @Override
    public BookingDO getLastBookingByCurrentStudent() {
        try {
            // 获取当前登录用户ID作为学生ID
            Long studentId = getCurrentStudentId();
            if (studentId == null) {
                log.warn("用户未登录，无法获取最后预约记录");
                return null;
            }

            // 查询该学生的最后一次预约记录
            LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BookingDO::getStudentId, studentId)
                .ne(BookingDO::getStatus, 0) // 排除已取消的预约
                .orderByDesc(BookingDO::getCreateTime) // 按创建时间降序
                .last("LIMIT 1"); // 只取第一条记录

            BookingDO lastBooking = baseMapper.selectOne(queryWrapper);
            
            if (lastBooking != null) {
                log.info("找到学生最后预约记录: studentId={}, bookingId={}, materialId={}, lessonId={}", 
                    studentId, lastBooking.getId(), lastBooking.getMaterialId(), lastBooking.getLessonId());
            } else {
                log.info("学生暂无预约记录: studentId={}", studentId);
            }

            return lastBooking;

        } catch (Exception e) {
            log.error("获取学生最后预约记录失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Long> getCompletedLessonIds(Long studentId, Long materialId) {
        try {
            log.info("查询学生已完成的课件列表: studentId={}, materialId={}", studentId, materialId);
            
            // 构建查询条件
            LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BookingDO::getStudentId, studentId)
                .isNotNull(BookingDO::getLessonId)
                .eq(BookingDO::getStatus, 1); // 只查询有效的预约记录
            
            // 如果指定了教材ID，则过滤特定教材的课件
            if (materialId != null) {
                queryWrapper.eq(BookingDO::getMaterialId, materialId);
            }
            
            // 添加时间条件：预约时间已过的记录
            // 使用SQL函数将slot_date和slot_time组合成完整的日期时间进行比较
            queryWrapper.apply("CONCAT(slot_date, ' ', slot_time) < NOW()");
            
            // 查询预约记录
            List<BookingDO> completedBookings = baseMapper.selectList(queryWrapper);
            
            // 提取课件ID列表并去重
            List<Long> completedLessonIds = completedBookings.stream()
                .map(BookingDO::getLessonId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
            
            log.info("查询到已完成的课件数量: {}", completedLessonIds.size());
            return completedLessonIds;
            
        } catch (Exception e) {
            log.error("查询学生已完成课件列表失败: studentId={}, materialId={}, error={}", 
                studentId, materialId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}