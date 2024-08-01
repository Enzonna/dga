package com.enzo.dga.governance.assessor.spec;

import com.enzo.dga.governance.assessor.Assessor;
import org.springframework.stereotype.Component;

@Component("TABLE_COMMENT")
public class TableCommentAssessor extends Assessor {
    @Override
    public void checkProblem() {
        System.out.println("TableCommentAssessor 查找问题。。。");
    }
}
