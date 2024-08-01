package com.enzo.dga.governance.assessor.calc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 是否长期未访问
 */
@Component("TABLE_NO_ACCESS")
public class TableNoAccessAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        // 思路：提取表的最后访问时间 与 当前的考评日期 进行差值计算，如果差值大于指标参数的建议值，则认为长期未访问

        // 提取指标参数
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject paramJsonObj = JSON.parseObject(metricParamsJson);
        Integer paramDay = paramJsonObj.getInteger("days");

        // 提取表的最后访问时间
        Date tableLastAccessTime = assessParam.getTableMetaInfo().getTableLastAccessTime();
        // 截断到天
        Date tableLastAccessYMD = DateUtils.truncate(tableLastAccessTime, Calendar.DATE);

        // 考评日期
        String assessDate = assessParam.getAssessDate();
        // 转成日期
        Date assessDateYMD = DateUtils.parseDate(assessDate, "yyyy-MM-dd");

        // 计算差值
        long diffMs = Math.abs(assessDateYMD.getTime() - tableLastAccessYMD.getTime());

        // 毫秒转成天
        long diffDay = TimeUnit.DAYS.convert(diffMs, TimeUnit.MILLISECONDS);

        // 比较
        if (diffDay > paramDay) {
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("长期未访问");
        }
        // 给备注
        governanceAssessDetail.setAssessComment("实际超过" + diffDay + "天未访问");

    }
}