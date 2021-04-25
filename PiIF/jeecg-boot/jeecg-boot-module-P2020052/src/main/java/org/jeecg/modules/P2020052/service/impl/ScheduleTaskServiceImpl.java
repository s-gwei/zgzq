package org.jeecg.modules.P2020052.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.jeecg.modules.P2020052.mapper.PlanOTMapper;
import org.jeecg.modules.P2020052.mapper.ScheduleTaskMapper;
import org.jeecg.modules.P2020052.pojo.PiplanActivityVo;
import org.jeecg.modules.P2020052.pojo.ProjectRiskVo;
import org.jeecg.modules.P2020052.pojo.TaskVo;
import org.jeecg.modules.P2020052.service.PlanOTService;
import org.jeecg.modules.P2020052.service.ScheduleTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@DS("multi-datasource1")
public class ScheduleTaskServiceImpl implements ScheduleTaskService {

    @Autowired
    ScheduleTaskMapper scheduleTaskMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    PlanOTService planOTService;

    @Override
    public void taskExecution() {
        //获取所有项目信息
        List<String> projectIds = scheduleTaskMapper.getProjectIds();
        for (String projectId : projectIds) {
            //查询每个项目下所有的任务
            List<TaskVo> taskList = scheduleTaskMapper.getTaskById(projectId);
            List projectList = new ArrayList();
            DecimalFormat df = new DecimalFormat("0.00");// 设置保留位数
            for (TaskVo taskVo : taskList) {
                List<TaskVo> list = null;
//                if("144355".equals(taskVo.getActiviteId() ) ){
//                    int a = 0;
//                }
                //获取任务下所有in指标
                List<TaskVo> inList = scheduleTaskMapper.getTaskInById(taskVo.getActiviteId());
                //获取任务下所有ot指标
                List<TaskVo> otList = scheduleTaskMapper.getTaskOTById(taskVo.getActiviteId());
                if (inList == null && otList == null) {
                    continue;
                }
                if (inList.size() == 0 && otList.size() == 0) {
                    continue;
                }
                //判断inlist与otlist长度
                if (inList.size() > otList.size()) {
                    list = inList;
                } else {
                    list = otList;
                }
                //将in，ot数据填充到list
                //每个任务都作为list存储
                List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
                Map<String, Object> totalMap = new HashMap<String, Object>();
                //kpi风险系数之和
                double OutputaualityKPISum = 0;
                //权重之和
                double weightSum = 0;
                //质量影响因子
                double qualityInfluenceSum = 0;
                int k = 0;
                //标准工期
                double StandardPeriod = 0;
                //项目工期
                double reportPeriod = 0;
                //平均发布次数
                double NumbereleasesAvg = 0;
                for (int i = 0; i < list.size(); i++) {
                    TaskVo task = list.get(i);
                    TaskVo inTask = i <= inList.size() - 1 ? inList.get(i) : null;
                    TaskVo otTask = i <= otList.size() - 1 ? otList.get(i) : null;
                    //resultMap存贮任务下一条in,ot的属性
                    Map<String, Object> resultMap = new HashMap<String, Object>();
                    //任务名称,任务的属性通过taskVo获取
                    resultMap.put("taskname", taskVo.getPlanName());
                    //执行职能
                    resultMap.put("executive", taskVo.getExecutive());
                    //任务id
                    resultMap.put("activiteId", taskVo.getActiviteId());
                    //标准工期
                    StandardPeriod = Double.parseDouble(taskVo.getStandardWork() == null ? "0" : taskVo.getStandardWork());
                    resultMap.put("StandardPeriod", StandardPeriod);
                    //项目工期
                    reportPeriod = Double.parseDouble(task.getReportWork() == null ? "0" : task.getReportWork());
                    resultMap.put("reportPeriod", reportPeriod);
                    //in的属性通过inTask获取,
                    if (inTask == null) {
                        //in指标编码
                        resultMap.put("in_code", null);
                        resultMap.put("in_description", null);
                        //in标准偏差
                        resultMap.put("in_standardDeviationValue", null);
                        // 汇报偏差
                        resultMap.put("in_deviationReport", null);
                        // 权重
                        Double weight = 0.0;
                        weightSum += weight;
                        resultMap.put("weight", null);
                        // 计算得出质量影响因子
                        String qualityInfluenceFactor = "0";
                        qualityInfluenceSum += Double.parseDouble(qualityInfluenceFactor);
                        resultMap.put("qualityInfluenceFactor", "0");
                        //输出评定
                        resultMap.put("OutputEvalua", null);
                    } else {
                        //in指标编码
                        resultMap.put("in_code", inTask.getIn_code());
                        resultMap.put("in_description", inTask.getIn_description());
                        //in标准偏差
                        Double in_standardDeviationValue = Double.parseDouble(inTask.getIn_standardDeviationValue() == null ?
                                "0" : inTask.getIn_standardDeviationValue());
                        resultMap.put("in_standardDeviationValue", inTask.getIn_standardDeviationValue());
                        // 汇报偏差
                        Double in_deviationReport = Double.parseDouble(inTask.getIn_deviationReport() == null ?
                                "0" : inTask.getIn_deviationReport());
                        resultMap.put("in_deviationReport", inTask.getIn_deviationReport());
                        // 权重
                        Double weight = Double.parseDouble(inTask.getWeights() == null ?
                                "0" : inTask.getWeights());
                        weightSum += weight;
                        resultMap.put("weight", inTask.getWeights());
                        // 计算得出质量影响因子
                        String qualityInfluenceFactor = df.format(weight * (in_deviationReport / (in_standardDeviationValue == 0 ? 1 : in_standardDeviationValue)));
                        qualityInfluenceSum += Double.parseDouble(qualityInfluenceFactor);
                        resultMap.put("qualityInfluenceFactor", qualityInfluenceFactor);
                        //输出评定
                        String OutputEvalua = inTask.getOutput() == null ? "" : inTask.getOutput();
                        resultMap.put("OutputEvalua", OutputEvalua);
                    }
                    //ot的属性通过otTask获取,
                    if (otTask == null) {
                        resultMap.put("code", null);
                        resultMap.put("reportTime", null);
                        resultMap.put("description", null);
                        //标准偏差
                        Double standardDeviation = 0.0;
                        resultMap.put("standardDeviation", null);
                        // 汇报偏差
                        Double reportingDeviations = 0.0;
                        resultMap.put("reportingDeviations", null);
                        // 汇报困难度
                        Double reportingDifficulty = 0.0;
                        resultMap.put("reportingDifficulty", null);
                        // 标准困难度
                        Double standardDifficulty = 0.0;
                        resultMap.put("standardDifficulty", null);
                        // 计算得出输出质量KPI
                        String OutputaualityKPI = df
                                .format((reportingDeviations / (standardDeviation == 0 ? 1 : standardDeviation)) *
                                        (reportingDifficulty / (standardDifficulty == 0 ? 1 : standardDifficulty)));
                        resultMap.put("OutputaualityKPI", OutputaualityKPI);
                        //计算输出质量KPI风险系数之和
                        OutputaualityKPISum += Double.parseDouble(OutputaualityKPI);
                        // 计算输出质量风险
                        String OutputQualityRisk = df.format(((abs(reportingDeviations - standardDeviation)) * 10 + 1)
                                * ((abs(reportingDifficulty - standardDifficulty)) * 10 + 1));
                        resultMap.put("OutputQualityRisk", OutputQualityRisk);
                        // 广度
                        Double span = 0.0;
                        resultMap.put("span", null);
                        // 关键度
                        Double Criticality = 0.0;
                        resultMap.put("criticality", null);
                        // 计算项目风险指标
                        String ProjectRiskIndicators = df.format((Double.parseDouble(OutputQualityRisk) - 1) * span * Criticality);
                        resultMap.put("ProjectRiskIndicators", ProjectRiskIndicators);
                        // 发布次数
                        Double Numbereleases = 0.0;
                        NumbereleasesAvg += Numbereleases;
                        resultMap.put("Numbereleases", null);
                        resultMap.put("ProjectRiskIndicators", ProjectRiskIndicators);
                        resultMap.put("Criticality", Criticality);
                    } else {
                        resultMap.put("code", otTask.getCode());
                        resultMap.put("reportTime", otTask.getReport_time());
                        resultMap.put("description", otTask.getDescription());
                        //标准偏差
                        Double standardDeviation = Double.parseDouble(otTask.getStandardDeviationValue() == null ?
                                "0" : otTask.getStandardDeviationValue());
                        resultMap.put("standardDeviation", otTask.getStandardDeviationValue());
                        // 汇报偏差
                        Double reportingDeviations = Double.parseDouble(otTask.getDeviationReport() == null ?
                                "0" : otTask.getDeviationReport());
                        resultMap.put("reportingDeviations", otTask.getDeviationReport());
                        // 汇报困难度
                        double reportingDifficulty = Double.parseDouble(otTask.getDifficultyReport() == null ?
                                "0" : otTask.getDifficultyReport());
                        resultMap.put("reportingDifficulty", otTask.getDifficultyReport());
                        // 标准困难度
                        double standardDifficulty = Double.parseDouble(otTask.getStandardDifficultyValue() == null ?
                                "0" : otTask.getStandardDifficultyValue());
                        resultMap.put("standardDifficulty", otTask.getStandardDifficultyValue());
                        // 计算得出输出质量KPI
                        String OutputaualityKPI = df
                                .format((reportingDeviations / (standardDeviation == 0 ? 1 : standardDeviation)) *
                                        (reportingDifficulty / (standardDifficulty == 0 ? 1 : standardDifficulty)));
                        resultMap.put("OutputaualityKPI", OutputaualityKPI);
                        //计算输出质量KPI风险系数之和
                        OutputaualityKPISum += Double.parseDouble(OutputaualityKPI);
                        // 计算输出质量风险
                        String OutputQualityRisk = df.format(((abs(reportingDeviations - standardDeviation)) * 10 + 1)
                                * ((abs(reportingDifficulty - standardDifficulty)) * 10 + 1));
                        resultMap.put("OutputQualityRisk", OutputQualityRisk);
                        // 广度
                        double span = Double.parseDouble(otTask.getBreadth() == null ?
                                "0" : otTask.getBreadth());
                        resultMap.put("span", otTask.getBreadth());
                        // 关键度
                        double Criticality = Double.parseDouble(otTask.getCriticality() == null ?
                                "0" : otTask.getCriticality());
                        resultMap.put("criticality", otTask.getCriticality());
                        // 计算项目风险指标
                        String ProjectRiskIndicators = df.format((Double.parseDouble(OutputQualityRisk) - 1) * span * Criticality);
                        resultMap.put("ProjectRiskIndicators", ProjectRiskIndicators);
                        // 发布次数
                        double Numbereleases = Double.parseDouble(otTask.getReleases() == null ?
                                "0" : otTask.getReleases());
                        NumbereleasesAvg += Numbereleases;
                        resultMap.put("Numbereleases", otTask.getReleases());
                        resultMap.put("ProjectRiskIndicators", ProjectRiskIndicators);
                        resultMap.put("Criticality", Criticality);
                    }
                    resultList.add(resultMap);

                    k = i + 1;
                }
                totalMap.put("taskname", taskVo.getPlanName());
                totalMap.put("Executive", taskVo.getExecutive());
                totalMap.put("taskid", taskVo.getActiviteId());
                //总质量指标
                String totalQualityKpi = df.format(OutputaualityKPISum / k);
                totalMap.put("totalQualityKpi", totalQualityKpi);
                // 平均发布次数
                NumbereleasesAvg = NumbereleasesAvg / k;
                totalMap.put("NumbereleasesAvg", df.format(NumbereleasesAvg));
                //环境质量指标
                String qualityIndex = df.format((qualityInfluenceSum - weightSum) / k);
                totalMap.put("qualityIndex", qualityIndex);
                //风险工期kpi
                String riskKPI = null;
                if (StandardPeriod != 0) {
                    riskKPI = df.format(reportPeriod / StandardPeriod);
                } else {
                    riskKPI = "0";
                }
                totalMap.put("riskKPI", riskKPI);
                resultList.add(totalMap);
                redisTemplate.opsForValue().set(projectId + taskVo.getActiviteId(), resultList, 7, TimeUnit.DAYS);
            }
        }
    }

