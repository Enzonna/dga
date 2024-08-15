package com.enzo.dga.governance.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.enzo.dga.governance.bean.GovernanceAssessTable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 表治理考评情况 Mapper 接口
 * </p>
 *
 * @author enzo
 * @since 2024-08-14
 */
@Mapper
@DS("dga")
public interface GovernanceAssessTableMapper extends BaseMapper<GovernanceAssessTable> {

    /**
     * 算出每个表的五维平均分数
     *
     * @param assessDate
     * @return
     */
    @Select("SELECT\n" +
            "    assess_date,\n" +
            "    TABLE_NAME,\n" +
            "    SCHEMA_NAME,\n" +
            "    tec_owner,\n" +
            "    AVG(IF(governance_type = 'SPEC', assess_score, NULL))     score_spec_avg,\n" +
            "    AVG(IF(governance_type = 'STORAGE', assess_score, NULL))  score_storage_avg,\n" +
            "    AVG(IF(governance_type = 'CALC', assess_score, NULL))     score_calc_avg,\n" +
            "    AVG(IF(governance_type = 'QUALITY', assess_score, NULL))  score_quality_avg,\n" +
            "    AVG(IF(governance_type = 'SECURITY', assess_score, NULL)) score_security_avg,\n" +
            "    SUM(IF(assess_score < 10, 1, 0))                          problem_num\n" +
            "FROM\n" +
            "    governance_assess_detail\n" +
            "where assess_date =#{assessDate}\n" +
            "GROUP BY TABLE_NAME, assess_date, SCHEMA_NAME, tec_owner")
    public List<GovernanceAssessTable> selectGovernanceAssessTableByDetail(String assessDate);
}
