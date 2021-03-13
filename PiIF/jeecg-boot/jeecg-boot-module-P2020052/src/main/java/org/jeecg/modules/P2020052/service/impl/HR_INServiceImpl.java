package org.jeecg.modules.P2020052.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.P2020052.mapper.HR_INMapper;
import org.jeecg.modules.P2020052.pojo.PlanINVo;
import org.jeecg.modules.P2020052.service.HR_INService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@DS("multi-datasource1")
public class HR_INServiceImpl implements HR_INService {

    @Autowired
    HR_INMapper hr_inMapper;

//    @Autowired
//    PlanOTMapper planOTMapper;

    @Override
    public IPage<PlanINVo> selectINTable(Page page, String[] time, String[] group, String planId) {
        String startTime = null;
        String endTime = null;
        if (time != null && !"".equals(time)) {
            startTime = time[0];
            endTime = time[1];
        }

        return hr_inMapper.selectINTable(page, startTime, endTime, group,planId);
    }



    // 计算绝对值
    public double abs(double a) {
        return (a > 0) ? a : -a;
    }

}
