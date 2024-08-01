package com.enzo.dga.governance.assessor.spec;


import com.enzo.dga.governance.assessor.Assessor;
import org.springframework.stereotype.Component;

/**
 * 指标：是否有技术OWNER
 */
@Component("TABLE_TEC_OWNER")
public class TableTecOwnerAssessor extends Assessor {

    @Override
    public void checkProblem() {
        System.out.println("TableTecOwnerAssessor 查找问题。。。");
    }
}
