package com.enzo.dga.meta.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.meta.bean.TableMetaInfo;
import com.enzo.dga.meta.bean.TableMetaInfoExtra;
import com.enzo.dga.meta.bean.TableMetaInfoQuery;
import com.enzo.dga.meta.bean.TableMetaInfoVO;
import com.enzo.dga.meta.mapper.TableMetaInfoMapper;
import com.enzo.dga.meta.service.TableMetaInfoExtraService;
import com.enzo.dga.meta.service.TableMetaInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enzo.dga.util.SqlUtil;
import org.apache.avro.data.Json;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.Metastore;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PublicKey;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 元数据表 服务实现类
 * </p>
 *
 * @author enzo
 * @since 2024-07-31
 */
@Service
@DS("dga")
public class TableMetaInfoServiceImpl extends ServiceImpl<TableMetaInfoMapper, TableMetaInfo> implements TableMetaInfoService {

    @Autowired
    TableMetaInfoExtraService tableMetaInfoExtraService;

    @Autowired
    TableMetaInfoMapper tableMetaInfoMapper;

    /**
     * 通过条件和分页查询表信息列表
     */
    @Override
    public List<TableMetaInfoVO> getTableListByConditionAndPage(TableMetaInfoQuery tableMetaInfoQuery) {
        // 条件的处理：条件是动态的(可能有值，也可能没有值)，按照实际情况来使用条件
        // 对于动态SQL的处理，只能写到service中，因为需要有代码的控制，Mapper是接口，只有抽象方法，没法写逻辑代码
        StringBuilder sqlBuilder = new StringBuilder(500);

        sqlBuilder.append(" select ti.id , ti.table_name , ti.schema_name , table_comment , table_size , " +
                " table_total_size , tec_owner_user_name , busi_owner_user_name ,table_last_access_time ," +
                " table_last_modify_time  from table_meta_info ti join table_meta_info_extra te " +
                " on ti.schema_name = te.schema_name and ti.table_name = te.table_name " +
                " and assess_date = (select max(assess_date) from table_meta_info) ");

        // 按照条件动态拼接SQL
        // 库名条件
        if (tableMetaInfoQuery.getSchemaName() != null && !tableMetaInfoQuery.getSchemaName().trim().isEmpty()) {
            sqlBuilder
                    .append(" and ti.schema_name = '")
                    .append(SqlUtil.filterUnsafeSql(tableMetaInfoQuery.getSchemaName().trim()))
                    .append("'");
        }
        // 表名条件
        if (tableMetaInfoQuery.getTableName() != null && !tableMetaInfoQuery.getTableName().trim().isEmpty()) {
            sqlBuilder
                    .append(" and ti.table_name like '%")
                    .append(SqlUtil.filterUnsafeSql(tableMetaInfoQuery.getTableName().trim()))
                    .append("%'");
        }
        // 数仓层级条件
        if (tableMetaInfoQuery.getDwLevel() != null && !tableMetaInfoQuery.getDwLevel().trim().isEmpty()) {
            sqlBuilder
                    .append(" and te.dw_level = '")
                    .append(SqlUtil.filterUnsafeSql(tableMetaInfoQuery.getDwLevel().trim()))
                    .append("'");
        }
        // 处理分页
        Integer pageSize = tableMetaInfoQuery.getPageSize();
        Integer pageNo = tableMetaInfoQuery.getPageNo();
        Integer start = (pageNo - 1) * pageSize;
        sqlBuilder.append(" limit ").append(start).append(",").append(pageSize);

        // 调用Mapper，执行SQL，完成查询
        List<TableMetaInfoVO> tableMetaInfoVOList =
                // tableMetaInfoMapper.selectTableMetaInfoVoList(sqlBuilder.toString());
                getBaseMapper().selectTableMetaInfoVoList(sqlBuilder.toString());

        return tableMetaInfoVOList;
    }

    /**
     * 根据条件查询表个数
     */
    @Override
    public Long getTableCountByCondition(TableMetaInfoQuery tableMetaInfoQuery) {
        // 根据条件拼接SQL
        StringBuilder sqlBuilder = new StringBuilder(500);
        sqlBuilder.append(" select count(*) cnt from table_meta_info ti join table_meta_info_extra te " +
                " on ti.schema_name = te.schema_name and ti.table_name = te.table_name " +
                " and assess_date = (select max(assess_date) from table_meta_info) ");
        // 按照条件动态拼接SQL
        // 库名条件
        if (tableMetaInfoQuery.getSchemaName() != null && !tableMetaInfoQuery.getSchemaName().trim().isEmpty()) {
            sqlBuilder
                    .append(" and ti.schema_name = '")
                    .append(SqlUtil.filterUnsafeSql(tableMetaInfoQuery.getSchemaName().trim()))
                    .append("'");
        }
        // 表名条件
        if (tableMetaInfoQuery.getTableName() != null && !tableMetaInfoQuery.getTableName().trim().isEmpty()) {
            sqlBuilder
                    .append(" and ti.table_name like '%")
                    .append(SqlUtil.filterUnsafeSql(tableMetaInfoQuery.getTableName().trim()))
                    .append("%'");
        }
        // 数仓层级条件
        if (tableMetaInfoQuery.getDwLevel() != null && !tableMetaInfoQuery.getDwLevel().trim().isEmpty()) {
            sqlBuilder
                    .append(" and te.dw_level = '")
                    .append(SqlUtil.filterUnsafeSql(tableMetaInfoQuery.getDwLevel().trim()))
                    .append("'");
        }

        // 调用Mapper层方法
        return baseMapper.selectTableMetaInfoCount(sqlBuilder.toString());
    }

