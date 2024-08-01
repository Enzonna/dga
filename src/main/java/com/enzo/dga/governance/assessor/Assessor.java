package com.enzo.dga.governance.assessor;

import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.apache.commons.math3.analysis.function.Max;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

/**
 * 考评器父类，整体控制考评器流程
 */
public abstract class Assessor {

    /**
     * 考评流程的控制
     */
    public final GovernanceAssessDetail doAssess(AssessParam assessParam) {
        // 1. 创建结果对象
        GovernanceAssessDetail governanceAssessDetail = new GovernanceAssessDetail();

        try {
            // 2. 给结果中的属性对象赋值（能赋哪些值就赋哪些值，剩下的在查找问题的过程中赋值）
            governanceAssessDetail.setAssessDate(assessParam.getAssessDate());
            governanceAssessDetail.setTableName(assessParam.getTableMetaInfo().getTableName());
            governanceAssessDetail.setSchemaName(assessParam.getTableMetaInfo().getSchemaName());
            governanceAssessDetail.setMetricId(assessParam.getGovernanceMetric().getId().toString());
            governanceAssessDetail.setMetricName(assessParam.getGovernanceMetric().getMetricName());
            governanceAssessDetail.setGovernanceType(assessParam.getGovernanceMetric().getGovernanceType());
            governanceAssessDetail.setTecOwner(assessParam.getTableMetaInfo().getTableMetaInfoExtra().getTecOwnerUserName());
            // 分数： 先给满分，在查找问题的过程中按照问题类型来扣分
            governanceAssessDetail.setAssessScore(BigDecimal.TEN);

            governanceAssessDetail.setCreateTime(new Date());

            // 3. 查找问题
            checkProblem(assessParam, governanceAssessDetail);

            // 治理链接的处理
            // 两个条件：
            // 1. 当前指标查找的问题可以被治理
            // 2. 考评的分数 < 10

            if (assessParam.getGovernanceMetric().getGovernanceUrl() != null
                    && governanceAssessDetail.getAssessScore().longValue() < 10
            ) {
                // 指标中定义的治理链接：/table_meta/table_meta/detail?tableId={tableId}
                String governanceUrl = assessParam.getGovernanceMetric().getGovernanceUrl();
                // 将当前被考评表的id设置到治理链接内
                // /table_meta/table_meta/detail?tableId=100
                String realGovernanceUrl = governanceUrl.replace("{tableId}", assessParam.getTableMetaInfo().getId().toString());
                governanceAssessDetail.setGovernanceUrl(realGovernanceUrl);
            }


            // 4.
        } catch (Exception e) {
            // 考评过程中出现异常
            governanceAssessDetail.setIsAssessException("1");
            // 捕获异常信息
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            String exceptionMsg = stringWriter.toString();
            // 截断异常
            String writeExceptionMsg = exceptionMsg.substring(0, Math.min(2000, exceptionMsg.length()));
            governanceAssessDetail.setAssessExceptionMsg(writeExceptionMsg);
        }


        // 返回结果
        return governanceAssessDetail;
    }

    public abstract void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception;

}
