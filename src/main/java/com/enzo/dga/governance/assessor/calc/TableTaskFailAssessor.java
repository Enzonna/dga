package com.enzo.dga.governance.assessor.calc;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.dolphinscheduler.bean.TDsTaskInstance;
import com.enzo.dga.dolphinscheduler.service.TDsTaskInstanceService;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 计算中是否有报错指标
 */
@Component("TABLE_TASK_FAILED")
public class TableTaskFailAssessor extends Assessor {

    @Autowired
    TDsTaskInstanceService tDsTaskInstanceService;

    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {

        // 到DS中查询当前表对应的任务在运行中是否有报错的实例
        List<TDsTaskInstance> failTaskInstanceList =
                tDsTaskInstanceService.getFailTdsTaskInstance(
                        assessParam.getTableMetaInfo().getSchemaName() + "." + assessParam.getTableMetaInfo().getTableName(),
                        assessParam.getAssessDate()
                );

        if (failTaskInstanceList.size() > 0) {
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("计算中有报错");
        }

    }
}
