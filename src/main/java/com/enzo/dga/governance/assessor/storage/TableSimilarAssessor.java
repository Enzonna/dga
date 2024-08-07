package com.enzo.dga.governance.assessor.storage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import com.enzo.dga.meta.bean.TableMetaInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 是否存在相似表
 */

@Component("TABLE_SIMILAR")
public class TableSimilarAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        // 提取指标参数
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject paramsJsonObj = JSON.parseObject(metricParamsJson);
        Integer paramPercent = paramsJsonObj.getInteger("percent");

        // 提取当前表所在层级
        String currentTableDwLevel = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getDwLevel();
        List<String> currentTableColNameList = extractedTableColName(assessParam.getTableMetaInfo());


        // 取出同层级的其他表
        // 方式1：基于MP进行一次数据库的查询操作❌❌❌❌
        // 方式2：通过调用者将需要的表通过参数传入
        List<TableMetaInfo> tableMetaInfoList = assessParam.getTableMetaInfoList();
        List<Object> similarTableNameList = new ArrayList<>();
        for (TableMetaInfo otherTableMetaInfo : tableMetaInfoList) {
            // 判断是否为同层次表并且排除自己
            if (!assessParam.getTableMetaInfo().getTableName().equals(otherTableMetaInfo.getTableName()) &&
                    currentTableDwLevel.equals(otherTableMetaInfo.getTableMetaInfoExtra().getDwLevel())) {
                // 取出其他表的字段
                List<String> otherTableColNameList = extractedTableColName(otherTableMetaInfo);

                // 提取当前表和其他表的重复字段（两个集合求交集）
                Collection duplicationColNameList =
                        CollectionUtils.intersection(currentTableColNameList, otherTableColNameList);

                // 判断重复字段的百分比是否超过指标参数建议值
                if ((duplicationColNameList.size() * 100 / currentTableColNameList.size()) > paramPercent) {
                    // 相似表
                    similarTableNameList.add(otherTableMetaInfo.getTableName());

                }

            }
        }

        // 判断是否存在相似表
        if (similarTableNameList.size() > 0) {
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("同层次存在相似表");
            // 考评备注
            governanceAssessDetail.setAssessComment("同层次相似表为：" + similarTableNameList);
        }

    }

    private List<String> extractedTableColName(TableMetaInfo tableMetaInfo) {
        // 提取json格式的字段信息
        String colNameJson = tableMetaInfo.getColNameJson();
        // 转换成json数组对象
        List<JSONObject> tableColList = JSON.parseArray(colNameJson, JSONObject.class);
        // 只保留字段名字
        List<String> tableColNameList =
                tableColList.stream().map(jsonObj -> jsonObj.getString("name")).collect(Collectors.toList());

        return tableColNameList;
    }
}
