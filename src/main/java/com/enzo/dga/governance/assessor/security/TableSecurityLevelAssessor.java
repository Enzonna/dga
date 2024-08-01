package com.enzo.dga.governance.assessor.security;

import com.enzo.dga.constants.DgaConstant;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 是否设置安全等级
 */
@Component("TABLE_SECURITY_LEVEL")
public class TableSecurityLevelAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) {
        // 获取安全等级
        String securityLevel = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getSecurityLevel();
        if (securityLevel == null || DgaConstant.TEC_OWNER_UNSET.equals(securityLevel)){
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("安全等级未设置");
        }

    }
}
