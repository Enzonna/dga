package com.enzo.dga.governance.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.governance.bean.GovernanceAssessTecOwner;
import com.enzo.dga.governance.mapper.GovernanceAssessTecOwnerMapper;
import com.enzo.dga.governance.service.GovernanceAssessTecOwnerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 技术负责人治理考评表 服务实现类
 * </p>
 *
 * @author enzo
 * @since 2024-08-15
 */
@Service
@DS("dga")
public class GovernanceAssessTecOwnerServiceImpl extends ServiceImpl<GovernanceAssessTecOwnerMapper, GovernanceAssessTecOwner> implements GovernanceAssessTecOwnerService {

    @Override
    public void calcAssessTecOwner(String assessDate) {
        remove(new QueryWrapper<GovernanceAssessTecOwner>().eq("assess_date", assessDate));

        List<GovernanceAssessTecOwner> assessTecOwnerList = baseMapper.getAssessTecOwner(assessDate);
        saveBatch(assessTecOwnerList);
    }
}
