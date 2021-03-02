package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.iot.entity.SysDepart;
import org.jeecg.modules.iot.vo.VisblePermissionVO;

import java.util.List;

/**
 * @author xduan
 * @version 2020/4/8
 * @Desc 可见性权限服务接口
 */

public interface ISysVisiblePermissionService {

    /**
     * 新增可见性权限
     * @param visblePermissionVO
     */
    void add(VisblePermissionVO visblePermissionVO);

    /**
     * 删除可见性权限
     * @param departId
     * @param objectPk
     * @param tableName
     */
    void delete(String departId, String objectPk, String tableName);

    /**
     * 根据表名和userId查询列表，分页
     * @param page
     * @param userId
     * @param tableName
     * @return
     */
    IPage<Object> getByUserId(Page<Object> page, String userId, String tableName);

    IPage<SysDepart> getByObjectPk(Page<SysDepart> page, String objectPk, String tableName);

    /**
     * 根据用户id，和表名查可查看对象的object_Pk
     * @param userId
     * @param tableName
     * @return
     */
    List<String> getObjectPkByUserId(String userId, String tableName);

}
