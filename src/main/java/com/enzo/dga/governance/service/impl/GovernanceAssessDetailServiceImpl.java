package com.enzo.dga.governance.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import com.enzo.dga.governance.mapper.GovernanceAssessDetailMapper;
import com.enzo.dga.governance.service.GovernanceAssessDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enzo.dga.meta.bean.TableMetaInfo;
import com.enzo.dga.meta.bean.TableMetaInfoExtra;
import com.enzo.dga.meta.service.TableMetaInfoExtraService;
import com.enzo.dga.meta.service.TableMetaInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 治理考评结果明细 服务实现类
 * </p>
 *
 * @author enzo
 * @since 2024-08-01
 */
@Service
@DS("dga")
public class GovernanceAssessDetailServiceImpl extends ServiceImpl<GovernanceAssessDetailMapper, GovernanceAssessDetail> implements GovernanceAssessDetailService {

    @Autowired
    TableMetaInfoService tableMetaInfoService;

    @Autowired
    TableMetaInfoExtraService tableMetaInfoExtraService;


    /**
     * 核心考评方法
     * <p>
     * 考评思想：每个指标，每张表，逐一进行考评
     * <p>
     * 步骤：
     * 1. 获取所有待考评的表
     * 方案一：不推荐 因为要频繁调动数据库
     * // 查询table_meta_info表和table_meta_info_extra表中的数据，最终封装到TableMetaInfoExtra对象中
     * // 先查询table_meta_info表
     * List<TableMetaInfo> tableMetaInfoList = tableMetaInfoService.list(
     * new QueryWrapper<TableMetaInfo>()
     * .eq("assess_date", assessDate)
     * );
     * // 遍历tableMetaInfoList，通过每个TableMetaInfoExtra对象的表名和库名查询对应的TableMetaInfoExtra
     * for (TableMetaInfo tableMetaInfo : tableMetaInfoList){
     * TableMetaInfoExtra tableMetaInfoExtra = tableMetaInfoExtraService.getOne(
     * new QueryWrapper<TableMetaInfoExtra>()
     * .eq("schema_name", tableMetaInfo.getSchemaName())
     * .eq("table_name", tableMetaInfo.getTableName())
     * );
     * tableMetaInfo.setTableMetaInfoExtra(tableMetaInfoExtra);
     * }
     * 2. 获取所有考评的指标
     * 3. 每张表，每个指标，逐一进行考评
     * 4. 将考评结果写到数据库的表中
     */
    @Override
    public void mainAssess(String assessDate) {
        // 1. 获取所有待考评的表
        // 方案二：将table_meta_info表和table_meta_info_extra表中的数据一次性拿出来，封装到集合中,
        // 再通过集合的操作，将tableMetaInfoExtra补充到TableMetaInfo对象中


    }
}
