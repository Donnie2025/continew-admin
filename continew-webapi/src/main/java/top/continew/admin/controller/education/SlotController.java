package top.continew.admin.controller.education;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.SlotQuery;
import top.continew.admin.education.model.req.SlotReq;
import top.continew.admin.education.model.resp.SlotDetailResp;
import top.continew.admin.education.model.resp.SlotResp;
import top.continew.admin.education.service.SlotService;

/**
 * 课程管理管理 API
 *
 * @author don
 * @since 2025/04/25 23:24
 */
@Tag(name = "课程管理管理 API")
@RestController
@CrudRequestMapping(value = "/education/slot", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class SlotController extends BaseController<SlotService, SlotResp, SlotDetailResp, SlotQuery, SlotReq> {}