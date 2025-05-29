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

package top.continew.admin.controller.education;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.starter.web.model.R;
import top.continew.admin.education.model.query.SlotQuery;
import top.continew.admin.education.model.req.SlotReq;
import top.continew.admin.education.model.req.BatchSlotReq;
import top.continew.admin.education.model.resp.SlotDetailResp;
import top.continew.admin.education.model.resp.SlotResp;
import top.continew.admin.education.service.SlotService;
import top.continew.admin.education.service.BookingService;

/**
 * 课程管理管理 API
 *
 * @author don
 * @since 2025/04/25 23:24
 */
@Tag(name = "课程管理管理 API")
@RestController
@CrudRequestMapping(value = "/education/slot", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class SlotController extends BaseController<SlotService, SlotResp, SlotDetailResp, SlotQuery, SlotReq> {

    private static final Logger log = LoggerFactory.getLogger(SlotController.class);

    @Autowired
    private SlotService slotService;
    
    @Autowired
    private BookingService bookingService;

    /**
     * 批量添加课程时间
     *
     * @param batchSlotReq 批量课程时间请求
     * @return 添加结果
     */
    @Operation(summary = "批量添加课程时间")
    @PostMapping("/batch")
    public R<List<SlotResp>> batchCreateSlot(@RequestBody BatchSlotReq batchSlotReq) {
        return R.ok(slotService.batchCreateSlot(batchSlotReq));
    }

    /**
     * 根据日期范围和教师ID查询可用课时
     *
     * @param teacherId 教师ID
     * @param startDate 开始日期（格式：YYYYMMDD）
     * @param endDate   结束日期（格式：YYYYMMDD）
     * @return 课时列表
     */
    @Operation(summary = "根据日期范围和教师ID查询可用课时")
    @GetMapping("/available")
    public R<List<SlotResp>> listAvailableSlots(@RequestParam("teacherId") Long teacherId,
                                                @RequestParam("startDate") String startDate,
                                                @RequestParam("endDate") String endDate) {
        log.info("查询可用课时, 教师ID: {}, 开始日期: {}, 结束日期: {}", teacherId, startDate, endDate);

        // 使用基础分页查询实现
        SlotQuery query = new SlotQuery();
        query.setTeacherId(teacherId);
        query.setDateRange(startDate, endDate);
        query.setStatus(1); // 状态为1表示可用

        List<SlotResp> result = slotService.list(query, null);
        
        // 如果查询结果不为空，关联查询预约信息
        if (result != null && !result.isEmpty()) {
            // 提取所有课时ID
            List<Long> slotIds = result.stream()
                .map(SlotResp::getId)
                .collect(Collectors.toList());
            
            // 查询所有相关的预约信息
            Map<Long, List<String>> studentNamesMap = bookingService.findStudentNamesBySlotIds(slotIds);
            
            // 设置学生姓名
            result.forEach(slot -> {
                List<String> studentNames = studentNamesMap.get(slot.getId());
                if (studentNames != null && !studentNames.isEmpty()) {
                    // 直接设置学生名字列表
                    slot.setStudentNameList(studentNames);
                }
            });
            
            log.info("已关联查询预约信息，共{}条课时，{}条有预约", result.size(), studentNamesMap.size());
        }
        
        return R.ok(result);
    }
}