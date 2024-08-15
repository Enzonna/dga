package com.enzo.dga.governance.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import com.enzo.dga.governance.bean.GovernanceAssessGlobal;
import com.enzo.dga.governance.bean.GovernanceAssessTecOwner;
import com.enzo.dga.governance.service.GovernanceAssessDetailService;
import com.enzo.dga.governance.service.GovernanceAssessGlobalService;
import com.enzo.dga.governance.service.GovernanceAssessTecOwnerService;
import com.enzo.dga.governance.service.MainAssessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/governance")
public class GovernanceAssessController {

    @Autowired
    GovernanceAssessGlobalService governanceAssessGlobalService;
    @Autowired
    GovernanceAssessDetailService governanceAssessDetailService;

    @Autowired
    GovernanceAssessTecOwnerService governanceAssessTecOwnerService;

    @Autowired
    MainAssessService mainAssessService;


    @PostMapping("/assess/{date}")
    public String getAssess(@PathVariable("date") String assessDate) throws Exception {
        mainAssessService.mainAssess(assessDate);
        return "success";
    }

    /**
     * 分组人员排行榜
     */
    @GetMapping("rankList")
    public String getRankList() {
        QueryWrapper<GovernanceAssessTecOwner> queryWrapper = new QueryWrapper<GovernanceAssessTecOwner>()
                .select("tec_owner as tecOwner", "score")
                .inSql("assess_date", "select max(assess_date) from governance_assess_tec_owner")
                .orderByDesc("score");
        List<Map<String, Object>> mapList = governanceAssessTecOwnerService.listMaps(queryWrapper);

        return JSON.toJSONString(mapList);
    }

    /**
     * 获取问题个数
     */
    @GetMapping("/problemNum")
    public String getProblemNum() {
        //求各个治理类型的问题个数
        List<Map<String, Object>> lastProblemNumMapList = governanceAssessDetailService.getLastProblemNum();

        //根据接口要求调整结构
        Map<String, Long> problemMap = new HashMap();
        for (Map<String, Object> map : lastProblemNumMapList) {
            String governanceType = (String) map.get("governance_type");
            Long problemNum = (Long) map.get("ct");
            problemMap.put(governanceType, problemNum);
        }
        return JSONObject.toJSONString(problemMap);
    }

    /**
     * 获取问题列表
     */
    @GetMapping("/problemList/{governType}/{pageNo}/{pageSize}")
    public String getProblemList(@PathVariable("governType") String governType, @PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize) {
        List<GovernanceAssessDetail> governanceAssessDetailList = governanceAssessDetailService.getLastProblemListByType(governType, pageNo, pageSize);
        return JSONObject.toJSONString(governanceAssessDetailList);
    }


    /**
     * 获取全局总分
     */
    @GetMapping("globalScore")
    public String getGlobalScore() {
        //取最新的全局总分
        GovernanceAssessGlobal governanceAssessGlobal
                = governanceAssessGlobalService.getOne(new QueryWrapper<GovernanceAssessGlobal>().orderByDesc("assess_date").last("limit 1"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("assessDate", governanceAssessGlobal.getAssessDate());
        jsonObject.put("sumScore", governanceAssessGlobal.getScore());
        List<BigDecimal> scoreList = new ArrayList<>();
        scoreList.add(governanceAssessGlobal.getScoreSpec());
        scoreList.add(governanceAssessGlobal.getScoreStorage());
        scoreList.add(governanceAssessGlobal.getScoreCalc());
        scoreList.add(governanceAssessGlobal.getScoreQuality());
        scoreList.add(governanceAssessGlobal.getScoreSecurity());
        jsonObject.put("scoreList", scoreList);

        return jsonObject.toJSONString();
    }
}