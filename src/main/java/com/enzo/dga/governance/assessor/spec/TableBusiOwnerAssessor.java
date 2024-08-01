package com.enzo.dga.governance.assessor.spec;


import com.enzo.dga.governance.assessor.Assessor;
import org.springframework.stereotype.Component;

/**
 * 指标：是否有业务OWNER
 */
@Component("TABLE_BUSI_OWNER")
public class TableBusiOwnerAssessor extends Assessor {

    @Override
    public void checkProblem() {
        System.out.println("TableBusiOwnerAssessor 查找问题。。。");
    }
}
