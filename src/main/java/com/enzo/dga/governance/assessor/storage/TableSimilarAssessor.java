package com.enzo.dga.governance.assessor.storage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import com.enzo.dga.meta.bean.TableMetaInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 是否存在相似表
 */

@Component("TABLE_SIMILAR")
public class TableSimilarAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {

        // 提取当前表所在层级
        String currentTableDwLevel = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getDwLevel();
        extractedTableColName(assessParam);


        // 取出同层级的其他表
        // 方式1：基于MP进行一次数据库的查询操作❌❌❌❌
        // 方式2：通过调用者将需要的表通过参数传入
        List<TableMetaInfo> tableMetaInfoList = assessParam.getTableMetaInfoList();
        for (TableMetaInfo otherTableMetaInfo : tableMetaInfoList){
            if (otherTableMetaInfo.getTableMetaInfoExtra().getDwLevel().equals(currentTableDwLevel)){
                // 取出其他表的字段
                // TODO
                //extractedTableColName(otherTableMetaInfo);
            }
        }

        // 判断

    }

    private static void extractedTableColName(AssessParam assessParam) {
        // 提取当前表的字段
        String colNameJson = assessParam.getTableMetaInfo().getColNameJson();
        List<JSONObject> currentTableColList = JSON.parseArray(colNameJson, JSONObject.class);
        // 只保留字段名字
        List<String> currentTableColNameList =
                currentTableColList.stream().map(jsonObj -> jsonObj.getString("name")).collect(Collectors.toList());
    }
}
