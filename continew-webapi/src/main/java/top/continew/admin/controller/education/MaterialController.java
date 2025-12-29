package top.continew.admin.controller.education;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.MaterialQuery;
import top.continew.admin.education.model.req.MaterialReq;
import top.continew.admin.education.model.resp.MaterialDetailResp;
import top.continew.admin.education.model.resp.MaterialResp;
import top.continew.admin.education.service.MaterialService;

/**
 * 教材管理 API
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Tag(name = "教材管理 API")
@RestController
@CrudRequestMapping(value = "/education/material", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class MaterialController extends BaseController<MaterialService, MaterialResp, MaterialDetailResp, MaterialQuery, MaterialReq> {}