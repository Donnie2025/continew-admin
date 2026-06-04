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

package top.continew.admin.education.service;

import java.util.List;

/**
 * 飞书服务接口
 *
 * @author continew-org
 * @since 2026-01-01
 */
public interface FeishuService {

    /**
     * 获取文件夹下的文件列表
     *
     * @param folderToken 文件夹token
     * @return 文件列表
     */
    List<FeishuFile> getFolderFiles(String folderToken);

    /**
     * 获取文件下载链接
     *
     * @param fileToken 文件token
     * @return 下载链接
     */
    String getFileDownloadUrl(String fileToken);

    /**
     * 获取访问令牌
     *
     * @return 访问令牌
     */
    String getAccessToken();

    /**
     * 上传文件到飞书云空间
     *
     * @param folderToken 目标文件夹token
     * @param file        文件
     * @param fileName    文件名
     * @return 文件token
     */
    String uploadFile(String folderToken, byte[] file, String fileName);

    /**
     * 在飞书云空间创建文件夹
     *
     * @param parentFolderToken 父文件夹token（null表示根目录）
     * @param folderName        文件夹名称
     * @return 文件夹token
     */
    String createFolder(String parentFolderToken, String folderName);

    /**
     * 删除飞书文件夹
     *
     * @param folderToken 文件夹token
     * @return 是否成功
     */
    boolean deleteFolder(String folderToken, String type);

    /**
     * 重命名飞书文件
     *
     * @param fileToken 文件token
     * @param newName   新文件名
     * @return 是否成功
     */
    boolean renameFile(String fileToken, String newName);

    /**
     * 已废弃：不再查找子文件夹，完全信任数据库中的token
     * 策略调整：如果数据库中已有feishuFolderToken，则直接使用，不在飞书端重复创建或查找
     *
     * @deprecated 此方法已废弃，不再使用
     */
    @Deprecated
    String findSubFolder(String parentFolderToken, String folderName);

    /**
     * 飞书文件信息
     */
    class FeishuFile {
        private String token;
        private String name;
        private String type;
        private Long size;
        private String url;

        // 构造函数
        public FeishuFile() {
        }

        public FeishuFile(String token, String name, String type, Long size, String url) {
            this.token = token;
            this.name = name;
            this.type = type;
            this.size = size;
            this.url = url;
        }

        // Getter和Setter
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
