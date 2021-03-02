package org.jeecg.modules.workflow.service.impl;

import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.jeecg.modules.workflow.entity.WorkflowForm;
import org.jeecg.modules.workflow.entity.WorkflowProcess;
import org.jeecg.modules.workflow.mapper.ProcessMapper;
import org.jeecg.modules.workflow.mapper.WorkflowFormMapper;
import org.jeecg.modules.workflow.service.IWorkflowHistoryService;
import org.jeecg.modules.workflow.vo.ProcessInstanceVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xduan
 * @version 2020/7/6
 */
@Service
public class WorkflowHistoryServiceImpl implements IWorkflowHistoryService {
    @Resource
    private ProcessMapper processMapper;

    @Resource
    private WorkflowFormMapper formMapper;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;


    /**
     * 获取流程历史任务列表
     *
     * @param pageNo
     * @param pageSize
     * @param processName
     * @param processDefId
     * @return
     */
    @Override
    public IPage<ProcessInstanceVO> getHistoryTaskList(Integer pageNo, Integer pageSize, String processName, String processDefId) {
        IPage<ProcessInstanceVO> page = new Page<>(pageNo, pageSize);
        //流程历史任务列表查询对象
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery().finished();
        if (StringUtils.hasText(processName)) {
            historicTaskInstanceQuery.processDefinitionName(processName);
        }
        if (StringUtils.hasText(processDefId)) {
            historicTaskInstanceQuery.processDefinitionId(processDefId);
        }
        //流程实例总数
        long count = historicTaskInstanceQuery.count();
        int totalPage = PageUtil.totalPage((int) count, pageSize);
        page.setPages(totalPage);
        page.setCurrent(pageNo);
        page.setTotal(count);
        int start = PageUtil.getStart(pageNo, pageSize);
        List<HistoricTaskInstance> historicTaskInstances = historicTaskInstanceQuery.listPage(start, start + pageSize);
        List<ProcessInstanceVO> result = getHistoryTaskInstanceList(historicTaskInstances);
        page.setRecords(result);
        return page;
    }

    /**
     * 获取历史流程列表
     *
     * @param pageNo
     * @param pageSize
     * @param processName
     * @param processDefId
     * @return
     */
    @Override
    public IPage<ProcessInstanceVO> getHistoryProcessInstanceList(Integer pageNo, Integer pageSize, String processName, String processDefId) {

        IPage<ProcessInstanceVO> page = new Page<>(pageNo, pageSize);
        //流程历史任务列表查询对象
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        if (StringUtils.hasText(processName)) {
            historicProcessInstanceQuery.processDefinitionName(processName);
        }
        if (StringUtils.hasText(processDefId)) {
            historicProcessInstanceQuery.processDefinitionId(processDefId);
        }
        //流程实例总数
        long count = historicProcessInstanceQuery.count();
        int totalPage = PageUtil.totalPage((int) count, pageSize);
        page.setPages(totalPage);
        page.setCurrent(pageNo);
        page.setTotal(count);
        int start = PageUtil.getStart(pageNo, pageSize);
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage(start, start + pageSize);
        List<ProcessInstanceVO> result = getHistoryProcessInstanceList(historicProcessInstances);
        page.setRecords(result);
        return page;
    }

