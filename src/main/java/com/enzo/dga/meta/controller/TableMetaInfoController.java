package com.enzo.dga.meta.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.meta.bean.TableMetaInfo;
import com.enzo.dga.meta.bean.TableMetaInfoExtra;
import com.enzo.dga.meta.bean.TableMetaInfoQuery;
import com.enzo.dga.meta.bean.TableMetaInfoVO;
import com.enzo.dga.meta.service.TableMetaInfoExtraService;
import com.enzo.dga.meta.service.TableMetaInfoService;
import com.enzo.dga.meta.service.impl.TableMetaInfoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 元数据表 前端控制器
 * </p>
 *
 * @author enzo
 * @since 2024-07-31
 */
@RestController
@RequestMapping("/tableMetaInfo")
public class TableMetaInfoController {

    public static final String SUCCESS = "success";

    @Autowired
    TableMetaInfoService tableMetaInfoService;

    @Autowired
    TableMetaInfoExtraService tableMetaInfoExtraService;


    /**
     * 手动更新全库元数据
     * <p>
     * 客户端的请求：http://dga.gmall.com/tableMetaInfo/init-tables/gmall/2024-08-01
     * <p>
     * 请求参数：gmall 2024-08-01
     * <p>
     * 请求方式：POST
     * <p>
     * 响应结果：success
     */
    @PostMapping("/init-tables/{schemaName}/{assessDate}")
    public String initTables(@PathVariable("schemaName") String schemaName, @PathVariable("assessDate") String assessDate) {
        // 1. 调用service层写好的初始化元数据的方法
        try {
            tableMetaInfoService.initTableMetaInfo(schemaName, assessDate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 2. 返回结果
        return SUCCESS;
    }


    /**
     * 辅助信息修改
     * <p>
     * 客户端的请求：http://dga.gmall.com/tableMetaInfo/tableExtra
     * <p>
     * 请求参数：{
     * "id": 1,
     * "tableName": "ads_traffic_stats_by_channel",
     * "schemaName": "gmall",
     * "tecOwnerUserName": "weiyunhui",
     * "busiOwnerUserName": "weiyunhui",
     * "lifecycleDays": 12,
     * "securityLevel": "1",
     * "dwLevel": "ADS",
     * "createTime": "2023-04-15T07:35:09.000+00:00",
     * "updateTime": null
     * }
     * <p>
     * 请求方式：POST
     * <p>
     * 返回结果：success
     */

    @PostMapping("/tableExtra")
    public String tableExtra(@RequestBody TableMetaInfoExtra tableMetaInfoExtra) {
        // 1. 基于传入的参数，到数据库的表中进行修改的操作
        // 补充修改时间
        tableMetaInfoExtra.setUpdateTime(new Date());
        tableMetaInfoExtraService.updateById(tableMetaInfoExtra);

        // 2. 返回结果
        return SUCCESS;
    }


    /**
     * 单表详情查询
     * <p>
     * 客户端的请求：http://dga.gmall.com/tableMetaInfo/table/131
     * <p>
     * 请求参数： 131
     * <p>
     * 请求方式： GET
     * <p>
     * 响应结果：{"id":1186,"tableName":"ads_traffic_stats_by_channel","schemaName":"gmall","colNameJson":"[{\"comment\":\"统计日期\",\"name\":\"dt\",\"type\":\"string\"},{\"comment\":\"最近天数,1:最近1天,7:最近7天,30:最近30天\",\"name\":\"recent_days\",\"type\":\"bigint\"},{\"comment\":\"渠道\",\"name\":\"channel\",\"type\":\"str  ing\"},{\"comment\":\"访客人数\",\"name\":\"uv_count\",\"type\":\"bigint\"},{\"comment\":\"会话平均停留时长，单位为秒\",\"name\":\"avg_duration_sec\",\"type\":\"bigint\"},{\"comment\":\"会话平均浏览页面数\",\"name\":\"avg_page_count\",\"type\":\"bigint\"},{\"comment\":\"会话数\",\"name\":\"sv_count\",\"type\":\"bigint\"},{\"comment\":\"跳出率\",\"name\":\"bounce_rate\",\"type\":\"decimal(16,2)\"}]","partitionColNameJson":"[]","tableFsOwner":"atguigu","tableParametersJson":"{\"totalSize\":\"2254\",\"EXTERNAL\":\"TRUE\",\"numFiles\":\"2\",\"transient_lastDdlTime\":\"1680235106\",\"bucketing_version\":\"2\",\"comment\":\"各渠道流量统计\"}","tableComment":"各渠道流量统计","tableFsPath":"hdfs://hadoop102:8020/warehouse/gmall/ads/ads_traffic_stats_by_channel","tableInputFormat":"org.apache.hadoop.mapred.TextInputFormat","tableOutputFormat":"org.apache.hadoop.mapred.TextInputFormat","tableRowFormatSerde":"org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe","tableCreateTime":"2023-03-31T03:58:26.000+00:00","tableType":"EXTERNAL_TABLE","tableBucketColsJson":null,"tableBucketNum":-1,"tableSortColsJson":null,"tableSize":2254,"tableTotalSize":6762,"tableLastModifyTime":"2023-02-07T09:52:58.000+00:00","tableLastAccessTime":"2023-02-07T09:52:58.000+00:00","fsCapcitySize":141652144128,"fsUsedSize":10058940416,"fsRemainSize":84828000256,"assessDate":"2023-04-15","createTime":"2023-04-15T07:35:05.000+00:00","updateTime":null,"tableMetaInfoExtra":{"id":1,"tableName":"ads_traffic_stats_by_channel","schemaName":"gmall","tecOwnerUserName":"weiyunhui","busiOwnerUserName":"weiyunhui","lifecycleDays":12,"securityLevel":"1","dwLevel":"ADS","createTime":"2023-04-15T07:35:09.000+00:00","updateTime":null}}
     */
    @GetMapping("/table/{tableId}")
    public String table(@PathVariable("tableId") Long tableId) {
        // 1. 通过tableId到 table_meta_info 表中查询对应的表信息
        TableMetaInfo tableMetaInfo = tableMetaInfoService.getById(tableId);

        // 2. 通过schema_name和table_name到 table_meta_info_extra 表中查询对应的表信息
        TableMetaInfoExtra tableMetaInfoExtra = tableMetaInfoExtraService.getOne(
                new QueryWrapper<TableMetaInfoExtra>()
                        .eq("schema_name", tableMetaInfo.getSchemaName())
                        .eq("table_name", tableMetaInfo.getTableName())
        );

        // 3. 封装返回结果
        tableMetaInfo.setTableMetaInfoExtra(tableMetaInfoExtra);

        // 4. 返回结果
        return JSONObject.toJSONString(tableMetaInfo);
    }


    /**
     * 表信息列表
     * <p>
     * 客户端的请求（接口路径）：http://dga.gmall.com/tableMetaInfo/table-list?schemaName=&tableName=&dwLevel=&pageSize=20&pageNo=1
     * <p>
     * 请求参数：
     * schemaName
     * tableName
     * dwLevel
     * pageSize
     * pageNo
     * <p>
     * 请求方式：GET
     * <p>
     * 响应结果：
     * {
     * "total": 79,
     * "list": [
     * { "id": 1186,
     * "tableName": "ads_traffic_stats_by_channel",
     * "schemaName": "gmall",
     * "tableSize": 2254,
     * "tableTotalSize": 6762,
     * "tableComment": "各渠道流量统计",
     * "tecOwnerUserName": "enzo",
     * "busiOwnerUserName": "enzo",
     * "tableLastModifyTime": "2023-02-07T09:52:58.000+00:00",
     * "tableLastAccessTime": "2023-02-07T09:52:58.000+00:00"
     * },
     * { "id": 1187,
     * ….
     * }
     * ]
     * }
     * <p>
     * <p>
     * 动态参数的处理：  @RequestParam(value = "schemaName", required = false) String schemaName
     */

    @GetMapping("/table-list")
    public String tableList(TableMetaInfoQuery tableMetaInfoQuery) {
        //System.out.println(tableMetaInfoQuery);
        // 1. 按照 条件 + 分页，到数据库表中查询对应的表信息,封装到List集合
        List<TableMetaInfoVO> tableMetaInfoVOList =
                tableMetaInfoService.getTableListByConditionAndPage(tableMetaInfoQuery);

        // 2. 按照条件，到数据库中查询对应的表个数
        Long count = tableMetaInfoService.getTableCountByCondition(tableMetaInfoQuery);

        // 3. 封装响应结果
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", count);
        jsonObject.put("list", tableMetaInfoVOList);

        // 4. 返回结果
        return jsonObject.toJSONString();
    }
}
