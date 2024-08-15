package com.enzo.dga.governance.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * <p>
 * 表治理考评情况
 * </p>
 *
 * @author enzo
 * @since 2024-08-14
 */
@Data
@TableName("governance_assess_table")
public class GovernanceAssessTable implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 考评日期
     */
    private String assessDate;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 库名
     */
    private String schemaName;

    /**
     * 技术负责人
     */
    private String tecOwner;

    /**
     * 规范分数
     */
    private BigDecimal scoreSpecAvg;

    /**
     * 存储分数
     */
    private BigDecimal scoreStorageAvg;

    /**
     * 计算分数
     */
    private BigDecimal scoreCalcAvg;

    /**
     * 质量分数
     */
    private BigDecimal scoreQualityAvg;

    /**
     * 安全分数
     */
    private BigDecimal scoreSecurityAvg;

    /**
     * 五维权重后分数
     */
    private BigDecimal scoreOnTypeWeight;

    /**
     * 问题项个数
     */
    private Long problemNum;

    /**
     * 创建日期
     */
    private Date createTime;
}