    /**
     * 获取流程历史任务的信息
     *
     * @param list
     * @return
     */
    private List<ProcessInstanceVO> getHistoryProcessInstanceList(List<HistoricProcessInstance> list) {
        List<ProcessInstanceVO> result = new ArrayList<>(list.size());
        for (HistoricProcessInstance processInstance : list) {
            Date startTime = processInstance.getStartTime();
            ProcessInstanceVO vo = new ProcessInstanceVO();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
            vo.setProcessInstanceId(processInstance.getId());
            vo.setProcessDefinitionName(processDefinition.getName());
            vo.setProcessDefId(processDefinition.getId());
            vo.setStartTime(startTime);
            // ProcessInstance processInstance = runtimeService.createNativeProcessInstanceQuery().sql("SELECT * FROM act_hi_procinst p WHERE p.PROC_INST_ID_ = #{PROC_INST_ID_}").parameter("PROC_INST_ID_", taskInstance.getProcessInstanceId()).singleResult();
            vo.setStartUsername(processInstance.getStartUserId());
            Date endTime = processInstance.getEndTime();
            vo.setEndTime(endTime);
            if (endTime == null) {
                vo.setStatus("处理中");
                endTime = new Date();
            } else {
                vo.setStatus(processInstance.getDeleteReason());
            }
            //获取时间差
            long diff = endTime.getTime() - startTime.getTime();//这样得到的差值是毫秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            vo.setSpendTimes("" + days + "天" + hours + "小时" + minutes + "分");
            WorkflowForm workflowForm = getWorkflowFormInfo(processDefinition.getDeploymentId());
            vo.setTitle(workflowForm.getTitleExp());
            result.add(vo);
        }
        return result;
    }


    /**
     * 获取流程历史任务的信息
     *
     * @param list
     * @return
     */
    private List<ProcessInstanceVO> getHistoryTaskInstanceList(List<HistoricTaskInstance> list) {
        List<ProcessInstanceVO> result = new ArrayList<>(list.size());
        for (HistoricTaskInstance taskInstance : list) {
            Date startTime = taskInstance.getStartTime();
            ProcessInstanceVO vo = new ProcessInstanceVO();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(taskInstance.getProcessDefinitionId()).singleResult();
            vo.setProcessInstanceId(taskInstance.getProcessInstanceId());
            vo.setProcessDefinitionName(processDefinition.getName());
            vo.setProcessDefId(processDefinition.getId());
            vo.setStartTime(startTime);
            ProcessInstance processInstance = runtimeService.createNativeProcessInstanceQuery().sql("SELECT * FROM act_hi_procinst p WHERE p.PROC_INST_ID_ = #{PROC_INST_ID_}").parameter("PROC_INST_ID_", taskInstance.getProcessInstanceId()).singleResult();
            vo.setStartUsername(processInstance.getStartUserId());
            vo.setTaskId(taskInstance.getId());
            vo.setTaskName(taskInstance.getName());
            vo.setEndTime(taskInstance.getEndTime());
            vo.setAssigneeName(taskInstance.getAssignee());
            vo.setEndTime(taskInstance.getEndTime());
            //获取时间差
            long diff = taskInstance.getEndTime().getTime() - startTime.getTime();//这样得到的差值是毫秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            vo.setSpendTimes("" + days + "天" + hours + "小时" + minutes + "分");
            WorkflowForm workflowForm = getWorkflowFormInfo(processDefinition.getDeploymentId());
            vo.setTitle(workflowForm.getTitleExp());
            result.add(vo);
        }
        return result;
    }

    /**
     * 获取表单配置信息
     *
     * @param deploymentId
     * @return
     */
    private WorkflowForm getWorkflowFormInfo(String deploymentId) {
        Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentId(deploymentId).singleResult();
        LambdaQueryWrapper<WorkflowProcess> qw = new QueryWrapper<WorkflowProcess>()
                .lambda().eq(WorkflowProcess::getProcessName, deployment.getName())
                .eq(WorkflowProcess::getProcessKey, deployment.getKey());
        WorkflowProcess workflowProcess = processMapper.selectOne(qw);
        LambdaQueryWrapper<WorkflowForm> qw2 = new QueryWrapper<WorkflowForm>()
                .lambda().eq(WorkflowForm::getProcessId, workflowProcess.getId());
        List<WorkflowForm> workflowForms = formMapper.selectList(qw2);
        if (workflowForms == null || workflowForms.size() == 0) {
            return new WorkflowForm();
        }
        return workflowForms.get(0);
    }
}
