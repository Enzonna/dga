package com.enzo.dga.governance.assessor.spec;

import com.enzo.dga.governance.assessor.Assessor;
import org.springframework.stereotype.Component;

/**
 * 表名是否合规
 */
@Component("TABLE_NAME_STANDARD")
public class TableNameStandardAssessor extends Assessor {

    @Override
    public void checkProblem() {
        System.out.println("TableNameStandardAssessor 查找问题。。。");
    }
}
