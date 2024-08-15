package com.enzo.dga.governance.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.dolphinscheduler.service.TDsTaskDefinitionService;
import com.enzo.dga.dolphinscheduler.service.TDsTaskInstanceService;
import com.enzo.dga.governance.bean.GovernanceLineageTable;
import com.enzo.dga.governance.mapper.GovernanceLineageTableMapper;
import com.enzo.dga.governance.service.GovernanceLineageTableService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enzo.dga.meta.service.TableMetaInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author enzo
 * @since 2024-08-15
 */
@Service
@DS("dga")
public class GovernanceLineageTableServiceImpl extends ServiceImpl<GovernanceLineageTableMapper, GovernanceLineageTable> implements GovernanceLineageTableService {
    @Autowired
    TableMetaInfoService tableMetaInfoService;

    @Autowired
    TDsTaskInstanceService tDsTaskInstanceService;

    @Autowired
    TDsTaskDefinitionService tDsTaskDefinitionService;

    // 0. 清除当日历史数据
    // 1. 获取数仓的所有表名
    // 2. 从ds中查询sql列表
    // 3. 对任务定义的sql进行分析提取到2个map中：
    //                  目标表 -> 来源表
    //                  目标表 -> 输出表集合
    // 4. 根据2个map,产生出GovernanceLineageTable的列表,写入到数据库

    public void calcLineageTable(String assessDate) {
        remove(
                new QueryWrapper<GovernanceLineageTable>()
                        .eq("governance_date", assessDate)
        );

        // 1. 获取数仓的所有表名
        // tableMetaInfoService.getTableMetaList(assessDate)

    }
}
