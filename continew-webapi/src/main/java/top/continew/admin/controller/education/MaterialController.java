package top.continew.admin.controller.education;

import top.continew.starter.extension.crud.enums.Api;
import top.continew.starter.web.model.R;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.MaterialQuery;
import top.continew.admin.education.model.req.MaterialReq;
import top.continew.admin.education.model.req.MaterialSortReq;
import top.continew.admin.education.model.resp.MaterialDetailResp;
import top.continew.admin.education.model.resp.MaterialResp;
import top.continew.admin.education.model.resp.MaterialStatisticsResp;
import top.continew.admin.education.service.MaterialService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 教材管理 API
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Tag(name = "教材管理 API")
@RestController
@Validated
@CrudRequestMapping(value = "/education/material", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class MaterialController extends BaseController<MaterialService, MaterialResp, MaterialDetailResp, MaterialQuery, MaterialReq> {

    /**
     * 根据分类获取教材列表
     */
    @Operation(summary = "根据分类获取教材列表", description = "根据分类获取教材列表")
    @Parameter(name = "category", description = "分类", required = true)
    @GetMapping("/category/{category}")
    public R<List<MaterialResp>> listByCategory(@PathVariable @NotBlank(message = "分类不能为空") String category) {
        return R.ok(baseService.listByCategory(category));
    }

    /**
     * 获取教材统计信息
     */
    @Operation(summary = "获取教材统计信息", description = "获取教材统计信息")
    @GetMapping("/statistics")
    public R<MaterialStatisticsResp> getStatistics() {
        return R.ok(baseService.getStatistics());
    }

    /**
     * 批量更新排序
     */
    @Operation(summary = "批量更新排序", description = "批量更新排序")
    @PutMapping("/sort")
    public R<Void> batchUpdateSort(@RequestBody @Valid List<MaterialSortReq> sortList) {
        if (sortList == null || sortList.isEmpty()) {
            throw new IllegalArgumentException("排序列表不能为空");
        }
        baseService.batchUpdateSort(sortList);
        return R.ok();
    }
}