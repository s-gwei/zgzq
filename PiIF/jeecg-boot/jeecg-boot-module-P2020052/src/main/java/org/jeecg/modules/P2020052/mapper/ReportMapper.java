package org.jeecg.modules.P2020052.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.P2020052.pojo.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReportMapper {
    List<PiplanActivityVo> pertTable(@Param("activeId") String activeId);

    List<GroupRiskVo> SectorRiskFactor(@Param("userId") String[] userId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    List<GroupRiskVo> SectorRiskFactor1(@Param("userId") String userId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    List<ProjectRiskVo> ProjectRiskTable(@Param("projectId")String projectId);

    List<UserVo> groupUser(@Param("projectId")String projectId);

    List<TaskVo> taskExecution(@Param("projectId")String projectId, @Param("userId")String[] userId);

    PiplanActivityVo projectWeekCycle(@Param("projectId")String id);

    List<String> selectAllActId(@Param("projectId")String projectId, @Param("userId")String[] userId);

    List<TaskVo> selectInById(@Param("id")TaskId id);

    List<TaskVo> selectOTById(@Param("id")TaskId id);

    List<TaskVo> selectINOTById(@Param("id")String id);


    List<String> selectAllId();
}
