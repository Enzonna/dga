package com.enzo.dga.governance.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.enzo.dga.governance.bean.GovernanceType;
import com.enzo.dga.governance.mapper.GovernanceTypeMapper;
import com.enzo.dga.governance.service.GovernanceTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 治理考评类别权重表 服务实现类
 * </p>
 *
 * @author enzo
 * @since 2024-08-01
 */
@Service
@DS("dga")
public class GovernanceTypeServiceImpl extends ServiceImpl<GovernanceTypeMapper, GovernanceType> implements GovernanceTypeService {

}
