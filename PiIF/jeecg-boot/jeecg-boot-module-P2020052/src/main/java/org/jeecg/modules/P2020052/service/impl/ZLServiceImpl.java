package org.jeecg.modules.P2020052.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.jeecg.modules.P2020052.mapper.ReportMapper;
import org.jeecg.modules.P2020052.mapper.ZLMapper;
import org.jeecg.modules.P2020052.pojo.*;
import org.jeecg.modules.P2020052.service.ZLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@DS("multi-datasource1")
public class ZLServiceImpl implements ZLService {

    @Autowired
    ZLMapper ZLMapper;
    @Override
    //PERT报表
    public List<PiplanActivityVo> pertTable(String activeId) throws ParseException {
        List<PiplanActivityVo> result = ZLMapper.pertTable(activeId);
        List list = new ArrayList();
        for (PiplanActivityVo pa : result) {
            Map<String, Object> map = new HashMap<String, Object>();
            // 预估开始时间
            String taskStart = pa.getTargetStartTime();
            map.put("estimatedTaskStart", taskStart);
            // 截至时间
            String taskComplete = pa.getByTime();
            map.put("taskComplete", taskComplete);
            // 预估完成时间
            String estimatedCompletion = pa.getExpectedFinishTime();
            map.put("estimatedCompletion", estimatedCompletion);
            // 权重  Complete：最可能完成时间
            double weight = 0.7;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long Complete = (long) ((taskComplete == null ? 0 : sdf.parse(taskComplete).getTime()) -
                    ((taskStart == null ? 0 : sdf.parse(taskStart).getTime()))) * weight
                    + taskStart == null ? 0 : sdf.parse(taskStart).getTime();
            // 实际开始时间
            String actualStart = pa.getActualStartTime() == null ? "0" : pa.getExpectedFinishTime();
            map.put("actualStart", actualStart);
            // PERT（（F6-c6+4*(E6-C6)+D6-C6）/6）
            long pertLong = Complete +
                    (taskComplete == null ? 0 : sdf.parse(taskComplete).getTime()) -
                    (taskStart == null ? 0 : sdf.parse(taskStart).getTime() * 2)
                    + 4 * ((estimatedCompletion == null ? 0 : sdf.parse(estimatedCompletion).getTime())
                    - (taskStart == null ? 0 : sdf.parse(taskStart).getTime()));
            DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
            String pert = df.format(pertLong / (1000 * 60 * 60 * 24 * 6));
            map.put("pert", pert);
            list.add(map);
        }
        return list;
    }

    // 计算绝对值
    public double abs(double a) {
        return (a > 0) ? a : -a;
    }

