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

package top.continew.admin.education.model.resp.classin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * ClassIn 云盘文件列表响应对象
 *
 * @author don
 * @since 2025/05/19
 */
@Data
public class ClassinCloudListResp {

    /**
     * 文件夹列表
     */
    @JsonProperty("folder_list")
    private List<FolderItem> folderList;

    /**
     * 文件列表
     */
    @JsonProperty("file_list")
    private List<FileItem> fileList;

    /**
     * 文件夹项
     */
    @Data
    public static class FolderItem {

        /**
         * 文件夹ID
         */
        @JsonProperty("folder_id")
        private String folderId;

        /**
         * 文件夹名称
         */
        @JsonProperty("folder_name")
        private String folderName;

        /**
         * 是否为系统文件夹（1=是，0=否）
         */
        @JsonProperty("is_system_folder")
        private Integer isSystemFolder;
    }

    /**
     * 文件项
     */
    @Data
    public static class FileItem {

        /**
         * 文件ID
         */
        private String id;

        /**
         * 文件名称
         */
        @JsonProperty("file_name")
        private String fileName;

        /**
         * 文件大小（如 "27KB"）
         */
        @JsonProperty("file_size")
        private String fileSize;
    }
}
