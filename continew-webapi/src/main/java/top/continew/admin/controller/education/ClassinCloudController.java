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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.model.resp.classin.ClassinCloudListResp;
import top.continew.starter.web.model.R;

import java.util.List;

import java.io.IOException;

/**
 * ClassIn 云盘 API
 *
 * @author don
 * @since 2025/05/19
 */
@Tag(name = "ClassIn 云盘 API")
@Validated
@RestController
@RequestMapping("/education/classin/cloud")
@RequiredArgsConstructor
public class ClassinCloudController {

    private final ClassinClient classinClient;

    @Operation(summary = "获取机构云盘顶级文件夹ID")
    @GetMapping("/top-folder-id")
    public R<String> getTopFolderId() {
        return R.ok(classinClient.getCloudTopFolderId());
    }

    @Operation(summary = "获取文件夹内容列表")
    @GetMapping("/list")
    public R<ClassinCloudListResp> getCloudList(@Parameter(description = "文件夹ID，为空则返回根目录") @RequestParam(required = false) String folderId) {
        return R.ok(classinClient.getCloudList(folderId));
    }

    @Operation(summary = "获取云盘所有文件夹列表")
    @GetMapping("/folder-list")
    public R<List<ClassinCloudListResp.FolderItem>> getFolderList() {
        return R.ok(classinClient.getCloudFolderList());
    }

    @Operation(summary = "获取云盘所有文件夹列表（原始响应，用于调试）")
    @GetMapping("/folder-list/raw")
    public R<String> getFolderListRaw() {
        return R.ok(classinClient.getCloudFolderListRaw());
    }

    @Operation(summary = "创建文件夹")
    @PostMapping("/folder")
    public R<String> createFolder(@Parameter(description = "父文件夹ID") @RequestParam String parentFolderId,
                                  @Parameter(description = "文件夹名称") @RequestParam String folderName) {
        return R.ok(classinClient.createCloudFolder(parentFolderId, folderName));
    }

    @Operation(summary = "删除文件夹")
    @DeleteMapping("/folder/{folderId}")
    public R<Void> deleteFolder(@PathVariable String folderId) {
        classinClient.deleteCloudFolder(folderId);
        return R.ok();
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/file/{fileId}")
    public R<Void> deleteFile(@PathVariable String fileId) {
        classinClient.deleteCloudFile(fileId);
        return R.ok();
    }

    @Operation(summary = "重命名文件夹")
    @PutMapping("/folder/{folderId}/rename")
    public R<Void> renameFolder(@PathVariable String folderId,
                                @Parameter(description = "新文件夹名") @RequestParam String folderName) {
        classinClient.renameCloudFolder(folderId, folderName);
        return R.ok();
    }

    @Operation(summary = "重命名文件")
    @PutMapping("/file/{fileId}/rename")
    public R<Void> renameFile(@PathVariable String fileId,
                              @Parameter(description = "新文件名") @RequestParam String fileName) {
        classinClient.renameCloudFile(fileId, fileName);
        return R.ok();
    }

    @Operation(summary = "上传文件到云盘")
    @PostMapping("/file/upload")
    public R<String> uploadFile(@Parameter(description = "目标文件夹ID，为空则上传到根目录") @RequestParam(required = false) String folderId,
                                @Parameter(description = "文件") @RequestPart MultipartFile file) throws IOException {
        String fileId = classinClient.uploadCloudFile(folderId, file.getBytes(), file.getOriginalFilename());
        return R.ok(fileId);
    }
}
