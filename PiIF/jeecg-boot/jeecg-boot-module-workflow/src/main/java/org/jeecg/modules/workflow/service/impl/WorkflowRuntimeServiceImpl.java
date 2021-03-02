package org.jeecg.modules.workflow.service.impl;

import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.workflow.entity.WorkflowForm;
import org.jeecg.modules.workflow.entity.WorkflowProcess;
import org.jeecg.modules.workflow.mapper.ProcessMapper;
import org.jeecg.modules.workflow.mapper.WorkflowFormMapper;
import org.jeecg.modules.workflow.service.IWorkflowRuntimeService;
import org.jeecg.modules.workflow.vo.ProcessInstanceVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xduan
 * @version 2020/7/2
 * @desc 流程运行时相关的服务实现
 */
@Service
public class WorkflowRuntimeServiceImpl implements IWorkflowRuntimeService {

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

    /**
     * 获取所有流程实例
     *
     * @param pageNo
     * @param pageSize
     * @param processName
     * @param startUsername
     * @return
     */
    @Override
    public IPage<ProcessInstanceVO> getInstanceList(Integer pageNo, Integer pageSize, String processName, String startUsername) {
        IPage<ProcessInstanceVO> page = new Page<>(pageNo, pageSize);
        //流程实例查询对象
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        if (StringUtils.hasText(processName)){
            processInstanceQuery.processDefinitionName(processName);
        }
        if (StringUtils.hasText(startUsername)){
            processInstanceQuery.startedBy(startUsername);
        }
        //流程实例总数
        long count = processInstanceQuery.count();
        int totalPage = PageUtil.totalPage((int) count, pageSize);
        page.setPages(totalPage);
        page.setCurrent(pageNo);
        page.setTotal(count);
        int start = PageUtil.getStart(pageNo, pageSize);
        List<ProcessInstance> list = processInstanceQuery.listPage(start, start + pageSize);
        List<ProcessInstanceVO> result = getProcessInstance(list);
        page.setRecords(result);
        return page;
    }

    /**
     * 根据流程实例获取相关信息
     *
     * @param list
     * @return
     */
    private List<ProcessInstanceVO> getProcessInstance(List<ProcessInstance> list) {
        List<ProcessInstanceVO> result = new ArrayList<>(list.size());
        for (ProcessInstance processInstance : list) {
            Date startTime = processInstance.getStartTime();
            ProcessInstanceVO vo = new ProcessInstanceVO();
            vo.setIsSuspended(processInstance.isSuspended());
            vo.setProcessInstanceId(processInstance.getId());
            vo.setProcessDefinitionName(processInstance.getProcessDefinitionName());
            vo.setProcessDefId(processInstance.getProcessDefinitionId());
            vo.setStartTime(startTime);
            vo.setStartUsername(processInstance.getStartUserId());
            vo.setParentId(processInstance.getParentId());
            //获取当前任务
            Task task = taskService.createTaskQuery()
                    .processInstanceId(processInstance.getId()).singleResult();
            vo.setTaskId(task.getId());
            vo.setTaskName(task.getName());
            vo.setAssigneeName(task.getAssignee());
            //获取时间差
            long diff = System.currentTimeMillis() - startTime.getTime();//这样得到的差值是毫秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            vo.setSpendTimes("" + days + "天" + hours + "小时" + minutes + "分");

            WorkflowForm workflowForm = getWorkflowFormInfo(processInstance);
            vo.setTitle(workflowForm.getTitleExp());
            result.add(vo);
        }
        return result;
    }

    /**
     * 获取表单配置信息
     * @param processInstance
     * @return
     */
    private WorkflowForm getWorkflowFormInfo(ProcessInstance processInstance) {
        Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentId(processInstance.getDeploymentId()).singleResult();
        LambdaQueryWrapper<WorkflowProcess> qw = new QueryWrapper<WorkflowProcess>()
                .lambda().eq(WorkflowProcess::getProcessName, deployment.getName())
                .eq(WorkflowProcess::getProcessKey, deployment.getKey());
        WorkflowProcess workflowProcess = processMapper.selectOne(qw);
        LambdaQueryWrapper<WorkflowForm> qw2 = new QueryWrapper<WorkflowForm>()
                .lambda().eq(WorkflowForm::getProcessId, workflowProcess.getId());
        List<WorkflowForm> workflowForms = formMapper.selectList(qw2);
        if (workflowForms == null || workflowForms.size()==0){
            return new WorkflowForm();
        }
        return workflowForms.get(0);
    }

    /**
     * 启动流程实例
     *
     * @param processId 流程设计对象的id
     */
    @Override
    public void startProcess(String processId) {
        //获取流程设计对象
        WorkflowProcess workflowProcess = processMapper.selectById(processId);
        ProcessDefinition processDefinition = getProcessDefinition(workflowProcess);

        LoginUser loginUser = TokenUtils.getLoginUser();
        //设置启动人信息
        Authentication.setAuthenticatedUserId(loginUser.getUsername());
        //启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
    }

    /**
     * 挂起流程实例
     *
     * @param processInstanceId
     */
    @Override
    public void suspended(String processInstanceId) {
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    /**
     * 激活流程实例
     * @param processInstanceId
     */
    @Override
    public void active(String processInstanceId) {
        runtimeService.activateProcessInstanceById(processInstanceId);
    }

    /**
     * 关闭流程实例
     *
     * @param processInstanceId
     */
    @Override
    public void close(String processInstanceId) {
       runtimeService.deleteProcessInstance(processInstanceId,"关闭");
    }

    /**
     * 委派任务
     *
     * @param taskId
     * @param username
     */
    @Override
    public void taskEntrust(String taskId, String username) {
        taskService.setAssignee(taskId,username);
    }

    /**
     * 根据流程设计对象获取  对应的 <p>最新的流程定义对象</p>
     *
     * @param workflowProcess 流程设计对象
     * @return
     */
    private ProcessDefinition getProcessDefinition(WorkflowProcess workflowProcess) {
        //根据 workflowProcess的name和 key获取部署对象的实例
        Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentKey(workflowProcess.getProcessKey()).deploymentName(workflowProcess.getProcessName()).latest().singleResult();
        //根据流程部署对象获取 流程定义对象
        return repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
    }

}
