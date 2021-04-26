package org.jeecg.modules.P2020052.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.P2020052.pojo.PiplanActivityVo;
import org.jeecg.modules.P2020052.pojo.ProjectRiskVo;
import org.jeecg.modules.P2020052.pojo.TaskVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ScheduleTaskMapper {
    void taskExecution();

    List<String> getProjectIds();

    List<TaskVo> getTaskById(@Param("projectId") String projectId);

    List<TaskVo> getTaskInById(@Param("activiteId")String activiteId);

    List<TaskVo> getTaskOTById(@Param("activiteId")String activiteId);

    List<String> selectAllId();

    List<ProjectRiskVo> ProjectRiskTable(@Param("projectId")String projectId);

    PiplanActivityVo projectWeekCycle(@Param("projectId")String projectId);

    String getProName(@Param("projectId")String projectId);

    Map getYaxis(@Param("projectId")String projectId);
}
