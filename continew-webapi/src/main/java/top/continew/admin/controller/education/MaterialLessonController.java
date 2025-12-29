package top.continew.admin.controller.education;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.MaterialLessonQuery;
import top.continew.admin.education.model.req.MaterialLessonReq;
import top.continew.admin.education.model.resp.MaterialLessonDetailResp;
import top.continew.admin.education.model.resp.MaterialLessonResp;
import top.continew.admin.education.service.MaterialLessonService;

/**
 * 课节管理 API
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Tag(name = "课节管理 API")
@RestController
@CrudRequestMapping(value = "/education/materialLesson", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class MaterialLessonController extends BaseController<MaterialLessonService, MaterialLessonResp, MaterialLessonDetailResp, MaterialLessonQuery, MaterialLessonReq> {}