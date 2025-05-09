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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.education.model.entity.SlotDO;

/**
 * 课程管理 Mapper
 *
 * @author don
 * @since 2025/04/25 23:24
 */
public interface SlotMapper extends BaseMapper<SlotDO> {
    
    /**
     * 检查是否已存在相同老师、日期、时间且状态为1的课时记录
     *
     * @param teacherId 老师ID
     * @param startDate 上课日期 (格式: YYYYMMDD)
     * @param startTime 上课时间 (格式: HH:mm)
     * @return 存在返回该记录，不存在返回null
     */
    default SlotDO checkExistingSlot(Long teacherId, String startDate, String startTime) {
        LambdaQueryWrapper<SlotDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SlotDO::getTeacherId, teacherId)
                    .eq(SlotDO::getStartDate, startDate)
                    .eq(SlotDO::getStartTime, startTime)
                    .eq(SlotDO::getStatus, 1); // 状态为1的记录
        
        return this.selectOne(queryWrapper);
    }
}