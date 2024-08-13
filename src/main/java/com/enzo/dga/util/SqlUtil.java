package com.enzo.dga.util;

import com.enzo.dga.governance.assessor.calc.TableSqlSelectAllAssessor;
import com.enzo.dga.governance.assessor.calc.TableSqlSimpleProcessAssessor;
import org.apache.hadoop.hive.ql.lib.DefaultGraphWalker;
import org.apache.hadoop.hive.ql.lib.Dispatcher;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseDriver;


import java.util.Collections;
import java.util.Stack;

public class SqlUtil {

    /**
     * 将SQL转换为树，并对树进行遍历，遍历过程中通过我们自定义的处理(Dispatcher)对树中的每个节点进行操作
     */
    public static void parseSql(String sql
            , Dispatcher dispatcher
    ) {
        // 使用Hive的方法解析SQL
        ParseDriver parseDriver = new ParseDriver();
        try {
            ASTNode astNode = parseDriver.parse(sql);
            while (astNode.getType() != HiveParser.TOK_QUERY) {
                // 将当前节点的第1个孩子节点作为根节点
                astNode = (ASTNode) astNode.getChild(0);
            }

            // 遍历树
            DefaultGraphWalker defaultGraphWalker = new DefaultGraphWalker(dispatcher);
            defaultGraphWalker.startWalking(Collections.singleton(astNode), null);


        } catch (Exception e) {
            throw new RuntimeException("解析SQL失败");
        }
    }


    public static void main(String[] args) {
        String sql = "select * from test where id = 1";
        String sql1 = "select a,b,c,sum(d) from test.order_info oi ,order_detail od where oi.id=od.id and name = xxx group by a,b,c";
        parseSql(sql1, new TableSqlSimpleProcessAssessor.SimpleProcessDispatcher("fast_food"));
    }

    public static String filterUnsafeSql(String input) {
        if (input == null) {
            return null;
        }

        // 替换 MySQL 中可能导致 SQL 注入的特殊字符
        return input.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\u001A", "\\Z")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}