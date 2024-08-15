package com.enzo.dga.governance.service;

import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 治理考评结果明细 服务类
 * </p>
 *
 * @author enzo
 * @since 2024-08-01
 */
public interface GovernanceAssessDetailService extends IService<GovernanceAssessDetail> {
    public List<Map<String, Object>> getLastProblemNum();

    public List<GovernanceAssessDetail> getLastProblemListByType(String governType, Integer pageNo, Integer pageSize);

    void mainAssess(String assessDate);
}
