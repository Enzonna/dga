package com.enzo.dga.governance.service;

import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 治理考评结果明细 服务类
 * </p>
 *
 * @author enzo
 * @since 2024-08-01
 */
public interface GovernanceAssessDetailService extends IService<GovernanceAssessDetail> {

    void mainAssess(String assessDate);
}
