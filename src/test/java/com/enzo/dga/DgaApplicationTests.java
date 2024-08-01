package com.enzo.dga;

import com.enzo.dga.meta.service.impl.TableMetaInfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DgaApplicationTests {

    @Autowired
    TableMetaInfoServiceImpl tableMetaInfoService;

    @Test
    public void testHiveClient() {
        tableMetaInfoService.createHiveClient();
    }

    @Test
    public void testInitTableMetaInfo() throws Exception {
        tableMetaInfoService.initTableMetaInfo("gmall240318", "2024-08-01");
    }

}
