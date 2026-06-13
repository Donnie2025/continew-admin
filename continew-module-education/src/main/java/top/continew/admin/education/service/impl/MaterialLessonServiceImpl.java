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

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.MaterialLessonMapper;
import top.continew.admin.education.mapper.MaterialMapper;
import top.continew.admin.education.model.entity.MaterialDO;
import top.continew.admin.education.model.entity.MaterialLessonDO;
import top.continew.admin.education.model.query.MaterialLessonQuery;
import top.continew.admin.education.model.req.MaterialLessonImportReq;
import top.continew.admin.education.model.req.MaterialLessonReq;
import top.continew.admin.education.model.resp.MaterialLessonDetailResp;
import top.continew.admin.education.model.resp.MaterialLessonImportResp;
import top.continew.admin.education.model.resp.MaterialLessonResp;
import top.continew.admin.education.service.FeishuService;
import top.continew.admin.education.service.MaterialLessonService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.education.model.resp.MaterialLessonWithCompletionResp;
import top.continew.admin.education.service.BookingService;

/**
 * 课节业务实现
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialLessonServiceImpl extends BaseServiceImpl<MaterialLessonMapper, MaterialLessonDO, MaterialLessonResp, MaterialLessonDetailResp, MaterialLessonQuery, MaterialLessonReq> implements MaterialLessonService {

    private final MaterialMapper materialMapper;
    private final FeishuService feishuService;

    @Autowired
    private BookingService bookingService;

    @Override
    protected QueryWrapper<MaterialLessonDO> buildQueryWrapper(MaterialLessonQuery query) {
        QueryWrapper<MaterialLessonDO> queryWrapper = super.buildQueryWrapper(query);
        // 只查询type=LESSON的记录（从edu_material表中）
        queryWrapper.eq("type", "LESSON");
        // 按照课节名称正序排列
        queryWrapper.orderByAsc("name");
        return queryWrapper;
    }

    @Override
    public void beforeCreate(MaterialLessonReq req) {
        // 自动拼装教材名称
        if (req.getMaterialName() == null && req.getMaterialId() != null) {
            MaterialDO material = materialMapper.selectById(req.getMaterialId());
            if (material != null) {
                String materialName = material.getName();
                req.setMaterialName(materialName);
            }
        }

        // 设置默认状态
        if (req.getStatus() == null) {
            req.setStatus(1); // 默认启用
        }

        // 设置创建人
        if (req.getCreateUser() == null) {
            try {
                req.setCreateUser(StpUtil.getLoginIdAsLong());
            } catch (Exception e) {
                req.setCreateUser(1L); // 默认用户ID
            }
        }

        // 设置创建时间
        if (req.getCreateTime() == null) {
            req.setCreateTime(LocalDateTime.now());
        }

        super.beforeCreate(req);
    }

    @Override
    public MaterialLessonImportResp importFromFeishu(MaterialLessonImportReq req) {
        log.info("开始从飞书链接导入课节，教材ID: {}, 飞书链接: {}, 覆盖模式: {}", req.getMaterialId(), req.getFeishuUrl(), req
            .getOverwrite());

        MaterialLessonImportResp resp = new MaterialLessonImportResp();
        resp.setTotalCount(0);
        resp.setSuccessCount(0);
        resp.setFailureCount(0);
        resp.setSkipCount(0);
        resp.setSuccessLessons(new ArrayList<>());
        resp.setFailureLessons(new ArrayList<>());
        resp.setSkipLessons(new ArrayList<>());

        try {
            // 验证教材是否存在
            MaterialDO material = materialMapper.selectById(req.getMaterialId());
            if (material == null) {
                MaterialLessonImportResp.FailureInfo failureInfo = new MaterialLessonImportResp.FailureInfo();
                failureInfo.setLessonName("导入验证");
                failureInfo.setReason("教材ID不存在: " + req.getMaterialId());
                resp.getFailureLessons().add(failureInfo);
                resp.setFailureCount(1);
                resp.setTotalCount(1);
                return resp;
            }

            // 解析飞书链接，提取文件夹ID
            String folderId = extractFolderIdFromUrl(req.getFeishuUrl());
            if (folderId == null) {
                MaterialLessonImportResp.FailureInfo failureInfo = new MaterialLessonImportResp.FailureInfo();
                failureInfo.setLessonName("链接解析");
                failureInfo.setReason("无效的飞书文件夹链接格式");
                resp.getFailureLessons().add(failureInfo);
                resp.setFailureCount(1);
                resp.setTotalCount(1);
                return resp;
            }

            // 从飞书获取文件列表
            List<FeishuService.FeishuFile> feishuFiles = feishuService.getFolderFiles(folderId);
            resp.setTotalCount(feishuFiles.size());

            String materialName = material.getName();

            // 处理每个文件
            for (FeishuService.FeishuFile feishuFile : feishuFiles) {
                String fileName = feishuFile.getName();
                try {
                    boolean lessonExists = isLessonExists(req.getMaterialId(), fileName);
                    log.info("处理文件: {}, 课节已存在: {}, 覆盖模式: {}", fileName, lessonExists, req.getOverwrite());

                    // 检查是否已存在相同名称的课节
                    if (!req.getOverwrite() && lessonExists) {
                        log.info("跳过已存在的课节: {}", fileName);
                        resp.getSkipLessons().add(fileName);
                        resp.setSkipCount(resp.getSkipCount() + 1);
                        continue;
                    }

                    // 创建课节
                    MaterialLessonDO lesson = new MaterialLessonDO();
                    lesson.setMaterialId(req.getMaterialId());
                    lesson.setMaterialName(materialName);
                    lesson.setLessonName(fileName);
                    lesson.setLessonUrl(feishuFile.getUrl()); // 使用飞书文件的真实URL
                    lesson.setStatus(true);
                    lesson.setCreateUser(getCurrentUserId());
                    lesson.setCreateTime(LocalDateTime.now());

                    // 如果覆盖模式且课节已存在，则更新
                    if (req.getOverwrite() && lessonExists) {
                        log.info("覆盖模式：更新已存在的课节: {}", fileName);
                        MaterialLessonDO existingLesson = getLessonByName(req.getMaterialId(), fileName);
                        if (existingLesson != null) {
                            lesson.setId(existingLesson.getId());
                            lesson.setUpdateUser(getCurrentUserId());
                            lesson.setUpdateTime(LocalDateTime.now());
                            baseMapper.updateById(lesson);
                            log.info("成功更新课节: {}", fileName);
                        }
                    } else {
                        log.info("新增课节: {}", fileName);
                        baseMapper.insert(lesson);
                    }

                    resp.getSuccessLessons().add(fileName);
                    resp.setSuccessCount(resp.getSuccessCount() + 1);
                    log.info("成功导入课节: {}, 文件大小: {} bytes", fileName, feishuFile.getSize());

                } catch (Exception e) {
                    log.error("导入课节失败: {}, 错误: {}", fileName, e.getMessage(), e);
                    MaterialLessonImportResp.FailureInfo failureInfo = new MaterialLessonImportResp.FailureInfo();
                    failureInfo.setLessonName(fileName);
                    failureInfo.setReason("导入失败: " + e.getMessage());
                    resp.getFailureLessons().add(failureInfo);
                    resp.setFailureCount(resp.getFailureCount() + 1);
                }
            }

            log.info("飞书导入完成，总计: {}, 成功: {}, 失败: {}, 跳过: {}", resp.getTotalCount(), resp.getSuccessCount(), resp
                .getFailureCount(), resp.getSkipCount());

        } catch (Exception e) {
            log.error("飞书导入过程中发生异常", e);
            MaterialLessonImportResp.FailureInfo failureInfo = new MaterialLessonImportResp.FailureInfo();
            failureInfo.setLessonName("系统错误");
            failureInfo.setReason("导入过程中发生异常: " + e.getMessage());
            resp.getFailureLessons().add(failureInfo);
            resp.setFailureCount(resp.getFailureCount() + 1);
        }

        return resp;
    }

    /**
     * 从飞书URL中提取文件夹ID
     */
    private String extractFolderIdFromUrl(String url) {
        // 飞书链接格式: https://ai.feishu.cn/drive/folder/TEV8fJml3lsaj2dLBOUcxovanvc
        Pattern pattern = Pattern.compile("https://[^/]+/drive/folder/([a-zA-Z0-9]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 检查课节是否已存在
     */
    private boolean isLessonExists(Long materialId, String lessonName) {
        return baseMapper.selectCount(Wrappers.<MaterialLessonDO>lambdaQuery()
            .eq(MaterialLessonDO::getMaterialId, materialId)
            .eq(MaterialLessonDO::getLessonName, lessonName)) > 0;
    }

    /**
     * 根据名称获取课节
     */
    private MaterialLessonDO getLessonByName(Long materialId, String lessonName) {
        return baseMapper.selectOne(Wrappers.<MaterialLessonDO>lambdaQuery()
            .eq(MaterialLessonDO::getMaterialId, materialId)
            .eq(MaterialLessonDO::getLessonName, lessonName)
            .last("LIMIT 1"));
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            return 1L; // 默认用户ID
        }
    }

    @Override
    public List<MaterialLessonWithCompletionResp> listWithCompletionStatus(Long materialId) {
        try {
            log.info("获取教材课程列表（包含完成状态）: materialId={}", materialId);

            // 1. 获取教材的所有课程
            LambdaQueryWrapper<MaterialLessonDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MaterialLessonDO::getMaterialId, materialId)
                .eq(MaterialLessonDO::getStatus, 1) // 只查询启用的课程
                .orderByAsc(MaterialLessonDO::getLessonName); // 按课程名称排序

            List<MaterialLessonDO> lessons = baseMapper.selectList(queryWrapper);

            if (lessons.isEmpty()) {
                log.info("教材下没有找到课程: materialId={}", materialId);
                return new ArrayList<>();
            }

            // 2. 获取当前学生已完成的课程ID列表
            Set<Long> completedLessonIds = new HashSet<>();
            try {
                // 获取当前登录学生ID
                Long currentStudentId = getCurrentStudentId();
                if (currentStudentId != null) {
                    // 调用BookingService获取已完成的课程ID
                    List<Long> completedIds = bookingService.getCompletedLessonIds(currentStudentId, materialId);
                    completedLessonIds.addAll(completedIds);
                    log.info("学生已完成课程数量: studentId={}, completedCount={}", currentStudentId, completedIds.size());
                }
            } catch (Exception e) {
                log.warn("获取学生完成状态失败，将返回未完成状态: {}", e.getMessage());
            }

            // 3. 组装响应数据
            List<MaterialLessonWithCompletionResp> result = new ArrayList<>();
            for (MaterialLessonDO lesson : lessons) {
                MaterialLessonWithCompletionResp resp = new MaterialLessonWithCompletionResp();

                // 复制基础字段
                BeanUtils.copyProperties(lesson, resp);

                // 设置完成状态
                boolean isCompleted = completedLessonIds.contains(lesson.getId());
                resp.setCompleted(isCompleted);

                // 如果已完成，可以设置完成时间（这里暂时不实现，因为需要额外查询）
                if (isCompleted) {
                    resp.setCompletionTime("已完成"); // 简化处理
                }

                result.add(resp);
            }

            log.info("返回课程列表: materialId={}, totalCount={}, completedCount={}", materialId, result
                .size(), completedLessonIds.size());

            return result;

        } catch (Exception e) {
            log.error("获取教材课程列表失败: materialId={}, error={}", materialId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取当前登录学生ID
     * 这里需要根据实际的用户上下文获取方式进行调整
     */
    private Long getCurrentStudentId() {
        try {
            // 从用户上下文获取当前用户ID
            // 这里假设小程序登录后，用户ID就是学生ID
            return UserContextHolder.getUserId();
        } catch (Exception e) {
            log.warn("获取当前学生ID失败: {}", e.getMessage());
            return null;
        }
    }
}