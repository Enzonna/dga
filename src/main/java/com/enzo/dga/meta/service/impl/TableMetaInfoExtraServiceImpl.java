package com.enzo.dga.meta.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.constants.DgaConstant;
import com.enzo.dga.meta.bean.TableMetaInfo;
import com.enzo.dga.meta.bean.TableMetaInfoExtra;
import com.enzo.dga.meta.mapper.TableMetaInfoExtraMapper;
import com.enzo.dga.meta.service.TableMetaInfoExtraService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 元数据表附加信息 服务实现类
 * </p>
 *
 * @author enzo
 * @since 2024-07-31
 */
@Service
@DS("dga")
public class TableMetaInfoExtraServiceImpl extends ServiceImpl<TableMetaInfoExtraMapper, TableMetaInfoExtra> implements TableMetaInfoExtraService {


    /**
     * 初始化表的辅助信息，每张表只对应一条辅助信息，因此每张表只需要初始化一次辅助信息
     * <p>
     * 判断每张表是否被初始化过，如果之前被初始化过，不再进行初始化，如果没有被初始化过，则进行初始化
     * <p>
     * 通过判断当前表是否已经在table_meta_info_extra表中，如果存在，则不再进行初始化
     *
     * @param tableMetaInfoList 待初始化的表
     */
    @Override
    public void initTableMetaInfoExtra(List<TableMetaInfo> tableMetaInfoList) {
        // 定义集合，维护处理好的TableMetaInfoExtra
        ArrayList<TableMetaInfoExtra> tableMetaInfoExtras = new ArrayList<>(tableMetaInfoList.size());


        for (TableMetaInfo tableMetaInfo : tableMetaInfoList) {
            // 从table_meta_info_extra尝试查询是否有对应的数据
            // 通过表的库名 和表的表名作为条件进行查询
            // select * from table_meta_info_extra where table_name = ? and schema_name = ?
            TableMetaInfoExtra tableMetaInfoExtra = getOne(
                    new QueryWrapper<TableMetaInfoExtra>()
                            .eq("table_name", tableMetaInfo.getTableName())
                            .eq("schema_name", tableMetaInfo.getSchemaName())
            );

            if (tableMetaInfoExtra == null) {
                // 当前表没有被初始化
                tableMetaInfoExtra = new TableMetaInfoExtra();
                // 设置表的库名
                tableMetaInfoExtra.setSchemaName(tableMetaInfo.getSchemaName());
                // 设置表的表名
                tableMetaInfoExtra.setTableName(tableMetaInfo.getTableName());
                // 技术负责人
                tableMetaInfoExtra.setTecOwnerUserName(DgaConstant.TEC_OWNER_UNSET);
                // 业务负责人
                tableMetaInfoExtra.setBusiOwnerUserName(DgaConstant.BUSI_OWNER_UNSET);
                // 生命周期类型
                tableMetaInfoExtra.setLifecycleType(DgaConstant.LIFECYCLE_TYPE_UNSET);
                // 生命周期天数
                tableMetaInfoExtra.setLifecycleDays(-1L);
                // 安全级别
                tableMetaInfoExtra.setSecurityLevel(DgaConstant.SECURITY_LEVEL_UNSET);
                // 数仓层级
                tableMetaInfoExtra.setDwLevel(getTableLevelByName(tableMetaInfo.getTableName()));
                // 创建时间
                tableMetaInfoExtra.setCreateTime(new Date());

                // 攒批
                tableMetaInfoExtras.add(tableMetaInfoExtra);
            }

        }

        // 批写到数据库表中
        saveBatch(tableMetaInfoExtras);
    }

    /**
     * 通过表名 确认数仓的层级
     */
    private String getTableLevelByName(String tableName) {
        if (tableName.startsWith("ods")) {
            return "ODS";
        } else if (tableName.startsWith("dwd")) {
            return "DWD";
        } else if (tableName.startsWith("dim")) {
            return "DIM";
        } else if (tableName.startsWith("dws")) {
            return "DWS";
        } else if (tableName.startsWith("ads")) {
            return "ADS";
        } else if (tableName.startsWith("dm")) {
            return "DM";
        } else {
            return "OTHER";
        }

    }
}
