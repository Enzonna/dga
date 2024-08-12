package com.enzo.dga.dolphinscheduler.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.dolphinscheduler.bean.TDsTaskInstance;
import com.enzo.dga.dolphinscheduler.mapper.TDsTaskInstanceMapper;
import com.enzo.dga.dolphinscheduler.service.TDsTaskInstanceService;
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
public class TDsTaskInstanceServiceImpl extends ServiceImpl<TDsTaskInstanceMapper, TDsTaskInstance> implements TDsTaskInstanceService {

    /**
     * 查询前days天的任务实例
     *
     * @param name
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<TDsTaskInstance> getBeforeDaysTaskInstanceList(String name, String startDate, String endDate) {
        return list(
                new QueryWrapper<TDsTaskInstance>()
                        .inSql("id",
                                "select\n" +
                                        "              max(id) max_id\n" +
                                        "          from\n" +
                                        "              t_ds_task_instance\n" +
                                        "          where name = '" + name + "'\n" +
                                        "            and state = 7\n" +
                                        "            and DATE_FORMAT(start_time, '%Y-%m-%d') >= '" + startDate + "'\n" +
                                        "            and DATE_FORMAT(start_time, '%Y-%m-%d') <= '" + endDate + "'\n" +
                                        "          group by name, DATE_FORMAT(start_time, '%Y-%m-%d')")
        );
    }


    /**
     * 查询指定考评日期，指定表对应的失败任务实例
     *
     * @param name
     * @param assessDate
     * @return
     */
    @Override
    public List<TDsTaskInstance> getFailTdsTaskInstance(String name, String assessDate) {
        return list(
                new QueryWrapper<TDsTaskInstance>()
                        .eq("name", name)
                        .eq("state", 6)
                        .eq("DATE_FORMAT(start_time,'%Y-%m-%d')", assessDate)
        );
    }

    /**
     * 查询DS中的任务实例，通过in的方式
     *
     * @param assessDate
     * @return
     */
    @Override
    public List<TDsTaskInstance> getTdsTaskInstanceList(String assessDate) {
        return list(
                new QueryWrapper<TDsTaskInstance>()
                        .inSql("id", "select\n" +
                                "           max(id) max_id\n" +
                                "       from\n" +
                                "           t_ds_task_instance\n" +
                                "       where state = 7\n" +
                                "         and DATE_FORMAT(start_time, '%Y-%m-%d') = '" + assessDate + "'\n" +
                                "       group by name")
        );
    }


}
