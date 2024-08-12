package com.enzo.dga.governance.assessor.quality;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.enzo.dga.constants.DgaConstant;
import com.enzo.dga.dolphinscheduler.bean.TDsTaskInstance;
import com.enzo.dga.dolphinscheduler.service.TDsTaskInstanceService;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 表产出时效监控指标
 */
@Component("TABLE_TIME_LINESS")
public class TableProduceTimelinessAssessor extends Assessor {

    @Autowired
    TDsTaskInstanceService tDsTaskInstanceService;

    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {

        // 排除ODS的表,如果是ODS层的表，十分但是不抛异常了
        if (DgaConstant.DW_LEVEL_ODS.equals(assessParam.getTableMetaInfo().getTableMetaInfoExtra().getDwLevel())) {
            return;
        }

        // 提取指标参数
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject parseObject = JSON.parseObject(metricParamsJson);
        Integer paramDays = parseObject.getInteger("days");
        Integer paramPercent = parseObject.getInteger("percent");

        // 提取当日的任务实例
        TDsTaskInstance tDsTaskInstance = assessParam.getTDsTaskInstance();

        // 当日产出时效
        Long currentProduceTimeMs = tDsTaskInstance.getEndTime().getTime() - tDsTaskInstance.getStartTime().getTime();

        // 计算开始日期
        Date assessDate = DateUtils.parseDate(assessParam.getAssessDate(), "yyyy-MM-dd");
        Date startDate = DateUtils.addDays(assessDate, -paramDays);
        String startDateStr = DateFormatUtils.format(startDate, "yyyy-MM-dd");

        // 前days天的平均产出时效
        List<TDsTaskInstance> beforeDaysTaskInstanceList = tDsTaskInstanceService.getBeforeDaysTaskInstanceList(
                assessParam.getTableMetaInfo().getSchemaName() + "." + assessParam.getTableMetaInfo().getTableName(),
                startDateStr,
                assessParam.getAssessDate()
        );

        Long totalProduceTime = 0L;
        for (TDsTaskInstance dsTaskInstance : beforeDaysTaskInstanceList) {
            totalProduceTime += dsTaskInstance.getEndTime().getTime() - dsTaskInstance.getStartTime().getTime();
        }

        if (beforeDaysTaskInstanceList.size() > 0) {
            // 计算平均产出时效
            long avgProduceTime = totalProduceTime / beforeDaysTaskInstanceList.size();
            if (currentProduceTimeMs > avgProduceTime && (currentProduceTimeMs - avgProduceTime) * 100 / avgProduceTime > paramPercent) {
                // 给分
                governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                // 问题项
                governanceAssessDetail.setAssessProblem("当日产出时效超过前" + paramDays + "天平均产出时效的" + paramPercent + "%");
                // 考评备注
                governanceAssessDetail.setAssessComment("当日产出时效为" + currentProduceTimeMs + "，前" + beforeDaysTaskInstanceList.size() + "天平均产出时效为" + avgProduceTime);
            }
        }
    }
}
