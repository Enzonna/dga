package com.enzo.dga.dolphinscheduler.service.impl;

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
 *  服务实现类
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
     * @param tDsTaskDefinitionList
     */
    private void extractTaskSql(List<TDsTaskDefinition> tDsTaskDefinitionList) {

    }
}
