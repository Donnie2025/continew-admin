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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.StudentMapper;
import top.continew.admin.education.model.entity.StudentDO;
import top.continew.admin.education.model.query.StudentQuery;
import top.continew.admin.education.model.req.StudentReq;
import top.continew.admin.education.model.resp.StudentDetailResp;
import top.continew.admin.education.model.resp.StudentResp;
import top.continew.admin.education.service.StudentService;
import top.continew.admin.system.service.FileService;
import org.dromara.x.file.storage.core.FileInfo;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学生管理业务实现
 *
 * @author don
 * @since 2025/04/20 01:32
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends BaseServiceImpl<StudentMapper, StudentDO, StudentResp, StudentDetailResp, StudentQuery, StudentReq> implements StudentService {

    private final FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(StudentReq req) {
        // 处理头像上传
        if (req.getAvatarFile() != null) {
            FileInfo fileInfo = uploadAvatar(req.getAvatarFile());
            req.setAvatar(fileInfo.getUrl());
        }
        return super.create(req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StudentReq req, Long id) {
        // 处理头像上传
        if (req.getAvatarFile() != null) {
            FileInfo fileInfo = uploadAvatar(req.getAvatarFile());
            req.setAvatar(fileInfo.getUrl());
        }
        super.update(req, id);
    }

    /**
     * 上传学生头像
     *
     * @param file 头像文件
     * @return 文件信息
     */
    public FileInfo uploadAvatar(MultipartFile file) {
        // 上传到默认存储，路径为：头像/年/月/日/
        String path = "avatar/" + fileService.getDefaultFilePath();
        return fileService.upload(file, path);
    }
}