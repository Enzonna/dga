package com.enzo.dga.governance.assessor.spec;

import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 是否有表备注指标
 */
@Component("TABLE_COMMENT")
public class TableCommentAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) {
        // 获取表的备注信息
        String tableComment = assessParam.getTableMetaInfo().getTableComment();
        if (tableComment == null || tableComment.trim().isEmpty()) {
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("表备注为空");
        }
    }
}