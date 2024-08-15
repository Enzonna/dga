package com.enzo.dga.governance.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.enzo.dga.governance.bean.GovernanceType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 治理考评类别权重表 Mapper 接口
 * </p>
 *
 * @author enzo
 * @since 2024-08-01
 */
@Mapper
@DS("dga")
public interface GovernanceTypeMapper extends BaseMapper<GovernanceType> {

}