    /**
     * @param schemaName 待考评的库（从hive中提取元数据的库）
     * @param assessDate 考评日期
     *                   1. 方便教学测试
     *                   2. 未来可以支持指定日期提取元数据
     *                   <p>
     *                   步骤：
     *                   0. 删除当前考评日期对应的元数据信息
     *                   1. 从hive中提取元数据
     *                   2. 从hdfs中提取元数据
     *                   3. 将提取好的元数据整合后写入对应的表中
     *                   4. 初始化辅助信息(给初始值)
     */
    @Override
    public void initTableMetaInfo(String schemaName, String assessDate) throws Exception {
        // 0. 删除当前考评日期对应的元数据信息
        // delete from table_meta_info where assess_date = assess_Date;
        remove(new QueryWrapper<TableMetaInfo>().eq("assess_date", assessDate));

        // 1. 从hive中提取元数据
        // 1.1 获取指定库下所有的表
        List<String> allTables = hiveClient.getAllTables(schemaName);

        // 定义集合对象，维护处理好的TableMetaInfo对象
        ArrayList<TableMetaInfo> tableMetaInfos = new ArrayList<>(allTables.size());

        for (String tableName : allTables) {
            // 1.2 通过表名获取表对象
            Table table = hiveClient.getTable(schemaName, tableName);
            // System.out.println(table);
            // 从table对象中获取元数据，封装到TableMetaInfo对象中
            TableMetaInfo tableMetaInfo = extractTableMetaInfoFromHive(table);
            // System.out.println(tableMetaInfo);

            // 2. 从HDFS中提取元数据
            extractTableMetaInfoFromHDFS(tableMetaInfo);

            // 补充 assess_date, update_time, create_time
            tableMetaInfo.setAssessDate(assessDate);
            tableMetaInfo.setCreateTime(new Date());
            // tableMetaInfo.setUpdateTime(new Date()); 因为不会更改元数据表的信息

            // 3. 将提取好的元数据整合后写入对应的表中
            // 一条一条写入   ❌
            // save(tableMetaInfo);    // save 是MP的内部方法 省略了this.

            // 攒批
            tableMetaInfos.add(tableMetaInfo);
        }

        // 3. 将提取好的元数据整合后写入对应的表中
        // 批量写入,默认批次大小：1000
        saveBatch(tableMetaInfos);


        // 4. 初始化辅助信息(给初始值)
        tableMetaInfoExtraService.initTableMetaInfoExtra(tableMetaInfos);

    }

    @Value("${hdfs.uris}")
    private String hdfsUri;

    /**
     * 从HDFS中提取元数据, 并更新TableMetaInfo对象
     * <p>
     * 通过递归的方式 递归 表对应的路径下所有的文件和目录，如果是文件，取文件下的信息，如果是目录，继续进入到该目录中，取所有的文件和目录，依次类推...
     */
    private void extractTableMetaInfoFromHDFS(TableMetaInfo tableMetaInfo) throws URISyntaxException, IOException, InterruptedException {
        // 1. 获取FileSystem文件系统对象
        FileSystem fs = FileSystem.get(new URI(hdfsUri), new Configuration(), tableMetaInfo.getTableFsOwner());

        // 2. 基于当前表的路径，获取表路径下所有的文件和目录
        FileStatus[] fileStatuses = fs.listStatus(new Path(tableMetaInfo.getTableFsPath()));

        // 3. 递归汇总Hdfs的信息
        addHdfsInfoToTableMetaInfo(fs, fileStatuses, tableMetaInfo);

        // hdfs容量信息
        tableMetaInfo.setFsCapcitySize(fs.getStatus().getCapacity());
        tableMetaInfo.setFsRemainSize(fs.getStatus().getRemaining());
        tableMetaInfo.setFsUsedSize(fs.getStatus().getUsed());


    }

