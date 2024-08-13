package com.enzo.dga.governance.assessor.calc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.enzo.dga.constants.DgaConstant;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import com.enzo.dga.util.HttpClientUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 计算中是否存在数据倾斜指标
 */
@Component("TABLE_DATA_SKEW")
public class TableDataSkewAssessor extends Assessor {

    @Value("${spark.history.api.prefix}")
    private String sparkHistoryApiPrefix;

    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        // 排除掉ODS的表
        if (DgaConstant.DW_LEVEL_ODS.equals(assessParam.getTableMetaInfo().getTableMetaInfoExtra().getDwLevel())) {
            return;
        }

        // 提取指标参数
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject paramJsonObj = JSON.parseObject(metricParamsJson);
        Integer paramPercent = paramJsonObj.getInteger("percent");
        Integer paramStageDurSeconds = paramJsonObj.getInteger("stage_dur_seconds");

        // 获取任务实例中的yarnId
        String yarnId = assessParam.getTDsTaskInstance().getAppLink();

        // 发送请求，获取到尝试的Id
        String completedAttemptId = getCompletedAppId(yarnId);

        // 获取所有的StageId
        List<String> stageIdList = getAllStageId(yarnId, completedAttemptId);

        // 将每个Stage处理成一个自定义的Stage对象，将指标关注的信息封装到Stage对象中
        List<Stage> stageList = getStageList(yarnId, completedAttemptId, stageIdList);

        // 存在数据倾斜的集合
        ArrayList<Stage> dataSkewStageList = new ArrayList<>();

        for (Stage stage : stageList) {
            if (stage.getMaxTaskDuration() > paramStageDurSeconds) {
                if (stage.getRealPercent() > paramPercent) {
                    // 存在数据倾斜
                    dataSkewStageList.add(stage);
                }
            }
        }

        if (dataSkewStageList.size() > 0) {
            // 存在数据倾斜，给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            // 问题项
            governanceAssessDetail.setAssessProblem("存在数据倾斜");
        }

        // 考评备注
        governanceAssessDetail.setAssessComment("所有的阶段：" + stageList + "，数据倾斜的阶段：" + dataSkewStageList);


    }

    /**
     * 将每个阶段中的task信息处理后，封装成一个自定义的Stage对象
     */
    private List<Stage> getStageList(String yarnId, String completedAttemptId, List<String> stageIdList) {
        // 创建集合，维护处理好的Stage对象
        ArrayList<Stage> stageList = new ArrayList<>();
        // 循环处理每个阶段
        for (String stageId : stageIdList) {
            // http://hadoop102:18080/api/v1/applications/{yarnId}/{attemptId}/stages/{stageId}
            // 拼接接口地址
            String url = sparkHistoryApiPrefix + "/" + yarnId + "/" + completedAttemptId + "/stages/" + stageId;
            // 发送请求
            String result = HttpClientUtil.get(url);
            // 转换成JSONArray
            List<JSONObject> stageJSONArray = JSON.parseArray(result, JSONObject.class);

            // 最大任务耗时
            Integer maxTaskDuration = Integer.MIN_VALUE;
            // 总任务耗时
            Integer sumTaskDuration = 0;
            // 阶段个数
            Integer taskCount = 0;


            for (int i = 0; i < stageJSONArray.size(); i++) {
                // 判断status:"COMPLETE"
                if ("COMPLETE".equals(stageJSONArray.get(i).getString("status"))) {
                    // 提取所有的Tasks
                    JSONObject tasksJsonObj = stageJSONArray.get(i).getJSONObject("tasks");
                    // 取出task的所有key
                    Set<String> allTaskKeys = tasksJsonObj.keySet();
                    for (String taskKey : allTaskKeys) {
                        JSONObject taskJsonObj = tasksJsonObj.getJSONObject(taskKey);
                        // 判断 status : "SUCCESS"
                        if ("SUCCESS".equals(taskJsonObj.getString("status"))) {
                            // 取当前Task的信息
                            Integer taskDuration = taskJsonObj.getInteger("duration");
                            // 维护最大的duration
                            maxTaskDuration = Math.max(maxTaskDuration, taskDuration);
                            // 总耗时
                            sumTaskDuration += taskDuration;
                            // task个数
                            taskCount++;
                        }
                    }
                }
            }
            // 创建阶段对象
            Stage stage = new Stage();
            // 设置id
            stage.setId(stageId);
            // 设置最大任务耗时
            stage.setMaxTaskDuration(maxTaskDuration);
            // 设置平均任务耗时
            // 当前阶段只有一个Task | 有多个Task
            if (taskCount == 1) {
                stage.setAvgTaskDuration(maxTaskDuration);
                // 百分比
                stage.setRealPercent(0);
            } else {
                // 计算平均耗时,排除最大值
                Integer avgTaskDuration = (sumTaskDuration - maxTaskDuration) / (taskCount - 1);
                stage.setAvgTaskDuration(avgTaskDuration);
                // 百分比
                Integer realPercent = (maxTaskDuration - avgTaskDuration) * 100 / avgTaskDuration;
                stage.setRealPercent(realPercent);
            }

            // 添加到集合中
            stageList.add(stage);
        }

        // 返回集合
        return stageList;
    }

    /**
     * 获取所有的StageId
     */
    private List<String> getAllStageId(String yarnId, String completedAttemptId) {
        // http://fastfood102:18080/api/v1/applications/{yarnId}/{attemptId}/stages
        // 拼接接口地址
        String url = sparkHistoryApiPrefix + "/" + yarnId + "/" + completedAttemptId + "/stages";
        // 发送请求
        String result = HttpClientUtil.get(url);
        // 转换成JSONArray
        List<JSONObject> stageJsonObjList = JSON.parseArray(result, JSONObject.class);
        // 过滤 status : "COMPLETE" 的Stage，并且提取 stageId
        List<String> stageIdList = stageJsonObjList.stream()
                .filter(jsonObj -> "COMPLETE".equals(jsonObj.getString("status")))
                .map(jsonObj -> jsonObj.getString("stageId"))
                .collect(Collectors.toList());

        return stageIdList;
    }


    /**
     * 获取yarn上任务的尝试成功的Id
     */
    private String getCompletedAppId(String yarnId) {
        // http://fastfood102:18080/api/v1/applications/{yarnId}
        // 拼接接口地址
        String url = sparkHistoryApiPrefix + "/" + yarnId;
        // 发送请求
        String result = HttpClientUtil.get(url);

        // 转换成JSONObject
        JSONObject jsonObject = JSON.parseObject(result);
        // 提取attempts
        JSONArray attemptsArray = jsonObject.getJSONArray("attempts");
        // 循环attemptArray，提取completed：true的attemptId
        for (int i = 0; i < attemptsArray.size(); i++) {
            JSONObject attemptJsonObj = attemptsArray.getJSONObject(i);
            if (attemptJsonObj.getBoolean("completed")) {
                return attemptJsonObj.getString("attemptId");
            }
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Stage {
        private String id;
        private Integer maxTaskDuration;
        private Integer avgTaskDuration;
        private Integer realPercent;
    }
}
