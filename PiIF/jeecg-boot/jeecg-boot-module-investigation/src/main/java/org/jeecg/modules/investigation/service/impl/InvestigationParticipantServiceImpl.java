package org.jeecg.modules.investigation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.investigation.entity.InvestigationParticipant;
import org.jeecg.modules.investigation.mapper.ParticipantMapper;
import org.jeecg.modules.investigation.service.IInvestigationParticipantService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xduan
 * @version 2020/4/26
 * @desc 评价关系维护 服务类
 */
@Service
public class InvestigationParticipantServiceImpl extends ServiceImpl<ParticipantMapper, InvestigationParticipant> implements IInvestigationParticipantService {

    @Resource
    private UserMapper userMapper;

    /**
     * 根据活动Id查询所有参与者
     *
     * @param page
     * @param containerId
     * @param realname
     * @return
     */
    @Override
    public IPage<SysUser> findUserByContainerId(Page<SysUser> page, String containerId, String realname) {

        if (StringUtils.isEmpty(realname)) {
            //realname 为空,直接查所有的参与者
            return baseMapper.findUserByContainerId(page, containerId);
        }
        //获取所有参与者用户Id
        QueryWrapper<InvestigationParticipant> qwIP = new QueryWrapper<>();
        qwIP.select("user_id");
        qwIP.eq("container_id", containerId);
        List<InvestigationParticipant> list = baseMapper.selectList(qwIP);
        List<String> ids = list.stream().map(InvestigationParticipant::getUserId).collect(Collectors.toList());
        //模糊查询参与者
        QueryWrapper<SysUser> userQueryWrapper = new QueryWrapper<SysUser>();
        userQueryWrapper.like("realname", realname);
        if (ids.size() > 0) {
            userQueryWrapper.in("id", ids);
        } else {
            return baseMapper.findUserByContainerId(page, containerId);
        }
        return userMapper.selectPage(page, userQueryWrapper);
    }
}


