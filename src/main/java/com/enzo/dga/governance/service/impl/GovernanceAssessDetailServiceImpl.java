package com.enzo.dga.governance.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enzo.dga.governance.assessor.Assessor;
import com.enzo.dga.governance.bean.AssessParam;
import com.enzo.dga.governance.bean.GovernanceAssessDetail;
import com.enzo.dga.governance.bean.GovernanceMetric;
import com.enzo.dga.governance.mapper.GovernanceAssessDetailMapper;
import com.enzo.dga.governance.service.GovernanceAssessDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enzo.dga.governance.service.GovernanceMetricService;
import com.enzo.dga.meta.bean.TableMetaInfo;
import com.enzo.dga.meta.bean.TableMetaInfoExtra;
import com.enzo.dga.meta.mapper.TableMetaInfoMapper;
import com.enzo.dga.meta.service.TableMetaInfoExtraService;
import com.enzo.dga.meta.service.TableMetaInfoService;
import com.enzo.dga.util.SpringIOCProvider;
import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 治理考评结果明细 服务实现类
 * </p>
 *
 * @author enzo
 * @since 2024-08-01
 */
@Service
@DS("dga")
public class GovernanceAssessDetailServiceImpl extends ServiceImpl<GovernanceAssessDetailMapper, GovernanceAssessDetail> implements GovernanceAssessDetailService {

    @Autowired
    TableMetaInfoService tableMetaInfoService;

    @Autowired
    TableMetaInfoExtraService tableMetaInfoExtraService;

    @Autowired
    TableMetaInfoMapper tableMetaInfoMapper;

    @Autowired
    GovernanceMetricService governanceMetricService;

    @Autowired
    SpringIOCProvider springIOCProvider;


    /**
     * 核心考评方法
     * <p>
     * 考评思想：每个指标，每张表，逐一进行考评
     * <p>
     * 步骤：
     * 0.删除考评日期对应的考评结果
     * <p>
     * 1. 获取所有待考评的表
     * 方案一：不推荐 因为要频繁调动数据库
     * // 查询table_meta_info表和table_meta_info_extra表中的数据，最终封装到TableMetaInfoExtra对象中
     * // 先查询table_meta_info表
     * List<TableMetaInfo> tableMetaInfoList = tableMetaInfoService.list(
     * new QueryWrapper<TableMetaInfo>()
     * .eq("assess_date", assessDate)
     * );
     * // 遍历tableMetaInfoList，通过每个TableMetaInfoExtra对象的表名和库名查询对应的TableMetaInfoExtra
     * for (TableMetaInfo tableMetaInfo : tableMetaInfoList){
     * TableMetaInfoExtra tableMetaInfoExtra = tableMetaInfoExtraService.getOne(
     * new QueryWrapper<TableMetaInfoExtra>()
     * .eq("schema_name", tableMetaInfo.getSchemaName())
     * .eq("table_name", tableMetaInfo.getTableName())
     * );
     * tableMetaInfo.setTableMetaInfoExtra(tableMetaInfoExtra);
     * }
     * <p>
     * <p>
     * <p>
     * // 方案二：将table_meta_info表和table_meta_info_extra表中的数据一次性拿出来，封装到集合中,
     * // 再通过集合的操作，将tableMetaInfoExtra补充到TableMetaInfo对象中
     * List<TableMetaInfo> tableMetaInfoList = tableMetaInfoService.list(
     * new QueryWrapper<TableMetaInfo>()
     * .eq("assess_date", assessDate)
     * );
     * <p>
     * // 查询所有的TableMetaInfoExtra对象
     * List<TableMetaInfoExtra> tableMetaInfoExtraList = tableMetaInfoExtraService.list();
     * <p>
     * // 将tableMetaInfoExtraList处理成Map结构
     * // key :    schemaName + tableName
     * // value :  TableMetaInfoExtra
     * Map<String, TableMetaInfoExtra> tableMetaInfoExtraMap = new HashMap<>(tableMetaInfoExtraList.size());
     * for (TableMetaInfoExtra tableMetaInfoExtra : tableMetaInfoExtraList) {
     * String key = tableMetaInfoExtra.getSchemaName() + ":" + tableMetaInfoExtra.getTableName();
     * tableMetaInfoExtraMap.put(key, tableMetaInfoExtra);
     * }
     * // 循环集合，将tableMetaInfoExtra补充到TableMetaInfo对象中
     * for (TableMetaInfo tableMetaInfo : tableMetaInfoList) {
     * String key = tableMetaInfo.getSchemaName() + ":" + tableMetaInfo.getTableName();
     * tableMetaInfo.setTableMetaInfoExtra(tableMetaInfoExtraMap.get(key));
     * }
     * <p>
     * // 还是效率差
     * //        // 循环集合，将tableMetaInfoExtra补充到TableMetaInfo对象中
     * //        for (TableMetaInfo tableMetaInfo : tableMetaInfoList) {
     * //            for (TableMetaInfoExtra tableMetaInfoExtra : tableMetaInfoExtraList) {
     * //                if (tableMetaInfo.getSchemaName().equals(tableMetaInfoExtra.getSchemaName()) &&
     * //                        tableMetaInfo.getTableName().equals(tableMetaInfoExtra.getTableName())) {
     * //                    tableMetaInfo.setTableMetaInfoExtra(tableMetaInfoExtra);
     * //                }
     * //            }
     * //        }
     * <p>
     * <p>
     * <p>
     * // 方案三：基于JOIN查询，一次性将两张表的数据都查询出来,再通过自定义映射，告诉Mybatis如何封装结果，需要使用xml文件来定义映射规则
     * <p>
     * List<TableMetaInfo> tableMetaInfoList = tableMetaInfoMapper.selectAllTableMetaInfoWithExtra(assessDate);
     * <p>
     * <p>
     * <p>
     * 2. 获取所有考评的指标
     * <p>
     * <p>
     * 3. 每张表，每个指标，逐一进行考评
     * <p>
     * <p>
     * 4. 将考评结果写到数据库的表中
     */

