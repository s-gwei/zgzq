package org.jeecg.modules.P2020052.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.P2020052.service.HttpClient;
import org.jeecg.modules.P2020052.service.ZQService;
import org.jeecg.modules.P2020052.util.Result;
import org.jeecg.modules.P2020052.util.TaskExecutionTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/HeavyDuty")
@Slf4j
@Api(tags = "重汽报表")
public class ZQController {


    @Autowired
    TaskExecutionTable taskExecutionTable;

    @Autowired
    HttpClient httpClient;


    @Autowired
    ZQService ZQService;

    @Value("${ProjectRiskUrl}")
    private String ProjectRiskUrl;

    @Value("${SectorRiskUrl}")
    private String SectorRiskUrl;

    @Value("${pertUrl}")
    private String pertUrl;


    @Value("${TaskUrl}")
    private String TaskUrl;

    @Value("${ProjectUrl}")
    private String ProjectUrl;


    @GetMapping(value = "exportExcel")
    public void exportTable(HttpServletResponse response, String planId, String ResourceSector, String name, String fileName) throws IOException {
        // 调用spd接口查询某个项目计划下数据
        String url = ProjectUrl + "?planId=" + planId;
        HttpMethod httpMethod = HttpMethod.GET;
        Map<String, Object> params = new HashMap<>();
        List<Map<String, Object>> result = httpClient.client(url, httpMethod, params);
        ZQService.exportTable(result, response, fileName);
    }

    @GetMapping(value = "/hello")
    public String hello() {
        // 请求地址
        System.out.println("hello");
        return "hello world";
    }

    @GetMapping(value = "TaskExecutionTable")
    @ApiOperation("任务执行报表")
    public Result TaskExecutionTable(String projectId, String userIds) {
        // 调用spd接口查询某个项目计划下数据
        String[] userIdList = userIds.split(",");
        HttpMethod httpMethod = HttpMethod.GET;
        Map<String, Object> params = new HashMap<>();
        List list = new ArrayList();
        for (String userId : userIdList) {
            String url = ProjectUrl + "?projectId=" + projectId + "&userId=" + userId;
            List<Map<String, Object>> result = httpClient.client(url, httpMethod, params);
            List listResult = ZQService.TaskExecutionTable(result);
            list.add(listResult);
        }
        return Result.ok(list);
    }

    @GetMapping(value = "/ProjectRiskCoefficient")
    @ApiOperation(value = "项目风险系数表")
    public Result ProjectRiskCoefficient(String projectIds) {
        System.out.println(projectIds);
        String[] projectId = projectIds.split(",");
        HttpMethod httpMethod = HttpMethod.GET;
        Map<String, Object> params = new HashMap<>();
        // 多个项目组id，循环查询项目组下数据
        List list = new ArrayList();
        for (int i = 0; i < projectId.length; i++) {
            String url = ProjectRiskUrl + "?projectId=" + projectId[i];
            // 调用SPD相应接口
            List<Map<String, Object>> result = httpClient.client(url, httpMethod, params);
            Map<Object, Object> map = new HashMap<Object, Object>();
            // 开始时间
            double startDate = Double.parseDouble(result.get(0).get("项目启动时间和当前日期差").toString());
            // 结束时间
            double endDate = Double.parseDouble(result.get(0).get("项目周期").toString());
            // x轴
            DecimalFormat df = new DecimalFormat("0.00");// 设置保留位数
            String Xaxis = df.format(endDate / startDate);
            map.put("Xaxis", Double.parseDouble(Xaxis));
            // y轴
            String Yaxis = result.get(0).get("预实比").toString();
            map.put("Yaxis", Double.parseDouble(Yaxis));
            // 项目名称
            String proName = result.get(0).get("项目名称") == null ? "" : result.get(0).get("项目名称").toString();
            map.put("proName", proName);
            //用户
            List<Map> user = result.get(0).get("员工") == null ? null : (List<Map>) result.get(0).get("员工");
            map.put("user", user);
            //资源部门
            List<Map> project = result.get(0).get("资源部门") == null ? null : (List<Map>) result.get(0).get("资源部门");
            map.put("project", project);
            // 项目风险系数
            //计划id
            List<Object> planIds = result.get(0).get("计划id") == null ? null : (List<Object>) result.get(0).get("计划id");
            map.put("planIds", planIds);
            if (planIds != null) {
                Double OutputQualityRiskSum = taskExecutionTable.OutputQualityRiskSum(planIds);
                System.out.println(OutputQualityRiskSum);
                map.put("OutputQualityRiskSum", df.format(OutputQualityRiskSum));
            }

            list.add(map);
        }
        return Result.ok(list);
    }

