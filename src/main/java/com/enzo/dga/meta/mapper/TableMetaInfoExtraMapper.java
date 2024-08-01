package com.enzo.dga.meta.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.enzo.dga.meta.bean.TableMetaInfoExtra;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 元数据表附加信息 Mapper 接口
 * </p>
 *
 * @author enzo
 * @since 2024-07-31
 */
@Mapper
@DS("dga")
public interface TableMetaInfoExtraMapper extends BaseMapper<TableMetaInfoExtra> {

}
