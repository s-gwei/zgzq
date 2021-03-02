package org.jeecg.modules.P2020052.service.impl;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jeecg.modules.P2020052.service.ZQService;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class ZQServiceImpl implements ZQService {

    @Override
    public List TaskExecutionTable(List<Map<String, Object>> result) {

        List list = new ArrayList();
        //循环遍历所有
        for (Map map : result) {
            //每个任务都作为数组存储
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            //获取每个任务下OT，IN
            List<Map<String, Object>> OT = (List<Map<String, Object>>) map.get("OT");
            List<Map<String, Object>> in = (List<Map<String, Object>>) map.get("in");
            DecimalFormat df = new DecimalFormat("0.00");// 设置保留位数
            //kpi风险系数之和
            double OutputaualityKPISum = 0;
            //权重之和
            double weightSum = 0;
            //质量影响因子
             double qualityInfluenceSum = 0;
             int i = 0;
            //标准工期
            double StandardPeriod = 0;
            //项目工期
            double reportPeriod = 0;
            //平均发布次数
            double NumbereleasesAvg = 0;
            //匹配每个OT与in
            if(OT==null ||OT.size()==0){
                continue;
            }
            for (Map mapot : OT) {
                if(in == null||in.size() == 0  ){
                    continue;
                }
                for (Map mapin : in) {
                    String id = mapot.get("id").toString();
                    List ids = (List) mapin.get("对应ot指标id");
                    if (mapot.get("id").toString().equals(ids.get(0).toString())) {
                        Map<String, Object> resultMap = new HashMap<String, Object>();
                        resultMap.put("任务名称", map.get("任务名称"));
                        resultMap.put("任务id", map.get("任务id"));
                        //标准工期
                        StandardPeriod = Double.parseDouble(mapot.get("标准工期") == null ? "0" : mapot.get("标准工期").toString());
                        resultMap.put("StandardPeriod", StandardPeriod);
                        //项目工期
                        reportPeriod = Double
                                .parseDouble(mapot.get("项目工期") == null ? "0" : mapot.get("项目工期").toString());
                        resultMap.put("reportPeriod", reportPeriod);
                        //标准偏差
                        Double standardDeviation = Double
                                .parseDouble(mapot.get("标准偏差值") == null ? "0" : mapot.get("标准偏差值").toString());
                        resultMap.put("standardDeviation", standardDeviation);
                        // 汇报偏差
                        Double reportingDeviations = Double
                                .parseDouble(mapot.get("汇报偏差值") == null ? "0" : mapot.get("汇报偏差值").toString());
                        resultMap.put("reportingDeviations", reportingDeviations);
                        // 权重
                        Double weight = Double.parseDouble(mapin.get("权重") == null ? "0" : mapin.get("权重").toString());
                        weightSum += weight;
                        resultMap.put("weight", weight);
                        // 计算得出质量影响因子
                        String qualityInfluenceFactor = df.format(weight * (reportingDeviations / standardDeviation));
                        qualityInfluenceSum += Double.parseDouble(qualityInfluenceFactor);
                        resultMap.put("qualityInfluenceFactor", qualityInfluenceFactor);

                        // 汇报困难度
                        double reportingDifficulty = Double.parseDouble(mapot.get("汇报困难度") == null ? "0" : mapot.get("汇报困难度").toString());
                        resultMap.put("reportingDifficulty", reportingDifficulty);
                        // 标准困难度
                        double standardDifficulty = Double.parseDouble(mapot.get("标准困难度") == null ? "0" : mapot.get("标准困难度").toString());
                        resultMap.put("standardDifficulty", standardDifficulty);
                        // 计算得出输出质量KPI
                        String OutputaualityKPI = df
                                .format((reportingDeviations / standardDeviation) * (reportingDifficulty / standardDifficulty));
                        resultMap.put("OutputaualityKPI", OutputaualityKPI);

                        //计算输出质量KPI风险系数之和
                        OutputaualityKPISum += Double.parseDouble(OutputaualityKPI);
                        // 计算输出质量风险
                        String OutputQualityRisk = df.format(((abs(reportingDeviations - standardDeviation)) * 10 + 1)
                                * ((abs(reportingDifficulty - standardDifficulty)) * 10 + 1));
                        resultMap.put("OutputQualityRisk", OutputQualityRisk);
                        // 广度
                        double span = Double.parseDouble(mapot.get("广度") == null ? "0" : mapot.get("广度").toString());
                        resultMap.put("span", span);
                        // 关键度
                        double Criticality = Double.parseDouble(mapot.get("关键度") == null ? "0" : mapot.get("关键度").toString());
                        resultMap.put("Criticality", Criticality);
                        //输出评定
                        String OutputEvalua = mapot.get("输出评定") == null ? "0" : mapot.get("输出评定").toString();
                        resultMap.put("OutputEvalua", OutputEvalua);
                        // 发布次数
                        double Numbereleases = Double.parseDouble(mapot.get("发布次数") == null ? "0" : mapot.get("发布次数").toString());
                        NumbereleasesAvg += Numbereleases;
                        resultMap.put("Numbereleases", Numbereleases);
                        // 计算项目风险指标
                        String ProjectRiskIndicators = df.format((Double.parseDouble(OutputQualityRisk) - 1) * span * Criticality);
                        resultMap.put("ProjectRiskIndicators", ProjectRiskIndicators);
                        resultList.add(resultMap);
                        i++;
                    }
                    continue;
                }
                Map<String, Object> totalMap = new HashMap<String, Object>();
                totalMap.put("taskname", map.get("任务名称"));
                totalMap.put("Executive", map.get("执行职能"));
                totalMap.put("taskid", map.get("任务id"));
                //总质量指标
                String totalQualityKpi = df.format(OutputaualityKPISum / i);
                totalMap.put("totalQualityKpi", totalQualityKpi);
                // 平均发布次数
                NumbereleasesAvg = NumbereleasesAvg / i;
                totalMap.put("NumbereleasesAvg", NumbereleasesAvg);
                //环境质量指标
                String qualityIndex = df.format((qualityInfluenceSum - weightSum) / i);
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

            }
            list.add(resultList);
        }
        System.out.println(list);
        return list;
    }

    @Override
    public void exportTable(List<Map<String, Object>> result, HttpServletResponse response, String fileName) throws IOException {
        List list = TaskExecutionTable(result);
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
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 2, 5);// 起始行号，结束行号，起始列号，结束列号
        CellRangeAddress cellRangeAddress1 = new CellRangeAddress(0, 0, 6, 8);// 起始行号，结束行号，起始列号，结束列号
        CellRangeAddress cellRangeAddress2 = new CellRangeAddress(0, 0, 9, 14);// 起始行号，结束行号，起始列号，结束列号
        CellRangeAddress cellRangeAddress3 = new CellRangeAddress(0, 0, 15, 16);// 起始行号，结束行号，起始列号，结束列号

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
        cell = row.createCell(6);
        cell.setCellStyle(style);
        cell.setCellValue("计划环境");
        cell = row.createCell(9);
        cell.setCellStyle(style);
        cell.setCellValue("输出质量");
        cell = row.createCell(15);
        cell.setCellStyle(style);
        cell.setCellValue("输出加权");
        cell = row.createCell(17);
        cell.setCellValue("项目风险");
        cell = row.createCell(18);
        cell.setCellValue("输出评定");
        cell = row.createCell(19);
        cell.setCellValue("发布次数");

        //设置第二行表头的值单元格的值
        row = sheet.createRow(1);
        cell = row.createCell(2);
        cell.setCellValue("权重");
        cell = row.createCell(3);
        cell.setCellValue("标准偏差");
        cell = row.createCell(4);
        cell.setCellValue("汇报偏差");
        cell = row.createCell(5);
        cell.setCellValue("质量影响因子");
        cell = row.createCell(6);
        cell.setCellValue("标准工期");
        cell = row.createCell(7);
        cell.setCellValue("项目工期");
        cell = row.createCell(8);
        cell.setCellValue("风险KPI");
        cell = row.createCell(9);
        cell.setCellValue("标准偏差");
        cell = row.createCell(10);
        cell.setCellValue("汇报偏差");
        cell = row.createCell(11);
        cell.setCellValue("标准困难度");
        cell = row.createCell(12);
        cell.setCellValue("汇报困难度");
        cell = row.createCell(13);
        cell.setCellValue("输出质量KPI");
        cell = row.createCell(14);
        cell.setCellValue("质量风险");
        cell = row.createCell(15);
        cell.setCellValue("广度");
        cell = row.createCell(16);
        cell.setCellValue("关键度");
        //环境质量指标：qualityIndex
        cell = row.createCell(20);
        cell.setCellValue("环境质量指标");
        //工期风险KPI:riskKPI
        cell = row.createCell(21);
        cell.setCellValue("工期风险KPI");
        //总质量指标：totalQualityKpi
        cell = row.createCell(22);
        cell.setCellValue("总质量指标");
        //平均发布次数：NumbereleasesAvg
        cell = row.createCell(23);
        cell.setCellValue("平均发布次数");
        //excel写入数据
        int k = 2; //记录sheet行
//        int x = k; // 记录总和数据写入sheet数
        for (int i = 0; i < list.size(); i++) {
            List<Map<String, Object>> list1 = (List<Map<String, Object>>) list.get(i);
            int x = k; // 记录总和数据写入sheet数
            for (int j = 0; j < list1.size(); j++) {
                if (j != list1.size() - 1) {
                    Map map = list1.get(j);
                    row = sheet.createRow(k);
                    cell = row.createCell(0);
                    cell.setCellValue("研发");
                    cell = row.createCell(1);
                    cell.setCellValue(map.get("任务名称").toString());
                    //权重
                    cell = row.createCell(2);
                    cell.setCellValue(map.get("weight").toString());
                    //标准偏差
                    cell = row.createCell(3);
                    cell.setCellValue(map.get("standardDeviation").toString());
                    //汇报偏差
                    cell = row.createCell(4);
                    cell.setCellValue(map.get("reportingDeviations").toString());
                    //质量影响因子
                    cell = row.createCell(5);
                    cell.setCellValue(map.get("qualityInfluenceFactor").toString());
                    //标准工期
                    cell = row.createCell(6);
                    cell.setCellValue(map.get("StandardPeriod").toString());
                    //项目工期
                    cell = row.createCell(7);
                    cell.setCellValue(map.get("reportPeriod").toString());
                    //风险KPI
                    cell = row.createCell(8);
                    cell.setCellValue(map.get("reportPeriod").toString());
                    //标准偏差
                    cell = row.createCell(9);
                    cell.setCellValue(map.get("standardDeviation").toString());
                    //汇报偏差
                    cell = row.createCell(10);
                    cell.setCellValue(map.get("reportingDeviations").toString());
                    //标准困难度
                    cell = row.createCell(11);
                    cell.setCellValue(map.get("standardDifficulty").toString());
                    //汇报困难度
                    cell = row.createCell(12);
                    cell.setCellValue(map.get("reportingDifficulty").toString());
                    //输出质量KPI
                    cell = row.createCell(13);
                    cell.setCellValue(map.get("OutputaualityKPI").toString());
                    //质量风险
                    cell = row.createCell(14);
                    cell.setCellValue(map.get("OutputQualityRisk").toString());
                    //广度
                    cell = row.createCell(15);
                    cell.setCellValue(map.get("span").toString());
                    //关键度
                    cell = row.createCell(16);
                    cell.setCellValue(map.get("Criticality").toString());
                    //项目风险
                    cell = row.createCell(17);
                    cell.setCellValue(map.get("ProjectRiskIndicators").toString());
                    //输出评定
                    cell = row.createCell(18);
                    cell.setCellValue(map.get("OutputEvalua").toString());
                    //发布次数
                    cell = row.createCell(19);
                    cell.setCellValue(map.get("Numbereleases").toString());
                    k++;
                    continue;
                } else {
                    Map map = list1.get(j);
                    row = sheet.createRow(x);
                    //环境质量指标：qualityIndex
                    cell = row.createCell(20);
                    cell.setCellValue(map.get("qualityIndex").toString());
                    //工期风险KPI:riskKPI
                    cell = row.createCell(21);
                    cell.setCellValue(map.get("riskKPI").toString());
                    //总质量指标：totalQualityKpi
                    cell = row.createCell(22);
                    cell.setCellValue(map.get("totalQualityKpi").toString());
                    //平均发布次数：NumbereleasesAvg
                    cell = row.createCell(23);
                    cell.setCellValue(map.get("NumbereleasesAvg").toString());
                    k++;
                    continue;
                }
            }
        }

        //导出数据
        try {
            //设置Http响应头告诉浏览器下载这个附件
            if (fileName == null) {
                fileName = "任务执行报表";
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
//            System.out.println(df.format(new Date()));// new Date()为获取当前系统时间+ df.format(new Date()) +
            fileName = new String(fileName.getBytes("gb2312"), "ISO8859-1");
            response.setContentType("application/x-download;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + df.format(new Date()) + ".xls");
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
