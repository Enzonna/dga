package com.enzo.dga;

import com.enzo.dga.governance.service.GovernanceAssessDetailService;
import com.enzo.dga.meta.service.impl.TableMetaInfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DgaApplicationTests {

    @Autowired
    TableMetaInfoServiceImpl tableMetaInfoService;

    @Autowired
    GovernanceAssessDetailService governanceAssessDetailService;

    @Test
    public void testMainAssess() {
        governanceAssessDetailService.mainAssess("2024-08-07");
    }

    @Test
    public void testHiveClient() {
        tableMetaInfoService.createHiveClient();
    }

    @Test
    public void testInitTableMetaInfo() throws Exception {
        tableMetaInfoService.initTableMetaInfo("fast_food", "2024-08-10");
    }



}
