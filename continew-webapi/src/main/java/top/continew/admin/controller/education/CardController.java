package top.continew.admin.controller.education;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.CardQuery;
import top.continew.admin.education.model.req.CardReq;
import top.continew.admin.education.model.resp.CardDetailResp;
import top.continew.admin.education.model.resp.CardResp;
import top.continew.admin.education.service.CardService;
import top.continew.starter.web.model.R;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;

import java.util.List;

/**
 * 会员卡管理管理 API
 *
 * @author don
 * @since 2025/05/10 00:06
 */
@Tag(name = "会员卡管理管理 API")
@RestController
@CrudRequestMapping(value = "/education/card", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class CardController extends BaseController<CardService, CardResp, CardDetailResp, CardQuery, CardReq> {

    /**
     * 获取所有可用会员卡
     *
     * @return 可用会员卡列表
     */
    @Operation(summary = "获取所有可用会员卡", description = "获取所有状态为1的会员卡，按照更新时间倒序排列")
    @GetMapping("/active")
    public R<List<CardResp>> listActiveCards() {
        return R.ok(baseService.listActiveCards());
    }
}