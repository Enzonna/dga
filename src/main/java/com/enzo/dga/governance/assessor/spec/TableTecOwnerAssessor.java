package com.enzo.dga.governance.assessor.spec;


import com.enzo.dga.constants.DgaConstant;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 指标：是否有技术OWNER
 */
@Component("TABLE_TEC_OWNER")
public class TableTecOwnerAssessor extends Assessor {

    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) {
        // 判断当前被考评的表是否设置过技术OWNER
        String tecOwnerUserName = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getTecOwnerUserName();
        if (tecOwnerUserName == null || DgaConstant.TEC_OWNER_UNSET.equals(tecOwnerUserName)) {
            // 没有设置过技术OWNER
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("当前表没有设置技术OWNER");
            // 考评备注，给一些额外的描述信息
            // governanceAssessDetail.setAssessComment("当前表没有设置技术OWNER");
        }
    }
}
