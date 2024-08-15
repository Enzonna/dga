package com.enzo.dga.governance.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.governance.bean.GovernanceAssessTable;
import com.enzo.dga.governance.bean.GovernanceType;
import com.enzo.dga.governance.mapper.GovernanceAssessTableMapper;
import com.enzo.dga.governance.service.GovernanceAssessTableService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enzo.dga.governance.service.GovernanceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 表治理考评情况 服务实现类
 * </p>
 *
 * @author enzo
 * @since 2024-08-14
 */
@Service
@DS("dga")
public class GovernanceAssessTableServiceImpl extends ServiceImpl<GovernanceAssessTableMapper, GovernanceAssessTable> implements GovernanceAssessTableService {

    @Autowired
    GovernanceTypeService governanceTypeService;

    /**
     * 结合权重
     *
     * @param assessDate
     */
    @Override
    public void calcGovernanceAssessTable(String assessDate) {

        // 删除当天的表
        remove(
                new QueryWrapper<GovernanceAssessTable>()
                        .eq("assess_date", assessDate)
        );


        // 拿到五维数据
        List<GovernanceAssessTable> governanceAssessTables = baseMapper.selectGovernanceAssessTableByDetail(assessDate);

        List<GovernanceType> governanceTypes = governanceTypeService.list();

        HashMap<String, BigDecimal> typeWeightMap = new HashMap<>();

        // 拿到权重
        for (GovernanceType governanceType : governanceTypes) {
            typeWeightMap.put(governanceType.getTypeCode(), governanceType.getTypeWeight());
        }

        for (GovernanceAssessTable governanceAssessTable : governanceAssessTables) {
            // 计算SPEC的带权重
            BigDecimal specScoreWithWeight =
                    governanceAssessTable.getScoreSpecAvg().multiply(typeWeightMap.get("SPEC")).divide(BigDecimal.TEN, 1, RoundingMode.HALF_UP);
            // 计算STORAGE的带权重
            BigDecimal storageScoreWithWeight =
                    governanceAssessTable.getScoreStorageAvg().multiply(typeWeightMap.get("STORAGE")).divide(BigDecimal.TEN, 1, RoundingMode.HALF_UP);
            // 计算CALC的带权重
            BigDecimal calcScoreWithWeight =
                    governanceAssessTable.getScoreCalcAvg().multiply(typeWeightMap.get("CALC")).divide(BigDecimal.TEN, 1, RoundingMode.HALF_UP);
            // 计算QUALITY的带权重
            BigDecimal qualityScoreWithWeight =
                    governanceAssessTable.getScoreQualityAvg().multiply(typeWeightMap.get("QUALITY")).divide(BigDecimal.TEN, 1, RoundingMode.HALF_UP);
            // 计算SECURITY的带权重
            BigDecimal securityScoreWithWeight =
                    governanceAssessTable.getScoreSecurityAvg().multiply(typeWeightMap.get("SECURITY")).divide(BigDecimal.TEN, 1, RoundingMode.HALF_UP);
            // 计算总分
            BigDecimal totalScore =
                    specScoreWithWeight.add(storageScoreWithWeight).add(calcScoreWithWeight).add(qualityScoreWithWeight).add(securityScoreWithWeight);

            governanceAssessTable.setScoreOnTypeWeight(totalScore);
            governanceAssessTable.setCreateTime(new Date());
        }

        saveBatch(governanceAssessTables);

    }
}
