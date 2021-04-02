package org.jeecg.modules.P2020052.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.P2020052.pojo.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanOTMapper {
    IPage<PlanOTVo> OTTable(Page page, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("group") String[] group,@Param("planId") String planId);

    List<GroupVo>  selectGroup();

    IPage<PlanINVo> selectINTable(Page page, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("group") String[] group, @Param("planId") String planId);

    List<RiskVo>  selectRiskProject(@Param("startTime")String startTime,@Param("endTime") String endTime, String[] group, @Param("projectId")String projectId);


    List<PlanOTVo> exportOTExcel(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("group") String[] group, @Param("planId")String planId);

    List<PlanINVo> exportINExcel(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("group") String[] group, @Param("planId")String planId);

    List<ProblemRickChainVo> problemRickChain();

    List<ProblemRickChainVo> selectPid();

    List<RiskVo> selectRiskByPlan(@Param("startTime")String startTime,@Param("endTime") String endTime, String[] group, String planId);

    String selectNameById(@Param("projectId")String projectId);

    List<PiplanActivityVo> WorkDelayTable(@Param("startTime")String startTime, @Param("endTime")String endTime,@Param("group") String[] group, @Param("projectId")String projectId,@Param("planId")String planId);
}
