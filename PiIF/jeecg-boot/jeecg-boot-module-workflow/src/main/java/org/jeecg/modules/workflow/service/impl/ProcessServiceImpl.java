package org.jeecg.modules.workflow.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.PageUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.workflow.entity.WorkflowProcess;
import org.jeecg.modules.workflow.mapper.ProcessMapper;
import org.jeecg.modules.workflow.service.IProcessService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xduan
 * @version 2020/6/19
 * @desc 流程定义 服务的接口实现层
 */
@Service
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, WorkflowProcess> implements IProcessService {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    /**
     * 新建流程 ，需要判断 流程名和流程定义key的唯一性
     *
     * @param entity
     * @return
     */
    @Override
    public boolean save(WorkflowProcess entity) {
        Integer integer = baseMapper.selectCount(new QueryWrapper<WorkflowProcess>()
                .lambda().eq(WorkflowProcess::getProcessName, entity.getProcessName()));
        if (integer > 0) {
            throw new JeecgBootException("流程名称不能重复");
        }

        integer = baseMapper.selectCount(new QueryWrapper<WorkflowProcess>()
                .lambda().eq(WorkflowProcess::getProcessKey, entity.getProcessKey()));
        if (integer > 0) {
            throw new JeecgBootException("流程定义Key不能重复");
        }
        entity.setCreateTime(new Date());
        //获取登录用户
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        entity.setCreateBy(sysUser.getUsername());
        return super.save(entity);
    }

    /**
     * 发布流程
     *
     * @param id
     */
    @Override
    public void deploy(String id) {
        WorkflowProcess workflowProcess = baseMapper.selectById(id);
        if (workflowProcess == null) {
            throw new JeecgBootException("实例不存在");
        }
        DeploymentBuilder deployment = repositoryService.createDeployment();
        String generate = UUIDGenerator.generate();
        //部署文件名
        String wfFileName = "process" + generate + ".bpmn20.xml";
        deployment.addString(wfFileName, workflowProcess.getProcessXml()).name(workflowProcess.getProcessName()).key(workflowProcess.getProcessKey()).deploy();
        //修改部署状态
        workflowProcess.setProcessStatus(true);
        setEditInfo(workflowProcess);
        baseMapper.updateById(workflowProcess);
    }

    @Override
    public void uploadProcess(HttpServletRequest request) {
        String ctxPath = uploadpath;
        String fileName = null;
        String bizPath = "files";
        try {
            String nowday = new SimpleDateFormat("yyyyMMdd").format(new Date());
            File file = new File(ctxPath + File.separator + bizPath + File.separator + nowday);
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
            String orgName = mf.getOriginalFilename();// 获取文件名
            fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
            String savePath = file.getPath() + File.separator + fileName;
            File saveFile = new File(savePath);
            //文件保存
            FileCopyUtils.copy(mf.getBytes(), saveFile);
            //流程id
            String id = request.getParameter("id");
            WorkflowProcess workflowProcess = baseMapper.selectById(id);
            //文件路径
            workflowProcess.setProcessXmlPath(savePath);
            //文件内容
            String xml = IOUtils.toString(mf.getBytes(), "UTF-8");
            workflowProcess.setProcessXml(xml);
            setEditInfo(workflowProcess);
            baseMapper.updateById(workflowProcess);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public JSONArray getNode(String id) {
        WorkflowProcess workflowProcess = baseMapper.selectById(id);
        Assert.notNull(workflowProcess, "流程定义对象不存在");
        Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentKey(workflowProcess.getProcessKey()).deploymentName(workflowProcess.getProcessName()).latest().singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .latestVersion().deploymentId(deployment.getId()).singleResult();
        Assert.notNull(processDefinition, "流程未发布");
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        //process中包含所有的节点信息,包括流程线
        Process process = bpmnModel.getProcessById(processDefinition.getKey());
        Assert.notNull(process, "process is null");
        //获取所有节点信息
        Collection<FlowElement> flowElements = process.getFlowElements();
        JSONArray array = new JSONArray();

        for (FlowElement flowElement : flowElements) {
            //跳过流程线
            if (flowElement instanceof SequenceFlow) {
                continue;
            }
            String nodeId = flowElement.getId();
            String name = flowElement.getName();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nodeId", nodeId);
            jsonObject.put("nodeName", name);
            array.add(jsonObject);
        }
        return array;
    }

    /**
     * 获取当前流程的历史版本
     *
     * @param pageNo
     * @param pageSize
     * @param processId
     * @return
     */
    @Override
    public IPage<JSONObject> getVersionList(Integer pageNo, Integer pageSize, String processId) {
        WorkflowProcess workflowProcess = baseMapper.selectById(processId);
        ProcessDefinition processDefinition = getProcessDefinition(workflowProcess);
        //根据流程定义对象获取流程定义的key，然后获取历史版本
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinition.getKey()).list();
        List<JSONObject> jsonObjects = object2JSONObject(list);
        return PageUtil.paging(pageNo, pageSize, jsonObjects);
    }

    /**
     * 根据流程设计对象获取  对应的 <p>最新的流程定义对象</p>
     * @param workflowProcess  流程设计对象
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

    /**
     * 获取流程的xml配置
     *
     * @param processDefId 流程定义对象id
     * @return
     */
    @Override
    public String getProcessXml(String processDefId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefId).singleResult();
        String deploymentId = processDefinition.getDeploymentId();
        InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, processDefinition.getResourceName());
        String processXml = "";
        try {
            processXml = IOUtils.toString(resourceAsStream, "UTF-8");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return processXml;
    }

    /**
     * 挂起流程，会同时挂起相关的流程实例
     *
     * @param processDefId 流程定义对象id
     */
    @Override
    public void suspensionProcess(String processDefId) {
        boolean flag = repositoryService.isProcessDefinitionSuspended(processDefId);
        if (flag){
            throw new JeecgBootException("流程已挂起");
        }
        repositoryService.suspendProcessDefinitionById(processDefId);
    }

    /**
     * 删除流程定义对象,同时删除部署对象，如果还有运行时的流程实例，会抛出异常
     *
     * @param processDefId  流程定义对象id
     */
    @Override
    public void deleteProcess(String processDefId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefId).singleResult();
        try {
            repositoryService.deleteDeployment(processDefinition.getDeploymentId());
        } catch (Exception e) {
            throw new JeecgBootException("流程还有运行实例，不允许删除");
        }
    }

  /*  *//**
     * 获取流程图预览
     *
     * @param processDefId
     * @return
     *//*
    @Override
    public InputStream getProcessImage(String processDefId) {
        // get process model
        BpmnModel model = repositoryService.getBpmnModel(processDefId);
        if (model != null && model.getLocationMap().size() > 0) {
            ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
            // 生成流程图 已启动的task 高亮
//                return generator.generateDiagram(model,
//                        runtimeService.getActiveActivityIds(processInstanceId));
            // 生成流程图 都不高亮
            return generator.generateDiagram(model, "png", Collections.<String>emptyList());
        }
        return null;
    }
*/
    /**
     * 激活流程
     *
     * @param processDefId
     */
    @Override
    public void activeProcess(String processDefId) {
        boolean flag = repositoryService.isProcessDefinitionSuspended(processDefId);
        if (!flag){
            throw new JeecgBootException("流程已激活");
        }
        repositoryService.activateProcessDefinitionById(processDefId);
    }




    /**
     * 将流程定义对象转为jsonObject 防止json转换异常
     *
     * @param list
     * @return
     */
    private List<JSONObject> object2JSONObject(List<ProcessDefinition> list) {
        List<JSONObject> result = new LinkedList<>();
        for (ProcessDefinition processDefinition : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", processDefinition.getId());
            jsonObject.put("key", processDefinition.getKey());
            jsonObject.put("name", processDefinition.getName());
            jsonObject.put("version", processDefinition.getVersion());
            jsonObject.put("state", processDefinition.isSuspended());
            result.add(jsonObject);
        }
        return result;
    }

    /**
     * 设置修改信息
     *
     * @param workflowProcess
     */
    private void setEditInfo(WorkflowProcess workflowProcess) {
        Assert.notNull(workflowProcess, "error : 工作流对象不能为null");
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        workflowProcess.setUpdateBy(sysUser.getUsername());
        workflowProcess.setUpdateTime(new Date());
    }

    /**
     * 修改工作流信息
     *
     * @param entity
     * @return
     */
    @Override
    public boolean updateById(WorkflowProcess entity) {
        entity.setUpdateTime(new Date());
        //获取登录用户
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        entity.setUpdateBy(sysUser.getUsername());
        return super.updateById(entity);
    }
}
