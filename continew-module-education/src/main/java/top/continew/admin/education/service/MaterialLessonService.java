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

package top.continew.admin.education.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.MaterialLessonQuery;
import top.continew.admin.education.model.req.MaterialLessonImportReq;
import top.continew.admin.education.model.req.MaterialLessonReq;
import top.continew.admin.education.model.resp.MaterialLessonDetailResp;
import top.continew.admin.education.model.resp.MaterialLessonImportResp;
import top.continew.admin.education.model.resp.MaterialLessonResp;
import top.continew.admin.education.model.resp.MaterialLessonWithCompletionResp;

import java.util.List;

/**
 * 课节业务接口
 *
 * @author don
 * @since 2025/12/29 21:22
 */
public interface MaterialLessonService extends BaseService<MaterialLessonResp, MaterialLessonDetailResp, MaterialLessonQuery, MaterialLessonReq> {

    /**
     * 从飞书链接导入课节
     *
     * @param req 导入请求参数
     * @return 导入结果
     */
    MaterialLessonImportResp importFromFeishu(MaterialLessonImportReq req);

    /**
     * 获取教材的课程列表（包含完成状态）
     * 
     * @param materialId 教材ID
     * @return 课程列表（包含完成状态）
     */
    List<MaterialLessonWithCompletionResp> listWithCompletionStatus(Long materialId);
}