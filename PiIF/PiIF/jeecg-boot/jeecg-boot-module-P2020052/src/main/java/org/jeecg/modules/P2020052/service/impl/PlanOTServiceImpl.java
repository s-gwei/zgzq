package org.jeecg.modules.P2020052.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.poi.hssf.usermodel.*;
import org.jeecg.modules.P2020052.mapper.PlanOTMapper;
import org.jeecg.modules.P2020052.pojo.*;
import org.jeecg.modules.P2020052.service.PlanOTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@DS("multi-datasource1")
public class PlanOTServiceImpl implements PlanOTService {


    @Autowired
    PlanOTMapper planOTMapper;

    @Override
    public IPage<PlanOTVo> OTTable(Page page, String[] time, String[] group) {
        String startTime = null;
        String endTime = null;
        if (time != null) {
            startTime = time[0];
            endTime = time[1];
        }

        return  planOTMapper.OTTable(page, startTime, endTime, group);
    }

    @Override
    public List<GroupVo> selectGroup() {

        return planOTMapper.selectGroup();
    }

    @Override
    public IPage<PlanINVo> selectINTable(Page page, String[] time, String[] group) {
        String startTime = null;
        String endTime = null;
        if (time != null && !"".equals(time)) {
            startTime = time[0];
            endTime = time[1];
        }

        return planOTMapper.selectINTable(page, startTime, endTime, group);
    }


