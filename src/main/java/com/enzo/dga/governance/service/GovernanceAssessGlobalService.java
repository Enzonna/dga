package com.enzo.dga.governance.service;

import com.enzo.dga.governance.bean.GovernanceAssessGlobal;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 治理总考评表 服务类
 * </p>
 *
 * @author enzo
 * @since 2024-08-15
 */
public interface GovernanceAssessGlobalService extends IService<GovernanceAssessGlobal> {
    public void calcAssessGlobal(String assessDate);
}
