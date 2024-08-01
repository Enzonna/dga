package com.enzo.dga.governance.assessor.spec;

import com.enzo.dga.constants.DgaConstant;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表名是否合规
 * <p>
 * 正则入门：
 * 1. ^ : 从头匹配
 * 2. $ : 从尾匹配
 * 3. \w : 字母、数字、下划线
 * 4. \d : 数字
 * 5. \s : 空格
 * 6. \W : 非字母、数字、下划线
 * 7. \D : 非数字
 * 8. \S : 非空格
 * 9. * ：0个或多个
 * 10. + ：1个或多个
 * 11. ? ：0个或1个
 * 12. {n} ：n个, {n,m} ：n到m个, {n,} ：n个以上, {,n} ：0到n个
 * 13. [] : 匹配字符, [abc] 匹配a或b或c, [^abc] 匹配非a或b或c, [a-z] 匹配a到z, [^a-z] 匹配非a到z
 * 14. | : 或
 * 15. . : 任意字符
 */
@Component("TABLE_NAME_STANDARD")
public class TableNameStandardAssessor extends Assessor {

    Pattern odsPattern = Pattern.compile("^ods_\\w+_(inc|full)$");
    Pattern dimPattern = Pattern.compile("^dim_\\w+_(zip|full)$");
    Pattern dwdPattern = Pattern.compile("^dwd_\\w+_\\w+_(inc|full)$");
    Pattern dwsPattern = Pattern.compile("^dws_\\w+_\\w+_\\w+_(\\d{1,2}d|nd|td)$");
    Pattern adsPattern = Pattern.compile("^ads_\\w+$");
    Pattern dmPattern = Pattern.compile("^dm_\\w+$");

    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) {
        // 表名
        String tableName = assessParam.getTableMetaInfo().getTableName();

        // 所属层级
        String dwLevel = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getDwLevel();

        // 按照表的所在层级 与 对应的正则匹配
        Matcher matcher;
        if (DgaConstant.DW_LEVEL_ODS.equals(dwLevel)) {
            matcher = odsPattern.matcher(tableName);
        } else if (DgaConstant.DW_LEVEL_DIM.equals(dwLevel)) {
            matcher = dimPattern.matcher(tableName);
        } else if (DgaConstant.DW_LEVEL_DWD.equals(dwLevel)) {
            matcher = dwdPattern.matcher(tableName);
        } else if (DgaConstant.DW_LEVEL_DWS.equals(dwLevel)) {
            matcher = dwsPattern.matcher(tableName);
        } else if (DgaConstant.DW_LEVEL_ADS.equals(dwLevel)) {
            matcher = adsPattern.matcher(tableName);
        } else if (DgaConstant.DW_LEVEL_DM.equals(dwLevel)) {
            matcher = dmPattern.matcher(tableName);
        } else {
            // 未纳入分层
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.valueOf(5L));
            // 问题项
            governanceAssessDetail.setAssessProblem("未纳入分层");

            return;
        }

        if (!matcher.matches()) {
            // 表名不满足规则
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("表名不合规");
        }

    }
}
