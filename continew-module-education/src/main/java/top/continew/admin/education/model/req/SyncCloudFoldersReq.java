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

package top.continew.admin.education.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 同步云盘文件夹到教材表请求参数
 *
 * @author don
 */
@Data
@Schema(description = "同步云盘文件夹到教材表请求参数")
public class SyncCloudFoldersReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 父节点ID（0=根节点）
     */
    @Schema(description = "父节点ID（0=根节点）")
    private Long pid = 0L;

    /**
     * 节点类型（CATEGORY/BOOK/LEVEL/UNIT/LESSON）
     */
    @Schema(description = "节点类型（CATEGORY/BOOK/LEVEL/UNIT/LESSON）")
    @NotBlank(message = "节点类型不能为空")
    private String type;

    /**
     * 是否跳过已存在的 cloud_id（避免重复导入）
     */
    @Schema(description = "是否跳过已存在的cloud_id（true=跳过 false=覆盖）")
    private Boolean skipExisting = true;

    /**
     * 文件夹列表
     */
    @Schema(description = "文件夹列表")
    private List<FolderItem> folders;

    /**
     * 文件列表
     */
    @Schema(description = "文件列表")
    private List<FileItem> files;

    @Data
    @Schema(description = "云盘文件夹项")
    public static class FolderItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "CloudIn云盘文件夹ID")
        @NotBlank(message = "文件夹ID不能为空")
        private String folderId;

        @Schema(description = "ClassIn云盘文件夹名称")
        @NotBlank(message = "文件夹名称不能为空")
        private String folderName;
    }

    @Data
    @Schema(description = "云盘文件项")
    public static class FileItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "ClassIn云盘文件ID")
        @NotBlank(message = "文件ID不能为空")
        private String fileId;

        @Schema(description = "ClassIn云盘文件名称")
        @NotBlank(message = "文件名称不能为空")
        private String fileName;
    }
}
