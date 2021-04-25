package org.jeecg.modules.P2020052.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.jeecg.modules.P2020052.mapper.HomeTableMapper;
import org.jeecg.modules.P2020052.pojo.*;
import org.jeecg.modules.P2020052.service.HomeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@Service
@DS("multi-datasource1")
public class HomeTableServiceImpl implements HomeTableService {

    @Autowired
    HomeTableMapper homeTableMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String,Object> projectType() {
        Map map = new HashMap();
        List<ProjectTypeVo> list  = homeTableMapper.projectType();
        //计算百分比
        Double total = 0.0;
        DecimalFormat df = new DecimalFormat("#.0000");
        //            list.add(projectTypeVo);
        for(ProjectTypeVo projectTypeVo:list) total += projectTypeVo.getCount();
        for(ProjectTypeVo projectTypeVo:list){
            String percentage = df.format(projectTypeVo.getCount()/total);
            projectTypeVo.setPercentage(Double.parseDouble(percentage));
        }
        map.put("list",list);
        map.put("total",total);
        return map;
    }

    @Override
    public List<ProjectTypeVo> projectLevel() {
        Map map = homeTableMapper.projectLevel();
        Double total = Double.parseDouble(map.get("总数").toString());
        DecimalFormat df = new DecimalFormat("#.0000");
        List<ProjectTypeVo> list = new ArrayList<>();
        for(int i=1;i<6;i++){
            ProjectTypeVo projectTypeVo = new ProjectTypeVo();
            if(i==1){
                projectTypeVo.setId("1");
                projectTypeVo.setName("S级");
                Double count= Double.parseDouble(map.get("S级").toString());
                String percentage = df.format(count/total);
                projectTypeVo.setCount(count );
                projectTypeVo.setPercentage((Double.parseDouble(percentage)));
                list.add(projectTypeVo);
            }
            if(i==2){
                projectTypeVo.setId("2");
                projectTypeVo.setName("A级");
                Double count= Double.parseDouble(map.get("A级").toString());
                String percentage = df.format(count/total);
                projectTypeVo.setCount(count );
                projectTypeVo.setPercentage((Double.parseDouble(percentage)));
                list.add(projectTypeVo);
            }
            if(i==3){
                projectTypeVo.setId("3");
                projectTypeVo.setName("B级");
                Double count= Double.parseDouble(map.get("B级").toString());
                String percentage = df.format(count/total);
                projectTypeVo.setCount(count );
                projectTypeVo.setPercentage((Double.parseDouble(percentage)));
                list.add(projectTypeVo);
            }
            if(i==4){
                projectTypeVo.setId("4");
                projectTypeVo.setName("C级");
                Double count= Double.parseDouble(map.get("C级").toString());
                String percentage = df.format(count/total);
                projectTypeVo.setCount(count );
                projectTypeVo.setPercentage((Double.parseDouble(percentage)));
                list.add(projectTypeVo);
            }
            if(i==5){
                projectTypeVo.setId("5");
                projectTypeVo.setName("D级");
                Double count= Double.parseDouble(map.get("D级").toString());
                String percentage = df.format(count/total);
                projectTypeVo.setCount(count );
                projectTypeVo.setPercentage((Double.parseDouble(percentage)));
                list.add(projectTypeVo);
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> riskPrevention() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map= homeTableMapper.riskPrevention();
        //获取风险预防总数
        DecimalFormat df = new DecimalFormat("#.0000");
        Double sum = 0.0;
        Map<String, Object> mapOne = new HashMap();
        mapOne.put("name","未认可");
        mapOne.put("count",map.get("未认可"));
        sum += Double.parseDouble(map.get("未认可").toString());
        Map<String, Object> mapTwo = new HashMap();
        mapTwo.put("name","已认可");
        mapTwo.put("count",map.get("已认可"));
        sum +=Double.parseDouble(map.get("已认可").toString());
        mapOne.put("proportion",Double.parseDouble(df.format(Double.parseDouble(map.get("未认可").toString())/sum)));
        mapTwo.put("proportion",Double.parseDouble(df.format(Double.parseDouble(map.get("已认可").toString())/sum)));
        list.add(mapOne);
        list.add(mapTwo);
        return list;
    }

    @Override
    public List<Map<String, Object>> selectRiskTable() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        Date date = new Date();
        String str = df.format(date);
        str += " 23:59:59";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long end=sdf.parse(str).getTime();
//        long end = new Date().getTime();
        //过去一月
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -2);
        Date m = c.getTime();
         long start = m.getTime();
        double weeks = (end - start) / (7.0*1000 * 60 * 60 * 24);
        long week = (long) Math.ceil(weeks);
        //返回list数组
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(long i=1;i<week;i++){
            //获取每周风险和措施
            Long startLong = null;

            Long endLong = null;
            String startStr =null;
            String endStr = null;
            if(i != week-1){
                startLong = start + (i - 1) * 1000 * 60 * 60 * 24 * 7;
                // 每周结束时间
                endLong = start + i * 1000 * 60 * 60 * 24 * 7;
                startStr = date(startLong);
                endStr = date(endLong);
            }else{
                startLong = start + (i - 1) * 1000 * 60 * 60 * 24 * 7;
                // 每周结束时间
                startStr = date(startLong);
                endStr = str;
            }


            //查询当前时间段内的风险及预防措施
            RiskMeasureVo riskMeasureVor  =  homeTableMapper.selectRiskTable(startStr,endStr);
            // 每周开始时间
            Map<String, Object> staffMap = new HashMap<String, Object>();
            Map<String, Object> markMap = new HashMap<String, Object>();
            staffMap.put("name","本周风险");
            staffMap.put("sum",riskMeasureVor.getRisk());
            staffMap.put("week",i);
            staffMap.put("startTime",startStr);
            staffMap.put("endTime",endStr);

            markMap.put("name","本周预防措施");
            markMap.put("sumBenchmark",riskMeasureVor.getMeasures());
            markMap.put("week",i);
            markMap.put("startTime",startStr);
            markMap.put("endTime",endStr);
            list.add(staffMap);
            list.add(markMap);
        }
        return list;
    }

    @Override
    public List<RiskVo> selectRisk(String startTime, String endTime) {
        return homeTableMapper.selectRisk(startTime,endTime);
    }

    @Override
    public List<RiskVo> selectMeaTable(String startTime, String endTime) {
        return homeTableMapper.selectMeaTable(startTime,endTime);
    }

    @Override
    public List<RiskVo> riskPreventionetails(int state) {
        return homeTableMapper.riskPreventionetails(state);
    }

    @Override
    public List<PiplanActivityVo> WorkDelayTable() {
        List<PiplanActivityVo> list = (List<PiplanActivityVo>) redisTemplate.opsForValue().get("WorkDelayTable");
        return list;
    }


    //long类型时间转String类型时间
    public String date(long time){
        Date date = new Date(time);
        SimpleDateFormat sim=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String timeStr=sim.format(date);
        return timeStr;
    }

}
