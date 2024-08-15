package com.enzo.dga.governance.service;

import com.enzo.dga.governance.bean.GovernanceAssessTecOwner;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 技术负责人治理考评表 服务类
 * </p>
 *
 * @author enzo
 * @since 2024-08-15
 */
public interface GovernanceAssessTecOwnerService extends IService<GovernanceAssessTecOwner> {

    public void calcAssessTecOwner(String assessDate);

}
