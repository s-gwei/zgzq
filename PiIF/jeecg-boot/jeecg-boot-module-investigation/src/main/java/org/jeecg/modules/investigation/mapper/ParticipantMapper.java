package org.jeecg.modules.investigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.investigation.entity.InvestigationParticipant;
import org.jeecg.modules.system.entity.SysUser;

/**
 * @author xduan
 * @version 2020/4/24
 * @Desc 活动参与者的数据操作接口
 */
@Mapper
public interface ParticipantMapper extends BaseMapper<InvestigationParticipant> {

    @Select("select * from sys_user where id in (select user_id from investigation_participant where container_id = #{containerId})")
    IPage<SysUser> findUserByContainerId(Page<SysUser> page, @Param("containerId") String containerId);

}
