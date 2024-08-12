package com.enzo.dga.governance.assessor.calc;


import avro.shaded.com.google.common.collect.Sets;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.enzo.dga.constants.DgaConstant;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import com.enzo.dga.meta.bean.TableMetaInfo;
import com.enzo.dga.util.SqlUtil;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.hive.ql.lib.Dispatcher;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * sql是否简单加工
 */
@Component("TABLE_SIMPLE_PROCESS")
public class TableSqlSimpleProcessAssessor extends Assessor {

    @Value("${default.database}")
    private String defaultDatabase;


    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        // 排除ODS表
        if (DgaConstant.DW_LEVEL_ODS.equals(assessParam.getTableMetaInfo().getTableMetaInfoExtra().getDwLevel())) {
            return;
        }

        // 提取SQL
        String taskSql = assessParam.getTDsTaskDefinition().getTaskSql();

        // 解析SQL
        SimpleProcessDispatcher simpleProcessDispatcher = new SimpleProcessDispatcher(defaultDatabase);
        SqlUtil.parseSql(taskSql, simpleProcessDispatcher);

        // 判断是否包含复杂计算
        if (simpleProcessDispatcher.getSqlComplicateSet().size() != 0) {
            // 考评备注
            governanceAssessDetail.setAssessComment("Sql中包含复杂计算：" + simpleProcessDispatcher.getSqlComplicateSet());
            return;
        }

        // 判断过滤字段是否包含非分区字段
        // 提取sql中实际的过滤字段
        Set<String> sqlWhereColSet = simpleProcessDispatcher.getSqlWhereColSet();

        // 提取sql中被查询表
        Set<String> sqlTableNameSet = simpleProcessDispatcher.getSqlTableNameSet();

        // 提取所有表
        List<TableMetaInfo> tableMetaInfoList = assessParam.getTableMetaInfoList();
        Map<String, TableMetaInfo> tableMetaInfoHashMap = new HashMap<>();

        for (TableMetaInfo tableMetaInfo : tableMetaInfoList) {
            String key = tableMetaInfo.getSchemaName() + "." + tableMetaInfo.getTableName();
            tableMetaInfoHashMap.put(key, tableMetaInfo);
        }

        // 定义集合，维护所有被查询表的分区字段
        Set<String> allPartitionColNameList = new HashSet<>();

        for (String tableName : sqlTableNameSet) {
            TableMetaInfo tableMetaInfo = tableMetaInfoHashMap.get(tableName);
            // 判断该表是否存在
            if (tableMetaInfo != null) {
                // 提取该表的分区字段
                String partitionColNameJson = tableMetaInfo.getPartitionColNameJson();
                List<JSONObject> partitionColJsonObjList = JSON.parseArray(partitionColNameJson, JSONObject.class);
                // 只保留分区字段名字
                List<String> partitionColNameList = partitionColJsonObjList.stream().map(jsonObj -> jsonObj.getString("name")).collect(Collectors.toList());
                // 保存到集合中
                allPartitionColNameList.addAll(partitionColNameList);

            }
        }

        // 集合差值计算
        Collection subtract = CollectionUtils.subtract(sqlWhereColSet, allPartitionColNameList);
        if (subtract.size() > 0) {
            // 考评备注
            governanceAssessDetail.setAssessComment("Sql中包含非分区字段：" + subtract);
            return;
        }

        // 简单加工
        governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
        // 问题项
        governanceAssessDetail.setAssessProblem("Sql为简单加工");
        // 考评备注
        governanceAssessDetail.setAssessComment("Sql中的复杂计算：" + simpleProcessDispatcher.getSqlComplicateSet()
                + ",Sql中的过滤字段：" + sqlWhereColSet
                + ",被查询表的分区字段：" + allPartitionColNameList);

    }


    public static class SimpleProcessDispatcher implements Dispatcher {

        private String defaultDatabase;

        public SimpleProcessDispatcher(String defaultDatabase) {
            this.defaultDatabase = defaultDatabase;
        }

        // 复杂计算的集合
        Set<Integer> complicateTokSet = Sets.newHashSet(
                HiveParser.TOK_JOIN,   // join ,包含通过where连接的情况
                HiveParser.TOK_GROUPBY, // group by
                HiveParser.TOK_LEFTOUTERJOIN, // left join
                HiveParser.TOK_RIGHTOUTERJOIN, //right join
                HiveParser.TOK_FULLOUTERJOIN, // full join
                HiveParser.TOK_FUNCTION, // count(1)
                HiveParser.TOK_FUNCTIONDI, // count(distinct xx)
                HiveParser.TOK_FUNCTIONSTAR, // count(*)
                HiveParser.TOK_SELECTDI, // distinct
                HiveParser.TOK_UNIONALL // union
        );

        // Sql中实际包含的复杂计算的集合
        @Getter
        Set<String> sqlComplicateSet = new HashSet<String>();


        // where的条件操作
        Set<Integer> whereOperateSet = Sets.newHashSet(
                HiveParser.EQUAL, // =
                HiveParser.GREATERTHAN, // >
                HiveParser.LESSTHAN, // <
                HiveParser.LESSTHANOREQUALTO, // <=
                HiveParser.GREATERTHANOREQUALTO, // >=
                HiveParser.NOTEQUAL, // !=
                HiveParser.KW_LIKE //like
        );


        @Getter
        Set<String> sqlWhereColSet = new HashSet<>();

        @Getter
        Set<String> sqlTableNameSet = new HashSet<>();


        @Override
        public Object dispatch(Node nd, Stack<Node> stack, Object... nodeOutputs) throws SemanticException {

            ASTNode astNode = (ASTNode) nd;
            // 判断当前节点是否为复杂计算节点
            if (complicateTokSet.contains(astNode.getType())) {
                sqlComplicateSet.add(astNode.getText());
            }

            // 提取过滤字段
            if (whereOperateSet.contains(astNode.getType()) && astNode.getAncestor(HiveParser.TOK_WHERE) != null) {
                // 判断当前节点的第一个孩子节点
                // 1. t1.a = xx
                if (astNode.getChild(0).getType() == HiveParser.DOT) {
                    String whereCol = astNode.getChild(0).getChild(1).getText();
                    sqlWhereColSet.add(whereCol);
                } else if (astNode.getChild(0).getType() == HiveParser.TOK_TABLE_OR_COL) {
                    // 2. a = xx
                    String whereCol = astNode.getChild(0).getChild(0).getText();
                    sqlWhereColSet.add(whereCol);
                }
            }

            // 提取被查询表
            // from fast_food.t1
            if (astNode.getType() == HiveParser.TOK_TABNAME && astNode.getAncestor(HiveParser.TOK_FROM) != null) {
                // 判断当前节点孩子的个数
                if (astNode.getChildren().size() == 1) {
                    // from t1
                    String tableName = astNode.getChild(0).getText();
                    sqlTableNameSet.add(defaultDatabase + "." + tableName);
                } else if (astNode.getChildren().size() == 2) {
                    // from fast_food.t1
                    String schemaName = astNode.getChild(0).getText();
                    String tableName = astNode.getChild(1).getText();
                    sqlTableNameSet.add(schemaName + "." + tableName);
                }
            }


            return null;
        }
    }
}
