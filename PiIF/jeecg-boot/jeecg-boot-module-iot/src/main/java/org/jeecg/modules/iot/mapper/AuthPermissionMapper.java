package org.jeecg.modules.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.iot.entity.AuthPermission;

import java.util.List;

/**
 * @author xduan
 * @version 2020/4/1
 */
@Mapper
public interface AuthPermissionMapper extends BaseMapper<AuthPermission> {
    /**
     * 根据表名和codename获取唯一id
     *
     * @param codename
     * @param tableName
     * @return
     */
    @Select("select a.id from auth_permission a LEFT JOIN django_content_type d on d.id=a.content_type_id where a.codename =#{codename} and d.table_name=#{tableName};")
    List<String> getIdByCodenameAndTableNam(@Param("codename") String codename, @Param("tableName") String tableName);
}
