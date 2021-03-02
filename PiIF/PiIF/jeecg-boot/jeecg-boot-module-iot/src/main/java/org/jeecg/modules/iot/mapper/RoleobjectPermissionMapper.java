package org.jeecg.modules.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.iot.entity.GuardianRoleobjectpermission;

import java.util.List;

/**
 * @author xduan
 * @version 2020/4/1
 */
@Mapper
public interface RoleobjectPermissionMapper extends BaseMapper<GuardianRoleobjectpermission> {

    @Select("select * from guardian_roleobjectpermission where object_pk=#{thingTemplateName} and permission_id in (SELECT a.id from auth_permission a LEFT JOIN django_content_type d ON a.content_type_id=d.id where d.table_name='thingtemplate_model');")
    List<GuardianRoleobjectpermission> getPermissionListByTemplateName(@Param("thingTemplateName") String thingTemplateName);

    /**
     * 根据对象id和对象表名删除权限
     *
     * @param objectId
     * @param tableName
     * @return
     */
    @Delete("DELETE from guardian_roleobjectpermission where object_pk=#{objectId} AND permission_id IN (SELECT a.id FROM auth_permission a LEFT JOIN django_content_type d ON a.content_type_id = d.id WHERE d.table_name =#{tableName});")
    Integer deleteByIdAndTableName(@Param("objectId") String objectId,@Param("tableName") String tableName);
}
