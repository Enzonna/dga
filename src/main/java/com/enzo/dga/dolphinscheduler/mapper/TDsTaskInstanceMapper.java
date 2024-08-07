package com.enzo.dga.dolphinscheduler.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.enzo.dga.dolphinscheduler.bean.TDsTaskInstance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author enzo
 * @since 2024-08-03
 */
@Mapper
@DS("dolphinscheduler")
public interface TDsTaskInstanceMapper extends BaseMapper<TDsTaskInstance> {

}
