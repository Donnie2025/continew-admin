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

import lombok.Data;

/**
 * 预约导入行数据
 *
 * @author don
 */
@Data
public class BookingImportRow {

    /**
     * 上课时间，格式：yyyy-MM-dd HH:mm
     */
    private String classTime;

    /**
     * 上课老师姓名
     */
    private String teacherName;

    /**
     * 预约会员姓名
     */
    private String studentName;

    /**
     * 会员手机号
     */
    private String studentPhone;

    /**
     * 使用会员卡名称（仅记录，不执行扣款）
     */
    private String cardName;

    /**
     * 预约备注
     */
    private String remark;
}
