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

import java.math.BigDecimal;
import java.util.Set;

/**
 * 薪资计算工具类
 *
 * @author don
 * @since 2025/10/25
 */
public class SalaryCalculationUtil {

    /**
     * 不参与小费结算的教师名单
     */
    private static final Set<String> EXCLUDED_TEACHERS = Set
        .of("Issa", "French", "Anna", "Mae", "Alex", "Lady", "Mary", "Ainie", "Daisy", "Mira", "Lina", "Yham", "Jenalyn", "Gistel", "Johanna", "Tasha", "Ray", "Jessa", "Via","Jhoe");

    private SalaryCalculationUtil() {
        // 工具类不允许实例化
    }

    /**
     * 根据课程总金额、教师姓名和所属组计算小费
     * 规则：
     * - 特殊规则：
     * * Group name 为 "Rona" 的所有老师：小费为 ₱0
     * * 教师名为 "Issa", "French", "Anna", "Mae", "Alex", "Lady", "Mary", "Ainie", "Daisy", "Mira", "Liina", "Yham" 的老师：小费为
     * ₱0
     * - 通用规则：
     * * Course Amount < 200: Tip = ₱0 (无小费)
     * * 200 <= Course Amount < 500: Tip = ₱5
     * * 500 <= Course Amount < 1000: Tip = ₱10
     * * 1000 <= Course Amount < 6000: Tip = ₱20
     * * 6000 <= Course Amount < 8000: Tip = ₱30
     * * Course Amount >= 8000: Tip = ₱50
     *
     * @param courseAmount 课程总金额
     * @param teacherName  教师姓名
     * @param groupName    所属组
     * @return 小费金额
     */
    public static BigDecimal calculateTipAmount(BigDecimal courseAmount, String teacherName, String groupName) {
        // 特殊规则1：Rona组的所有老师小费为0
        if ("Rona".equalsIgnoreCase(groupName)) {
            return BigDecimal.ZERO;
        }

        // 特殊规则2：特定老师小费为0
        if (teacherName != null && EXCLUDED_TEACHERS.stream().anyMatch(name -> name.equalsIgnoreCase(teacherName))) {
            return BigDecimal.ZERO;
        }

        // 通用规则
        if (courseAmount == null) {
            return BigDecimal.ZERO;
        }

        if (courseAmount.compareTo(new BigDecimal("200")) < 0) {
            return BigDecimal.ZERO;  // 小于200没有小费
        } else if (courseAmount.compareTo(new BigDecimal("500")) < 0) {
            return new BigDecimal("5");
        } else if (courseAmount.compareTo(new BigDecimal("1000")) < 0) {
            return new BigDecimal("10");
        } else if (courseAmount.compareTo(new BigDecimal("6000")) < 0) {
            return new BigDecimal("20");
        } else if (courseAmount.compareTo(new BigDecimal("8000")) < 0) {
            return new BigDecimal("30");
        } else {
            return new BigDecimal("50");
        }
    }
}
