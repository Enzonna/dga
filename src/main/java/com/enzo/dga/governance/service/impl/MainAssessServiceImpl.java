package com.enzo.dga.governance.service.impl;

import com.enzo.dga.governance.service.*;
import com.enzo.dga.meta.service.TableMetaInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MainAssessServiceImpl implements MainAssessService {
    @Value("${default.database}")
    private String assessSchema;

    @Autowired
    TableMetaInfoService tableMetaInfoService;

    @Autowired
    GovernanceAssessDetailService governanceAssessDetailService;


    @Autowired
    GovernanceAssessTableService governanceAssessTableService;

    @Autowired
    GovernanceAssessTecOwnerService governanceAssessTecOwnerService;

    @Autowired
    GovernanceAssessGlobalService governanceAssessGlobalService;

    @Override
    public void mainAssess(String assessDate) throws Exception {

        //1. 元数据采集❌❌❌没开启
        // tableMetaInfoService.initTableMetaInfo( assessSchema , assessDate);

        //2. 考评
        governanceAssessDetailService.mainAssess(assessDate);

        //3. 核算分数
        governanceAssessTableService.calcGovernanceAssessTable(assessDate);
        governanceAssessTecOwnerService.calcAssessTecOwner(assessDate);
        governanceAssessGlobalService.calcAssessGlobal(assessDate);
    }
}
