package org.jeecg.modules.system.service;

import java.util.List;

/**
 * @author xduan
 * @version 2020/4/17
 */
public interface IPermissionService {

    List<String> getDepartList(String userId);

    boolean isAdmin(String userId);

    /**
     * 根据部门id查询所有的人员
     *
     * @param departId
     * @return
     */
    List<String> getUserIdList(String departId);
}
