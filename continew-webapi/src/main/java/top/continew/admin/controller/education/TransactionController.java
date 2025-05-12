package top.continew.admin.controller.education;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.TransactionQuery;
import top.continew.admin.education.model.req.TransactionReq;
import top.continew.admin.education.model.resp.TransactionDetailResp;
import top.continew.admin.education.model.resp.TransactionResp;
import top.continew.admin.education.service.TransactionService;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;

/**
 * 订单管理 API
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Tag(name = "订单管理 API")
@RestController
@CrudRequestMapping(value = "/education/transaction", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class TransactionController extends BaseController<TransactionService, TransactionResp, TransactionDetailResp, TransactionQuery, TransactionReq> {}