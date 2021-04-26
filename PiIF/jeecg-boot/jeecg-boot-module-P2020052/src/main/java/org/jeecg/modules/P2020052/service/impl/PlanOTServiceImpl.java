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
    public IPage<PlanOTVo> OTTable(Page page, String[] time, String[] group, String planId) {

        String startTime = null;
        String endTime = null;
        if (time != null) {
            startTime = time[0] + " 00:00:00";
            endTime = time[1] + " 23:59:59";
        }
        if(group != null && "null".equals(group[0]) && group.length==1){
            group =null;
        }
        IPage<PlanOTVo> list = planOTMapper.OTTable(page, startTime, endTime, group, planId);
        return list;
    }

    @Override
    public List<GroupVo> selectGroup() {

        return planOTMapper.selectGroup();
    }

    @Override
    public IPage<PlanINVo> selectINTable(Page page, String[] time, String[] group, String planId) {
        String startTime = null;
        String endTime = null;
        if (time != null && !"".equals(time)) {
            startTime = time[0] + " 00:00:00";
            endTime = time[1] + " 23:59:59";
        }
        if(group != null && "null".equals(group[0]) && group.length==1){
            group =null;
        }
        IPage<PlanINVo> planINVoIPage = planOTMapper.selectINTable(page, startTime, endTime, group, planId);
        return planINVoIPage;
    }


    @Override
    public List<RiskVo> selectRiskTable(String[] time, String[] group, String planId, String projectId) {
        String startTime = null;
        String endTime = null;
        if (time != null && !"".equals(time)) {
            startTime = time[0] + " 00:00:00";
            endTime = time[1] + " 23:59:59";
        }
        if (planId == null || "".equals(planId)) {
            //获取所有的风险
            List<RiskVo> list = planOTMapper.selectRiskProject(startTime, endTime, group, projectId);
            //将同一个项目风险下的措施放在同一个list中
            List resultList = new ArrayList();
            List nameList = new ArrayList();
            ;
            String riskName = "";
            for (int i = 0; i < list.size(); i++) {
                if ("".equals(riskName) || list.get(i).getRiskName().equals(riskName)) {
                    riskName = list.get(i).getRiskName();
                    nameList.add(list.get(i));
                    continue;
                }
                resultList.add(nameList);
                nameList = new ArrayList();
                riskName = list.get(i).getRiskName();
                nameList.add(list.get(i));
                continue;
            }
            resultList.add(nameList);
            return resultList;
        } else {
            List<RiskVo> list = planOTMapper.selectRiskByPlan(startTime, endTime, group, planId);
            //将同一个项目风险下的措施放在同一个list中
            List resultList = new ArrayList();
            List nameList = new ArrayList();
            ;
            String riskName = "";
            for (int i = 0; i < list.size(); i++) {
                if ("".equals(riskName) || list.get(i).getRiskName().equals(riskName)) {
                    riskName = list.get(i).getRiskName();
                    nameList.add(list.get(i));
                    continue;
                }
                resultList.add(nameList);
                nameList = new ArrayList();
                riskName = list.get(i).getRiskName();
                nameList.add(list.get(i));
                continue;
            }
            resultList.add(nameList);
            return resultList;
        }

    }

    @Override
    public List<PiplanActivityVo> WorkDelayTable(String[] time, String[] group, String projectId, String planId) throws ParseException {
        String startTime = null;
        String endTime = null;
        if (time != null && !"".equals(time)) {
            startTime = time[0] + " 00:00:00";
            endTime = time[1] + " 23:59:59";
        }
        if (group != null) {
            String a = group[0];
            if ("''".equals(group[0]) || "null".equals(group[0]) || group[0] == null) {
                group = null;
            }
        }
        List<PiplanActivityVo> list = planOTMapper.WorkDelayTable(startTime, endTime, group, projectId, planId);

        // 计算时间周数
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        long start = startTime == null ? 0 : formatter.parse(startTime).getTime();
        long end = endTime == null ? 0 : formatter.parse(endTime).getTime();
        double weeks = (end - start) / (1000 * 60 * 60 * 24 * 7.0);
        long week = (long) Math.ceil(weeks);
        DecimalFormat df = new DecimalFormat("0.00");// 设置保留两位位数
        //返回数据
        List<PiplanActivityVo> resultList= new ArrayList();
        for (long i = 1; i <= week; i++) {
            // 每周开始时间
            long startLong = start + (i - 1) * 1000 * 60 * 60 * 24 * 7;
            // 每周结束时间
            long endLong = start + i * 1000 * 60 * 60 * 24 * 7;

            for (PiplanActivityVo act : list) {
                String taskStr = act.getTargetStartTime();
//                String taskStr = act.getByTime();
                long taskLong = taskStr == null ? 0 : formatter.parse(taskStr).getTime();
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
                    if (actualEndLong == 0 && btTImeLong > expectedFinishLong && btTImeLong >= currentStr) {
                        taskTypa = "normal";
                    }
                    //任务状态2，可能逾期
                    if (actualEndLong == 0 && btTImeLong <= expectedFinishLong) {
                        taskTypa = "isoverdue";
                    }
                    //任务状态3，已完成
                    if (actualEndLong != 0 && btTImeLong >= actualEndLong) {
                        taskTypa = "finished";
                    }
                    //任务状态4，逾期未完成
                    if (actualEndLong == 0 && btTImeLong < currentStr) {
                        taskTypa = "overdue";
                    }
                    //任务状态5，逾期已完成
                    if (actualEndLong != 0 && btTImeLong < actualEndLong) {
                        taskTypa = "red";
                    }
                    act.setTaskType(taskTypa);
                    //横坐标
//                    long X = (taskLong - startLong)%(1000 * 60 * 60 * 24 * 7)+1;

//                    double x = (taskLong - start) / (1000 * 60 * 60 * 24 * 7.0) + 1 + Math.random() * 0.3;
                    double x = (currentStr - taskLong) / (1000 * 60 * 60 * 24 * 7.0) + 1 + Math.random() * 0.3;
                    String Xaxis = df.format(x);
                    act.setXaxis(Double.parseDouble(Xaxis));
                    //偏差值
                    String deviation = "0";
                    if (actualEndLong != 0) {
                        deviation = df.format((btTImeLong - actualEndLong) / (1000 * 60 * 60 * 24));
                    } else if (actualEndLong == 0 && targetStartTimeStrLong < currentStr) {
                        deviation = df.format((btTImeLong - currentStr) / (1000 * 60 * 60 * 24));
                    }else{
                        deviation = "0";
                    }
                    act.setDeviation(Double.parseDouble(deviation));
                    resultList.add(act);
                }
            }
        }

        return resultList;
    }

    @Override
    public void exportOTExcel(HttpServletResponse response, String[] time, String[] group, String planId) throws IOException {
        String startTime = null;
        String endTime = null;
        if (time != null) {
            startTime = time[0] + " 00:00:00";
            endTime = time[1] + " 23:59:59";
        }
        if(group != null && "null".equals(group[0]) && group.length==1){
            group =null;
        }
        List<PlanOTVo> list = planOTMapper.exportOTExcel(startTime, endTime, group, planId);
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
        cell.setCellValue("任务名称");
        cell = row.createCell(2);
        cell.setCellValue("指标编码");
        cell = row.createCell(3);
        cell.setCellValue("指标描述");
        cell = row.createCell(4);
        cell.setCellValue("汇报偏差");
        cell = row.createCell(5);
        cell.setCellValue("汇报困难度");
        cell = row.createCell(6);
        cell.setCellValue("汇报时间");
        cell = row.createCell(7);
        cell.setCellValue("广度");
        cell = row.createCell(8);
        cell.setCellValue("关键度");
        cell = row.createCell(9);
        cell.setCellValue("标准偏差");
        cell = row.createCell(10);
        cell.setCellValue("标准困难度");
        int i = 1;
        for (PlanOTVo planOTVo : list) {
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(i);
            cell = row.createCell(1);
            cell.setCellValue(planOTVo.getName());
            cell = row.createCell(2);
            cell.setCellValue(planOTVo.getCode());
            cell = row.createCell(3);
            cell.setCellValue(planOTVo.getDescription());
            cell = row.createCell(4);
            cell.setCellValue(planOTVo.getDeviation_report());
            cell = row.createCell(5);
            cell.setCellValue(planOTVo.getDifficulty_report());
            cell = row.createCell(6);
            cell.setCellValue(planOTVo.getReportTime());
            cell = row.createCell(7);
            cell.setCellValue(planOTVo.getBreadth());
            cell = row.createCell(8);
            cell.setCellValue(planOTVo.getCriticiailty());
            cell = row.createCell(9);
            cell.setCellValue(planOTVo.getStandard_deviation_value());
            cell = row.createCell(10);
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
    public void exportINExcel(HttpServletResponse response, String[] time, String[] group, String planId) throws IOException {
        String startTime = null;
        String endTime = null;
        if (time != null && !"".equals(time)) {
            startTime = time[0] + " 00:00:00";
            endTime = time[1] + " 23:59:59";
        }
        if(group != null && "null".equals(group[0]) && group.length==1){
            group =null;
        }
        List<PlanINVo> list = planOTMapper.exportINExcel(startTime, endTime, group, planId);
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
        cell.setCellValue("任务名称");
        cell = row.createCell(2);
        cell.setCellValue("指标编码");
        cell = row.createCell(3);
        cell.setCellValue("指标描述");
        cell = row.createCell(4);
        cell.setCellValue("权重");
        cell = row.createCell(5);
        cell.setCellValue("指标评定值");
        cell = row.createCell(6);
        cell.setCellValue("评定描述");
        cell = row.createCell(7);
        cell.setCellValue("汇报时间");
        cell = row.createCell(8);
        cell.setCellValue("更改次数");

        int i = 1;
        for (PlanINVo planINVo : list) {
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(i);
            cell = row.createCell(1);
            cell.setCellValue(planINVo.getName());
            cell = row.createCell(2);
            cell.setCellValue(planINVo.getCode());
            cell = row.createCell(3);
            cell.setCellValue(planINVo.getCodeDescription());
            cell = row.createCell(4);
            cell.setCellValue(planINVo.getWeights());
            cell = row.createCell(5);
            cell.setCellValue(planINVo.getOt_rating());
            cell = row.createCell(6);
            cell.setCellValue(planINVo.getDescription());
            cell = row.createCell(7);
            cell.setCellValue(planINVo.getReportTime());
            cell = row.createCell(8);
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
    public void exportRiskExcel(HttpServletResponse response, String[] time, String[] group, String planId, String projectId) throws IOException {
        List<RiskVo> list = null;
        String startTime = null;
        String endTime = null;
        if (time != null && !"".equals(time)) {
            startTime = time[0] + " 00:00:00";
            endTime = time[1] + " 23:59:59";
        }
        if (planId == null || "".equals(planId)) {
            list = planOTMapper.selectRiskProject(startTime, endTime, group, projectId);
        } else {
            list = planOTMapper.selectRiskByPlan(startTime, endTime, group, planId);

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
        cell.setCellValue("风险描述");
        cell = row.createCell(3);
        cell.setCellValue("提出人");
        cell = row.createCell(4);
        cell.setCellValue("提出时间");
        cell = row.createCell(5);
        cell.setCellValue("预防措施名称");
        cell = row.createCell(6);
        cell.setCellValue("涉及部门");
        cell = row.createCell(7);
        cell.setCellValue("预防措施描述");
        cell = row.createCell(8);
        cell.setCellValue("责任人");
        int i = 1;
        for (RiskVo riskVo : list) {
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(i);
            cell = row.createCell(1);
            cell.setCellValue(riskVo.getRiskName());
            cell = row.createCell(2);
            cell.setCellValue(riskVo.getRiskDescription());
            cell = row.createCell(3);
            cell.setCellValue(riskVo.getUserName());
            cell = row.createCell(4);
            Date date = riskVo.getCreateStamp();
            String timeFormat = null;
            if (date != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                timeFormat = sdf.format(riskVo.getCreateStamp());
            }
            cell.setCellValue(timeFormat);

            cell = row.createCell(5);
            cell.setCellValue(riskVo.getName());
            cell = row.createCell(6);
            cell.setCellValue(riskVo.getGroupName());
            cell = row.createCell(7);
            cell.setCellValue(riskVo.getPrecaution());
            cell = row.createCell(8);
            cell.setCellValue(riskVo.getUser_name());
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
        //查询所有父节点
        List<ProblemRickChainVo> pList = planOTMapper.selectPid();
        //查询所有节点
        List<ProblemRickChainVo> list = planOTMapper.problemRickChain();
        List<ProblemRickChainVo> result = new ArrayList<>();

        for (ProblemRickChainVo pVo : pList) {
            Map map = new HashMap();
            ProblemRickChainVo parent = pVo;
            map.put(parent.getId(), parent.getId());
            Map map1 = new HashMap();
            for (ProblemRickChainVo ProblemRickChainVo : list) {
                if (parent.getId().equals(ProblemRickChainVo.getPid())) {
                    map.put(ProblemRickChainVo.getId(), ProblemRickChainVo.getId());
                    map1.put(ProblemRickChainVo.getChildrenId(), ProblemRickChainVo.getChildrenId());
                    parent = ProblemRickChainVo;
                }
            }
            List<ProblemRickChainVo> children1red = new ArrayList<>();
            Map map2 = new HashMap();
            for (ProblemRickChainVo ProblemRickChainVo : list) {
                if (map1.get(ProblemRickChainVo.getId()) != null) {
                    map2.put(ProblemRickChainVo.getChildrenId(), ProblemRickChainVo.getChildrenId());
                    if (map.get(ProblemRickChainVo.getId()) == null) {
                        map.put(ProblemRickChainVo.getId(), ProblemRickChainVo.getId());
                        children1red.add(ProblemRickChainVo);
                    }
                }
            }
            if (children1red.size() != 0) {
                parent.setChildren(children1red);
            }
            recursion(map, map2, children1red, list);
            if (riskId != null) {
                if (map.get(riskId) != null) {
                    result.add(parent);
                }
            } else {
                result.add(parent);
            }
        }
        return result;
    }

    @Override
    public Map problemRickChainList(String riskId) {
        //返回map对象，其中有nodes属性，和edges属性
        Map<String,Object> resultMap = new HashMap<>();
        //节点属性
        List<Map<String,Object>> nodes = new ArrayList<>();
        //节点关系属性edges
        List<Map<String,Object>> edges = new ArrayList<>();

        List<String> noedIds = null;

        if("".equals(riskId) || riskId == null){
            //查询所有的风险节点
            nodes =  planOTMapper.selectAllNodes(noedIds);
            //查询所有的风险关系
            edges = planOTMapper.selectChain(noedIds);
        }else{
            //查询某个节点，及和它有关联的风险
            //先找到所有的父节点
            List<ProblemRickChainVo> pList = planOTMapper.selectPid();
            //再找到所有的节点关系，每个父节点及所有子节点都放在map中，
            List<ProblemRickChainVo> chainList = planOTMapper.problemRickChain();
            List<Map<String,Object>> nodeList = new ArrayList<>();
            //用于copy
            List<Map<String, Object>> noedIdsCopy = new ArrayList<>();
            for(ProblemRickChainVo problemRickChainVo : pList){
                Map parentMap = new HashMap();
                parentMap.put(problemRickChainVo.getId(),problemRickChainVo.getId());
                Map childMap = new HashMap();
                for(ProblemRickChainVo child : chainList){
                    if(child.getId().equals(problemRickChainVo.getId())){
                        childMap.put(child.getChildrenId(),child.getChildrenId());
                        parentMap.put(child.getChildrenId(),child.getChildrenId());
                    }
                }
                //递归获取所有的子节点
                if(childMap.size() != 0){
                    getAllChild(childMap,parentMap,chainList);
                }
                nodeList.add(parentMap);
            }
            //最后如果map中有这个子节点，就取出所有的节点
            noedIds = new ArrayList<>();
            //相关的节点都放在map中
            Map nodeMap = new HashMap();
            noedIdsCopy.addAll(nodeList);
            for(Map map : nodeList){
                List<String> noedId = null;
                if(map.get(riskId) != null){
                    nodeMap = map;
                    noedId = new ArrayList(map.values());
                    noedIdsCopy.remove(map);
                    noedIds.addAll(noedId);
                }
                if(map.size() == 1){
                    noedIdsCopy.remove(map);
                }
            }
            if(nodeMap.size() != 0 && nodeMap.size() != 1){
                //再次递归查询有关的所有节点
                nodeList.clear();
                nodeList.addAll(noedIdsCopy);
                getAllFather(nodeList,noedIdsCopy,noedIds,nodeMap);
            }

            if(noedIds.size() != 0){
                //查询所有跟riskId有关的风险
                nodes =  planOTMapper.selectAllNodes(noedIds);
                //查询所有跟riskId有关的风险关系
                edges = planOTMapper.selectChain(noedIds);
            }
        }
        resultMap.put("nodes",nodes);
        resultMap.put("edges",edges);
        return resultMap;
    }

    private void getAllFather(List<Map<String, Object>> nodeList, List<Map<String, Object>> noedIdsCopy, List<String> noedIds, Map nodeMap) {

        if(nodeList.size() == 0){
            return;
        }
        for(Map map : nodeList){
            List<String> noedId = null;
            for(Object node : map.keySet()){
                if(nodeMap.get(node) != null){
                    Map combineResultMap = new HashMap();
                    combineResultMap.putAll(map);
                    combineResultMap.putAll(nodeMap);
                    noedId = new ArrayList(combineResultMap.values());
                    noedIds.addAll(noedId);
                    nodeMap = combineResultMap;
                    noedIdsCopy.remove(map);
                    break;
                }
            }
            break;
        }
        if(nodeList.size() != noedIdsCopy.size()){
            nodeList.clear();
            nodeList.addAll(noedIdsCopy);
            getAllFather(nodeList, noedIdsCopy,noedIds,nodeMap);
        }else{
            return;
        }

    }

    private void getAllChild(Map childMap, Map parentMap, List<ProblemRickChainVo> chainList) {
        if(childMap.size() == 0){
            return;
        }else{
            Map thildMap = new HashMap();
            for(Object obj : childMap.keySet()){
                for(ProblemRickChainVo child : chainList){
                    if(child.getId().equals(obj)){
                        thildMap.put(child.getChildrenId(),child.getChildrenId());
                        parentMap.put(child.getChildrenId(),child.getChildrenId());
                    }
                }
            }
            getAllChild(thildMap,parentMap,chainList);
        }

    }


    private ProblemRickChainVo recursion(Map map1, Map map2, List<ProblemRickChainVo> children1red, List<ProblemRickChainVo> list) {
        List<ProblemRickChainVo> children2red = new ArrayList<>();
        for (ProblemRickChainVo children2 : children1red) {
            Map map3 = new HashMap();
            for (ProblemRickChainVo ProblemRickChainVo : list) {
                if (map2.get(ProblemRickChainVo.getId()) != null) {
                    map3.put(ProblemRickChainVo.getChildrenId(), ProblemRickChainVo.getChildrenId());
                    if (map1.get(ProblemRickChainVo.getId()) == null) {
                        map1.put(ProblemRickChainVo.getId(), ProblemRickChainVo.getId());
                        children2red.add(ProblemRickChainVo);
                    }
                }
            }
            if (children2red.size() != 0) {
                children2.setChildren(children2red);
                recursion(map1, map3, children2red, list);
            } else {
                return null;
            }
        }
        return null;
    }
}
