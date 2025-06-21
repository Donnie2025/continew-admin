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

package top.continew.admin.education.mapper;

import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.education.model.entity.TeacherDO;

/**
 * 教师 Mapper
 *
 * @author donnie
 * @since 2025/04/04 18:33
 */
public interface TeacherMapper extends BaseMapper<TeacherDO> {
    /**
     * 根据手机号查询教师
     * @param phone 手机号
     * @return 教师实体
     */
    TeacherDO selectByPhone(String phone);
}