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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.admin.education.model.entity.FixedLogDO;

import java.util.List;

/**
 * 固定课操作记录 Mapper
 *
 * @author Charles7c
 * @since 2024/12/28 18:00
 */
@Mapper
public interface FixedLogMapper extends BaseMapper<FixedLogDO> {

    /**
     * 根据固定课ID查询操作记录列表
     *
     * @param fixedId 固定课ID
     * @return 操作记录列表
     */
    @Select("SELECT * FROM edu_fixed_log WHERE fixed_id = #{fixedId} ORDER BY op_time DESC")
    List<FixedLogDO> selectByFixedId(@Param("fixedId") Long fixedId);

    /**
     * 根据学生ID查询操作记录列表
     *
     * @param studentId 学生ID
     * @return 操作记录列表
     */
    @Select("SELECT * FROM edu_fixed_log WHERE student_id = #{studentId} ORDER BY op_time DESC")
    List<FixedLogDO> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据教师ID查询操作记录列表
     *
     * @param teacherId 教师ID
     * @return 操作记录列表
     */
    @Select("SELECT * FROM edu_fixed_log WHERE teacher_id = #{teacherId} ORDER BY op_time DESC")
    List<FixedLogDO> selectByTeacherId(@Param("teacherId") Long teacherId);
}