    @Override
    public void mainAssess(String assessDate) {
        // 0.删除考评日期对应的考评结果
        remove(
                new QueryWrapper<GovernanceAssessDetail>()
                        .eq("assess_date", assessDate)
        );


        // 1. 获取所有待考评的表
        // 方案三：基于JOIN查询，一次性将两张表的数据都查询出来,再通过自定义映射，告诉Mybatis如何封装结果，需要使用xml文件来定义映射规则

        List<TableMetaInfo> tableMetaInfoList = tableMetaInfoMapper.selectAllTableMetaInfoWithExtra(assessDate);

        // 2. 获取所有考评的指标
        List<GovernanceMetric> governanceMetricList = governanceMetricService.list(
                new QueryWrapper<GovernanceMetric>()
                        .eq("is_disabled", "0")
        );


        // 3. 每张表，每个指标，逐一进行考评

        // 创建集合，保存考评结果
        ArrayList<GovernanceAssessDetail> governanceAssessDetailList
                = new ArrayList<>(tableMetaInfoList.size() * governanceMetricList.size());

        for (TableMetaInfo tableMetaInfo : tableMetaInfoList) {
            for (GovernanceMetric governanceMetric : governanceMetricList) {
                // 每张表 tableMetaInfo
                // 每个指标 governanceMetric

                // 每个指标只是数据库表中定义的一条数据，我们期望指标是能工作的，说白了 就是能在代码中执行，并且拥有一些功能，比如查找表中存在的问题
                // 将每个指标设计成一个具体的类（考评器），类中拥有方法，方法中写查找表问题的代码
                // 例如：
                //      是否有技术OWNER  =>  TableTecOwnerAssessor
                //      是否有业务OWNER  =>  TableBusiOwnerAssessor
                //      .........       =>  ........

                // 考评器 Assessor 的设计：    !!❌!!模板设计模式!!❌!!
                // 每个指标对应的考评器中用于查找问题的方法关注点不一样，就是找的问题不同，写的代码也不同
                // 但是每个考评器工作的流程是一样的，只不过某些细节不一样
                // 考评器父类： Assessor，整体控制考评流程
                // 具体的考评器： xxxAssessor，只关注查找问题的细节即可


                // 如何通过指标获取到对应的考评器
                // 反射方式：
                //      约定：
                //          1. 每个考评器所在的包的名字必须 用指标类型来命名
                //          2. 每个考评器类名必须 用指标编码来命名 格式为：指标编码 + Assessor


                // ❌❌❌❌ 反射太麻烦！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
//                Assessor assessor;
//
//                // 通过指标处理包名
//                String assessorPackage = "com.enzo.dga.governance.assessor";
//                String subPackage = governanceMetric.getGovernanceType().toLowerCase();
//                String fullPackage = assessorPackage + "." + subPackage;
//
//                // 通过指标处理类名
//                // 编码指标：TABLE_TECH_OWNER
//                // 类名：TableTecOwnerAssessor
//                String metricCode = governanceMetric.getMetricCode();
//                String classNameByMetricCode = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, metricCode);
//                String className = classNameByMetricCode + "Assessor";
//
//                // 全类名
//                String fullClassName = fullPackage + "." + className;
//
//                // 反射创建对象
//                try {
//                    Class<?> clsObj = Class.forName(fullClassName);
//                    assessor = (Assessor)clsObj.newInstance();
//
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//
//                // 开始考评
//                assessor.doAssess();


                // ✅✅✅✅ 最终方式：基于Spring容器来完成，将所有的考评器通过Spring容器管理，考评器对象由容器来创建
                // 约定：把所有考评对象管理到容器🀄️，要明确指定名字，名字使用指标的编码
                String metricCode = governanceMetric.getMetricCode();
                // 从容器中获取对应的考评器对象
                Assessor assessor = springIOCProvider.getBean(metricCode, Assessor.class);
                // 封装考评参数
                // ❌❌❌❌ new 的方式
//                AssessParam assessParam = new AssessParam();
//                assessParam.setAssessDate(assessDate);
//                assessParam.setTableMetaInfo(tableMetaInfo);
//                assessParam.setGovernanceMetric(governanceMetric);

                // ✅✅✅✅ 建造者方式
                AssessParam assessParam = AssessParam.builder()
                        .assessDate(assessDate)
                        .tableMetaInfo(tableMetaInfo)
                        .governanceMetric(governanceMetric)
                        .tableMetaInfoList(tableMetaInfoList)
                        .build();

                // 开始考评
                GovernanceAssessDetail governanceAssessDetail = assessor.doAssess(assessParam);

                // 将结果保存到集合中（攒批）
                governanceAssessDetailList.add(governanceAssessDetail);
            }

        }

        // 4. 将考评结果写到数据库的表中
        saveBatch(governanceAssessDetailList);

    }
}
