package com.enzo.dga.dolphinscheduler.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.enzo.dga.dolphinscheduler.bean.TDsTaskInstance;
import com.enzo.dga.dolphinscheduler.mapper.TDsTaskInstanceMapper;
import com.enzo.dga.dolphinscheduler.service.TDsTaskInstanceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
public class TDsTaskInstanceServiceImpl extends ServiceImpl<TDsTaskInstanceMapper, TDsTaskInstance> implements TDsTaskInstanceService {

}
