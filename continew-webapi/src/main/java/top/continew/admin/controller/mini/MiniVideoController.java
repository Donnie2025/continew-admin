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

package top.continew.admin.controller.mini;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.starter.web.model.R;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 小程序视频解析 API
 *
 * @author donnie
 * @since 2025/12/28
 */
@Tag(name = "小程序视频解析 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/video")
public class MiniVideoController {

    @SaIgnore
    @PostMapping("/parse")
    @Operation(summary = "解析视频链接", description = "解析B站等视频平台链接，获取可播放的视频地址")
    public R<Map<String, Object>> parseVideo(@RequestBody Map<String, String> request) {
        String url = request.get("url");

        if (url == null || url.trim().isEmpty()) {
            return R.fail("INVALID_URL", "视频链接不能为空");
        }

        Map<String, Object> result = new HashMap<>();

        try {
            // 解析B站视频
            if (url.contains("bilibili.com")) {
                String parsedUrl = parseBilibiliVideo(url);
                if (parsedUrl != null) {
                    result.put("videoUrl", parsedUrl);
                    result.put("type", "bilibili");
                    result.put("success", true);
                    return R.ok(result);
                }
            }

            // 其他平台的解析可以在这里添加

            // 如果无法解析，返回原链接
            result.put("videoUrl", url);
            result.put("type", "original");
            result.put("success", false);
            result.put("message", "暂不支持该平台视频解析");

            return R.ok(result);

        } catch (Exception e) {
            return R.fail("PARSE_ERROR", "视频解析失败: " + e.getMessage());
        }
    }

    /**
     * 解析B站视频链接
     * 注意：这是一个简化的示例，实际生产环境需要更复杂的解析逻辑
     */
    private String parseBilibiliVideo(String url) {
        try {
            // 提取BV号
            Pattern pattern = Pattern.compile("BV[a-zA-Z0-9]+");
            Matcher matcher = pattern.matcher(url);

            if (matcher.find()) {
                String bvid = matcher.group();

                // 这里应该调用B站API或使用第三方解析服务
                // 由于B站API需要认证，这里返回一个示例视频
                // 实际项目中需要：
                // 1. 调用B站官方API
                // 2. 使用第三方解析服务
                // 3. 自建解析服务

                // 返回一个测试视频URL
                return "https://media.w3.org/2010/05/sintel/trailer.mp4";
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException("B站视频解析失败", e);
        }
    }
}
