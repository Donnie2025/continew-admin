package top.continew.admin.education.service;

import top.continew.admin.education.model.query.MaterialQuery;
import top.continew.admin.education.model.req.MaterialReq;
import top.continew.admin.education.model.req.MaterialSortReq;
import top.continew.admin.education.model.resp.MaterialDetailResp;
import top.continew.admin.education.model.resp.MaterialResp;
import top.continew.admin.education.model.resp.MaterialStatisticsResp;
import top.continew.starter.extension.crud.service.BaseService;

import java.util.List;

/**
 * 教材业务接口
 *
 * @author don
 * @since 2025/12/29 21:22
 */
public interface MaterialService extends BaseService<MaterialResp, MaterialDetailResp, MaterialQuery, MaterialReq> {

    /**
     * 根据分类获取教材列表
     *
     * @param category 分类
     * @return 教材列表
     */
    List<MaterialResp> listByCategory(String category);

    /**
     * 获取教材统计信息
     *
     * @return 统计信息
     */
    MaterialStatisticsResp getStatistics();

    /**
     * 批量更新排序
     *
     * @param sortList 排序列表
     */
    void batchUpdateSort(List<MaterialSortReq> sortList);
}