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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.education.service.FeishuService;
import top.continew.admin.education.config.FeishuConfig;
import top.continew.starter.web.model.R;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 飞书云空间 API
 *
 * @author continew-org
 * @since 2026-01-01
 */
@Tag(name = "飞书云空间 API")
@Validated
@RestController
@Slf4j
@RequestMapping("/education/feishu")
@RequiredArgsConstructor
public class FeishuController {

    private static final int MAX_RETRY = 3;

    private final FeishuService feishuService;
    private final FeishuConfig feishuConfig;

    private <T> T withRetry(String label, Callable<T> action) throws Exception {
        Exception lastException = null;
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                return action.call();
            } catch (Exception e) {
                lastException = e;
                log.warn("{} 第 {}/{} 次失败: {}", label, attempt, MAX_RETRY, e.getMessage());
                if (attempt < MAX_RETRY) {
                    Thread.sleep(1000L * attempt);
                }
            }
        }
        throw lastException;
    }

    @Operation(summary = "创建飞书文件夹")
    @PostMapping("/folder")
    public R<String> createFolder(@Parameter(description = "父文件夹token，为空则创建在根目录") @RequestParam(required = false) String parentFolderToken,
                                  @Parameter(description = "文件夹名称") @RequestParam String folderName) throws Exception {
        String folderToken = withRetry("飞书创建文件夹[" + folderName + "]", () -> feishuService
            .createFolder(parentFolderToken, folderName));
        return R.ok(folderToken);
    }

    /**
     * 已废弃：不再查找子文件夹
     * 策略调整：完全信任数据库中的feishuFolderToken，如果数据库中已有token，飞书端不再重复创建或查找
     *
     * @deprecated 此接口已废弃，不再使用
     */
    @Deprecated
    @Operation(summary = "查找飞书子文件夹（已废弃）", deprecated = true)
    @GetMapping("/folder/find")
    public R<String> findSubFolder(@Parameter(description = "父文件夹token") @RequestParam String parentFolderToken,
                                   @Parameter(description = "文件夹名称") @RequestParam String folderName) {
        log.warn("调用了已废弃的findSubFolder接口，请使用数据库中的feishuFolderToken");
        String folderToken = feishuService.findSubFolder(parentFolderToken, folderName);
        return R.ok(folderToken);
    }

    @Operation(summary = "上传文件到飞书云空间")
    @PostMapping("/file/upload")
    public R<Map<String, String>> uploadFile(@Parameter(description = "目标文件夹token") @RequestParam String folderToken,
                                             @Parameter(description = "文件") @RequestPart MultipartFile file) throws Exception {
        byte[] fileBytes = file.getBytes();
        String fileName = file.getOriginalFilename();

        String fileToken = withRetry("飞书上传[" + fileName + "]", () -> feishuService
            .uploadFile(folderToken, fileBytes, fileName));

        Map<String, String> result = new HashMap<>();
        result.put("fileToken", fileToken);
        if (fileToken != null) {
            result.put("lessonUrl", feishuConfig.getFileDomain() + "/file/" + fileToken);
        }

        return R.ok(result);
    }

    @Operation(summary = "重命名飞书文件")
    @PutMapping("/file/{fileToken}/rename")
    public R<Boolean> renameFile(@Parameter(description = "文件token") @PathVariable String fileToken,
                                 @Parameter(description = "新文件名") @RequestParam String newName) throws Exception {
        boolean success = withRetry("飞书重命名文件[" + newName + "]", () -> feishuService.renameFile(fileToken, newName));
        return R.ok(success);
    }
}