    @GetMapping(value = "/SectorRiskFactor")
    @ApiOperation(value = "部门风险系数表")
    public Result SectorRiskFactor(String userIds, String name, String startTime, String endTime) throws ParseException {
        // 请求地址
        String[] userId = userIds.split(",");
        if (userId.length > 1) {
            List list = SectorRiskFactors(userId, startTime, endTime, name);
            return Result.ok(list);
        }
        String url = SectorRiskUrl + "?userId=" + userIds + "&startTime=" + startTime + "&endTime=" + endTime;
        HttpMethod httpMethod = HttpMethod.GET;
        Map<String, Object> params = new HashMap<>();

        // 调用SPD相应接口
        List<Map<String, Object>> result = httpClient.client(url, httpMethod, params);
        // 计算时间周数
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        long start = formatter.parse(startTime).getTime() / (1000 * 60 * 60 * 24);
        long end = formatter.parse(endTime).getTime() / (1000 * 60 * 60 * 24);
        long week = (end - start) / 7 > 0 ? (end - start) / 7 + 1 : (end - start) / 7;
        List list = new ArrayList();
        // 计算每周风险总数
        for (int i = 1; i <= week; i++) {
            // 每周开始时间
            long startLong = formatter.parse(startTime).getTime() + (week - 1) * 1000 * 60 * 60 * 24;
            // 每周结束时间
            long endLong = formatter.parse(endTime).getTime() + week * 1000 * 60 * 60 * 24;
            Map<String, Object> staffMap = new HashMap<String, Object>();
            Map<String, Object> markMap = new HashMap<String, Object>();
            // 输出质量风险之和
            Double sum = 0.0;
            // 基准之和
            Double sumBenchmark = 0.0;
            // 员工姓名
            staffMap.put("name", name);
            staffMap.put("week", i + "");
            // 员工基准
            markMap.put("name", name + "基准");
            markMap.put("week", i + "");
            // 计划工时之和
            Double planSum = 0.0;
            // 实际工时之和
            Double actualSum = 0.0;
            for (Map map : result) {
                List<Map<String, Object>> OT = (List<Map<String, Object>>) map.get("OT");

                // 任务开始时间
                String taskStart = map.get("实际开始时间") == null ? "0" : map.get("实际开始时间").toString();
                if (taskStart == "0") {
                    continue;
                }
                long taskLong = formatter.parse(taskStart).getTime();
                for (Map mapot : OT) {
                    if (taskLong >= startLong && taskLong < endLong) {
                        // 标准偏差
                        double standardDeviation = 0;
                        if (mapot.get("标准偏差值") != null) {
                            standardDeviation = Double.parseDouble(mapot.get("标准偏差值").toString());
                        }
                        // 汇报偏差
                        double reportingDeviations = 0;
                        if (mapot.get("汇报偏差值") != null) {
                            reportingDeviations = Double.parseDouble(mapot.get("汇报偏差值").toString());
                        }
//					double reportingDeviations = Double.parseDouble(mapot.get("汇报偏差").toString());
                        // 汇报困难度
                        double reportingDifficulty = 0;
                        if (mapot.get("汇报困难度") != null) {
                            reportingDifficulty = Double.parseDouble(mapot.get("汇报困难度").toString());
                        }
                        //double reportingDifficulty = Double.parseDouble(mapot.get("汇报困难度").toString());
                        // 标准困难度
                        double standardDifficulty = 0;
                        if (mapot.get("标准困难度") != null) {
                            standardDifficulty = Double.parseDouble(mapot.get("标准困难度").toString());
                        }
                        //double standardDifficulty = Double.parseDouble(mapot.get("标准困难度").toString());

                        // 计算输出质量风险
                        DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
                        String OutputQualityRisk = df.format(((abs(reportingDeviations - standardDeviation)) * 10 + 1)
                                * ((abs(reportingDifficulty - standardDifficulty)) * 10 + 1));
                        double RiskDouble = Double.parseDouble(OutputQualityRisk);

                        // 广度
                        double span = 0;
                        if (mapot.get("广度") != null) {
                            span = Double.parseDouble(mapot.get("广度").toString());
                        }
                        // 关键度
                        double Criticality = 0;
                        if (mapot.get("关键度") != null) {
                            Criticality = Double.parseDouble(mapot.get("关键度").toString());
                        }
                        // 计算项目风险指标
                        double ProjectRiskIndicators = (RiskDouble - 1) * span * Criticality;
                        // 计算基准
                        double ProjectRiskBenchmark = RiskDouble * span * Criticality;
                        // 输出质量风险之和
                        sum += ProjectRiskIndicators;
                        // 基准之和
                        sumBenchmark += ProjectRiskBenchmark;
                        // 计划工时
                        double plan = 0;
                        if (mapot.get("计划工时") != null) {
                            plan = Double.parseDouble(map.get("计划工时").toString());
                        }
                        planSum += plan;
                        // 实际工时
                        double actual = 1;
                        if (mapot.get("实际工时") != null) {
                            actual = Double.parseDouble(map.get("实际工时").toString());
                        }
                        actualSum += actual;
                    }
                }
                // 这一周员工风险系数之和
                staffMap.put("sum", sum);
                // 本周员工基准之和
                markMap.put("sumBenchmark", sumBenchmark);
                // 计划工时与实际工时之比
                DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
                String ratio = df.format(planSum / actualSum);
                staffMap.put("ratio", Double.parseDouble(ratio));
                markMap.put("ratio", Double.parseDouble(ratio));
                list.add(staffMap);
                list.add(markMap);
            }

        }
        return Result.ok(list);
    }

