package com.enzo.dga.meta.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableMetaInfoQuery {
    private String schemaName;
    private String tableName;
    private String dwLevel;
    private Integer pageSize;
    private Integer pageNo;
}
