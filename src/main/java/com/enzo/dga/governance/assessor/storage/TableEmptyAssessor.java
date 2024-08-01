package com.enzo.dga.governance.assessor.storage;

import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 是否空表指标
 */
@Component("TABLE_EMPTY")
public class TableEmptyAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) {
        // 获取表数据大小
        Long tableSize = assessParam.getTableMetaInfo().getTableSize();
        System.out.println("tableSize = " + tableSize);
        if (tableSize == null || tableSize == 0L ) {
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("空表");
        }

    }
}
