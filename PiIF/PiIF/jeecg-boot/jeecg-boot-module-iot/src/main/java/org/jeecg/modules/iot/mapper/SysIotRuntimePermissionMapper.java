package org.jeecg.modules.iot.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author sliu
 * @description
 * @date 2020/3/10
 */
public interface SysIotRuntimePermissionMapper {

    List<String> getIotUserEditRight(@Param("personId") String personId, @Param("tableName") String tableName, @Param("thingId") String thingId);

    List<String> getIotRoleEditRight(@Param("personId") String personId, @Param("tableName") String tableName, @Param("thingId") String thingId);

    int deleteIotUserEditRight(@Param("personId") String personId, @Param("tableName") String tableName, @Param(
            "thingId") String thingId, @Param(
            "rightName") String rightName);

    int addIotUserEditRight(@Param("personId") String personId, @Param("tableName") String tableName, @Param(
            "thingId") String thingId, @Param("rightName") String rightName, @Param("is_extend") boolean isExtend);

    int deleteIotRoleEditRight(@Param("personId") String personId, @Param("tableName") String tableName, @Param(
            "thingId") String thingId, @Param(
            "rightName") String rightName);

    int addIotRoleEditRight(@Param("personId") String personId, @Param("tableName") String tableName, @Param(
            "thingId") String thingId, @Param(
            "rightName") String rightName, @Param("is_extend") boolean isExtend);

    List<Map<String, Object>> queryIotEditRightUserList(@Param("tableName") String tableName,
                                                        @Param("thingId") String thingId);

    List<Map<String, Object>> queryIotEditRightRoleList(@Param("tableName") String tableName,
                                                        @Param("thingId") String thingId);

}
