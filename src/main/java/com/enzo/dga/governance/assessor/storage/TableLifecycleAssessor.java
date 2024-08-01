package com.enzo.dga.governance.assessor.storage;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.enzo.dga.constants.DgaConstant;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 生命周期是否合理指标
 */
@Component("TABLE_LIFECYCLE")
public class TableLifecycleAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        // 提取指标参数
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject metricParamsJsonObject = JSON.parseObject(metricParamsJson);
        Integer paramDays = metricParamsJsonObject.getInteger("days");


        // 提取表的生命周期
        String lifecycleType = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getLifecycleType();
        // 提取表的生命周期天数
        Long lifecycleDays = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getLifecycleDays();

        // 判断是否设定生命周期类型
        if (lifecycleType == null || DgaConstant.LIFECYCLE_TYPE_UNSET.equals(lifecycleType)) {
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("生命周期未设置");
            return;
        }

        // 判断生命周期是否为日分区的表
        if (DgaConstant.LIFECYCLE_TYPE_DAY.equals(lifecycleType)) {
            // 判断表的分区信息
            // 提取表的分区信息
            String partitionColNameJson = assessParam.getTableMetaInfo().getPartitionColNameJson();
            // 转换成json数组对象
            if (partitionColNameJson != null) {
                List<JSONObject> partitionColList = JSON.parseArray(partitionColNameJson, JSONObject.class);
                if (partitionColList.size() == 0) {
                    // 没有分区信息
                    // 给分
                    governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                    // 问题项
                    governanceAssessDetail.setAssessProblem("生命周期为日分区，但分区字段未设置");
                    return;
                }
            } else {
                // 没有分区信息
                // 给分
                governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                // 问题项
                governanceAssessDetail.setAssessProblem("生命周期为日分区，但分区字段未设置");
                return;
            }

            // 判断表的生命周期天数
            if (lifecycleDays == null || lifecycleDays == -1L) {
                // 给分
                governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                // 问题项
                governanceAssessDetail.setAssessProblem("生命周期为日分区，但生命周期天数未设置");
                return;
            }

            // 判断设定的生命周期天数是否超过指标参数的建议值
            if (lifecycleDays > paramDays) {
                // 给分
                governanceAssessDetail.setAssessScore(BigDecimal.valueOf(paramDays * 10L / lifecycleDays));
                // 问题项
                governanceAssessDetail.setAssessProblem("生命周期为日分区，生命周期天数超过指标参数的建议值");
                // 考评备注
                governanceAssessDetail.setAssessComment("建议天数：" + paramDays + "，实际天数：" + lifecycleDays);
            }


        }
    }
}
