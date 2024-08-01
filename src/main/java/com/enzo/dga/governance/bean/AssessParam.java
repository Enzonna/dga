package com.enzo.dga.governance.bean;

import com.enzo.dga.meta.bean.TableMetaInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用来封装考评参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssessParam {

    // 待考评的表
    private TableMetaInfo tableMetaInfo;

    // 考评指标
    private GovernanceMetric governanceMetric;

    // 考评日期
    private String assessDate;

    // 未来，需要传递别的参数，直接在当前类中添加即可

}
