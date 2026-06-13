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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import top.continew.admin.education.service.FeishuService;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.MaterialMapper;
import top.continew.admin.education.model.entity.MaterialDO;
import top.continew.admin.education.model.query.MaterialQuery;
import top.continew.admin.education.model.req.MaterialReq;
import top.continew.admin.education.model.req.MaterialSortReq;
import top.continew.admin.education.model.req.SyncCloudFoldersReq;
import top.continew.admin.education.model.resp.MaterialDetailResp;
import top.continew.admin.education.model.resp.MaterialResp;
import top.continew.admin.education.model.resp.MaterialStatisticsResp;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.model.resp.classin.ClassinCloudListResp;
import top.continew.admin.education.service.MaterialService;
import cn.dev33.satoken.stp.StpUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 教材业务实现
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MaterialServiceImpl extends BaseServiceImpl<MaterialMapper, MaterialDO, MaterialResp, MaterialDetailResp, MaterialQuery, MaterialReq> implements MaterialService {

    private final ClassinClient classinClient;
    private final FeishuService feishuService;

    @Override
    public List<MaterialResp> listAll() {
        LambdaQueryWrapper<MaterialDO> wrapper = Wrappers.<MaterialDO>lambdaQuery()
            .orderByAsc(MaterialDO::getSort)
            .orderByAsc(MaterialDO::getId);
        return baseMapper.selectList(wrapper).stream().map(this::toResp).collect(Collectors.toList());
    }

    @Override
    public List<MaterialResp> listByCategory(String category) {
        LambdaQueryWrapper<MaterialDO> queryWrapper = Wrappers.<MaterialDO>lambdaQuery();
        queryWrapper.eq(MaterialDO::getType, category)
            .eq(MaterialDO::getStatus, true)
            .orderByAsc(MaterialDO::getSort)
            .orderByAsc(MaterialDO::getId);

        List<MaterialDO> materialList = baseMapper.selectList(queryWrapper);
        return materialList.stream().map(this::toResp).collect(Collectors.toList());
    }

    @Override
    public MaterialStatisticsResp getStatistics() {
        MaterialStatisticsResp statistics = new MaterialStatisticsResp();

        // 总数统计
        statistics.setTotalCount(baseMapper.selectCount(null));

        // 启用/禁用统计
        LambdaQueryWrapper<MaterialDO> enabledWrapper = Wrappers.<MaterialDO>lambdaQuery();
        enabledWrapper.eq(MaterialDO::getStatus, true);
        statistics.setEnabledCount(baseMapper.selectCount(enabledWrapper));

        LambdaQueryWrapper<MaterialDO> disabledWrapper = Wrappers.<MaterialDO>lambdaQuery();
        disabledWrapper.eq(MaterialDO::getStatus, false);
        statistics.setDisabledCount(baseMapper.selectCount(disabledWrapper));

        // 前端展示统计
        LambdaQueryWrapper<MaterialDO> displayWrapper = Wrappers.<MaterialDO>lambdaQuery();
        displayWrapper.eq(MaterialDO::getIsShow, true);
        statistics.setDisplayCount(baseMapper.selectCount(displayWrapper));

        // 按节点类型统计
        List<MaterialDO> allMaterials = baseMapper.selectList(null);
        Map<String, Long> typeStats = allMaterials.stream()
            .filter(m -> m.getType() != null)
            .collect(Collectors.groupingBy(MaterialDO::getType, Collectors.counting()));
        statistics.setTypeStats(typeStats);

        // 最近7天创建的教材数
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LambdaQueryWrapper<MaterialDO> recentWrapper = Wrappers.<MaterialDO>lambdaQuery();
        recentWrapper.ge(MaterialDO::getCreateTime, sevenDaysAgo);
        statistics.setRecentCount(baseMapper.selectCount(recentWrapper));

        return statistics;
    }

    @Override
    public void batchUpdateSort(List<MaterialSortReq> sortList) {
        for (MaterialSortReq sortReq : sortList) {
            MaterialDO material = new MaterialDO();
            material.setId(sortReq.getId());
            material.setSort(sortReq.getSort());
            baseMapper.updateById(material);
        }
    }

    @Override
    protected void afterCreate(MaterialReq req, MaterialDO entity) {
        boolean needUpdate = false;

        // 如果前端没有传递status，设置默认值为启用状态
        if (req.getStatus() == null) {
            entity.setStatus(true); // true表示启用
            needUpdate = true;
        }

        // 如果前端没有传递创建人，设置为当前登录用户
        if (req.getCreateUser() == null) {
            try {
                Long currentUserId = StpUtil.getLoginIdAsLong();
                entity.setCreateUser(currentUserId);
                needUpdate = true;
            } catch (Exception e) {
                // 如果获取登录用户失败，使用默认值
                entity.setCreateUser(1L);
                needUpdate = true;
            }
        }

        // 如果前端没有传递创建时间，设置为当前时间
        if (req.getCreateTime() == null) {
            entity.setCreateTime(LocalDateTime.now());
            needUpdate = true;
        }

        // 如果有字段需要更新，执行更新操作
        if (needUpdate) {
            baseMapper.updateById(entity);
        }

        super.afterCreate(req, entity);
    }

    @Override
    protected void afterUpdate(MaterialReq req, MaterialDO entity) {
        // 查询更新前的数据
        MaterialDO oldEntity = baseMapper.selectById(entity.getId());
        if (oldEntity == null) {
            super.afterUpdate(req, entity);
            return;
        }

        // 检查名称是否发生了变化
        boolean nameChanged = req.getName() != null && !req.getName().equals(oldEntity.getName());
        if (!nameChanged) {
            super.afterUpdate(req, entity);
            return;
        }

        String newName = req.getName();
        List<String> failedServices = new ArrayList<>();

        // 1. 同步重命名到 ClassIn
        if (oldEntity.getCloudId() != null) {
            try {
                if ("LESSON".equals(oldEntity.getType())) {
                    String baseName = newName.contains(".") ? newName.substring(0, newName.lastIndexOf(".")) : newName;
                    classinClient.renameCloudFile(oldEntity.getCloudId(), baseName);
                } else {
                    classinClient.renameCloudFolder(oldEntity.getCloudId(), newName);
                }
            } catch (Exception e) {
                failedServices.add("ClassIn");
            }
        }

        // 2. 飞书云空间文件夹不支持 API 重命名，跳过

        // 如果有同步失败，抛出异常提示用户
        if (!failedServices.isEmpty()) {
            String failedMsg = String.join("、", failedServices);
            throw new RuntimeException("教材已更新，但" + failedMsg + "重命名失败，请手动同步");
        }

        super.afterUpdate(req, entity);
    }

    @Override
    protected void beforeDelete(List<Long> ids) {
        List<MaterialDO> materials = baseMapper.selectList(Wrappers.<MaterialDO>lambdaQuery()
            .in(MaterialDO::getId, ids));
        for (MaterialDO material : materials) {
            if (material.getCloudId() != null) {
                try {
                    if ("LESSON".equals(material.getType())) {
                        classinClient.deleteCloudFile(material.getCloudId());
                    } else {
                        classinClient.deleteCloudFolder(material.getCloudId());
                    }
                } catch (Exception ignored) {
                }
            }
            if (material.getFeishuFolderToken() != null) {
                try {
                    // LESSON 节点存的是飞书文件 token，其他节点存的是文件夹 token
                    feishuService.deleteFolder(material.getFeishuFolderToken(), "LESSON".equals(material.getType())
                        ? "file"
                        : "folder");
                } catch (Exception ignored) {
                }
            }
        }
        super.beforeDelete(ids);
    }

    @Override
    public int syncCloudFolders(SyncCloudFoldersReq req) {
        long currentUserId;
        try {
            currentUserId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            currentUserId = 1L;
        }
        int count = 0;
        // 同步文件夹
        if (req.getFolders() != null && !req.getFolders().isEmpty()) {
            List<SyncCloudFoldersReq.FolderItem> sortedFolders = req.getFolders()
                .stream()
                .sorted(Comparator.comparing(SyncCloudFoldersReq.FolderItem::getFolderName))
                .collect(Collectors.toList());
            for (SyncCloudFoldersReq.FolderItem item : sortedFolders) {
                if (Boolean.TRUE.equals(req.getSkipExisting())) {
                    if (baseMapper.exists(Wrappers.<MaterialDO>lambdaQuery()
                        .eq(MaterialDO::getCloudId, item.getFolderId()))) {
                        continue;
                    }
                }
                String name = item.getFolderName();
                MaterialDO node = new MaterialDO();
                node.setPid(req.getPid() != null ? req.getPid() : 0L);
                node.setType(req.getType());
                node.setName(name);
                node.setCode(extractCodeFromName(name));
                node.setCloudId(item.getFolderId());
                node.setCloudName(name);
                node.setIsShow(true);
                node.setSort(count + 1);
                node.setStatus(true);
                node.setCreateUser(currentUserId);
                node.setCreateTime(LocalDateTime.now());
                baseMapper.insert(node);
                count++;
            }
        }
        // 同步文件
        if (req.getFiles() != null && !req.getFiles().isEmpty()) {
            List<SyncCloudFoldersReq.FileItem> sortedFiles = req.getFiles()
                .stream()
                .sorted(Comparator.comparing(SyncCloudFoldersReq.FileItem::getFileName))
                .collect(Collectors.toList());
            for (SyncCloudFoldersReq.FileItem item : sortedFiles) {
                if (Boolean.TRUE.equals(req.getSkipExisting())) {
                    if (baseMapper.exists(Wrappers.<MaterialDO>lambdaQuery()
                        .eq(MaterialDO::getCloudId, item.getFileId()))) {
                        continue;
                    }
                }
                String name = item.getFileName();
                MaterialDO node = new MaterialDO();
                node.setPid(req.getPid() != null ? req.getPid() : 0L);
                node.setType(req.getType());
                node.setName(name);
                node.setCode(extractCodeFromName(name));
                node.setCloudId(item.getFileId());
                node.setCloudName(name);
                node.setIsShow(true);
                node.setSort(count + 1);
                node.setStatus(true);
                node.setCreateUser(currentUserId);
                node.setCreateTime(LocalDateTime.now());
                baseMapper.insert(node);
                count++;
            }
        }
        return count;
    }

    @Override
    public int syncCloudData(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return 0;

        List<MaterialDO> parents = baseMapper.selectList(Wrappers.<MaterialDO>lambdaQuery()
            .in(MaterialDO::getId, ids)
            .isNotNull(MaterialDO::getCloudId));
        if (parents.isEmpty())
            return 0;

        long currentUserId;
        try {
            currentUserId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            currentUserId = 1L;
        }

        int count = 0;
        for (MaterialDO parent : parents) {
            ClassinCloudListResp cloudList;
            try {
                cloudList = classinClient.getCloudList(parent.getCloudId());
            } catch (Exception e) {
                continue;
            }
            if (cloudList == null)
                continue;

            String childFolderType = getChildFolderType(parent.getType());

            // Sync folders
            if (childFolderType != null && cloudList.getFolderList() != null) {
                for (ClassinCloudListResp.FolderItem folder : cloudList.getFolderList()) {
                    MaterialDO existing = baseMapper.selectOne(Wrappers.<MaterialDO>lambdaQuery()
                        .eq(MaterialDO::getCloudId, folder.getFolderId()));
                    if (existing == null) {
                        MaterialDO node = buildCloudNode(parent.getId(), childFolderType, folder.getFolderName(), folder
                            .getFolderId(), currentUserId);
                        baseMapper.insert(node);
                        count++;
                    } else {
                        MaterialDO update = new MaterialDO();
                        update.setId(existing.getId());
                        update.setCloudName(folder.getFolderName());
                        baseMapper.updateById(update);
                    }
                }
            }

            // Sync files as LESSON
            if (cloudList.getFileList() != null) {
                for (ClassinCloudListResp.FileItem file : cloudList.getFileList()) {
                    MaterialDO existing = baseMapper.selectOne(Wrappers.<MaterialDO>lambdaQuery()
                        .eq(MaterialDO::getCloudId, file.getId()));
                    if (existing == null) {
                        MaterialDO node = buildCloudNode(parent.getId(), "LESSON", file.getFileName(), file
                            .getId(), currentUserId);
                        baseMapper.insert(node);
                        count++;
                    } else {
                        MaterialDO update = new MaterialDO();
                        update.setId(existing.getId());
                        update.setCloudName(file.getFileName());
                        baseMapper.updateById(update);
                    }
                }
            }

            // Re-sort all children of this parent by name ascending
            List<MaterialDO> children = baseMapper.selectList(Wrappers.<MaterialDO>lambdaQuery()
                .eq(MaterialDO::getPid, parent.getId()));
            children.sort(Comparator.comparing(m -> m.getName() != null ? m.getName() : ""));
            for (int i = 0; i < children.size(); i++) {
                MaterialDO sortUpdate = new MaterialDO();
                sortUpdate.setId(children.get(i).getId());
                sortUpdate.setSort(i + 1);
                baseMapper.updateById(sortUpdate);
            }
        }
        return count;
    }

    private String getChildFolderType(String parentType) {
        if (parentType == null)
            return null;
        switch (parentType) {
            case "CATEGORY":
                return "BOOK";
            case "BOOK":
                return "LEVEL";
            case "LEVEL":
                return "UNIT";
            case "UNIT":
                return "UNIT";
            default:
                return null;
        }
    }

    private MaterialDO buildCloudNode(Long pid, String type, String name, String cloudId, long userId) {
        MaterialDO node = new MaterialDO();
        node.setPid(pid);
        node.setType(type);
        node.setName(name);
        node.setCode(extractCodeFromName(name));
        node.setCloudId(cloudId);
        node.setCloudName(name);
        node.setIsShow(true);
        node.setSort(999);
        node.setStatus(true);
        node.setCreateUser(userId);
        node.setCreateTime(LocalDateTime.now());
        return node;
    }

    private static final Pattern CODE_PATTERN = Pattern.compile("[\u3010【]([A-Za-z0-9_\\-]+)[\u3011】]");

    /**
     * 从名称的【】中提取字母编码，如"【EFK_K1】幼儿园小班" → "EFK_K1"
     */
    private String extractCodeFromName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        Matcher matcher = CODE_PATTERN.matcher(name);
        return matcher.find() ? matcher.group(1) : null;
    }

    /**
     * 转换为响应对象
     */
    private MaterialResp toResp(MaterialDO materialDO) {
        MaterialResp resp = new MaterialResp();
        resp.setId(materialDO.getId());
        resp.setPid(materialDO.getPid());
        resp.setType(materialDO.getType());
        resp.setName(materialDO.getName());
        resp.setCode(materialDO.getCode());
        resp.setCoverImg(materialDO.getCoverImg());
        resp.setDescription(materialDO.getDescription());
        resp.setLessonUrl(materialDO.getLessonUrl());
        resp.setCloudId(materialDO.getCloudId());
        resp.setCloudName(materialDO.getCloudName());
        resp.setIsShow(materialDO.getIsShow());
        resp.setSort(materialDO.getSort());
        resp.setStatus(materialDO.getStatus() != null && materialDO.getStatus() ? 1 : 0);
        resp.setUpdateUser(materialDO.getUpdateUser());
        resp.setUpdateTime(materialDO.getUpdateTime());
        return resp;
    }

    @Override
    public Map<String, Object> syncFeishu(Long id, boolean recursive) {
        MaterialDO root = baseMapper.selectById(id);
        if (root == null) {
            throw new RuntimeException("节点不存在");
        }
        if ("LESSON".equals(root.getType())) {
            throw new RuntimeException("课程节点不支持同步，请选择文件夹节点");
        }

        Map<String, Object> result = new HashMap<>();
        List<String> failedDetails = new ArrayList<>();
        int[] counts = {0, 0, 0}; // [total, success, failed]
        Map<String, List<FeishuService.FeishuFile>> folderCache = new HashMap<>();

        if (recursive) {
            // 递归同步节点及其所有子节点
            syncNodeRecursive(root, failedDetails, counts, folderCache);
        } else {
            // 只同步直接子节点（下一层级）
            List<MaterialDO> children = baseMapper.selectList(Wrappers.<MaterialDO>lambdaQuery()
                .eq(MaterialDO::getPid, root.getId()));
            for (MaterialDO child : children) {
                syncSingleNode(child, failedDetails, counts, folderCache);
            }
        }

        result.put("total", counts[0]);
        result.put("success", counts[1]);
        result.put("failed", counts[2]);
        result.put("details", failedDetails);
        return result;
    }

    private void syncSingleNode(MaterialDO node,
                                List<String> failedDetails,
                                int[] counts,
                                Map<String, List<FeishuService.FeishuFile>> folderCache) {
        // 如果当前节点没有飞书token（null或空字符串），尝试同步
        if (node.getFeishuFolderToken() == null || node.getFeishuFolderToken().isEmpty()) {
            counts[0]++;
            MaterialDO parent = baseMapper.selectById(node.getPid());
            if (parent != null && parent.getFeishuFolderToken() != null && !parent.getFeishuFolderToken().isEmpty()) {
                try {
                    String parentToken = parent.getFeishuFolderToken();
                    List<FeishuService.FeishuFile> files;

                    // 检查缓存中是否已有该文件夹的文件列表
                    if (folderCache.containsKey(parentToken)) {
                        log.info("[飞书同步] 使用缓存获取文件夹 {} 的文件", parentToken);
                        files = folderCache.get(parentToken);
                    } else {
                        log.info("[飞书同步] 查询飞书获取文件夹 {} 的文件", parentToken);
                        files = feishuService.getFolderFiles(parentToken);
                        folderCache.put(parentToken, files);
                    }

                    String targetName = "LESSON".equals(node.getType()) ? node.getCloudName() : node.getName();

                    log.info("[飞书同步] 节点: {}, 类型: {}, 目标名称: {}, 父节点token: {}", node.getName(), node
                        .getType(), targetName, parentToken);

                    // 匹配飞书文件/文件夹
                    FeishuService.FeishuFile matched = files.stream().filter(f -> {
                        boolean result = matchName(f.getName(), targetName);
                        if (result) {
                            log.info("[飞书同步] ✓ 匹配成功: {} 与 {} 匹配", f.getName(), targetName);
                        }
                        return result;
                    }).findFirst().orElse(null);

                    if (matched != null) {
                        log.info("[飞书同步] 节点 {} 同步成功，token: {}", node.getName(), matched.getToken());
                        MaterialDO update = new MaterialDO();
                        update.setId(node.getId());
                        update.setFeishuFolderToken(matched.getToken());
                        if ("LESSON".equals(node.getType()) && matched.getUrl() != null) {
                            update.setLessonUrl(matched.getUrl());
                        }
                        baseMapper.updateById(update);
                        node.setFeishuFolderToken(matched.getToken());
                        counts[1]++;
                    } else {
                        log.warn("[飞书同步] ✗ 节点 {} 在飞书中未找到匹配文件，目标名称: {}", node.getName(), targetName);
                        counts[2]++;
                        failedDetails.add(node.getName() + "（飞书中未找到匹配文件）");
                    }
                } catch (Exception e) {
                    log.error("[飞书同步] 节点 {} 同步异常", node.getName(), e);
                    counts[2]++;
                    failedDetails.add(node.getName() + "（" + e.getMessage() + "）");
                }
            } else {
                log.warn("[飞书同步] 节点 {} 的父节点缺少飞书token", node.getName());
                counts[2]++;
                failedDetails.add(node.getName() + "（父节点缺少飞书token）");
            }
        }
    }

    private void syncNodeRecursive(MaterialDO node,
                                   List<String> failedDetails,
                                   int[] counts,
                                   Map<String, List<FeishuService.FeishuFile>> folderCache) {
        // 同步当前节点
        syncSingleNode(node, failedDetails, counts, folderCache);

        // 递归处理子节点
        List<MaterialDO> children = baseMapper.selectList(Wrappers.<MaterialDO>lambdaQuery()
            .eq(MaterialDO::getPid, node.getId()));
        for (MaterialDO child : children) {
            syncNodeRecursive(child, failedDetails, counts, folderCache);
        }
    }

    private boolean matchName(String feishuName, String localName) {
        if (feishuName == null || localName == null)
            return false;

        // 去掉扩展名后比较
        String fn = feishuName.replaceAll("\\.[^.]+$", "");
        String ln = localName.replaceAll("\\.[^.]+$", "");

        // 标准化：去掉所有空格进行比较
        fn = fn.replaceAll("\\s+", "");
        ln = ln.replaceAll("\\s+", "");

        log.debug("[飞书同步-matchName] 去空格后比较: '{}' vs '{}', 结果: {}", fn, ln, fn.equals(ln));

        return fn.equals(ln);
    }
}