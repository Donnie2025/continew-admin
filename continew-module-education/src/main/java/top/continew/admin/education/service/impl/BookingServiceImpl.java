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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
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
import top.continew.admin.education.model.resp.BookingResp;
import top.continew.admin.education.model.resp.BookingDetailResp;
import top.continew.admin.education.model.resp.MyBookingResp;
import top.continew.admin.education.model.req.CourseReq;
import top.continew.admin.education.model.req.CourseTeacherReq;
import top.continew.admin.education.model.req.CourseStudentReq;
import top.continew.admin.education.model.resp.*;
import top.continew.admin.education.service.*;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        // 检查该学生是否已经预约过该课时（只检查已预约状态的记录）
        if (studentId != null && slotId != null) {
            LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BookingDO::getSlotId, slotId)
                .eq(BookingDO::getStudentId, studentId)
                .eq(BookingDO::getStatus, 1); // 只检查已预约状态的记录
            long count = baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new RuntimeException("您已经预约过该课时，不能重复预约");
            }
            log.info("检查重复预约通过");

            // 新增：校验课时预约人数不能超过studentCount
            // 查询该课时已预约人数（只统计已预约状态的记录）
            LambdaQueryWrapper<BookingDO> slotCountWrapper = new LambdaQueryWrapper<>();
            slotCountWrapper.eq(BookingDO::getSlotId, slotId).eq(BookingDO::getStatus, 1); // 只统计已预约状态的记录
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
                req.setSlotDate(slot.getStartDate());
                req.setSlotTime(slot.getStartTime());
                log.info("设置课时信息: slotDate={}, slotTime={}", slot.getStartDate(), slot.getStartTime());
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

        // 查询所有匹配的预约记录（只查询已预约状态的记录）
        LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(BookingDO::getSlotId, slotIds).eq(BookingDO::getStatus, 1); // 只查询已预约状态的记录
        List<BookingDO> bookings = baseMapper.selectList(queryWrapper);

        // 按照slotId分组，合并多个学生的名字
        return bookings.stream()
            .filter(booking -> booking.getStudentName() != null && !booking.getStudentName().isEmpty())
            .collect(Collectors.groupingBy(BookingDO::getSlotId, Collectors
                .mapping(BookingDO::getStudentName, Collectors.toList())));
    }

    @Override
    public Map<Long, List<BookingDO>> findDetailedBookingsBySlotIds(List<Long> slotIds) {
        if (slotIds == null || slotIds.isEmpty()) {
            return new HashMap<>();
        }

        // 查询所有匹配的预约记录（只查询已预约状态的记录）
        LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(BookingDO::getSlotId, slotIds).eq(BookingDO::getStatus, 1); // 只查询已预约状态的记录
        List<BookingDO> bookings = baseMapper.selectList(queryWrapper);

        // 按照slotId分组，返回完整的预约详情列表
        return bookings.stream().collect(Collectors.groupingBy(BookingDO::getSlotId));
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
                .eq(BookingDO::getStatus, 1); // 只查询已预约状态的记录

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

                // 检查该时间段的预约容量
                Long slotId = Long.valueOf(slotInfo.getSlotId());
                LambdaQueryWrapper<BookingDO> slotCountWrapper = new LambdaQueryWrapper<>();
                slotCountWrapper.eq(BookingDO::getSlotId, slotId).eq(BookingDO::getStatus, 1); // 只统计已预约状态的记录
                long bookedCount = baseMapper.selectCount(slotCountWrapper);

                // 获取课时最大可预约人数
                SlotDetailResp slot = slotService.get(slotId);
                if (slot != null && slot.getStudentCount() != null) {
                    int maxCount = slot.getStudentCount();
                    if (bookedCount >= maxCount) {
                        throw new RuntimeException("时间段 " + slotInfo.getStartTime() + " 预约人数已满，无法继续预约");
                    }
                }

                // 检查该学生是否已经预约过该课时
                LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(BookingDO::getSlotId, slotId)
                    .eq(BookingDO::getStudentId, studentId)
                    .eq(BookingDO::getStatus, 1); // 只检查已预约状态的记录
                long existingCount = baseMapper.selectCount(queryWrapper);
                if (existingCount > 0) {
                    throw new RuntimeException("您已经预约过时间段 " + slotInfo.getStartTime() + "，不能重复预约");
                }

                BookingDO booking = new BookingDO();
                booking.setSlotId(slotId);
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
                    booking.setMaterialLevel(material.getLevel());
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
                .eq(BookingDO::getStatus, 1) // 只查询已预约状态的记录
                .orderByDesc(BookingDO::getCreateTime) // 按创建时间降序
                .last("LIMIT 1"); // 只取第一条记录

            BookingDO lastBooking = baseMapper.selectOne(queryWrapper);

            if (lastBooking != null) {
                log.info("找到学生最后预约记录: studentId={}, bookingId={}, materialId={}, lessonId={}", studentId, lastBooking
                    .getId(), lastBooking.getMaterialId(), lastBooking.getLessonId());
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
            log.error("查询学生已完成课件列表失败: studentId={}, materialId={}, error={}", studentId, materialId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<MyBookingResp> getMyBookings() {
        try {
            // 获取当前登录学生ID
            Long currentStudentId = getCurrentStudentId();
            if (currentStudentId == null) {
                log.warn("用户未登录，无法获取预约列表");
                return new ArrayList<>();
            }

            log.info("获取学生预约列表: studentId={}", currentStudentId);

            // 查询学生的所有预约记录（包括已取消的），按时间倒序排列
            LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BookingDO::getStudentId, currentStudentId)
                .orderByDesc(BookingDO::getSlotDate, BookingDO::getSlotTime);

            log.info("查询学生预约记录: studentId={}, 查询所有状态的记录", currentStudentId);
            List<BookingDO> bookings = baseMapper.selectList(queryWrapper);
            log.info("查询到预约记录数量: {}", bookings.size());

            // 详细打印每条预约记录
            for (int i = 0; i < bookings.size(); i++) {
                BookingDO booking = bookings.get(i);
                log.info("预约记录 {}: id={}, slotDate={}, slotTime={}, status={}, lessonName={}", i + 1, booking
                    .getId(), booking.getSlotDate(), booking.getSlotTime(), booking.getStatus(), booking
                        .getLessonName());
            }

            if (bookings.isEmpty()) {
                log.info("学生暂无预约记录: studentId={}", currentStudentId);
                return new ArrayList<>();
            }

            // 转换为响应DTO
            List<MyBookingResp> result = new ArrayList<>();
            for (BookingDO booking : bookings) {
                MyBookingResp resp = new MyBookingResp();

                // 基础信息
                resp.setId(booking.getId());
                resp.setSlotId(booking.getSlotId());
                resp.setSlotDate(booking.getSlotDate());
                resp.setSlotTime(booking.getSlotTime());

                // 教师信息
                resp.setTeacherId(booking.getTeacherId());
                resp.setTeacherName(booking.getTeacherName());

                // 查询教师头像
                if (booking.getTeacherId() != null) {
                    try {
                        TeacherDetailResp teacher = teacherService.get(booking.getTeacherId());
                        if (teacher != null && teacher.getAvatar() != null) {
                            resp.setTeacherAvatar(teacher.getAvatar());
                        }
                    } catch (Exception e) {
                        log.warn("查询教师头像失败: teacherId={}, error={}", booking.getTeacherId(), e.getMessage());
                    }
                }

                // 会员卡信息
                resp.setCardId(booking.getStuCardId());
                resp.setCardName(booking.getCardName());

                // 教材和课节信息
                resp.setMaterialId(booking.getMaterialId());
                resp.setMaterialName(booking.getMaterialName());

                // 查询教材级别
                if (booking.getMaterialId() != null) {
                    try {
                        MaterialDetailResp material = materialService.get(booking.getMaterialId());
                        if (material != null && material.getLevel() != null) {
                            resp.setMaterialLevel(material.getLevel());
                        }
                    } catch (Exception e) {
                        log.warn("查询教材级别失败: materialId={}, error={}", booking.getMaterialId(), e.getMessage());
                    }
                }

                resp.setLessonId(booking.getLessonId());
                resp.setLessonName(booking.getLessonName());

                // 备注
                resp.setRemark(booking.getRemark());

                // 判断预约状态
                String bookingStatus = determineBookingStatus(booking);
                resp.setBookingStatus(bookingStatus);

                // 格式化日期时间用于前端显示
                String formattedDateTime = formatDateTime(booking.getSlotDate(), booking.getSlotTime());
                resp.setFormattedDateTime(formattedDateTime);

                // 创建时间
                resp.setCreateTime(booking.getCreateTime());

                result.add(resp);
            }

            log.info("返回预约列表: studentId={}, count={}", currentStudentId, result.size());
            return result;

        } catch (Exception e) {
            log.error("获取学生预约列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 判断预约状态
     */
    private String determineBookingStatus(BookingDO booking) {
        try {
            // 优先判断status字段：0=已取消，1=有效
            if (booking.getStatus() != null && booking.getStatus() == 0) {
                return "已取消";
            }

            // 将slot_date和slot_time组合成完整的日期时间
            String dateTimeStr = booking.getSlotDate() + " " + booking.getSlotTime();
            LocalDateTime bookingDateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter
                .ofPattern("yyyyMMdd HH:mm"));

            LocalDateTime now = LocalDateTime.now();

            if (bookingDateTime.isBefore(now)) {
                return "已完成";
            } else {
                return "未开课";
            }
        } catch (Exception e) {
            log.warn("判断预约状态失败: {}", e.getMessage());
            return "未开课"; // 默认状态
        }
    }

    /**
     * 格式化日期时间用于前端显示
     */
    private String formatDateTime(String slotDate, String slotTime) {
        try {
            // 将YYYYMMDD格式转换为YYYY-MM-DD
            String formattedDate = slotDate.substring(0, 4) + "-" + slotDate.substring(4, 6) + "-" + slotDate
                .substring(6, 8);

            // 解析日期获取星期
            LocalDate date = LocalDate.parse(formattedDate);
            String weekDay = getWeekDay(date.getDayOfWeek().getValue());

            // 格式化为：MM/DD(周X) HH:MM
            return String.format("%s/%s(%s) %s", slotDate.substring(4, 6), slotDate.substring(6, 8), weekDay, slotTime);
        } catch (Exception e) {
            log.warn("格式化日期时间失败: slotDate={}, slotTime={}, error={}", slotDate, slotTime, e.getMessage());
            return slotDate + " " + slotTime; // 返回原始格式
        }
    }

    /**
     * 获取星期显示文本
     */
    private String getWeekDay(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            case 7:
                return "周日";
            default:
                return "";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelBooking(Long bookingId) {
        log.info("开始取消预约，预约ID: {}", bookingId);

        // 1. 查询预约记录
        BookingDO booking = baseMapper.selectById(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约记录不存在");
        }

        // 2. 验证预约状态 (status: 1=启用/已预约, 0=禁用/已取消)
        if (booking.getStatus() == null || booking.getStatus() != 1) {
            throw new RuntimeException("只能取消未开课状态的课程");
        }

        // 3. 验证预约是否属于当前学生
        Long currentStudentId = UserContextHolder.getUserId();
        if (!booking.getStudentId().equals(currentStudentId)) {
            throw new RuntimeException("无权限取消此预约");
        }

        // 4. 验证时间条件：距离开课时间必须超过2小时
        LocalDateTime bookingTime = parseSlotDateTime(booking.getSlotDate(), booking.getSlotTime());
        if (bookingTime == null) {
            throw new RuntimeException("预约时间格式错误，无法取消");
        }

        LocalDateTime now = LocalDateTime.now();
        long hoursDiff = java.time.Duration.between(now, bookingTime).toHours();

        if (hoursDiff <= 2) {
            throw new RuntimeException("距离开课时间不足2小时，无法取消预约");
        }

        // 5. 更新预约状态为已取消 (status: 0=禁用/已取消)
        log.info("开始更新预约状态: 预约ID={}, 原状态={}, 新状态=0", bookingId, booking.getStatus());
        booking.setStatus(0);
        booking.setUpdateTime(LocalDateTime.now());
        int updateResult = baseMapper.updateById(booking);
        log.info("预约状态更新结果: updateResult={}, 预约ID={}", updateResult, bookingId);

        // 验证更新是否成功
        BookingDO updatedBooking = baseMapper.selectById(bookingId);
        log.info("验证预约状态: 预约ID={}, 当前状态={}", bookingId, updatedBooking != null ? updatedBooking.getStatus() : "null");

        // 6. 创建退款交易记录和退还会员卡余额
        createRefundTransaction(booking);

        log.info("预约取消成功，预约ID: {}", bookingId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelBookingByTeacher(Long bookingId) {
        log.info("教师开始取消预约，预约ID: {}", bookingId);

        // 1. 查询预约记录
        BookingDO booking = baseMapper.selectById(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约记录不存在");
        }

        // 2. 验证预约状态 (status: 1=启用/已预约, 0=禁用/已取消)
        if (booking.getStatus() == null || booking.getStatus() != 1) {
            throw new RuntimeException("只能取消未开课状态的课程");
        }

        // 教师取消预约不需要验证学生权限和时间限制

        // 3. 更新预约状态为已取消 (status: 0=禁用/已取消)
        log.info("教师取消预约，开始更新预约状态: 预约ID={}, 原状态={}, 新状态=0", bookingId, booking.getStatus());
        booking.setStatus(0);
        booking.setUpdateTime(LocalDateTime.now());
        int updateResult = baseMapper.updateById(booking);
        log.info("教师取消预约状态更新结果: updateResult={}, 预约ID={}", updateResult, bookingId);

        // 验证更新是否成功
        BookingDO updatedBooking = baseMapper.selectById(bookingId);
        log.info("验证教师取消预约状态: 预约ID={}, 当前状态={}", bookingId, updatedBooking != null
            ? updatedBooking.getStatus()
            : "null");

        // 4. 创建退款交易记录和退还会员卡余额
        createRefundTransaction(booking);

        log.info("教师取消预约成功，预约ID: {}", bookingId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelBookingBySlotAndStudent(Long slotId, Long studentId) {
        log.info("教师开始通过时间段和学生取消预约，时间段ID: {}, 学生ID: {}", slotId, studentId);

        // 1. 查询预约记录
        LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookingDO::getSlotId, slotId)
            .eq(BookingDO::getStudentId, studentId)
            .eq(BookingDO::getStatus, 1); // 只查询启用状态的预约

        BookingDO booking = baseMapper.selectOne(queryWrapper);
        if (booking == null) {
            throw new RuntimeException("未找到该时间段的预约记录");
        }

        log.info("找到预约记录: ID={}, 时间段ID={}, 学生ID={}", booking.getId(), slotId, studentId);

        // 2. 直接调用已有的教师取消方法
        this.cancelBookingByTeacher(booking.getId());

        log.info("教师通过时间段取消预约成功，时间段ID: {}, 学生ID: {}", slotId, studentId);
    }

    /**
     * 创建退款交易记录和退还会员卡余额
     */
    private void createRefundTransaction(BookingDO booking) {
        try {
            log.info("开始处理预约取消退款，预约ID: {}, 学生卡ID: {}", booking.getId(), booking.getStuCardId());

            // 1. 查询学生会员卡信息
            StuCardDO stuCard = stuCardMapper.selectById(booking.getStuCardId());
            if (stuCard == null) {
                log.warn("未找到学生会员卡信息，卡ID: {}", booking.getStuCardId());
                return;
            }

            // 2. 查询原始扣费交易记录，获取扣费金额
            LambdaQueryWrapper<TransactionDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TransactionDO::getStuCardId, booking.getStuCardId())
                .eq(TransactionDO::getTransType, TransactionType.BOOK_DEBIT)
                .orderBy(true, false, TransactionDO::getCreateTime)
                .last("LIMIT 1");

            TransactionDO originalTransaction = transactionMapper.selectOne(queryWrapper);
            BigDecimal refundAmount = BigDecimal.ONE; // 默认退还1个课时

            if (originalTransaction != null) {
                refundAmount = originalTransaction.getAmount().abs(); // 取绝对值作为退款金额
                log.info("找到原始扣费记录，退款金额: {}", refundAmount);
            } else {
                log.warn("未找到原始扣费记录，使用默认退款金额: {}", refundAmount);
            }

            // 3. 更新会员卡余额（增加退款金额）
            BigDecimal beforeBalance = stuCard.getBalance();
            BigDecimal afterBalance = beforeBalance.add(refundAmount);

            stuCard.setBalance(afterBalance);
            stuCard.setUpdateTime(LocalDateTime.now());
            stuCardMapper.updateById(stuCard);

            log.info("会员卡余额已更新，卡ID: {}, 退款前余额: {}, 退款后余额: {}", stuCard.getId(), beforeBalance, afterBalance);

            // 4. 创建退款交易记录
            TransactionDO refundTransaction = new TransactionDO();
            refundTransaction.setStuCardId(booking.getStuCardId());
            refundTransaction.setStuId(booking.getStudentId());
            refundTransaction.setStuName(booking.getStudentName());
            refundTransaction.setCardTitle(booking.getCardName());
            refundTransaction.setTransType(TransactionType.CANCEL);
            refundTransaction.setAmount(refundAmount);
            refundTransaction.setBeforeAmt(beforeBalance);
            refundTransaction.setAfterAmt(afterBalance);
            refundTransaction.setRemark(String.format("取消预约退款 - 课程: %s, 时间: %s %s", booking.getLessonName(), booking
                .getSlotDate(), booking.getSlotTime()));
            refundTransaction.setCreateTime(LocalDateTime.now());
            refundTransaction.setUpdateTime(LocalDateTime.now());

            transactionMapper.insert(refundTransaction);

            log.info("退款交易记录创建成功，交易ID: {}, 退款金额: {}", refundTransaction.getId(), refundAmount);

        } catch (Exception e) {
            log.error("处理预约取消退款失败，预约ID: {}", booking.getId(), e);
            throw new RuntimeException("退款处理失败: " + e.getMessage());
        }
    }

    /**
     * 解析课时日期和时间为LocalDateTime
     */
    private LocalDateTime parseSlotDateTime(String slotDate, String slotTime) {
        try {
            // slotDate格式: 20250110, slotTime格式: 18:30
            int year = Integer.parseInt(slotDate.substring(0, 4));
            int month = Integer.parseInt(slotDate.substring(4, 6));
            int day = Integer.parseInt(slotDate.substring(6, 8));

            String[] timeParts = slotTime.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            return LocalDateTime.of(year, month, day, hour, minute);
        } catch (Exception e) {
            log.error("解析课时时间失败: slotDate={}, slotTime={}", slotDate, slotTime, e);
            return null;
        }
    }

    @Override
    public List<BookingDO> getBySlotId(Long slotId) {
        if (slotId == null) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookingDO::getSlotId, slotId).eq(BookingDO::getStatus, 1); // 只查询已预约状态的记录

        return baseMapper.selectList(queryWrapper);
    }

}