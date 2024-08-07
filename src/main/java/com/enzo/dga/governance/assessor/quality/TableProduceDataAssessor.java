package com.enzo.dga.governance.assessor.quality;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.enzo.dga.constants.DgaConstant;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * 表产出数据量监控
 */
@Component("TABLE_PRODUCE_DATA")
public class TableProduceDataAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {

        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject metricParamsJsonObj = JSON.parseObject(metricParamsJson);
        Integer paramDays = metricParamsJsonObj.getInteger("days");
        Integer paramUpperLimit = metricParamsJsonObj.getInteger("upper_limit");
        Integer paramLowerLimit = metricParamsJsonObj.getInteger("lower_limit");


        // 判断是否为日分区表
        if (!DgaConstant.LIFECYCLE_TYPE_DAY.equals(assessParam.getTableMetaInfo().getTableMetaInfoExtra().getLifecycleType())) {
            return;
        }

        // 处理当前表 当日 在hdfs中对应的分区路径
        String tableFsPath = assessParam.getTableMetaInfo().getTableFsPath();
        // 考评日期 减一天
        String assessDateStr = assessParam.getAssessDate();
        Date assessDate = DateUtils.parseDate(assessDateStr, "yyyy-MM-dd");
        Date currentDate = DateUtils.addDays(assessDate, -1);
        String currentDateStr = DateFormatUtils.format(currentDate, "yyyy-MM-dd");

        // 取表的分区字段
        String partitionColNameJson = assessParam.getTableMetaInfo().getPartitionColNameJson();
        List<JSONObject> partitionColList = JSON.parseArray(partitionColNameJson, JSONObject.class);
        String partitionColName = partitionColList.get(0).getString("name");

        // 拼接分区路径
        String tablePartitionFsPath = tableFsPath + "/" + partitionColName + "=" + currentDateStr;

        // 汇总当天的产出数据量大小
        // 文件系统对象
        FileSystem fs =
                FileSystem.get(new URI(tablePartitionFsPath), new Configuration(), assessParam.getTableMetaInfo().getTableFsOwner());
        // 获取分区路径下所有文件和目录
        FileStatus[] fileStatuses = fs.listStatus(new Path(tablePartitionFsPath));
        // 递归汇总数据量大小
        Long currentTotalDataSize = calcDataSize(fileStatuses, fs, 0L);

        // 计算前paramDays的平均产出数据量
        Long beforeTotalDataSize = 0L;
        Long realBeforeDays = 0L;
        for (int i = 1; i <= paramDays; i++) {
            // 前 1.2.3.4...n天
            Date beforeData = DateUtils.addDays(assessDate, -i);
            String beforeDateStr = DateFormatUtils.format(beforeData, "yyyy-MM-dd");

            // 拼接分区路径
            String beforeTablePartitionFsPath = tableFsPath + "/" + partitionColName + "=" + beforeDateStr;
            // 判断路径是否存在
            if (!fs.exists(new Path(beforeTablePartitionFsPath))) {
                continue;
            }
            FileStatus[] beforeFileStatuses = fs.listStatus(new Path(beforeTablePartitionFsPath));
            // 递归汇总数据量大小
            Long beforeTotalDateSize = calcDataSize(beforeFileStatuses, fs, 0L);
            // 总大小
            beforeTotalDataSize += beforeTotalDateSize;
            // 记录真实存在的之前的天数
            realBeforeDays++;

        }

        // 存在之前分区的数据
        if (realBeforeDays > 0) {
            // 平均产生数据量
            long avgDataSize = beforeTotalDataSize / realBeforeDays;
            // 判断是否大于指标建议值
            if ((currentTotalDataSize - avgDataSize) * 100 / avgDataSize > paramUpperLimit) {
                // 给分
                governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                // 问题项
                governanceAssessDetail.setAssessProblem("当日产出数据量超过前" + paramDays + "天的平均产出数据量的" + paramUpperLimit + "%");
            } else if (currentTotalDataSize * 100 / avgDataSize < paramLowerLimit) {
                // 给分
                governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                // 问题项
                governanceAssessDetail.setAssessProblem("当日产出数据量低于前" + paramDays + "天的平均产出数据量的" + paramLowerLimit + "%");
            }
            // 考评备注
            governanceAssessDetail.setAssessComment("当日产出数据量:" + currentTotalDataSize + "，实际前" + realBeforeDays + "天的平均产出量：" + avgDataSize);
        }
    }

    /**
     * 递归汇总指定路径下的数据量大小
     */
    private Long calcDataSize(FileStatus[] fileStatuses, FileSystem fs, long totalSize) throws IOException {
        for (FileStatus fileStatus : fileStatuses) {
            if (fileStatus.isFile()) {
                // 累计大小
                totalSize += fileStatus.getLen();
            } else {
                // 获取当前目录下所有文件和目录
                FileStatus[] subFileStatuses = fs.listStatus(fileStatus.getPath());
                // 递归
                return calcDataSize(subFileStatuses, fs, totalSize);
            }
        }
        return totalSize;
    }
}
