package org.jeecg.modules.P2020052.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jeecg.modules.P2020052.mapper.ReportMapper;
import org.jeecg.modules.P2020052.pojo.*;
import org.jeecg.modules.P2020052.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Service
@DS("multi-datasource1")
public class reportServiceImpl implements ReportService {

    @Autowired
    ReportMapper reportMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public List<PiplanActivityVo> pertTable(String activeId) throws ParseException {
        Map<String, Object> aa = (Map<String, Object>) redisTemplate.opsForValue().get("133705133455");
        List<PiplanActivityVo> result = reportMapper.pertTable(activeId);
        List list = new ArrayList();
        for (PiplanActivityVo pa : result) {
            Map<String, Object> map = new HashMap<String, Object>();
            // 预估开始时间
            String taskStart = pa.getTargetStartTime();
            map.put("taskStart", taskStart);
            // 截至时间
            String taskComplete = pa.getByTime();
            map.put("taskComplete", taskComplete);
            // 预估完成时间
            String estimatedCompletion = pa.getExpectedFinishTime();
            map.put("estimatedCompletion", estimatedCompletion);
            // 权重
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

    @Override
    public List SectorRiskFactor(String userIds, String name, String startTime, String endTime) throws ParseException {
        String[] userId = userIds.split(",");
        startTime = startTime + " 00:00:00";
        endTime = endTime + " 23:59:59";
        List<GroupRiskVo> list = reportMapper.SectorRiskFactor(userId, startTime, endTime);
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

    @Override
    public List ProjectRiskTableById(String projectIds) {
        List<String> projectId = new ArrayList<>();
        if ("".equals(projectIds) || projectIds == null) {
            projectId = reportMapper.selectAllId();
        } else {
            String[] project = projectIds.split(",");
            for (int i = 0; i < project.length; i++) {
                projectId.add(project[i]);
            }
        }
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (String id : projectId) {
            Map<String, Object> map = (Map<String, Object>) redisTemplate.opsForValue().get(id);
            list.add(map);
        }
        return list;
    }

    @Override
    public List ProjectRiskTable(String projectIds) throws ParseException {
        String[] projectId = projectIds.split(",");
        List result = new ArrayList();
        Date Date = new Date();
        for (String id : projectId) {
            List<ProjectRiskVo> list = reportMapper.ProjectRiskTable(id);
            // 计算时间周数
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            DecimalFormat df = new DecimalFormat("0.00");// 设置保留位数
            String project = "";
            Map<Object, Object> map = new HashMap();
            double OutputQualityRiskSum = 0;
            if (list.size() == 0) {
                continue;
            }
            for (ProjectRiskVo projectRiskVo : list) {
                if ("".equals(project)) {
                    project = projectRiskVo.getProjectId();
                    // 开始时间
                    long startTime = projectRiskVo.getStartTime() == "" ? 0 : formatter.parse(projectRiskVo.getStartTime()).getTime();
                    //项目启动时间和当前日期差=当前时间 - 项目开始时间
                    long startDate = Date.getTime() - startTime;
                    //项目周期 = 项目计划最早开始时间 - 项目计划最晚完成时间
                    PiplanActivityVo piplanActivityVo = reportMapper.projectWeekCycle(id);
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
    public List groupUser(String projectId) {
        List result = new ArrayList();
        List<UserVo> list = reportMapper.groupUser(projectId);
        Map map = new HashMap();
        for (UserVo userVo : list) {
            if (userVo.getGroupId().equals(map.get("groupId"))) {
                continue;
            }
            map = new HashMap();
            map.put("groupId", userVo.getGroupId());
            map.put("groupName", userVo.getGroupName());
            List userList = new ArrayList();
            for (UserVo user : list) {
                if (userVo.getGroupId().equals(map.get("groupId"))) {
                    Map userMap = new HashMap();
                    userMap.put("userName", userVo.getUserName());
                    userMap.put("userId", userVo.getUserId());
                    userList.add(userMap);
                }
            }
            map.put("user", userList);
            result.add(map);
        }
        return result;
    }

    @Override
    public List taskExecution1(String projectId, String userIds) {
        String[] userId = null;
        if (!"".equals(userIds) && userIds != null) {
            userId = userIds.split(",");
        }
        //查询所有的任务id
        List<String> idList = reportMapper.selectAllActId(projectId, userId);
        String planId = "";

        List result = new ArrayList();
        for (String id : idList) {
            DecimalFormat df = new DecimalFormat("0.00");// 设置保留位数
            //查询任务下in,ot的值
            List<TaskVo> list = reportMapper.selectINOTById(id);
            if (list == null || list.size() == 0) {
                continue;
            }
            //每个任务都作为数组存储
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
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
            Map<String, Object> totalMap = new HashMap<String, Object>();
            for (int j = 0; j < list.size(); j++) {
                TaskVo task = list.get(j);
                Map<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("taskname", task.getPlanName());
                resultMap.put("executive", task.getExecutive());
                resultMap.put("任务id", task.getActiviteId());
                //标准工期
                StandardPeriod = Double.parseDouble(task.getStandardWork() == null ? "0" : task.getStandardWork());
                resultMap.put("StandardPeriod", StandardPeriod);
                //项目工期
                reportPeriod = Double.parseDouble(task.getReportWork() == null ? "0" : task.getReportWork());
                resultMap.put("reportPeriod", reportPeriod);
                //in标准偏差
                Double in_standardDeviationValue = Double.parseDouble(task.getIn_standardDeviationValue() == null ?
                        "0" : task.getIn_standardDeviationValue());
                resultMap.put("in_standardDeviationValue", task.getIn_standardDeviationValue());
                // 汇报偏差
                Double in_deviationReport = Double.parseDouble(task.getIn_deviationReport() == null ?
                        "0" : task.getIn_deviationReport());
                resultMap.put("in_deviationReport", in_deviationReport);

                // 权重
                Double weight = Double.parseDouble(task.getWeights() == null ?
                        "0" : task.getWeights());
                weightSum += weight;
                resultMap.put("weight", task.getWeights());
                // 计算得出质量影响因子
                String qualityInfluenceFactor = df.format(weight * (in_deviationReport / (in_standardDeviationValue == 0 ? 1 : in_standardDeviationValue)));
                qualityInfluenceSum += Double.parseDouble(qualityInfluenceFactor);
                resultMap.put("qualityInfluenceFactor", qualityInfluenceFactor);
                //标准偏差
                Double standardDeviation = Double.parseDouble(task.getStandardDeviationValue() == null ?
                        "0" : task.getStandardDeviationValue());

                resultMap.put("standardDeviation", task.getStandardDeviationValue());
                // 汇报偏差
                Double reportingDeviations = Double.parseDouble(task.getDeviationReport() == null ?
                        "0" : task.getDeviationReport());
                resultMap.put("reportingDeviations", task.getDeviationReport());
                // 汇报困难度
                double reportingDifficulty = Double.parseDouble(task.getDifficultyReport() == null ?
                        "0" : task.getDifficultyReport());
                resultMap.put("reportingDifficulty", task.getDifficultyReport());
                // 标准困难度
                double standardDifficulty = Double.parseDouble(task.getStandardDifficultyValue() == null ?
                        "0" : task.getStandardDifficultyValue());
                resultMap.put("standardDifficulty", task.getStandardDifficultyValue());
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
                double span = Double.parseDouble(task.getBreadth() == null ?
                        "0" : task.getBreadth());
                resultMap.put("span", task.getBreadth());
                // 关键度
                double Criticality = Double.parseDouble(task.getCriticality() == null ?
                        "0" : task.getCriticality());
                resultMap.put("criticality", task.getCriticality());
                //输出评定
                String OutputEvalua = task.getOutput() == null ? "" : task.getOutput();
                resultMap.put("OutputEvalua", OutputEvalua);
                // 发布次数
                double Numbereleases = Double.parseDouble(task.getReleases() == null ?
                        "0" : task.getReleases());
                NumbereleasesAvg += Numbereleases;
                resultMap.put("Numbereleases", task.getReleases());
                // 计算项目风险指标
                String ProjectRiskIndicators = df.format((Double.parseDouble(OutputQualityRisk) - 1) * span * Criticality);
                resultMap.put("ProjectRiskIndicators", ProjectRiskIndicators);
                resultMap.put("Criticality", Criticality);
                resultMap.put("code", task.getCode());
                resultMap.put("reportTime", task.getReport_time());
                resultMap.put("description", task.getDescription());
                //in指标编码
                resultMap.put("in_code", task.getIn_code());
                resultMap.put("in_description", task.getIn_description());
                resultList.add(resultMap);
                totalMap.put("taskname", task.getPlanName());
                totalMap.put("Executive", task.getExecutive());
                totalMap.put("taskid", task.getActiviteId());
                k++;
            }

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
            result.add(resultList);

        }
        return result;

    }

    @Override
    public List taskExecution(String projectId, String userIds) {
        String[] userId = null;
        if (!"".equals(userIds) && userIds != null) {
            userId = userIds.split(",");
        }
        //查询所有的任务id
        List<String> idList = reportMapper.selectAllActId(projectId, userId);

        ExecutorService executor = Executors.newFixedThreadPool(idList.size());
        List<Future<List>> futures = new ArrayList<>();
        for (String TaskId : idList) {
            Future<List> submit = executor.submit(() -> {
                return getToDo(reportMapper, TaskId);
            });
            futures.add(submit);
        }

//        List<CompletableFuture<List>> futures = idList.stream().map(s -> CompletableFuture.supplyAsync(() -> {
//            return     getToDo(reportMapper,s);
//        },executor)).collect(Collectors.toList());
//
        List List = new ArrayList();
////                futures.stream()
        for (Future completableFuture : futures) {
            Object join = null;
            try {
                join = completableFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            List.add(join);
        }
        executor.shutdown();
        return List;


    }

    private List getToDo(ReportMapper reportMapper1, String id) {
        DecimalFormat df = new DecimalFormat("0.00");// 设置保留位数
        //查询任务下in,ot的值
        List<TaskVo> list = reportMapper1.selectINOTById(id);
        if (list == null || list.size() == 0) {
            return null;
        }
        //每个任务都作为数组存储
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
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
        Map<String, Object> totalMap = new HashMap<String, Object>();
        for (int j = 0; j < list.size(); j++) {
            TaskVo task = list.get(j);
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("taskname", task.getPlanName());
            resultMap.put("executive", task.getExecutive());
            resultMap.put("任务id", task.getActiviteId());
            //标准工期
            StandardPeriod = Double.parseDouble(task.getStandardWork() == null ? "0" : task.getStandardWork());
            resultMap.put("StandardPeriod", StandardPeriod);
            //项目工期
            reportPeriod = Double.parseDouble(task.getReportWork() == null ? "0" : task.getReportWork());
            resultMap.put("reportPeriod", reportPeriod);
            //in标准偏差
            Double in_standardDeviationValue = Double.parseDouble(task.getIn_standardDeviationValue() == null ?
                    "0" : task.getIn_standardDeviationValue());
            resultMap.put("in_standardDeviationValue", task.getIn_standardDeviationValue());
            // 汇报偏差
            Double in_deviationReport = Double.parseDouble(task.getIn_deviationReport() == null ?
                    "0" : task.getIn_deviationReport());
            resultMap.put("in_deviationReport", in_deviationReport);

            // 权重
            Double weight = Double.parseDouble(task.getWeights() == null ?
                    "0" : task.getWeights());
            weightSum += weight;
            resultMap.put("weight", task.getWeights());
            // 计算得出质量影响因子
            String qualityInfluenceFactor = df.format(weight * (in_deviationReport / (in_standardDeviationValue == 0 ? 1 : in_standardDeviationValue)));
            qualityInfluenceSum += Double.parseDouble(qualityInfluenceFactor);
            resultMap.put("qualityInfluenceFactor", qualityInfluenceFactor);
            //标准偏差
            Double standardDeviation = Double.parseDouble(task.getStandardDeviationValue() == null ?
                    "0" : task.getStandardDeviationValue());

            resultMap.put("standardDeviation", task.getStandardDeviationValue());
            // 汇报偏差
            Double reportingDeviations = Double.parseDouble(task.getDeviationReport() == null ?
                    "0" : task.getDeviationReport());
            resultMap.put("reportingDeviations", task.getDeviationReport());
            // 汇报困难度
            double reportingDifficulty = Double.parseDouble(task.getDifficultyReport() == null ?
                    "0" : task.getDifficultyReport());
            resultMap.put("reportingDifficulty", task.getDifficultyReport());
            // 标准困难度
            double standardDifficulty = Double.parseDouble(task.getStandardDifficultyValue() == null ?
                    "0" : task.getStandardDifficultyValue());
            resultMap.put("standardDifficulty", task.getStandardDifficultyValue());
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
            double span = Double.parseDouble(task.getBreadth() == null ?
                    "0" : task.getBreadth());
            resultMap.put("span", task.getBreadth());
            // 关键度
            double Criticality = Double.parseDouble(task.getCriticality() == null ?
                    "0" : task.getCriticality());
            resultMap.put("criticality", task.getCriticality());
            //输出评定
            String OutputEvalua = task.getOutput() == null ? "" : task.getOutput();
            resultMap.put("OutputEvalua", OutputEvalua);
            // 发布次数
            double Numbereleases = Double.parseDouble(task.getReleases() == null ?
                    "0" : task.getReleases());
            NumbereleasesAvg += Numbereleases;
            resultMap.put("Numbereleases", task.getReleases());
            // 计算项目风险指标
            String ProjectRiskIndicators = df.format((Double.parseDouble(OutputQualityRisk) - 1) * span * Criticality);
            resultMap.put("ProjectRiskIndicators", ProjectRiskIndicators);
            resultMap.put("Criticality", Criticality);
            resultMap.put("code", task.getCode());
            resultMap.put("reportTime", task.getReport_time());
            resultMap.put("description", task.getDescription());
            //in指标编码
            resultMap.put("in_code", task.getIn_code());
            resultMap.put("in_description", task.getIn_description());
            resultList.add(resultMap);
            totalMap.put("taskname", task.getPlanName());
            totalMap.put("Executive", task.getExecutive());
            totalMap.put("taskid", task.getActiviteId());
            k++;
        }

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
        return resultList;

    }


    @Override
    public void exportTaskTable(HttpServletResponse response, String fileName, String projectId, String userIds) throws IOException {
        String[] userId = null;
        if (!"".equals(userIds) && userIds != null) {
            userId = userIds.split(",");
        }
        //查询所有的任务id
//        List<TaskVo> list = reportMapper.taskExecution(projectId, userId);
        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet0");
        //设置默认行宽
        sheet.setDefaultColumnWidth(20);
        // 加载单元格样式
        HSSFCellStyle style = wb.createCellStyle();
//        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平居中
//        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
        //创建合并单元格
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 2, 7);// 起始行号，结束行号，起始列号，结束列号
        CellRangeAddress cellRangeAddress1 = new CellRangeAddress(0, 0, 8, 10);// 起始行号，结束行号，起始列号，结束列号
        CellRangeAddress cellRangeAddress2 = new CellRangeAddress(0, 0, 11, 18);// 起始行号，结束行号，起始列号，结束列号
        CellRangeAddress cellRangeAddress3 = new CellRangeAddress(0, 0, 19, 20);// 起始行号，结束行号，起始列号，结束列号

        sheet.addMergedRegion(cellRangeAddress);
        sheet.addMergedRegion(cellRangeAddress1);
        sheet.addMergedRegion(cellRangeAddress2);
        sheet.addMergedRegion(cellRangeAddress3);

        //设置第一行表头的值单元格的值
        HSSFRow row = sheet.createRow(0);
        //创建HSSFCell对象
        HSSFCell cell = row.createCell(0);
        //设置单元格的值
        cell.setCellValue("执行职能");
        cell = row.createCell(1);
        cell.setCellValue("任务名称");
        cell = row.createCell(2);
        cell.setCellValue("数据准备");
        cell.setCellStyle(style);
        cell = row.createCell(8);
        cell.setCellStyle(style);
        cell.setCellValue("计划环境");
        cell = row.createCell(11);
        cell.setCellStyle(style);
        cell.setCellValue("输出质量");
        cell = row.createCell(19);
        cell.setCellStyle(style);
        cell.setCellValue("输出加权");
        cell = row.createCell(21);
        cell.setCellValue("项目风险");
        cell = row.createCell(22);
        cell.setCellValue("输出评定");
        cell = row.createCell(23);
        cell.setCellValue("发布次数");

        //设置第二行表头的值单元格的值
        row = sheet.createRow(1);
        cell = row.createCell(2);
        cell.setCellValue("指标编码");
        cell = row.createCell(3);
        cell.setCellValue("指标名称");
        cell = row.createCell(4);
        cell.setCellValue("权重");
        cell = row.createCell(5);
        cell.setCellValue("标准偏差");
        cell = row.createCell(6);
        cell.setCellValue("汇报偏差");
        cell = row.createCell(7);
        cell.setCellValue("质量影响因子");
        cell = row.createCell(8);
        cell.setCellValue("标准工期");
        cell = row.createCell(9);
        cell.setCellValue("项目工期");
        cell = row.createCell(10);
        cell.setCellValue("风险KPI");
        cell = row.createCell(11);
        cell.setCellValue("指标code");
        cell = row.createCell(12);
        cell.setCellValue("指标名称");
        cell = row.createCell(13);
        cell.setCellValue("标准偏差");
        cell = row.createCell(14);
        cell.setCellValue("汇报偏差");
        cell = row.createCell(15);
        cell.setCellValue("标准困难度");
        cell = row.createCell(16);
        cell.setCellValue("汇报困难度");
        cell = row.createCell(17);
        cell.setCellValue("输出质量KPI");
        cell = row.createCell(18);
        cell.setCellValue("质量风险");
        cell = row.createCell(19);
        cell.setCellValue("广度");
        cell = row.createCell(16);
        cell.setCellValue("关键度");
        //环境质量指标：qualityIndex
        cell = row.createCell(24);
        cell.setCellValue("环境质量指标");
        //工期风险KPI:riskKPI
        cell = row.createCell(25);
        cell.setCellValue("工期风险KPI");
        //总质量指标：totalQualityKpi
        cell = row.createCell(26);
        cell.setCellValue("总质量指标");
        //平均发布次数：NumbereleasesAvg
        cell = row.createCell(27);
        cell.setCellValue("平均发布次数");
        //excel写入数据
        DecimalFormat df = new DecimalFormat("0.00");// 设置保留位数
        //查询所有的任务id
        List<String> idList = reportMapper.selectAllActId(projectId, userId);
        List result = new ArrayList();
        //kpi风险系数之和
        double OutputaualityKPISum = 0;
        //权重之和
        double weightSum = 0;
        //质量影响因子之和
        double qualityInfluenceSum = 0;
        //标准工期
        double StandardPeriod = 0;
        //项目工期
        double reportPeriod = 0;
        //平均发布次数
        double NumbereleasesAvg = 0;
        //标记excel行数
        int k = 2;
        //标记任务条数
        int i = 1;
        for (String id : idList) {
            //查询任务下in,ot的值
            List<TaskVo> list = reportMapper.selectINOTById(id);
            if (list == null || list.size() == 0) {
                continue;
            }
            for (TaskVo taskVo : list) {
                row = sheet.createRow(k);
                cell = row.createCell(0);
                cell.setCellValue("研发");
                cell = row.createCell(1);
                String name = taskVo.getPlanName() == null ? taskVo.getInPlanName() : taskVo.getPlanName();
                cell.setCellValue(name);
                //指标编码
                cell = row.createCell(2);
                cell.setCellValue(taskVo.getIn_code());
                //指标名称
                cell = row.createCell(3);
                cell.setCellValue(taskVo.getIn_description());
                // 权重
                Double weight = Double.parseDouble(taskVo.getWeights() == null ?
                        "0" : taskVo.getWeights());
                cell = row.createCell(4);
                cell.setCellValue(weight);
                weightSum += weight;
                //标准偏差
                Double in_standardDeviationValue = Double.parseDouble(taskVo.getIn_standardDeviationValue() == null ?
                        "0" : taskVo.getIn_standardDeviationValue());
                cell = row.createCell(5);
                cell.setCellValue(taskVo.getIn_standardDeviationValue());
                // 汇报偏差
                Double in_deviationReport = Double.parseDouble(taskVo.getIn_deviationReport() == null ?
                        "0" : taskVo.getIn_deviationReport());
                cell = row.createCell(6);
                cell.setCellValue(taskVo.getIn_deviationReport());

                //质量影响因子
                String qualityInfluenceFactor = df.format(weight * (in_deviationReport / (in_standardDeviationValue == 0 ? 1 : in_standardDeviationValue)));
                qualityInfluenceSum += Double.parseDouble(qualityInfluenceFactor);
                cell = row.createCell(7);
                cell.setCellValue(qualityInfluenceFactor);
                //标准工期
                StandardPeriod = taskVo.getStandardWork() == null ? 0 : Double.parseDouble(taskVo.getStandardWork());
                cell = row.createCell(8);
                cell.setCellValue(StandardPeriod);
                //项目工期
                reportPeriod = taskVo.getReportWork() == null ? 0 : Double.parseDouble(taskVo.getReportWork());
                cell = row.createCell(9);
                cell.setCellValue(reportPeriod);
                //风险KPI
                cell = row.createCell(10);
                cell.setCellValue(df.format(reportPeriod / (StandardPeriod == 0 ? 1 : StandardPeriod)));
                //指标编码
                cell = row.createCell(11);
                cell.setCellValue(taskVo.getCode());
                //指标名称
                cell = row.createCell(12);
                cell.setCellValue(taskVo.getDescription());
                //标准偏差
                Double standardDeviationValue = Double.parseDouble(taskVo.getStandardDeviationValue() == null ?
                        "0" : taskVo.getStandardDeviationValue());
                cell = row.createCell(13);
                cell.setCellValue(taskVo.getStandardDeviationValue());
                //汇报偏差
                Double deviationReport = Double.parseDouble(taskVo.getDeviationReport() == null ?
                        "0" : taskVo.getDeviationReport());
                cell = row.createCell(14);
                cell.setCellValue(taskVo.getDeviationReport());
                // 标准困难度
                double standardDifficulty = Double.parseDouble(taskVo.getStandardDifficultyValue() == null ?
                        "0" : taskVo.getStandardDifficultyValue());
                cell = row.createCell(15);
                cell.setCellValue(taskVo.getStandardDifficultyValue());
                //汇报困难度
                double reportingDifficulty = Double.parseDouble(taskVo.getDifficultyReport() == null ?
                        "0" : taskVo.getDifficultyReport());
                cell = row.createCell(16);
                cell.setCellValue(taskVo.getDifficultyReport());
                // 计算得出输出质量KPI
                String OutputaualityKPI = df
                        .format((deviationReport / (standardDeviationValue == 0 ? 1 : standardDeviationValue)) *
                                (reportingDifficulty / (standardDifficulty == 0 ? 1 : standardDifficulty)));
                //输出质量KPI
                cell = row.createCell(17);
                cell.setCellValue(reportPeriod / (StandardPeriod == 0 ? 1 : StandardPeriod));
                OutputaualityKPISum += Double.parseDouble(OutputaualityKPI);
                // 计算输出质量风险
                String OutputQualityRisk = df.format(((abs(deviationReport - standardDeviationValue)) * 10 + 1)
                        * ((abs(reportingDifficulty - standardDifficulty)) * 10 + 1));
                cell = row.createCell(18);
                cell.setCellValue(OutputQualityRisk);
                //广度
                double span = Double.parseDouble(taskVo.getBreadth() == null ?
                        "0" : taskVo.getBreadth());
                cell = row.createCell(19);
                cell.setCellValue(taskVo.getBreadth());
                //关键度
                double Criticality = Double.parseDouble(taskVo.getCriticality() == null ?
                        "0" : taskVo.getCriticality());
                cell = row.createCell(20);
                cell.setCellValue(taskVo.getCriticality());
                //项目风险
                String ProjectRiskIndicators = df.format((Double.parseDouble(OutputQualityRisk) - 1) * span * Criticality);
                cell = row.createCell(21);
                cell.setCellValue(ProjectRiskIndicators);
                //输出评定
                String OutputEvalua = taskVo.getOutput() == null ? "" : taskVo.getOutput();
                cell = row.createCell(22);
                cell.setCellValue(taskVo.getOutput());
                //发布次数
                double Numbereleases = Double.parseDouble(taskVo.getReleases() == null ?
                        "0" : taskVo.getReleases());
                NumbereleasesAvg += Numbereleases;
                cell = row.createCell(23);
                cell.setCellValue(taskVo.getReleases());
                if (i == list.size()) {
                    //环境质量指标：qualityIndex
                    cell = row.createCell(24);
                    String qualityIndex = df.format(qualityInfluenceSum / i);
                    cell.setCellValue(qualityIndex);
                    //工期风险KPI:riskKPI
                    cell = row.createCell(25);
                    String riskKPI = df.format(reportPeriod / (StandardPeriod == 0 ? 1 : StandardPeriod));
                    cell.setCellValue(riskKPI);
                    StandardPeriod = 0;
                    //项目工期
                    reportPeriod = 0;
                    //总质量指标：totalQualityKpi
                    cell = row.createCell(26);
                    String totalQualityKpi = df.format(OutputaualityKPISum / i);
                    cell.setCellValue(totalQualityKpi);
                    //平均发布次数：NumbereleasesAvg
                    cell = row.createCell(27);
                    String NumbereleasesAvgs = df.format(NumbereleasesAvg / i);
                    cell.setCellValue(NumbereleasesAvgs);
                    i = 0;
                }
                i++;
                k++;
            }
        }


        //导出数据
        try {
            //设置Http响应头告诉浏览器下载这个附件
            if (fileName == null || "".equals(fileName)) {
                fileName = "任务执行报表";
            }
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
//            System.out.println(df.format(new Date()));// new Date()为获取当前系统时间+ df.format(new Date()) +
            fileName = new String(fileName.getBytes("gb2312"), "ISO8859-1");
            response.setContentType("application/x-download;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + df1.format(new Date()) + ".xls");
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("导出Excel出现严重异常，异常信息：" + ex.getMessage());
        }
    }

    @Override
    public List taskExecutionById(String projectId, String userIds) {
        //通过项目id和userId找到所有的任务
        String[] userId = null;
        if (!"".equals(userIds) && userIds != null) {
            userId = userIds.split(",");
        }
        List<String> actIds = reportMapper.selectAllActId(projectId, userId);
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(
//                10, 20, 0, TimeUnit.SECONDS,
//                new ArrayBlockingQueue<Runnable>(100), new ThreadPoolExecutor.CallerRunsPolicy());
        List list = new ArrayList();
        for (String actId : actIds) {
//            Future<List> submit = executor.submit(new Callable<List>() {
//                @Override
//                public List call() throws Exception {
//                    return redisGet(projectId, actId);
//                }
//            });
            Object join = redisGet(projectId, actId);
//            try {
//                join = submit.get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
            if (join != null) {
                list.add(join);
            }
        }
        System.out.println(list.size());

//        executor.shutdown();
        return list;
    }

    private List redisGet(String projectId, String actId) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) redisTemplate.opsForValue().get(projectId + actId);
        return list;
    }

    @Override
    public void exportTaskTableById(HttpServletResponse response, String fileName, String projectId, String userIds) throws IOException {

        String[] userId = null;
        if (!"".equals(userIds) && userIds != null) {
            userId = userIds.split(",");
        }
        //查询所有的任务id
        List<String> actIds = reportMapper.selectAllActId(projectId, userId);
        ExecutorService executor = Executors.newFixedThreadPool(actIds.size());
        List<Future<List>> futures = new ArrayList<>();
        for (String actId : actIds) {
            Future<List> submit = executor.submit(() -> {
                return redisGet(projectId, actId);
            });
            futures.add(submit);
        }
        List list = new ArrayList();
        for (Future completableFuture : futures) {
            Object join = null;
            try {
                join = completableFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (join != null) {
                list.add(join);
            }

        }
        executor.shutdown();
        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet0");
        //设置默认行宽
        sheet.setDefaultColumnWidth(20);
        // 加载单元格样式
        HSSFCellStyle style = wb.createCellStyle();
        //创建合并单元格
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 2, 7);// 起始行号，结束行号，起始列号，结束列号
        CellRangeAddress cellRangeAddress1 = new CellRangeAddress(0, 0, 8, 10);// 起始行号，结束行号，起始列号，结束列号
        CellRangeAddress cellRangeAddress2 = new CellRangeAddress(0, 0, 11, 18);// 起始行号，结束行号，起始列号，结束列号
        CellRangeAddress cellRangeAddress3 = new CellRangeAddress(0, 0, 19, 20);// 起始行号，结束行号，起始列号，结束列号

        sheet.addMergedRegion(cellRangeAddress);
        sheet.addMergedRegion(cellRangeAddress1);
        sheet.addMergedRegion(cellRangeAddress2);
        sheet.addMergedRegion(cellRangeAddress3);

        //设置第一行表头的值单元格的值
        HSSFRow row = sheet.createRow(0);
        //创建HSSFCell对象
        HSSFCell cell = row.createCell(0);
        //设置单元格的值
        cell.setCellValue("执行职能");
        cell = row.createCell(1);
        cell.setCellValue("任务名称");
        cell = row.createCell(2);
        cell.setCellValue("数据准备");
        cell.setCellStyle(style);
        cell = row.createCell(8);
        cell.setCellStyle(style);
        cell.setCellValue("计划环境");
        cell = row.createCell(11);
        cell.setCellStyle(style);
        cell.setCellValue("输出质量");
        cell = row.createCell(19);
        cell.setCellStyle(style);
        cell.setCellValue("输出加权");
        cell = row.createCell(21);
        cell.setCellValue("项目风险");
        cell = row.createCell(22);
        cell.setCellValue("输出评定");
        cell = row.createCell(23);
        cell.setCellValue("发布次数");

        //设置第二行表头的值单元格的值
        row = sheet.createRow(1);
        cell = row.createCell(2);
        cell.setCellValue("指标编码");
        cell = row.createCell(3);
        cell.setCellValue("指标名称");
        cell = row.createCell(4);
        cell.setCellValue("权重");
        cell = row.createCell(5);
        cell.setCellValue("标准偏差");
        cell = row.createCell(6);
        cell.setCellValue("汇报偏差");
        cell = row.createCell(7);
        cell.setCellValue("质量影响因子");
        cell = row.createCell(8);
        cell.setCellValue("标准工期");
        cell = row.createCell(9);
        cell.setCellValue("项目工期");
        cell = row.createCell(10);
        cell.setCellValue("风险KPI");
        cell = row.createCell(11);
        cell.setCellValue("指标code");
        cell = row.createCell(12);
        cell.setCellValue("指标名称");
        cell = row.createCell(13);
        cell.setCellValue("标准偏差");
        cell = row.createCell(14);
        cell.setCellValue("汇报偏差");
        cell = row.createCell(15);
        cell.setCellValue("标准困难度");
        cell = row.createCell(16);
        cell.setCellValue("汇报困难度");
        cell = row.createCell(17);
        cell.setCellValue("输出质量KPI");
        cell = row.createCell(18);
        cell.setCellValue("质量风险");
        cell = row.createCell(19);
        cell.setCellValue("广度");
        cell = row.createCell(16);
        cell.setCellValue("关键度");
        //环境质量指标：qualityIndex
        cell = row.createCell(24);
        cell.setCellValue("环境质量指标");
        //工期风险KPI:riskKPI
        cell = row.createCell(25);
        cell.setCellValue("工期风险KPI");
        //总质量指标：totalQualityKpi
        cell = row.createCell(26);
        cell.setCellValue("总质量指标");
        //平均发布次数：NumbereleasesAvg
        cell = row.createCell(27);
        cell.setCellValue("平均发布次数");
        //excel写入数据
        DecimalFormat df = new DecimalFormat("0.00");// 设置保留位数
        //查询所有的任务id
        //标记excel行数
        int k = 2;
        for (Object task : list) {
            List<Map<String, Object>> result = (List<Map<String, Object>>) task;
            //标记任务条数
            int i = 1;
            //查询任务下in,ot的值
            for (Map map : result) {
                if (map.size() == 7) {
                    //环境质量指标：qualityIndex
                    System.out.println(map.get("qualityIndex"));
                    cell = row.createCell(24);
                    cell.setCellValue(map.get("qualityIndex").toString());
                    //工期风险KPI:riskKPI
                    cell = row.createCell(25);
                    cell.setCellValue(map.get("riskKPI").toString());
                    //总质量指标：totalQualityKpi
                    cell = row.createCell(26);
                    cell.setCellValue(map.get("totalQualityKpi").toString());
                    //平均发布次数：NumbereleasesAvg
                    cell = row.createCell(27);
                    cell.setCellValue(map.get("NumbereleasesAvg").toString());
                    continue;
                }
                row = sheet.createRow(k);
                cell = row.createCell(0);
                cell.setCellValue("研发");
                cell = row.createCell(1);
                String name = (String) map.get("taskname");
                cell.setCellValue(name);
                //指标编码
                String inCode = map.get("in_code") == null ? "" : (String) map.get("in_code");
                cell = row.createCell(2);
                cell.setCellValue(inCode);
                //指标名称in_description
                String InDescription = map.get("in_description") == null ? "" : (String) map.get("in_description");
                cell = row.createCell(3);
                cell.setCellValue(InDescription);
                // 权重
                Double weight = Double.parseDouble(map.get("weight") == null ?
                        "0" : map.get("weight").toString());
                cell = row.createCell(4);
                cell.setCellValue(weight);
                //标准偏差
                Double in_standardDeviationValue = Double.parseDouble(map.get("in_standardDeviationValue") == null ?
                        "0" : map.get("in_standardDeviationValue").toString());
                cell = row.createCell(5);
                cell.setCellValue(in_standardDeviationValue);
                // 汇报偏差
                Double in_deviationReport = Double.parseDouble(map.get("in_deviationReport") == null ?
                        "0" : map.get("in_deviationReport").toString());
                cell = row.createCell(6);
                cell.setCellValue(in_deviationReport);

                //质量影响因子
                String qualityInfluenceFactor = df.format(weight * (in_deviationReport / (in_standardDeviationValue == 0 ? 1 : in_standardDeviationValue)));

                cell = row.createCell(7);
                cell.setCellValue(qualityInfluenceFactor);
                //标准工期
                Double StandardPeriod = map.get("StandardPeriod") == null ? 0 : Double.parseDouble(map.get("StandardPeriod").toString());
                cell = row.createCell(8);
                cell.setCellValue(StandardPeriod);
                //项目工期
                Double reportPeriod = map.get("reportPeriod") == null ? 0 : Double.parseDouble(map.get("reportPeriod").toString());
                cell = row.createCell(9);
                cell.setCellValue(reportPeriod);
                //风险KPI
                cell = row.createCell(10);
                cell.setCellValue(df.format(reportPeriod / (StandardPeriod == 0 ? 1 : StandardPeriod)));
                //指标编码
                String code = map.get("code") == null ? null : map.get("code").toString();
                cell = row.createCell(11);
                cell.setCellValue(code);
                //指标名称
                String description = map.get("description") == null ? null : map.get("description").toString();
                cell = row.createCell(12);
                cell.setCellValue(description);
                //标准偏差
                Double standardDeviationValue = Double.parseDouble(map.get("standardDeviation") == null ?
                        "0" : map.get("standardDeviation").toString());
                cell = row.createCell(13);
                cell.setCellValue(standardDeviationValue);
                //汇报偏差
                Double deviationReport = Double.parseDouble(map.get("reportingDeviations") == null ?
                        "0" : map.get("reportingDeviations").toString());
                cell = row.createCell(14);
                cell.setCellValue(deviationReport);
                // 标准困难度
                double standardDifficulty = Double.parseDouble(map.get("standardDifficulty") == null ?
                        "0" : map.get("standardDifficulty").toString());
                cell = row.createCell(15);
                cell.setCellValue(standardDifficulty);
                //汇报困难度
                double reportingDifficulty = Double.parseDouble(map.get("reportingDifficulty") == null ?
                        "0" : map.get("reportingDifficulty").toString());
                cell = row.createCell(16);
                cell.setCellValue(reportingDifficulty);
                // 计算得出输出质量KPI
                String OutputaualityKPI = map.get("OutputaualityKPI") == null ? null : map.get("OutputaualityKPI").toString();
                //输出质量KPI
                cell = row.createCell(17);
                cell.setCellValue(OutputaualityKPI);
                // 计算输出质量风险
                String OutputQualityRisk = map.get("OutputQualityRisk") == null ? null : map.get("OutputQualityRisk").toString();
                cell = row.createCell(18);
                cell.setCellValue(OutputQualityRisk);
                //广度
                double span = Double.parseDouble(map.get("span") == null ?
                        "0" : map.get("span").toString());
                cell = row.createCell(19);
                cell.setCellValue(span);
                //关键度
                double Criticality = Double.parseDouble(map.get("criticality") == null ?
                        "0" : map.get("criticality").toString());
                cell = row.createCell(20);
                cell.setCellValue(Criticality);
                //项目风险
                String ProjectRiskIndicators = map.get("ProjectRiskIndicators") == null ? null : map.get("ProjectRiskIndicators").toString();
                cell = row.createCell(21);
                cell.setCellValue(ProjectRiskIndicators);
                //输出评定
                String OutputEvalua = map.get("OutputEvalua") == null ? "" : map.get("OutputEvalua").toString();
                cell = row.createCell(22);
                cell.setCellValue(OutputEvalua);
                //发布次数
                double Numbereleases = Double.parseDouble(map.get("Numbereleases") == null ?
                        "0" : map.get("Numbereleases").toString());
                cell = row.createCell(23);
                cell.setCellValue(Numbereleases);
                k++;
            }
        }
        //导出数据
        try {
            //设置Http响应头告诉浏览器下载这个附件
            if (fileName == null || "".equals(fileName)) {
                fileName = "任务执行报表";
            }
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
//            System.out.println(df.format(new Date()));// new Date()为获取当前系统时间+ df.format(new Date()) +
            fileName = new String(fileName.getBytes("gb2312"), "ISO8859-1");
            response.setContentType("application/x-download;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + df1.format(new Date()) + ".xls");
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("导出Excel出现严重异常，异常信息：" + ex.getMessage());
        }
    }


    // 计算绝对值
    public double abs(double a) {
        return (a > 0) ? a : -a;
    }

}
