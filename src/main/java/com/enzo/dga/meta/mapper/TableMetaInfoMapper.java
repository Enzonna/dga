package com.enzo.dga.meta.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.enzo.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enzo.dga.meta.bean.TableMetaInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
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

    @Select(
            "select ti.id          as ti_id,\n" +
                    "       ti.table_name,\n" +
                    "       ti.schema_name,\n" +
                    "       ti.col_name_json,\n" +
                    "       ti.partition_col_name_json,\n" +
                    "       ti.table_comment,\n" +
                    "       ti.table_fs_path,\n" +
                    "       ti.table_input_format,\n" +
                    "       ti.table_output_format,\n" +
                    "       ti.table_row_format_serde,\n" +
                    "       ti.table_create_time,\n" +
                    "       ti.table_type,\n" +
                    "       ti.table_bucket_cols_json,\n" +
                    "       ti.table_sort_cols_json,\n" +
                    "       ti.table_size,\n" +
                    "       ti.table_total_size,\n" +
                    "       ti.table_last_access_time,\n" +
                    "       ti.table_last_modify_time,\n" +
                    "       ti.fs_capcity_size,\n" +
                    "       ti.fs_used_size,\n" +
                    "       ti.fs_remain_size,\n" +
                    "       ti.assess_date,\n" +
                    "       ti.create_time as ti_create_time,\n" +
                    "       ti.update_time as ti_update_time,\n" +
                    "       te.id          as te_id,\n" +
                    "       te.table_name,\n" +
                    "       te.schema_name,\n" +
                    "       te.tec_owner_user_name,\n" +
                    "       te.busi_owner_user_name,\n" +
                    "       te.lifecycle_type,\n" +
                    "       te.lifecycle_days,\n" +
                    "       te.security_level,\n" +
                    "       te.dw_level,\n" +
                    "       te.create_time as te_create_time,\n" +
                    "       te.update_time as te_update_time\n" +
                    "from table_meta_info ti\n" +
                    "         join table_meta_info_extra te\n" +
                    "              on ti.schema_name = te.schema_name and ti.table_name = te.table_name\n" +
                    "where ti.assess_date = #{assessDate}"
    )
    @ResultMap("table_meta_info_with_extra_resultmap")
    List<TableMetaInfo> selectAllTableMetaInfoWithExtra(String assessDate);

    @Select("${sql}")
    Long selectTableMetaInfoCount(String sql);

    @Select("${sql}")
    List<TableMetaInfoVO> selectTableMetaInfoVoList(String sql);


}