    /**
     * 递归汇总Hdfs的信息
     * <p>
     * 递归思想：
     * 1. 计算  如果是文件，直接提取文件的信息
     * <p>
     * 2. 下探  如果是目录，获取该目录下所有的文件和目录
     */
    private void addHdfsInfoToTableMetaInfo(FileSystem fs, FileStatus[] fileStatuses, TableMetaInfo tableMetaInfo) throws IOException {
        for (FileStatus fileStatus : fileStatuses) {
            // 判断是文件还是目录
            if (fileStatus.isFile()) {
                // 计算
                // 表大小
                tableMetaInfo.setTableSize((tableMetaInfo.getTableSize() == null ? 0L : tableMetaInfo.getTableSize()) + fileStatus.getLen());
                // 表总大小
                tableMetaInfo.setTableTotalSize((tableMetaInfo.getTableTotalSize() == null ? 0L : tableMetaInfo.getTableTotalSize()) + fileStatus.getLen() * fileStatus.getReplication());
                // 表的最后修改时间
                long maxModifyTime = Math.max(fileStatus.getModificationTime()
                        , (tableMetaInfo.getTableLastModifyTime() == null ? 0L : tableMetaInfo.getTableLastModifyTime().getTime()));
                tableMetaInfo.setTableLastModifyTime(new Date(maxModifyTime));
                // 表的最后访问时间
                long maxAccessTime = Math.max(fileStatus.getAccessTime()
                        , (tableMetaInfo.getTableLastAccessTime() == null ? 0L : tableMetaInfo.getTableLastAccessTime().getTime()));
                tableMetaInfo.setTableLastAccessTime(new Date(maxAccessTime));
            } else {
                // 下探
                // 获取当前目录下所有的文件和目录
                FileStatus[] subFileStatuses = fs.listStatus(fileStatus.getPath());
                addHdfsInfoToTableMetaInfo(fs, subFileStatuses, tableMetaInfo);
            }

        }
    }

    /**
     * 从Hive的Table对象中获取元数据，封装到TableMetaInfo对象中
     */
    private TableMetaInfo extractTableMetaInfoFromHive(Table table) {
        // 创建TableMetaInfo对象
        TableMetaInfo tableMetaInfo = new TableMetaInfo();
        // 库名
        tableMetaInfo.setSchemaName(table.getDbName());
        // 表名
        tableMetaInfo.setTableName(table.getTableName());
        // 列信息
        // 将每个列中的无用信息过滤掉
        PropertyPreFilters.MySimplePropertyPreFilter colFieldFilter = new PropertyPreFilters().addFilter("comment", "type", "name");
        tableMetaInfo.setColNameJson(JSON.toJSONString(table.getSd().getCols(), colFieldFilter));
        // 分区列信息
        tableMetaInfo.setPartitionColNameJson(JSON.toJSONString(table.getPartitionKeys(), colFieldFilter));
        // 所属者
        tableMetaInfo.setTableFsOwner(table.getOwner());
        // 表参数
        tableMetaInfo.setTableParametersJson(JSON.toJSONString(table.getParameters()));
        // 表描述
        tableMetaInfo.setTableComment(table.getParameters().get("comment"));
        // 表路径
        tableMetaInfo.setTableFsPath(table.getSd().getLocation());
        // 输入格式
        tableMetaInfo.setTableInputFormat(table.getSd().getInputFormat());
        // 输出格式
        tableMetaInfo.setTableOutputFormat(table.getSd().getOutputFormat());
        // 序列化
        tableMetaInfo.setTableRowFormatSerde(table.getSd().getSerdeInfo().getSerializationLib());
        // 表的创建时间
        tableMetaInfo.setTableCreateTime(new Date(table.getCreateTime() * 1000L));
        // 表类型
        tableMetaInfo.setTableType(table.getTableType());
        // 分桶数
        tableMetaInfo.setTableBucketNum((long) table.getSd().getNumBuckets());
        // 基于分桶数判断是否有分桶
        if (table.getSd().getNumBuckets() != -1L) {
            // 有分桶
            // 分桶列
            tableMetaInfo.setTableBucketColsJson(JSON.toJSONString(table.getSd().getBucketCols()));
            // 分桶排序
            tableMetaInfo.setTableSortColsJson(JSON.toJSONString(table.getSd().getSortCols()));
        }

        return tableMetaInfo;
    }

    private IMetaStoreClient hiveClient;

    // 从application.properties配置文件中解读配置并赋值给变量
    @Value("${hive.metastore.server.uris}")
    private String HiveMetaStoreServerUris;


    /**
     * 创建 Hive客户端对象
     */
    @PostConstruct
    public void createHiveClient() {
        Configuration configuration = new Configuration();
        // 指定Hive MetaStore Server的地址
        // configuration.set("配置项名称", "配置值");
        MetastoreConf.setVar(configuration, MetastoreConf.ConfVars.THRIFT_URIS, HiveMetaStoreServerUris);
        try {
            hiveClient = new HiveMetaStoreClient(configuration);
            System.out.println("hiveClient = " + hiveClient);
        } catch (MetaException e) {
            e.printStackTrace();
            throw new RuntimeException("获取Hive客户端对象失败！！");
        }
    }


}
