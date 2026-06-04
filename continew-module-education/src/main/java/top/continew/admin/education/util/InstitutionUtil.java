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

package top.continew.admin.education.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import top.continew.admin.education.model.resp.InstitutionResp;
import top.continew.admin.education.service.InstitutionService;

/**
 * 机构工具类
 *
 * @author Charles7c
 * @since 2026/01/03 11:30
 */
@Slf4j
@UtilityClass
public class InstitutionUtil {

    /**
     * 默认机构ID（当没有激活机构时使用）
     */
    private static final Long DEFAULT_INSTITUTION_ID = 2L;

    /**
     * 获取当前有效的机构ID
     * <p>
     * 优先从数据库获取is_active=1的机构ID，如果没有则返回默认机构ID
     * </p>
     *
     * @param institutionService 机构服务
     * @return 机构ID
     */
    public static Long getEffectiveInstitutionId(InstitutionService institutionService) {
        try {
            InstitutionResp activeInstitution = institutionService.getActiveInstitution();
            if (activeInstitution != null) {
                log.debug("获取到当前激活机构ID: {} ({})", activeInstitution.getId(), activeInstitution.getName());
                return activeInstitution.getId();
            } else {
                log.warn("未找到激活的机构，使用默认机构ID: {}", DEFAULT_INSTITUTION_ID);
                return DEFAULT_INSTITUTION_ID;
            }
        } catch (Exception e) {
            log.error("获取激活机构失败，使用默认机构ID: {}", DEFAULT_INSTITUTION_ID, e);
            return DEFAULT_INSTITUTION_ID;
        }
    }

    /**
     * 获取当前有效的机构ID（带自定义日志前缀）
     * <p>
     * 优先从数据库获取is_active=1的机构ID，如果没有则返回默认机构ID
     * </p>
     *
     * @param institutionService 机构服务
     * @param logPrefix          日志前缀，如"为课时设置"、"为学生设置"等
     * @return 机构ID
     */
    public static Long getEffectiveInstitutionId(InstitutionService institutionService, String logPrefix) {
        try {
            InstitutionResp activeInstitution = institutionService.getActiveInstitution();
            if (activeInstitution != null) {
                log.debug("{}当前激活机构ID: {} ({})", logPrefix, activeInstitution.getId(), activeInstitution.getName());
                return activeInstitution.getId();
            } else {
                log.warn("未找到激活的机构，{}默认机构ID: {}", logPrefix, DEFAULT_INSTITUTION_ID);
                return DEFAULT_INSTITUTION_ID;
            }
        } catch (Exception e) {
            log.error("获取激活机构失败，{}默认机构ID: {}", logPrefix, DEFAULT_INSTITUTION_ID, e);
            return DEFAULT_INSTITUTION_ID;
        }
    }

    /**
     * 获取默认机构ID
     *
     * @return 默认机构ID
     */
    public static Long getDefaultInstitutionId() {
        return DEFAULT_INSTITUTION_ID;
    }
}
