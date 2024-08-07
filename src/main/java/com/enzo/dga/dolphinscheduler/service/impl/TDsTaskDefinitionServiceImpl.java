package com.enzo.dga.dolphinscheduler.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.dolphinscheduler.bean.TDsTaskDefinition;
import com.enzo.dga.dolphinscheduler.mapper.TDsTaskDefinitionMapper;
import com.enzo.dga.dolphinscheduler.service.TDsTaskDefinitionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author enzo
 * @since 2024-08-03
 */
@Service
@DS("dolphinscheduler")
public class TDsTaskDefinitionServiceImpl extends ServiceImpl<TDsTaskDefinitionMapper, TDsTaskDefinition> implements TDsTaskDefinitionService {

    /**
     * 从DS中将任务定义信息提取出来，同时将SQL语句解析出来
     *
     * @return
     */
    @Override
    public List<TDsTaskDefinition> getTdsTaskDefinitionList() {
        List<TDsTaskDefinition> tDsTaskDefinitionList = list(
                new QueryWrapper<TDsTaskDefinition>()
                        .eq("task_type", "SHELL")
        );

        // 解析SQL
        extractTaskSql(tDsTaskDefinitionList);

        return tDsTaskDefinitionList;
    }

    /**
     * 从任务定义中提取SQL
     *
     * @param tDsTaskDefinitionList
     */
    private void extractTaskSql(List<TDsTaskDefinition> tDsTaskDefinitionList) {
        for (TDsTaskDefinition tDsTaskDefinition : tDsTaskDefinitionList) {
            // 取任务参数
            String taskParams = tDsTaskDefinition.getTaskParams();
            // 转换成json
            JSONObject taskParamJsonObj = JSON.parseObject(taskParams);
            // 提取rawScript
            String rawScript = taskParamJsonObj.getString("rawScript");

            // 开始位置
            // 先找with，如果有则使用，如果没有则找insert
            int startIndex;
            int withIndex = rawScript.indexOf("with");
            if (withIndex == -1) {
                // 找insert位置
                startIndex = rawScript.indexOf("insert");
            } else {
                startIndex = withIndex;
            }


            //如果找不到开始位置，说明当前任务定义中没有SQL，直接跳过
            if (startIndex == -1) {
                continue;
            }


            // 结束位置
            // 从开始位置向后，找分号，如果有则使用，如果没有找引号的位置
            int endIndex;
            int semiColonIndex = rawScript.indexOf(";", startIndex);
            if (semiColonIndex == -1) {
                // 找引号
                endIndex = rawScript.indexOf("\"", startIndex);
            } else {
                endIndex = semiColonIndex;
            }


            // 截取SQL
            String TaskSql = rawScript.substring(startIndex, endIndex);
            tDsTaskDefinition.setTaskSql(TaskSql);

        }

    }
}
