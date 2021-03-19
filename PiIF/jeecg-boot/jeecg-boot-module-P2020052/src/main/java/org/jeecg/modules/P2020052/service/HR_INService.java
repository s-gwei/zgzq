package org.jeecg.modules.P2020052.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.P2020052.pojo.PlanINVo;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.List;

@Repository
public interface HR_INService {
    IPage<PlanINVo> selectINTable(Page page, String[] time, String[] group, String planId);
}
