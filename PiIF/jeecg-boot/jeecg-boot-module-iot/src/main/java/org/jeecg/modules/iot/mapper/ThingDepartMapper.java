package org.jeecg.modules.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.iot.entity.ThingDepart;
import org.jeecg.modules.iot.vo.ThingDepartPermissionVO;

import java.util.List;

/**
 * @author 张泽革
 * @create 2020/3/6
 */
public interface ThingDepartMapper extends BaseMapper<ThingDepart> {
    /**
     * 根据物实例id查询关联的部门
     *
     * @param thingId
     * @return
     */
    IPage<ThingDepartPermissionVO> getDepartsBythingId(Page<ThingDepartPermissionVO> page, @Param("thingId") String thingId);

    @Select("select dep_id from sys_user_depart where user_id=#{userId}")
    List<String> getDepartIdByUserId(@Param("userId") String userId);

    @Select("select id from sys_depart where parent_id=#{depart}")
    List<String> queryChildDepart(@Param("depart") String depart);

    @Select("select thing_id from iot_thing_depart where depart_id=#{departId}")
    List<String> getThingIdByDepartId(@Param("departId") String departId);

    @Select("select id from sys_depart where  depart_name = #{departName} ")
    String getDepartIdByDepartName(@Param("departName") String departName);

    /**
     * 根据userId查角色
     *
     * @param username
     * @return
     */
    @Select("select role_code from sys_role where id in (select role_id from sys_user_role where user_id = (select id from sys_user where username=#{username}))")
    List<String> getRoleByUserName(@Param("username") String username);

    /**
     * 根据userId查用户名
     *
     * @param userId
     * @return
     */
    @Select("select username from sys_user where id = #{userId}")
    List<String> getUsernameByUserId(@Param("userId") String userId);
}
