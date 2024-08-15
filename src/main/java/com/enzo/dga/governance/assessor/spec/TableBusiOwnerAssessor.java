package com.enzo.dga.governance.assessor.spec;


import com.enzo.dga.constants.DgaConstant;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 指标：是否有业务OWNER
 */
@Component("TABLE_BUSI_OWNER")
public class TableBusiOwnerAssessor extends Assessor {

    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) {
        String busiOwnerUserName = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getBusiOwnerUserName();
        if (busiOwnerUserName == null || DgaConstant.BUSI_OWNER_UNSET.equals(busiOwnerUserName)) {
            // 没设置过业务负责人
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("未设置业务OWNER");
        }
    }
}