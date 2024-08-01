package com.enzo.dga.meta.service;

import com.enzo.dga.meta.bean.TableMetaInfo;
import com.enzo.dga.meta.bean.TableMetaInfoExtra;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 元数据表附加信息 服务类
 * </p>
 *
 * @author enzo
 * @since 2024-07-31
 */
public interface TableMetaInfoExtraService extends IService<TableMetaInfoExtra> {

    void initTableMetaInfoExtra(List<TableMetaInfo> tableMetaInfoList);

}
