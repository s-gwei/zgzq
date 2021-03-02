package org.jeecg.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.workflow.entity.WorkflowNodeConfig;
import org.jeecg.modules.workflow.mapper.WorkflowNodeConfigMapper;
import org.jeecg.modules.workflow.service.IWorkflowNodeConfigService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author xduan
 * @version 2020/6/29
 */
@Service
public class WorkflowNodeConfigServiceImpl extends ServiceImpl<WorkflowNodeConfigMapper, WorkflowNodeConfig> implements IWorkflowNodeConfigService {

    @Override
    public boolean save(WorkflowNodeConfig entity) {
        LambdaQueryWrapper<WorkflowNodeConfig> qw = new QueryWrapper<WorkflowNodeConfig>()
                .lambda()
                .eq(WorkflowNodeConfig::getProcessNodeCode, entity.getProcessNodeCode());
        List<WorkflowNodeConfig> workflowNodeConfigs = baseMapper.selectList(qw);
        if (!CollectionUtils.isEmpty(workflowNodeConfigs)) {
            throw new JeecgBootException("同一节点不能存在多个配置");
        }
        return super.save(entity);
    }
}
