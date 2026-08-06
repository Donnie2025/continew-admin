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

import top.continew.admin.education.model.entity.TeacherPaymentDO;

/**
 * 教师收款信息业务接口
 *
 * @author donnie
 * @since 2026/06/17
 */
public interface TeacherPaymentService {

    /**
     * 保存或更新教师收款信息
     *
     * @param teacherId      教师ID
     * @param teacherName    教师姓名
     * @param paymentChannel 支付渠道
     * @param accountNumber  账号
     * @param accountName    账户名
     * @param qrCode         二维码地址
     * @param bankName       银行名称
     * @param rate           单价
     */
    void saveOrUpdate(Long teacherId,
                      String teacherName,
                      String paymentChannel,
                      String accountNumber,
                      String accountName,
                      String qrCode,
                      String bankName,
                      Integer rate);

    /**
     * 根据教师ID获取收款信息
     *
     * @param teacherId 教师ID
     * @return 收款信息
     */
    TeacherPaymentDO getByTeacherId(Long teacherId);
}
