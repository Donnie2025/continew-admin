package top.continew.admin.education.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.CardMapper;
import top.continew.admin.education.model.entity.CardDO;
import top.continew.admin.education.model.query.CardQuery;
import top.continew.admin.education.model.req.CardReq;
import top.continew.admin.education.model.resp.CardDetailResp;
import top.continew.admin.education.model.resp.CardResp;
import top.continew.admin.education.service.CardService;

import java.util.List;

/**
 * 会员卡管理业务实现
 *
 * @author don
 * @since 2025/05/10 00:06
 */
@Service
@RequiredArgsConstructor
public class CardServiceImpl extends BaseServiceImpl<CardMapper, CardDO, CardResp, CardDetailResp, CardQuery, CardReq> implements CardService {
    
    @Override
    public List<CardResp> listActiveCards() {
        // 查询状态为1的会员卡，首先按sort字段升序排列，如果sort相同，则按更新时间倒序排列
        LambdaQueryWrapper<CardDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardDO::getStatus, 1)
                   .orderByAsc(CardDO::getSort)
                   .orderByDesc(CardDO::getUpdateTime);
        List<CardDO> cardDOList = baseMapper.selectList(queryWrapper);
        
        // 转换为响应对象
        return BeanUtil.copyToList(cardDOList, CardResp.class);
    }
}