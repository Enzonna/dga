package com.enzo.dga.dolphinscheduler.service;

import com.enzo.dga.dolphinscheduler.bean.TDsTaskInstance;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author enzo
 * @since 2024-08-03
 */
public interface TDsTaskInstanceService extends IService<TDsTaskInstance> {

    List<TDsTaskInstance> getFailTdsTaskInstance(String name, String assessDate);


    List<TDsTaskInstance> getTdsTaskInstanceList(String assessDate);

    List<TDsTaskInstance> getBeforeDaysTaskInstanceList(String name, String startDate, String endDate);
}
