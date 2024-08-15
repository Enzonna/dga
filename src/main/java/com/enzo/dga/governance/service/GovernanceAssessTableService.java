package com.enzo.dga.governance.service;

import com.enzo.dga.governance.bean.GovernanceAssessTable;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 表治理考评情况 服务类
 * </p>
 *
 * @author enzo
 * @since 2024-08-14
 */
public interface GovernanceAssessTableService extends IService<GovernanceAssessTable> {
    public void calcGovernanceAssessTable(String assessDate);
}
