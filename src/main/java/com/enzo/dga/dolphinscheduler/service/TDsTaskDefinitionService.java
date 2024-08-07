package com.enzo.dga.dolphinscheduler.service;

import com.enzo.dga.dolphinscheduler.bean.TDsTaskDefinition;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author enzo
 * @since 2024-08-03
 */
public interface TDsTaskDefinitionService extends IService<TDsTaskDefinition> {

    List<TDsTaskDefinition> getTdsTaskDefinitionList();
}
