package org.jeecg.modules.iot.service;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.iot.vo.IotPermissionVO;

import java.util.List;

/**
 * @author sliu
 * @description 物或物模板权限编辑service类
 * @date 2020/3/10
 */
public interface ISysIotRuntimePermissionService {

    List<String> getIotEditRight(boolean isUser, String personId, String tableName, String thingId);

    boolean changeIotEditRight(boolean isUser, String personId, String tableName, String thingId, Boolean isChangeable,
                               Boolean isDeleteable) throws JeecgBootException;

    List<IotPermissionVO> queryIotEditRightUserList(String tableName, String thingId);
}
