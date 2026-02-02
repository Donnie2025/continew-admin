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

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.education.model.entity.FixedDO;

import java.util.List;

/**
 * 固定课 Mapper
 *
 * @author Charles7c
 * @since 2024/12/28 18:00
 */
public interface FixedMapper extends BaseMapper<FixedDO> {

    /**
     * 根据教师ID查询固定课列表
     *
     * @param teacherId 教师ID
     * @return 固定课列表
     */
    @Select("SELECT * FROM edu_fixed WHERE teacher_id = #{teacherId} AND status = 1 ORDER BY week_day, start_time")
    List<FixedDO> selectByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 根据星期几查询固定课列表
     *
     * @param weekDay 星期几
     * @return 固定课列表
     */
    @Select("SELECT * FROM edu_fixed WHERE week_day = #{weekDay} AND status = 1 ORDER BY start_time")
    List<FixedDO> selectByWeekDay(@Param("weekDay") Integer weekDay);

    /**
     * 检查固定课时间冲突
     *
     * @param teacherId 教师ID
     * @param weekDay   星期几
     * @param startTime 开始时间
     * @param excludeId 排除的固定课ID（用于更新时排除自己）
     * @return 冲突数量
     */
    @Select("SELECT COUNT(*) FROM edu_fixed WHERE teacher_id = #{teacherId} AND week_day = #{weekDay} AND start_time = #{startTime} AND status = 1" + " AND (#{excludeId} IS NULL OR id != #{excludeId})")
    int countTimeConflict(@Param("teacherId") Long teacherId,
                          @Param("weekDay") Integer weekDay,
                          @Param("startTime") String startTime,
                          @Param("excludeId") Long excludeId);
}
