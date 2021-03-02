package org.jeecg.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.workflow.entity.WorkflowProcess;

/**
 * @author xduan
 * @version 2020/6/19
 */
@Mapper
public interface ProcessMapper extends BaseMapper<WorkflowProcess> {

}