    @Override
    public List<RiskVo> selectRiskTable(String[] time, String[] group, String planId) {
        if (planId == null || "".equals(planId)) {
            List<RiskVo> list = planOTMapper.selectRiskProject(time, group);
            List resultList = new ArrayList();
            Map map = new HashMap<>();
            List nameList = null;
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    nameList = new ArrayList();
                    map.put("name", list.get(i).getRisk_name());
                    nameList.add(list.get(i));
                    continue;
                }
                if (map.get(list.get(i).getRisk_name()) == null) {

                    resultList.add(nameList);
                    map.put("name", list.get(i).getRisk_name());
                    nameList = new ArrayList();
                    nameList.add(list.get(i));
                } else {
                    nameList.add(list.get(i));
                    continue;
                }
                if (i == list.size() - 1) {
                    resultList.add(nameList);

                }
            }
            for (Object set : map.keySet()) {
                for (RiskVo riskVo : list) {

                }
            }
            return resultList;
        } else {
            List<RiskVo> list = planOTMapper.selectRiskByPlan(time, group, planId);
            List resultList = new ArrayList();
            Map map = new HashMap<>();
            List nameList = null;
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    nameList = new ArrayList();
                    map.put("name", list.get(i).getRisk_name());
                    nameList.add(list.get(i));
                    continue;
                }
                if (map.get(list.get(i).getRisk_name()) == null) {
                    resultList.add(nameList);
                    map.put("name", list.get(i).getRisk_name());
                    nameList = new ArrayList();
                } else {
                    nameList.add(list.get(i));
                    continue;
                }
                if (i == list.size() - 1) {
                    resultList.add(nameList);

                }
            }
            for (Object set : map.keySet()) {
                for (RiskVo riskVo : list) {

                }
            }
            return resultList;
        }

    }

    @Override
    public List<PiplanActivityVo> WorkDelayTable(String[] time, String projectId, String flag) throws ParseException {
        String startTime = null;
        String endTime = null;
        time = new String[2];
        time[0] = "2021-01-10";
        time[1] = "2021-02-16";
        if (time != null && !"".equals(time)) {
            startTime = time[0];
            endTime = time[1];
        }
        List<PiplanActivityVo> list = planOTMapper.WorkDelayTable(startTime, endTime, projectId, flag);
        // 计算时间周数
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        long start = formatter.parse(startTime).getTime();
        long end = formatter.parse(endTime).getTime() ;
        long week = (end - start) / (1000 * 60 * 60 * 24*7) > 0 ? (end - start) / (1000 * 60 * 60 * 24*7) + 1 : (end - start) / (1000 * 60 * 60 * 24*7);
        DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
        //返回数据
        List result = new ArrayList();
        for (int i = 1; i <= week; i++) {
            for (PiplanActivityVo act : list) {
                // 每周开始时间
                long startLong = start + (i - 1) * 1000 * 60 * 60 * 24 * 7;
                // 每周结束时间
                long endLong = start + i * 1000 * 60 * 60 * 24 * 7;
                String taskStr = act.getActualStartTime();
                long taskLong = taskStr == null ? 0 :formatter.parse(taskStr).getTime();
                if (taskLong >= startLong && taskLong < endLong) {
                    long currentStr = new Date().getTime();//当前时间
                    String byTimeStr = act.getByTime();//计划完成时间
                    long btTImeLong = byTimeStr == null ? 0 : formatter.parse(byTimeStr).getTime();
                    String actualEnd = act.getActualEndTime();//实际完成时间
                    long actualEndLong = actualEnd == null ? 0 : formatter.parse(actualEnd).getTime();
                    String expectedFinishStr = act.getExpectedFinishTime();//预估完成时间
                    long expectedFinishLong = expectedFinishStr == null ? 0 : formatter.parse(expectedFinishStr).getTime();


                    String targetStartTimeStr = act.getTargetStartTime();//预估完成时间
                    long targetStartTimeStrLong = targetStartTimeStr == null ? 0 : formatter.parse(targetStartTimeStr).getTime();

                    String taskTypa = null;
                    //任务状态1，正常执行
                    if(actualEndLong == 0 && btTImeLong>expectedFinishLong){
                        taskTypa = "normal";
                    }
                    //任务状态2，可能逾期
                    if(actualEndLong == 0 && btTImeLong<=expectedFinishLong){
                        taskTypa = "isoverdue";
                    }
                    //任务状态3，已完成
                    if(actualEndLong != 0){
                        taskTypa = "finished";
                    }
                    //任务状态4，已逾期
                    if(actualEndLong == 0 && btTImeLong<currentStr){
                        taskTypa = "overdue";
                    }
                    if(actualEndLong != 0 && btTImeLong<=expectedFinishLong){
                        taskTypa = "overdue";
                    }
                    act.setTaskType(taskTypa);
                    //横坐标
//                    long X = (taskLong - startLong)%(1000 * 60 * 60 * 24 * 7)+1;
                    double  x = (taskLong - startLong)/(1000 * 60 * 60 * 24 * 7.0)+1;
                    String Xaxis = df.format(x);
                    act.setXaxis(Double.parseDouble(Xaxis));
                    //偏差值
                    String deviation = null;
                    if(actualEndLong !=0) {
                        deviation = df.format((btTImeLong - actualEndLong) / (1000 * 60 * 60 * 24));
                    }else if(actualEndLong == 0 && targetStartTimeStrLong<currentStr){
                        deviation = df.format((btTImeLong - currentStr) / (1000 * 60 * 60 * 24));
                    }else if(actualEndLong == 0 && targetStartTimeStrLong>currentStr){
                        deviation = "0";
                    }
                    act.setDeviation(Double.parseDouble(deviation));
                }
            }
        }
        return list;
    }

    @Override
    public void exportOTExcel(HttpServletResponse response, String[] time, String[] group) throws IOException {
        String startTime = null;
        String endTime = null;
        if (time != null) {
            startTime = time[0];
            endTime = time[1];
        }
        List<PlanOTVo> list = planOTMapper.exportOTExcel(startTime, endTime, group);
        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet0");
        //设置默认行宽
        sheet.setDefaultColumnWidth(20);
        // 加载单元格样式
        HSSFCellStyle style = wb.createCellStyle();
        //设置第一行表头的值单元格的值
        HSSFRow row = sheet.createRow(0);
        //创建HSSFCell对象
        HSSFCell cell = row.createCell(0);
        //设置单元格的值
        cell.setCellValue("序号");
        cell = row.createCell(1);
        cell.setCellValue("描述");
        cell = row.createCell(2);
        cell.setCellValue("汇报偏差");
        cell = row.createCell(3);
        cell.setCellValue("汇报困难度");
        cell = row.createCell(4);
        cell.setCellValue("广度");
        cell = row.createCell(5);
        cell.setCellValue("关键度");
        cell = row.createCell(6);
        cell.setCellValue("标准偏差");
        cell = row.createCell(7);
        cell.setCellValue("标准困难度");
        int i = 1;
        for(PlanOTVo planOTVo :list){
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(i);
            cell = row.createCell(1);
            cell.setCellValue(planOTVo.getDescription());
            cell = row.createCell(2);
            cell.setCellValue(planOTVo.getDeviation_report());
            cell = row.createCell(3);
            cell.setCellValue(planOTVo.getDifficulty_report());
            cell = row.createCell(4);
            cell.setCellValue(planOTVo.getBreadth());
            cell = row.createCell(5);
            cell.setCellValue(planOTVo.getCriticiailty());
            cell = row.createCell(6);
            cell.setCellValue(planOTVo.getStandard_deviation_value());
            cell = row.createCell(7);
            cell.setCellValue(planOTVo.getStandard_difficulty_value());
            i++;
        }
        //导出数据
        try {
            //设置Http响应头告诉浏览器下载这个附件
            String fileName = "计划OT交付情况报表";

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

    @Override
    public void exportINExcel(HttpServletResponse response, String[] time, String[] group) throws IOException {
        String startTime = null;
        String endTime = null;
        if (time != null && !"".equals(time)) {
            startTime = time[0];
            endTime = time[1];
        }

        List<PlanINVo> list = planOTMapper.exportINExcel( startTime, endTime, group);
        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet0");
        //设置默认行宽
        sheet.setDefaultColumnWidth(20);
        // 加载单元格样式
        HSSFCellStyle style = wb.createCellStyle();
        //设置第一行表头的值单元格的值
        HSSFRow row = sheet.createRow(0);
        //创建HSSFCell对象
        HSSFCell cell = row.createCell(0);
        //设置单元格的值
        cell.setCellValue("序号");
        cell = row.createCell(1);
        cell.setCellValue("权重");
        cell = row.createCell(2);
        cell.setCellValue("是否删除");
        cell = row.createCell(3);
        cell.setCellValue("是否存在");
        cell = row.createCell(4);
        cell.setCellValue("更改次数");
        int i = 1;
        for(PlanINVo planINVo :list){
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(i);
            cell = row.createCell(1);
            cell.setCellValue(planINVo.getWeights());
            cell = row.createCell(2);
            cell.setCellValue(planINVo.getIs_deleted());
            cell = row.createCell(3);
            cell.setCellValue(planINVo.getIs_persisted());
            cell = row.createCell(4);
            cell.setCellValue(planINVo.getUpdate_count());
            i++;
        }
        //导出数据
        try {
            //设置Http响应头告诉浏览器下载这个附件
            String fileName = "计划in报表导出";

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

    @Override
    public void exportRiskExcel(HttpServletResponse response, String[] time, String[] group, String planId) throws IOException {
        List<RiskVo> list = null;
        if (planId == null || "".equals(planId)) {
            list = planOTMapper.selectRiskProject(time, group);
        } else {
            list = planOTMapper.selectRiskByPlan(time, group, planId);

        }
        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet0");
        //设置默认行宽
        sheet.setDefaultColumnWidth(20);
        // 加载单元格样式
        HSSFCellStyle style = wb.createCellStyle();
        //设置第一行表头的值单元格的值
        HSSFRow row = sheet.createRow(0);
        //创建HSSFCell对象
        HSSFCell cell = row.createCell(0);
        //设置单元格的值
        cell.setCellValue("序号");
        cell = row.createCell(1);
        cell.setCellValue("风险名称");
        cell = row.createCell(2);
        cell.setCellValue("创建时间");
        cell = row.createCell(3);
        cell.setCellValue("措施名称");
        cell = row.createCell(4);
        cell.setCellValue("是否删除");
        cell = row.createCell(5);
        cell.setCellValue("是否存在");
        cell = row.createCell(6);
        cell.setCellValue("更改次数");
        int i = 1;
        for(RiskVo riskVo :list){
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(i);
            cell = row.createCell(1);
            cell.setCellValue(riskVo.getRisk_name());
            cell = row.createCell(2);
//            Date date = riskVo.getCreate_time();
            String timeFormat = null;
            if(riskVo.getCreate_time() != null){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                timeFormat = sdf.format(riskVo.getCreate_time());
            }
            cell.setCellValue(timeFormat);
            cell = row.createCell(3);
            cell.setCellValue(riskVo.getName());
            cell = row.createCell(4);
            cell.setCellValue(riskVo.getIs_deleted());
            cell = row.createCell(5);
            cell.setCellValue(riskVo.getIs_persisted());
            cell = row.createCell(6);
            cell.setCellValue(riskVo.getUpdate_count());
            i++;
        }
        //导出数据
        try {
            //设置Http响应头告诉浏览器下载这个附件
            String fileName = "风险措施报表";

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

    @Override
    public List<ProblemRickChainVo> problemRickChain(String riskId) {
        List<ProblemRickChainVo> list = planOTMapper.problemRickChain(riskId);
        ProblemRickChainVo parent = new ProblemRickChainVo();
        int i= 0;
        List<ProblemRickChainVo> children = new ArrayList<>();
        List<ProblemRickChainVo> result = new ArrayList<>();
        for(ProblemRickChainVo problemRickChainVo : list){
            if(i == 0){
                parent = problemRickChainVo;
            }else{
                children.add(problemRickChainVo);
            }
            i++;
        }
        parent.setChildren(children);
        result.add(parent);
        return result;
    }


}
