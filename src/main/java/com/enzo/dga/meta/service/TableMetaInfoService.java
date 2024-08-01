package com.enzo.dga.meta.service;

import com.enzo.dga.meta.bean.TableMetaInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enzo.dga.meta.bean.TableMetaInfoQuery;
import com.enzo.dga.meta.bean.TableMetaInfoVO;
import org.apache.thrift.TException;

import java.util.List;

/**
 * <p>
 * 元数据表 服务类
 * </p>
 *
 * @author enzo
 * @since 2024-07-31
 */
public interface TableMetaInfoService extends IService<TableMetaInfo> {
    void initTableMetaInfo(String schemaName, String assessDate) throws Exception;

    List<TableMetaInfoVO> getTableListByConditionAndPage(TableMetaInfoQuery tableMetaInfoQuery);

    Long getTableCountByCondition(TableMetaInfoQuery tableMetaInfoQuery);


}
