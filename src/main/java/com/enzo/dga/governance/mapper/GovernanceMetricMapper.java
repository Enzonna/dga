package com.enzo.dga.governance.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.enzo.dga.governance.bean.GovernanceMetric;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 考评指标参数表 Mapper 接口
 * </p>
 *
 * @author enzo
 * @since 2024-08-01
 */
@Mapper
@DS("dga")
public interface GovernanceMetricMapper extends BaseMapper<GovernanceMetric> {

}