    @Override
    //项目风险系统报表*************************************************************************************************
    public List ProjectRiskTable(String projectIds) throws ParseException {
        String[] projectId = projectIds.split(",");    //多个项目id被分割成数组
        List result = new ArrayList();
        Date Date = new Date();
        for(String id :projectId) {
            List<ProjectRiskVo> list = ZLMapper.ProjectRiskTable(id);
            // 计算时间周数
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            DecimalFormat df = new DecimalFormat("0.00");// 设置保留位数
            String project = "";
            Map<Object, Object> map = new HashMap();
            //定义输出风险系数
            double OutputQualityRiskSum = 0;
            if(list.size() == 0){
                continue;
            }
            for (ProjectRiskVo projectRiskVo : list) {
                if ("".equals(project)) {
                    project = projectRiskVo.getProjectId();     //项目ID 传过来
                    // 项目的开始时间
                    long startTime = projectRiskVo.getStartTime() == "" ? 0 : formatter.parse(projectRiskVo.getStartTime()).getTime();
                    //项目启动时间和当前日期差=当前时间 - 项目开始时间
                    long startDate = Date.getTime() - startTime;
                    //项目周期 = 项目计划最早开始时间 - 项目计划最晚完成时间
                    PiplanActivityVo piplanActivityVo = ZLMapper.projectWeekCycle(id);
                    long end1 =piplanActivityVo.getTargetStartTime() == ""? 0:formatter.parse(piplanActivityVo.getTargetStartTime()).getTime();
                    long end2 =piplanActivityVo.getByTime() == ""? 0:formatter.parse(piplanActivityVo.getByTime()).getTime();
                    double end = end2-end1;
                    // x轴
                    map.put("Xaxis",startDate/(end == 0 ? 1 :end));
                    // y轴
                    String Yaxis = projectRiskVo.getPreRatio() == "" ? "0.5" : projectRiskVo.getPreRatio();
                    map.put("Yaxis", 0.5);
                    //项目名称
                    String proName = projectRiskVo.getName() == "" ? "0.5" : projectRiskVo.getName();
                    map.put("proName", proName);
                    map.put("projectId", projectRiskVo.getProjectId());
                }
                //标准偏差
                double standardDeviation = projectRiskVo.getStandardDeviationValue() == "" ?
                        0 : Double.parseDouble(projectRiskVo.getStandardDeviationValue());
                // 汇报偏差
                double reportingDeviations = projectRiskVo.getDeviationReport() == "" ?
                        0 : Double.parseDouble(projectRiskVo.getDeviationReport());
                // 汇报困难度
                double reportingDifficulty = projectRiskVo.getDifficultyReport() == "" ?
                        0 : Double.parseDouble(projectRiskVo.getDifficultyReport());
                // 标准困难度
                double standardDifficulty = projectRiskVo.getStandardDifficultyValue() == "" ?
                        0 : Double.parseDouble(projectRiskVo.getStandardDifficultyValue());
                // 计算输出质量风险
                String OutputQualityRisk = df.format(((abs(reportingDeviations - standardDeviation)) * 10 + 1)
                        * ((abs(reportingDifficulty - standardDifficulty)) * 10 + 1));
                // 计算输出质量风险之和
                OutputQualityRiskSum += Double.parseDouble(OutputQualityRisk);
            }
            map.put("OutputQualityRiskSum", Double.parseDouble(df.format(OutputQualityRiskSum)));
            result.add(map);
        }
        return result;
    }
    @Override
    public List SectorRiskFactor(String userIds, String name, String startTime, String endTime) throws ParseException {
        String[] userId = userIds.split(",");
        startTime = startTime + " 00:00:00";
        endTime = endTime + " 23:59:59";
        List<GroupRiskVo> list = ZLMapper.SectorRiskFactor(userId, startTime, endTime);
        // 计算时间周数
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        long start = formatter.parse(startTime).getTime() / (1000 * 60 * 60 * 24);
        long end = formatter.parse(endTime).getTime() / (1000 * 60 * 60 * 24);
        double weeks = (end - start) / (7.0);
        long week = (long) Math.ceil(weeks);
        DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
        //返回数据
        List result = new ArrayList();
        for (long i = 1; i <= week; i++) {
            Map<String, Object> staffMap = new HashMap<String, Object>();
            Map<String, Object> markMap = new HashMap<String, Object>();
            //每周项目风险系数之和
            // 输出质量风险之和
            Double sum = 0.0;
            // 基准之和
            Double sumBenchmark = 0.0;
            // 计划工时之和
            Double planSum = 0.0;
            // 实际工时之和
            Double actualSum = 0.0;
            for (GroupRiskVo groupRiskVo : list) {
                // 每周开始时间
                long startLong = formatter.parse(startTime).getTime() + (i - 1) * 1000 * 60 * 60 * 24 * 7;
                // 每周结束时间
                long endLong = formatter.parse(startTime).getTime() + i * 1000 * 60 * 60 * 24 * 7;
                String taskStr = groupRiskVo.getActualStartDate();
                long taskLong = taskStr == null ? 0 : formatter.parse(taskStr).getTime();
                if (taskLong >= startLong && taskLong < endLong) {
                    // 计算输出质量风险
                    double reportingDeviations = groupRiskVo.getDeviationReport()
                            == "" ? 0 : Double.parseDouble(groupRiskVo.getDeviationReport());
                    double standardDeviation = groupRiskVo.getStandardDeviationValue()
                            == "" ? 0 : Double.parseDouble(groupRiskVo.getStandardDeviationValue());
                    double reportingDifficulty = groupRiskVo.getDifficultyReport()
                            == "" ? 0 : Double.parseDouble(groupRiskVo.getDifficultyReport());
                    double standardDifficulty = groupRiskVo.getStandardDifficultyValue()
                            == "" ? 0 : Double.parseDouble(groupRiskVo.getStandardDifficultyValue());
                    String OutputQualityRisk = df.format(((abs(reportingDeviations - standardDeviation)) * 10 + 1)
                            * ((abs(reportingDifficulty - standardDifficulty)) * 10 + 1));
                    double RiskDouble = Double.parseDouble(OutputQualityRisk);

                    double span = groupRiskVo.getBreadth()
                            == "" ? 0 : Double.parseDouble(groupRiskVo.getBreadth());
                    double Criticality = groupRiskVo.getCriticality()
                            == "" ? 0 : Double.parseDouble(groupRiskVo.getCriticality());
                    // 计算项目风险指标
                    double ProjectRiskIndicators = (RiskDouble - 1) * span * Criticality;
                    // 计算基准
                    double ProjectRiskBenchmark = RiskDouble * span * Criticality;
                    // 输出质量风险之和
                    sum += ProjectRiskIndicators;
                    // 基准之和
                    sumBenchmark += ProjectRiskBenchmark;
                    // 计划工时
                    double plan = groupRiskVo.getStandardWorkQty()
                            == "" ? 0 : Double.parseDouble(groupRiskVo.getStandardWorkQty());

                    //Double plan = Double.parseDouble(map.get("计划工时").toString());
                    planSum += plan;
                    // 实际工时
                    double actual = groupRiskVo.getActualWorkQty()
                            == "" ? 0 : Double.parseDouble(groupRiskVo.getActualWorkQty());
                    //Double actual = Double.parseDouble(map.get("实际工时").toString());
                    actualSum += actual;
                }
            }
            // 员工姓名
            staffMap.put("name", name);
            staffMap.put("week", i);
            // 员工基准
            markMap.put("name", name + "基准");
            markMap.put("week", i);
            // 这一周员工风险系数之和
            staffMap.put("sum", df.format(sum));
            // 本周员工基准之和
            markMap.put("sumBenchmark", df.format(sumBenchmark));
            String ratio = df.format(planSum / (actualSum == 0 ? 1 : actualSum));
            staffMap.put("ratio", Double.parseDouble(ratio));
            markMap.put("ratio", Double.parseDouble(ratio));
            result.add(staffMap);
            result.add(markMap);
        }
        return result;
    }
}