    private List SectorRiskFactors(String[] userId, String startTime, String endTime, String name) throws ParseException {
        long mostweek = 0;
        List resultList = new ArrayList();
        // 计算时间周数
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
        for (String id : userId) {
            String url = SectorRiskUrl + "?userId=" + id + "&startTime=" + startTime + "&endTime=" + endTime;
            HttpMethod httpMethod = HttpMethod.GET;
            Map<String, Object> params = new HashMap<>();
            // 调用SPD相应接口
            List<Map<String, Object>> result = httpClient.client(url, httpMethod, params);


            long start = formatter.parse(startTime).getTime() / (1000 * 60 * 60 * 24);
            long end = formatter.parse(endTime).getTime() / (1000 * 60 * 60 * 24);
            long week = (end - start) / 7 > 0 ? (end - start) / 7 + 1 : (end - start) / 7;
            mostweek = mostweek > week ? mostweek : week;
            List list = new ArrayList();
            // 计算每周风险总数
            for (int i = 1; i <= week; i++) {
                // 每周开始时间
                long startLong = formatter.parse(startTime).getTime() + (week - 1) * 1000 * 60 * 60 * 24;
                // 每周结束时间
                long endLong = formatter.parse(endTime).getTime() + week * 1000 * 60 * 60 * 24;
                Map<String, Object> staffMap = new HashMap<String, Object>();
                Map<String, Object> markMap = new HashMap<String, Object>();
                // 输出质量风险之和
                Double sum = 0.0;
                // 基准之和
                Double sumBenchmark = 0.0;
                // 计划工时之和
                Double planSum = 0.0;
                // 实际工时之和
                Double actualSum = 0.0;
                for (Map map : result) {
                    List<Map<String, Object>> OT = (List<Map<String, Object>>) map.get("OT");
                    // 任务开始时间
                    String taskStart = map.get("实际开始时间") == null ? "0" : map.get("实际开始时间").toString();
                    if (taskStart == "0") {
                        continue;
                    }
                    long taskLong = formatter.parse(taskStart).getTime();
                    for (Map mapot : OT) {
                        if (taskLong >= startLong && taskLong < endLong) {
                            // 标准偏差
                            double standardDeviation = 0;
                            if (mapot.get("标准偏差值") != null) {
                                standardDeviation = Double.parseDouble(mapot.get("标准偏差值").toString());
                            }
                            // 汇报偏差
                            double reportingDeviations = 0;
                            if (mapot.get("汇报偏差值") != null) {
                                reportingDeviations = Double.parseDouble(mapot.get("汇报偏差值").toString());
                            }
                            //double reportingDeviations = Double.parseDouble(mapot.get("汇报偏差").toString());
                            // 汇报困难度
                            double reportingDifficulty = 0;
                            if (mapot.get("汇报困难度") != null) {
                                reportingDifficulty = Double.parseDouble(mapot.get("汇报困难度").toString());
                            }
                            //double reportingDifficulty = Double.parseDouble(mapot.get("汇报困难度").toString());
                            // 标准困难度
                            double standardDifficulty = 0;
                            if (mapot.get("标准困难度") != null) {
                                standardDifficulty = Double.parseDouble(mapot.get("标准困难度").toString());
                            }
                            //double standardDifficulty = Double.parseDouble(mapot.get("标准困难度").toString());

                            // 计算输出质量风险
                            String OutputQualityRisk = df.format(((abs(reportingDeviations - standardDeviation)) * 10 + 1)
                                    * ((abs(reportingDifficulty - standardDifficulty)) * 10 + 1));
                            double RiskDouble = Double.parseDouble(OutputQualityRisk);

                            // 广度
                            double span = 0;
                            if (mapot.get("广度") != null) {
                                span = Double.parseDouble(mapot.get("广度").toString());
                            }
                            //double span = Double.parseDouble(mapot.get("广度").toString());
                            // 关键度
                            double Criticality = 0;
                            if (mapot.get("关键度") != null) {
                                Criticality = Double.parseDouble(mapot.get("关键度").toString());
                            }
                            //double Criticality = Double.parseDouble(mapot.get("关键度").toString());
                            // 计算项目风险指标
                            double ProjectRiskIndicators = (RiskDouble - 1) * span * Criticality;
                            // 计算基准
                            double ProjectRiskBenchmark = RiskDouble * span * Criticality;
                            // 输出质量风险之和
                            sum += ProjectRiskIndicators;
                            // 基准之和
                            sumBenchmark += ProjectRiskBenchmark;
                            // 计划工时
                            double plan = 0;
                            if (mapot.get("计划工时") != null) {
                                plan = Double.parseDouble(map.get("计划工时").toString());
                            }
                            //Double plan = Double.parseDouble(map.get("计划工时").toString());
                            planSum += plan;
                            // 实际工时
                            double actual = 0;
                            if (mapot.get("实际工时") != null) {
                                actual = Double.parseDouble(map.get("实际工时").toString());
                            }
                            //Double actual = Double.parseDouble(map.get("实际工时").toString());
                            actualSum += actual;
                        }
                    }
                    // 这一周员工风险系数之和
                    staffMap.put("sum", sum);
                    // 本周员工基准之和
                    markMap.put("sumBenchmark", sumBenchmark);
                    // 计划工时与实际工时之比
                    staffMap.put("planSum", planSum);
                    markMap.put("actualSum", actualSum);

                    String ratio = df.format(planSum / actualSum);
                    staffMap.put("ratio", ratio);
                    markMap.put("ratio", ratio);
                    list.add(staffMap);
                    list.add(markMap);
                }

            }
            resultList.add(list);
        }
        List result = new ArrayList();
        for (int i = 1; i <= mostweek; i++) {
            // 输出质量风险之和
            Double sum = 0.0;
            // 基准之和
            Double sumBenchmark = 0.0;
            // 计划工时之和
            Double planSum = 0.0;
            // 实际工时之和
            Double actualSum = 0.0;
            Map<String, Object> staffMap = new HashMap<String, Object>();
            Map<String, Object> markMap = new HashMap<String, Object>();
            for (int j = 0; j < resultList.size(); j++) {
                List list = (List) resultList.get(j);
                if (list.size() < 2 * i - 1 || list.size() == 0) {
                    continue;
                }
                Map<String, Object> staffMapList = (Map) list.get(2 * i - 2);
                Map<String, Object> markMapList = (Map) list.get(2 * i - 1);
                sum += Double.parseDouble(staffMapList.get("sum").toString());
                sumBenchmark += Double.parseDouble(markMapList.get("sumBenchmark").toString());
                planSum += Double.parseDouble(staffMapList.get("planSum").toString());
                actualSum += Double.parseDouble(markMapList.get("actualSum").toString());
            }
            //这一周员工风险系数之和
            staffMap.put("sum", sum);
            staffMap.put("name", name);
            staffMap.put("week", i);
            // 本周员工基准之和
            markMap.put("sumBenchmark", sumBenchmark);
            markMap.put("name", name + "基准");
            markMap.put("week", i);
            // 计划工时与实际工时之比
            String ratio = "0";
            if (actualSum != 0) {
                ratio = df.format(planSum / actualSum);
            }
            staffMap.put("ratio", ratio);
            markMap.put("ratio", ratio);
            result.add(staffMap);
            result.add(markMap);

        }

        return result;
    }

