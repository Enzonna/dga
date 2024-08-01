package com.enzo.dga.meta.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.enzo.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enzo.dga.meta.bean.TableMetaInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 元数据表 Mapper 接口
 * </p>
 *
 * @author enzo
 * @since 2024-07-31
 */
@Mapper
@DS("dga")
public interface TableMetaInfoMapper extends BaseMapper<TableMetaInfo> {

    @Select("${sql}")
    Long selectTableMetaInfoCount(String sql);

    @Select("${sql}")
    List<TableMetaInfoVO> selectTableMetaInfoVoList(String sql);


}
