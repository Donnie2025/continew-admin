package top.continew.admin.education.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.service.ClassinUserService;

import java.util.UUID;

@Slf4j
@Component
public class ClassinHelper {

    @Autowired
    private ClassinUserService classinUserService;

    public ClassinUserDO getClassinUser(Long memberId, String userType, String name, String phone, String email){
        // 查询学生的 Classin 用户信息
        ClassinUserDO classinUser = classinUserService.getByMemberIdAndUserType(memberId, userType);
        if (classinUser == null) {
            log.info("未找到 Classin 用户信息，开始注册: memberId={}", memberId);
            // 构建注册请求参数
            ClassinUserReq registerReq = new ClassinUserReq();
            // 设置用户信息
            registerReq.setNickname(name);
            registerReq.setTelephone(phone);
            registerReq.setEmail(email);
            // 生成随机密码
            String randomPassword = UUID.randomUUID().toString().substring(0, 8);
            registerReq.setPassword(randomPassword);
            // 设置学生ID
            registerReq.setMemberId(memberId);
            // 设置用户类型为学生
            registerReq.setUserType(userType);
            // 设置机构ID（需要从配置或上下文中获取）
            registerReq.setClassinInstitutionId(1L); // TODO: 从配置中获取
            try {
                // 调用注册接口
                Long classinUserId = classinUserService.create(registerReq);
                log.info("Classin 用户注册成功: memberId={}, classinUserId={}", memberId, classinUserId);

                // 重新查询用户信息
                classinUser = classinUserService.getByMemberIdAndUserType(memberId, userType);
            } catch (Exception e) {
                log.error("Classin 用户注册失败: memberId={}, error={}", memberId, e.getMessage(), e);
                throw e;
            }
        }
        return classinUser;
    }


}
