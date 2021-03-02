package org.jeecg.modules.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.workflow.vo.ProcessInstanceVO;

/**
 * @author xduan
 * @version 2020/7/2
 * @desc 流程运行时相关的服务
 */
public interface IWorkflowRuntimeService {

    /**
     * 获取所有流程实例
     * @param pageNo
     * @param pageSize
     * @param processInstanceId
     * @param startUsername
     * @return
     */
    IPage<ProcessInstanceVO> getInstanceList(Integer pageNo, Integer pageSize, String processName, String startUsername);

    /**
     * 启动流程实例
     * @param processId  流程设计对象的id
     */
    void startProcess(String processId);

    /**
     * 挂起流程实例
     * @param processInstanceId
     */
    void suspended(String processInstanceId);

    /**
     * 激活流程实例
     * @param processInstanceId
     */
    void active(String processInstanceId);

    /**
     * 关闭流程实例
     * @param processInstanceId
     */
    void close(String processInstanceId);

    /**
     * 委派任务
     * @param taskId
     * @param username
     */
    void taskEntrust(String taskId, String username);
}
