package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper extends BaseMapper {

    /**
     * 查询普通用户角色id
     *
     * @return String
     */
    String findUserRoleId();

    /**
     * 查询管理角色id
     *
     * @return String
     */
    String findManagerRoleId();

}
