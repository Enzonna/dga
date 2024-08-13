package com.enzo.dga.governance.assessor.calc;


import com.enzo.dga.constants.DgaConstant;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import com.enzo.dga.util.SqlUtil;
import lombok.Getter;
import org.apache.hadoop.hive.ql.lib.Dispatcher;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Stack;

/**
 * Sql中是否包含select*指标
 */
@Component("TABLE_SQL_SELECT_ALL")
public class TableSqlSelectAllAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {

        // 排除ODS表
        if (DgaConstant.DW_LEVEL_ODS.equals(assessParam.getTableMetaInfo().getTableMetaInfoExtra().getDwLevel())) {
            return;
        }

        // 从任务定义中提取Sql
        String taskSql = assessParam.getTDsTaskDefinition().getTaskSql();

        // 将SQL解析成树，遍历树，查找是否有select*的节点
        SelectAllDispatcher selectAllDispatcher = new SelectAllDispatcher();
        SqlUtil.parseSql(taskSql, selectAllDispatcher);

        // 判断是否包含select*
        if (selectAllDispatcher.isContainsSelectAll()) {
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("SQL中包含select *的操作");
            // 考评备注
            governanceAssessDetail.setAssessComment("SQL为：" + taskSql);

        }

    }


    public static class SelectAllDispatcher implements Dispatcher {

        @Getter
        private boolean isContainsSelectAll = false;


        /**
         * 每遍历到树中的一个节点，都要执行一次该方法
         */
        @Override
        public Object dispatch(Node node, Stack<Node> stack, Object... objects) throws SemanticException {
            // 转换成AstNode类型
            ASTNode astNode = (ASTNode) node;

            // 判断当前节点是否为select*
            if (astNode.getType() == HiveParser.TOK_ALLCOLREF) {
                isContainsSelectAll = true;
            }

            return null;
        }
    }
}
