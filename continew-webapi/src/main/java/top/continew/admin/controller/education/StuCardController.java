package top.continew.admin.controller.education;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.StuCardQuery;
import top.continew.admin.education.model.req.StuCardBindReq;
import top.continew.admin.education.model.req.StuCardReq;
import top.continew.admin.education.model.resp.StuCardDetailResp;
import top.continew.admin.education.model.resp.StuCardResp;
import top.continew.admin.education.service.StuCardService;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;

/**
 * 会员绑卡管理 API
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Tag(name = "会员绑卡管理 API")
@RestController
@RequiredArgsConstructor
@CrudRequestMapping(value = "/education/stuCard", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class StuCardController extends BaseController<StuCardService, StuCardResp, StuCardDetailResp, StuCardQuery, StuCardReq> {
    
    private final StuCardService stuCardService;
    
    @Operation(summary = "绑定会员卡", description = "绑定学生会员卡并保存交易记录")
    @PostMapping("/bind")
    public StuCardResp bindCard(@Valid @RequestBody StuCardBindReq req) {
        return stuCardService.bindCard(req);
    }
}