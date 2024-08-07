package com.enzo.dga.governance.assessor.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.yarn.webapp.hamlet2.Hamlet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;

/**
 * 目录文件数据访问权限超过建议值指标
 */
@Component("TABLE_DIR_FIRE_PERMISSION")
public class TableDirFilePermissionAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        // 提取指标参数
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject parseJsonObj = JSON.parseObject(metricParamsJson);
        String paramFilePermission = parseJsonObj.getString("file_permission");
        String paramDirPermission = parseJsonObj.getString("dir_permission");

        // 提取当前表在hdfs中的路径
        String tableFsPath = assessParam.getTableMetaInfo().getTableFsPath();

        // 定义集合，保存越权的文件和目录
        ArrayList<String> beyondDirList = new ArrayList<>();
        ArrayList<String> beyondFileList = new ArrayList<>();

        // 检查表路径是否越权
        FileSystem fs =
                FileSystem.get(new URI(tableFsPath), new Configuration(), assessParam.getTableMetaInfo().getTableFsOwner());
        FileStatus fileStatus = fs.getFileStatus(new Path(tableFsPath));
        FsPermission currentPermission = fileStatus.getPermission();    // 获取权限
        boolean isBeyond = CheckRWX(currentPermission, paramDirPermission);
        if (isBeyond) {
            // 添加到集合中
            beyondDirList.add(fileStatus.getPath().toString());
        }


        // 基于越界的文件和目录的集合判断
        if (beyondDirList.size() > 0 || beyondFileList.size() > 0) {
            // 给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("文件或者目录越权");
            // 考评备注
            governanceAssessDetail.setAssessComment("越权的目录：" + beyondDirList + "越权的文件：" + beyondFileList);
        }


        // 检查表路径下的文件和目录是否越权
        FileStatus[] fileStatuses = fs.listStatus(fileStatus.getPath());
        // 递归处理
        checkDirOrFilePermission(fileStatuses, fs, paramFilePermission, paramDirPermission, beyondFileList, beyondDirList);


    }


    /**
     * 递归判断表路径下所有的文件和目录是否越权
     */
    private void checkDirOrFilePermission(FileStatus[] fileStatuses, FileSystem fs, String paramFilePermission, String paramDirPermission, ArrayList<String> beyondFileList, ArrayList<String> beyondDirList) throws Exception {
        for (FileStatus fileStatus : fileStatuses) {
            FsPermission currentPermission = fileStatus.getPermission();
            if (fileStatus.isFile()) {
                // 文件
                Boolean isBeyond = CheckRWX(currentPermission, paramFilePermission);
                if (isBeyond) {
                    beyondFileList.add(fileStatus.getPath().toString());
                }
            } else if (fileStatus.isDirectory()) {
                // 目录
                Boolean isBeyond = CheckRWX(currentPermission, paramDirPermission);
                if (isBeyond) {
                    beyondDirList.add(fileStatus.getPath().toString());
                }

                // 获取当前目录下的文件和目录
                FileStatus[] subFileStatuses = fs.listStatus(fileStatus.getPath());
                checkDirOrFilePermission(subFileStatuses, fs, paramFilePermission, paramDirPermission, beyondFileList, beyondDirList);
            }
        }
    }


    /**
     * 检查给定的目录或者文件的权限是否超过建议值
     */
    private boolean CheckRWX(FsPermission currentPermission, String paramPermission) {

        // 指标参数定义的权限
        Integer userRWX = paramPermission.charAt(0) - '0';
        Integer groupRWX = paramPermission.charAt(1) - '0';
        Integer otherRWX = paramPermission.charAt(2) - '0';

        // 对位判断
        // ordinal()方法返回的是枚举值在枚举中的位置，从0开始
        if (currentPermission.getUserAction().ordinal() > userRWX) {
            return true;
        } else if (currentPermission.getGroupAction().ordinal() > groupRWX) {
            return true;
        } else if (currentPermission.getOtherAction().ordinal() > otherRWX) {
            return true;
        }
        return false;
    }
}