    // 计算绝对值
    public double abs(double a) {
        return (a > 0) ? a : -a;
    }

    @GetMapping(value = "/pert")
    @ApiOperation(value = "pert表")
    public Result pert(String activeId) throws ParseException {
        // 请求地
        String url = pertUrl + "?activityId=" + activeId;
        System.out.println(url);
        HttpMethod httpMethod = HttpMethod.GET;
        Map<String, Object> params = new HashMap<>();
        // 调用SPD相应接口
        List<Map<String, Object>> result = httpClient.client(url, httpMethod, params);
        for (Map map : result) {
            // 预估开始时间
            String taskStart = map.get("预估开始时间").toString() == null ? "" : map.get("预估开始时间").toString();
            map.put("taskStart", taskStart);
            // 截至时间
            String taskComplete = map.get("截至时间").toString() == null ? "" : map.get("截至时间").toString();
            map.put("taskComplete", taskComplete);
            // 预估完成时间
            String estimatedCompletion = map.get("预估完成时间").toString() == null ? "" : map.get("预估完成时间").toString();
            map.put("estimatedCompletion", estimatedCompletion);
            //权重
            double weight = 0.7;
            // 最可能完成时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            long Complete = (long) ((sdf.parse(taskComplete).getTime() - sdf.parse(taskStart).getTime()) * weight
                    + sdf.parse(taskStart).getTime());
             // 实际开始时间
            String actualStart = map.get("实际开始时间").toString();
            map.put("actualStart", actualStart);
            // PERT（（F6-c6+4*(E6-C6)+D6-C6）/6）
            long pertLong = Complete + sdf.parse(taskComplete).getTime() - sdf.parse(taskStart).getTime() * 2
                    + 4 * (sdf.parse(estimatedCompletion).getTime() - sdf.parse(taskStart).getTime());
            DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
            String pert = df.format(pertLong / (1000 * 60 * 60 * 24 * 6));
            map.put("pert", Double.parseDouble(pert));
        }
        return Result.ok(result);
    }

}
