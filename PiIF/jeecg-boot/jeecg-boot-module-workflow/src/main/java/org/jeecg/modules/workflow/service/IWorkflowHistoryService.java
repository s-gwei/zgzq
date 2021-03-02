package org.jeecg.modules.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.workflow.vo.ProcessInstanceVO;

/**
 * @author xduan
 * @version 2020/7/6
 */
public interface IWorkflowHistoryService {
    /**
     * 获取流程历史任务列表
     * @param pageNo
     * @param pageSize
     * @param processName
     * @param processDefId
     * @return
     */
    IPage<ProcessInstanceVO> getHistoryTaskList(Integer pageNo, Integer pageSize, String processName, String processDefId);

    /**
     * 获取历史流程列表
     * @param pageNo
     * @param pageSize
     * @param processName
     * @param processDefId
     * @return
     */
    IPage<ProcessInstanceVO> getHistoryProcessInstanceList(Integer pageNo, Integer pageSize, String processName, String processDefId);
}
