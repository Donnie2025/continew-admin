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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.education.mapper.AgentMapper;
import top.continew.admin.education.model.entity.AgentDO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 代理机构管理 API
 *
 * @author don
 * @since 2025/01/09
 */
@Tag(name = "代理机构管理 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/education/agent")
public class AgentController {

    private final AgentMapper agentMapper;

    /**
     * 获取代理机构下拉选项（code + name）
     *
     * @return 代理机构列表
     */
    @Operation(summary = "获取代理机构下拉选项", description = "返回所有启用状态的代理机构编码和名称")
    @GetMapping("/options")
    public List<AgentOption> listOptions() {
        List<AgentDO> agents = agentMapper.selectList(new LambdaQueryWrapper<AgentDO>().eq(AgentDO::getStatus, 1)
            .select(AgentDO::getCode, AgentDO::getAlias)
            .orderByAsc(AgentDO::getAlias));
        return agents.stream().map(a -> new AgentOption(a.getCode(), a.getAlias())).collect(Collectors.toList());
    }

    @Data
    public static class AgentOption {
        private final String code;
        private final String name;
    }
}
