package com.enzo.dga.governance.assessor.spec;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 是否有字段备注指标
 */
@Component("TABLE_COL_COMMENT")
public class TableColComment extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) {
        // 提取字段信息
        String colNameJson = assessParam.getTableMetaInfo().getColNameJson();
        // 转换成JSON数组
        List<JSONObject> colList = JSON.parseArray(colNameJson, JSONObject.class);

        // 循环集合，判断每个字段的备注信息，找出没有备注的字段
        ArrayList<String> missCommentColList = new ArrayList<>();
        for (JSONObject colJsonObj : colList) {
            String colComment = colJsonObj.getString("comment");
            if (colComment == null || colComment.trim().isEmpty()) {
                // 将没有备注的字段的名字保存到集合中
                missCommentColList.add(colJsonObj.getString("name"));
            }
        }
        if (missCommentColList.size() > 0) {
            // 有字段没有备注
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.valueOf((colList.size() - missCommentColList.size()) * 10L / colList.size()));
            // 问题项
            governanceAssessDetail.setAssessProblem("有字段没有备注信息");
            // 给备注
            governanceAssessDetail.setAssessComment("没有备注信息的列为：" + missCommentColList);

        }
    }

}
