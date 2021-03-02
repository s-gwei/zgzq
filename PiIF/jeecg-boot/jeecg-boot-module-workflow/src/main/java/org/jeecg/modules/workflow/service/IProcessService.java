package org.jeecg.modules.workflow.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.workflow.entity.WorkflowProcess;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xduan
 * @version 2020/6/19
 * @desc 流程定义的服务层接口
 */
public interface IProcessService extends IService<WorkflowProcess> {
    void deploy(String id);

    void uploadProcess(HttpServletRequest request);

    JSONArray getNode(String id);

    IPage<JSONObject> getVersionList(Integer pageNo, Integer pageSize, String processId);

    /**
     * 获取流程的xml配置
     * @param processDefId
     * @return
     */
    String getProcessXml(String processDefId);

    /**
     * 挂起流程，会同时挂起相关的流程实例
     * @param processDefId
     */
    void suspensionProcess(String processDefId);

    /**
     * 删除流程定义对象
     * @param processDefId
     */
    void deleteProcess(String processDefId);

    /**
     * 获取流程图预览
     * @param processDefId
     * @return
     *//*
    InputStream getProcessImage(String processDefId);
*/
    /**
     * 激活流程
     * @param processDefId
     */
    void activeProcess(String processDefId);

}
