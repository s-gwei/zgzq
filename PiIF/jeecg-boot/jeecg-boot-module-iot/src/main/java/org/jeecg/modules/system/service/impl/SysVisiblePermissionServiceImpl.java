package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.iot.entity.SysDepart;
import org.jeecg.modules.iot.vo.VisblePermissionVO;
import org.jeecg.modules.system.entity.GuardianDepartobjectermission;
import org.jeecg.modules.system.mapper.DepartobjectPermissionMapper;
import org.jeecg.modules.system.service.IPermissionService;
import org.jeecg.modules.system.service.ISysVisiblePermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/8
 */
@Service
public class SysVisiblePermissionServiceImpl extends ServiceImpl<DepartobjectPermissionMapper, GuardianDepartobjectermission> implements ISysVisiblePermissionService {

    @Resource
    private DepartobjectPermissionMapper visiblePermissionMapper;


    @Resource
    private IPermissionService permissionService;

    /**
     * 新增可见性权限
     *
     * @param visblePermissionVO
     */
    @Override
    public void add(VisblePermissionVO visblePermissionVO) {
        String tableName = visblePermissionVO.getTableName();
        String objectPk = visblePermissionVO.getObjectPk();
        String departId = visblePermissionVO.getDepartId();
        //根据部门id，对象object_pk,tableName查询对应的权限
        List<GuardianDepartobjectermission> list = visiblePermissionMapper.getByTableNameAndObjectPkAndDepartId(tableName, objectPk, departId);
        if (list != null && list.size() > 0) {
            //如果权限已存在则默认不添加新数据
            return;
        }
        visiblePermissionMapper.add(tableName, objectPk, departId);
    }

    /**
     * 删除可见性权限
     *
     * @param departId
     * @param objectPk
     * @param tableName
     */
    @Override
    public void delete(String departId, String objectPk, String tableName) {
        visiblePermissionMapper.delete(departId, objectPk, tableName);
    }

    /**
     * 根据表名和userId查询列表，分页
     *
     * @param page
     * @param userId
     * @param tableName
     * @return
     */
    @Override
    public IPage<Object> getByUserId(Page<Object> page, String userId, String tableName) {
        boolean admin = permissionService.isAdmin(userId);
        if (admin) {
            return visiblePermissionMapper.getByTableNameAndDepartIds(page, tableName, null);
        }
        //该用户拥有的部门权限
        List<String> departList = permissionService.getDepartList(userId);
        if (!(departList.size() > 0)) {
            //没有可查看的对象
            return new Page<>();
        }
        return visiblePermissionMapper.getByTableNameAndDepartIds(page, tableName, departList);
    }

    /**
     * 根据对象的objectPk 和tableName查询关联的部门列表
     *
     * @param page
     * @param objectPk
     * @param tableName
     * @return
     */
    @Override
    public IPage<SysDepart> getByObjectPk(Page<SysDepart> page, String objectPk, String tableName) {
        return visiblePermissionMapper.getByObjectPk(page, objectPk, tableName);
    }

    /**
     * 根据用户id，和表名,查可查看对象的object_Pk
     *
     * @param userId
     * @param tableName
     * @return
     */
    @Override
    public List<String> getObjectPkByUserId(String userId, String tableName) {
        boolean admin = permissionService.isAdmin(userId);
        if (admin) {
            return visiblePermissionMapper.getIdByUserId(tableName, null);
        }
        //该用户拥有的部门权限
        List<String> departList = permissionService.getDepartList(userId);
        if (departList.size() > 0) {
            return visiblePermissionMapper.getIdByUserId(tableName, departList);
        } else {
            //没有可查看的对象
            return new ArrayList<>();
        }
    }


}
