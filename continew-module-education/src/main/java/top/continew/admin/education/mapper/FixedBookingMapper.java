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
import top.continew.admin.education.model.entity.FixedBookingDO;

import java.util.List;

/**
 * 固定课预约 Mapper
 *
 * @author Charles7c
 * @since 2024/12/28 18:00
 */
@Mapper
public interface FixedBookingMapper extends BaseMapper<FixedBookingDO> {

    /**
     * 根据学生ID查询预约列表
     *
     * @param studentId 学生ID
     * @return 预约列表
     */
    @Select("SELECT * FROM edu_fixed_booking WHERE student_id = #{studentId} AND status = 1 ORDER BY week_day, start_time")
    List<FixedBookingDO> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据固定课ID查询预约列表
     *
     * @param fixedId 固定课ID
     * @return 预约列表
     */
    @Select("SELECT * FROM edu_fixed_booking WHERE fixed_id = #{fixedId} AND status = 1")
    List<FixedBookingDO> selectByFixedId(@Param("fixedId") Long fixedId);

    /**
     * 根据教师ID查询预约列表
     *
     * @param teacherId 教师ID
     * @return 预约列表
     */
    @Select("SELECT * FROM edu_fixed_booking WHERE teacher_id = #{teacherId} AND status = 1 ORDER BY week_day, start_time")
    List<FixedBookingDO> selectByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 统计固定课的预约数量
     *
     * @param fixedId 固定课ID
     * @return 预约数量
     */
    @Select("SELECT COUNT(*) FROM edu_fixed_booking WHERE fixed_id = #{fixedId} AND status = 1")
    int countByFixedId(@Param("fixedId") Long fixedId);

    /**
     * 检查学生是否已预约该固定课
     *
     * @param fixedId   固定课ID
     * @param studentId 学生ID
     * @return 预约记录数
     */
    @Select("SELECT COUNT(*) FROM edu_fixed_booking WHERE fixed_id = #{fixedId} AND student_id = #{studentId} AND status = 1")
    int countByFixedIdAndStudentId(@Param("fixedId") Long fixedId, @Param("studentId") Long studentId);
}