    @Override
    public void ProjectRiskTable() throws ParseException {
        //获取所有的项目id
        List<String> projectIds = scheduleTaskMapper.selectAllId();
        //查询每个项目下所有的任务信息
        Date date = new Date();
        // 计算时间周数
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat df = new DecimalFormat("0.00");// 设置保留位数
        for (String projectId : projectIds) {
            List<ProjectRiskVo> list = scheduleTaskMapper.ProjectRiskTable(projectId);
            String project = "";
            Map<Object, Object> map = new HashMap();
            double OutputQualityRiskSum = 0;
            if (list.size() == 0) {
                map.put("Xaxis", 1);
                // y轴
                map.put("Yaxis", 0.5);
                //项目名称
                String proName = scheduleTaskMapper.getProName(projectId);
                map.put("proName", proName);
                map.put("proName", "");
                map.put("projectId", projectId);
                map.put("OutputQualityRiskSum", 0);
                redisTemplate.opsForValue().set(projectId, map, 7, TimeUnit.DAYS);
                continue;
            }
            for (ProjectRiskVo projectRiskVo : list) {
                if ("".equals(project)) {
                    project = projectRiskVo.getProjectId();
                    // 开始时间
                    long startTime = projectRiskVo.getStartTime() == "" ? 0 : formatter.parse(projectRiskVo.getStartTime()).getTime();
                    //项目启动时间和当前日期差=当前时间 - 项目开始时间
                    long startDate = date.getTime() - startTime;
                    //项目周期 = 项目计划最早开始时间 - 项目计划最晚完成时间
                    PiplanActivityVo piplanActivityVo = scheduleTaskMapper.projectWeekCycle(projectId);
                    long end1 = piplanActivityVo.getTargetStartTime() == "" ? 0 : formatter.parse(piplanActivityVo.getTargetStartTime()).getTime();
                    long end2 = piplanActivityVo.getByTime() == "" ? 0 : formatter.parse(piplanActivityVo.getByTime()).getTime();
                    double end = end2 - end1;
                    // x轴
                    double x = startDate / (end == 0 ? 1 : end);
                    map.put("Xaxis", Double.parseDouble(df.format(x)));
                    // y轴
                    String Yaxis = projectRiskVo.getPreRatio() == "" ? "0.5" : projectRiskVo.getPreRatio();
                    map.put("Yaxis", 0.5);
                    //项目名称
                    String proName = projectRiskVo.getName() == "" ? "0.5" : projectRiskVo.getName();
                    map.put("proName", proName);
                    map.put("projectId",projectId);
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
            redisTemplate.opsForValue().set(projectId, map, 7, TimeUnit.DAYS);
        }

    }

    @Override
    public void WorkDelayTable() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String end = df.format(new Date());
        //过去一月
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -3);
//        c.add(Calendar.DAY_OF_MONTH, -7);
        Date m = c.getTime();
        String start = df.format(m);
        String[] time = {start,end};
        List<PiplanActivityVo> list = planOTService.WorkDelayTable(time,null,null,null);
        redisTemplate.opsForValue().set("WorkDelayTable", list, 7, TimeUnit.DAYS);

    }


    // 计算绝对值
    public double abs(double a) {
        return (a > 0) ? a : -a;
    }
}

