package org.jeecg.modules.workflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.workflow.entity.WorkflowForm;
import org.jeecg.modules.workflow.mapper.WorkflowFormMapper;
import org.jeecg.modules.workflow.service.IWorkflowFormService;
import org.springframework.stereotype.Service;

/**
 * @author xduan
 * @version 2020/6/29
 */
@Service
public class WorkflowFormServiceImpl extends ServiceImpl<WorkflowFormMapper, WorkflowForm> implements IWorkflowFormService {
}
