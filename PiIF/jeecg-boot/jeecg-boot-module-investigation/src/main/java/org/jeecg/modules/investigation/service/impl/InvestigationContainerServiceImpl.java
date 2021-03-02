package org.jeecg.modules.investigation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.investigation.entity.InvestigationContainer;
import org.jeecg.modules.investigation.mapper.ContainerMapper;
import org.jeecg.modules.investigation.service.IInvestigationContainerService;
import org.springframework.stereotype.Service;

/**
 * @author xduan
 * @version 2020/4/26
 * @desc 活动 服务类
 */
@Service
public class InvestigationContainerServiceImpl extends ServiceImpl<ContainerMapper, InvestigationContainer> implements IInvestigationContainerService {
}
