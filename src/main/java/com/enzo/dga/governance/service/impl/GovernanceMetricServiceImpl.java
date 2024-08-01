package com.enzo.dga.governance.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.enzo.dga.governance.bean.GovernanceMetric;
import com.enzo.dga.governance.mapper.GovernanceMetricMapper;
import com.enzo.dga.governance.service.GovernanceMetricService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 考评指标参数表 服务实现类
 * </p>
 *
 * @author enzo
 * @since 2024-08-01
 */
@Service
@DS("dga")
public class GovernanceMetricServiceImpl extends ServiceImpl<GovernanceMetricMapper, GovernanceMetric> implements GovernanceMetricService {

}
