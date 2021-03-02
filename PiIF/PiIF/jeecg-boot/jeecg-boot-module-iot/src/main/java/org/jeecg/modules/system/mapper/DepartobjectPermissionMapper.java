package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;
import org.jeecg.modules.iot.entity.SysDepart;
import org.jeecg.modules.system.entity.GuardianDepartobjectermission;

import java.util.List;

/**
 * @author xduan
 * @version 2020/4/1
 * @Desc 可见性权限的数据操作类
 */
@Mapper
public interface DepartobjectPermissionMapper extends BaseMapper<GuardianDepartobjectermission> {
    /**
     * 根据表名，主键，部门名称，新增可见性权限
     *
     * @param tableName
     * @param objectPk
     * @param departId
     */
    @Insert("insert into guardian_departobjectpermission (object_pk, content_type_id, depart_id) values (#{objectPk}, (select id from  django_content_type where table_name= #{tableName}), #{departId})")
    void add(@Param("tableName") String tableName, @Param("objectPk") String objectPk, @Param("departId") String departId);

    @Select("select *  from guardian_departobjectpermission  where(object_pk = #{objectPk} and content_type_id = (select id from  django_content_type where table_name= #{tableName}) and depart_id = #{departId})")
    List<GuardianDepartobjectermission> getByTableNameAndObjectPkAndDepartId(@Param("tableName") String tableName, @Param("objectPk") String objectPk, @Param("departId") String departId);

    /**
     * 删除对应的可见性权限
     *
     * @param departId  如果传null，则没有这个条件，只根据表名和 objectPk删除
     * @param objectPk
     * @param tableName
     */

    void delete(@Param("departId") String departId, @Param("objectPk") String objectPk, @Param("tableName") String tableName);

    /**
     * 根据表名 分页查询
     *
     * @param page
     * @param tableName
     * @param departList 如果传null，则查该表所有的数据
     * @return
     */
    IPage<Object> getByTableNameAndDepartIds(Page<Object> page, @Param("tableName") String tableName, @Param("departList") List<String> departList);

    @Select("select s.* from sys_depart s LEFT JOIN guardian_departobjectpermission g ON g.depart_id=s.id where g.object_pk =#{objectPk} and content_type_id =(select id from django_content_type where table_name= #{tableName})")
    IPage<SysDepart> getByObjectPk(Page<SysDepart> page, String objectPk, String tableName);

    List<String> getIdByUserId(@Param("tableName") String tableName, @Param("departList") List<String> departList);
}
