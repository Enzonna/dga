package com.enzo.dga.governance.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.governance.bean.GovernanceAssessGlobal;
import com.enzo.dga.governance.mapper.GovernanceAssessGlobalMapper;
import com.enzo.dga.governance.service.GovernanceAssessGlobalService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 治理总考评表 服务实现类
 * </p>
 *
 * @author enzo
 * @since 2024-08-15
 */
@Service
@DS("dga")
public class GovernanceAssessGlobalServiceImpl extends ServiceImpl<GovernanceAssessGlobalMapper, GovernanceAssessGlobal> implements GovernanceAssessGlobalService {

    @Override
    public void calcAssessGlobal(String assessDate) {
        remove(new QueryWrapper<GovernanceAssessGlobal>().eq("assess_date", assessDate));

        List<GovernanceAssessGlobal> assessGlobal = baseMapper.getAssessGlobal(assessDate);
        saveBatch(assessGlobal);
    }
}
