package org.jeecg.modules.P2020052.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.P2020052.pojo.*;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Repository
public interface PlanOTService {
    IPage<PlanOTVo> OTTable(Page page, String[] time, String[] group, String planId);

    List<GroupVo> selectGroup();

    IPage<PlanINVo> selectINTable(Page page, String[] time, String[] group, String planId);

    List<RiskVo>  selectRiskTable(String[] time, String[] group,  String planId,String projectId);

    List<PiplanActivityVo> WorkDelayTable(String[] time, String[] projectId, String id, String flag) throws ParseException;

    void exportOTExcel(HttpServletResponse response, String[] time, String[] group, String planId) throws IOException;

    void exportINExcel(HttpServletResponse response, String[] time, String[] group, String planId) throws IOException;

    void exportRiskExcel(HttpServletResponse response, String[] time, String[] group, String id, String planId) throws IOException;

    List problemRickChain(String riskId);

    Map  problemRickChainList(String riskId);
}
