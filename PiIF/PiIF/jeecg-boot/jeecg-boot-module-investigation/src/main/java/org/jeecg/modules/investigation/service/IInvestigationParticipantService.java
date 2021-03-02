package org.jeecg.modules.investigation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.investigation.entity.InvestigationParticipant;
import org.jeecg.modules.system.entity.SysUser;

/**
 * @author xduan
 * @version 2020/4/24
 * @desc 调查问卷实体的服务类
 */
public interface IInvestigationParticipantService extends IService<InvestigationParticipant> {
    /**
     * 根据活动Id查询所有参与者
     *
     * @param page
     * @param containerId
     * @param realname
     * @return
     */
    IPage<SysUser> findUserByContainerId(Page<SysUser> page, String containerId, String realname);
}
