package org.jeecg.modules.iot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.iot.mapper.SysIotRuntimePermissionMapper;
import org.jeecg.modules.iot.service.ISysIotRuntimePermissionService;
import org.jeecg.modules.iot.vo.IotPermissionVO;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.mapper.ThingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sliu
 * @description 权限编辑service实现类
 * @date 2020/3/10
 */
@Slf4j
@Service
public class SysIotRuntimePermissionServiceImpl implements ISysIotRuntimePermissionService {

    private static final String PERMISSION_CHANGE = "change"; // 编辑权限

    private static final String PERMISSION_DELETE = "delete"; // 删除权限

    private static final String THINGTEMPLATE_MODEL = "iot_thingtemplate_model"; // 物模型模板表名
    private static final String THING_MODEL = "iot_thing_model"; // 物模型实例表名

    @Autowired
    private SysIotRuntimePermissionMapper sysIotRuntimePermissionMapper;

    @Resource
    private ThingMapper thingMapper;

    /**
     * 获取拥有物权限的用户列表
     *
     * @param tableName 表名
     * @param thingId   物id
     * @return IotPermissionVO列表
     */
    @Override
    public List<IotPermissionVO> queryIotEditRightUserList(String tableName, String thingId) {
        List<IotPermissionVO> iotPermissionVOList = new ArrayList<>();
        Map<String, IotPermissionVO> userPermissionIdMap = new HashMap<>();
        Map<String, IotPermissionVO> rolePermissionIdMap = new HashMap<>();
        // 查询组表
        List<Map<String, Object>> rolePermissionList = sysIotRuntimePermissionMapper.queryIotEditRightRoleList(tableName,
                thingId);
        for (Map<String, Object> rolePermissionMap : rolePermissionList) {
            String id = (String) rolePermissionMap.get("id");
            if (id != null) {
                String name = (String) rolePermissionMap.get("role_name");
                String codename = (String) rolePermissionMap.get("codename");
                Boolean is_extend = (Boolean) rolePermissionMap.get("is_extend");
                IotPermissionVO iotPermissionVO;
                if (rolePermissionIdMap.containsKey(id)) {
                    iotPermissionVO = rolePermissionIdMap.get(id);
                } else {
                    iotPermissionVO = new IotPermissionVO();
                    iotPermissionVO.setId(id);
                    iotPermissionVO.setName(name);
                    iotPermissionVO.setUser(false);
                    //设置是否是继承的权限
                    iotPermissionVO.setIsExtend(is_extend);
                }
                if (PERMISSION_CHANGE.equals(codename)) {
                    iotPermissionVO.setChangeable(true);
                }
                if (PERMISSION_DELETE.equals(codename)) {
                    iotPermissionVO.setDeleteable(true);
                }
                rolePermissionIdMap.put(id, iotPermissionVO);
            }
        }
        iotPermissionVOList.addAll(rolePermissionIdMap.values());

        // 查询用户表
        List<Map<String, Object>> userPermissionList = sysIotRuntimePermissionMapper.queryIotEditRightUserList(tableName,
                thingId);
        for (Map<String, Object> userPermissionMap : userPermissionList) {
            String id = (String) userPermissionMap.get("id");
            if (id != null) {
                String name = (String) userPermissionMap.get("realname");
                String codename = (String) userPermissionMap.get("codename");
                Boolean is_extend = (Boolean) userPermissionMap.get("is_extend");
                IotPermissionVO iotPermissionVO;
                if (userPermissionIdMap.containsKey(id)) {
                    iotPermissionVO = userPermissionIdMap.get(id);
                } else {
                    iotPermissionVO = new IotPermissionVO();
                    iotPermissionVO.setId(id);
                    iotPermissionVO.setName(name);
                    iotPermissionVO.setUser(true);
                    //设置是否是继承的权限
                    iotPermissionVO.setIsExtend(is_extend);
                }
                if (PERMISSION_CHANGE.equals(codename)) {
                    iotPermissionVO.setChangeable(true);
                }
                if (PERMISSION_DELETE.equals(codename)) {
                    iotPermissionVO.setDeleteable(true);
                }
                userPermissionIdMap.put(id, iotPermissionVO);
            }
        }
        iotPermissionVOList.addAll(userPermissionIdMap.values());
        return iotPermissionVOList;
    }

    /**
     * 获取用户对单个物的权限列表
     *
     * @param isUser    是否为用户或者组
     * @param personId  人员id,对应用户id或者组id
     * @param tableName 表名
     * @param thingId   物id
     * @return 权限列表
     */
    @Override
    public List<String> getIotEditRight(boolean isUser, String personId, String tableName, String thingId) {
        List<String> resultList;
        if (isUser) {
            resultList = sysIotRuntimePermissionMapper.getIotUserEditRight(personId, tableName, thingId);
        } else {
            resultList = sysIotRuntimePermissionMapper.getIotRoleEditRight(personId, tableName, thingId);
        }
        return resultList;
    }

    /**
     * 设置用户或者组的权限，若需要添加且没有该权限，则新增数据，否则报错。若不需要则删除。给已有权限再次置为true会报错，但没有权限再置为false不会报错
     *
     * @param isUser       是否为用户或者组
     * @param personId     人员id,对应用户id或者组id
     * @param tableName    表名
     * @param thingId      物id
     * @param isChangeable 是否可修改
     * @param isDeleteable 是否可删除
     * @return 是否设置成功
     */
    @Override
    @Transactional
    public boolean changeIotEditRight(boolean isUser, String personId, String tableName, String thingId,
                                      Boolean isChangeable,
                                      Boolean isDeleteable) throws JeecgBootException {
        boolean bFlag = true;
        //如果修改物模型模板
        boolean isTemplate = THINGTEMPLATE_MODEL.equals(tableName);
        //继承模板的实例
        List<Thing> things = null;
        if (isTemplate) {
            //获取继承的实例
            things = thingMapper.findThingByThingTemplate(thingId);
        }
        // 以下逻辑较为重复。需要先区分是否为用户或者组，修改和删除权限可以单独配置
        if (isUser) {
            List<String> permissionList = sysIotRuntimePermissionMapper.getIotUserEditRight(personId, tableName, thingId);
            if (isChangeable != null) {
                if (isChangeable) {
                    if (!permissionList.contains(PERMISSION_CHANGE)) {
                        sysIotRuntimePermissionMapper.addIotUserEditRight(personId, tableName, thingId, PERMISSION_CHANGE, false);
                        if (isTemplate) {
                            //如果是修改物模型模板，则需要修改继承它的设备实例
                            for (Thing thing : things) {
                                //给继承模板的实例添加修改权限
                                sysIotRuntimePermissionMapper.addIotUserEditRight(personId, THING_MODEL, thing.getName(), PERMISSION_CHANGE, true);
                            }
                        }
                    } else {
                        log.error("添加权限错误。权限：" + PERMISSION_CHANGE + " 已存在");
                        throw new JeecgBootException("添加权限错误：该权限已存在");
                    }
                } else {
                    log.debug("删除用户权限：" + personId + ":" + tableName + ":" + thingId + ":" + PERMISSION_CHANGE);
                    int deleteCount = sysIotRuntimePermissionMapper.deleteIotUserEditRight(personId, tableName, thingId,
                            PERMISSION_CHANGE);

                    if (isTemplate) {
                        //如果是修改物模型模板，则需要修改继承它的设备实例
                        for (Thing thing : things) {
                            //删除实例的修改权限
                            sysIotRuntimePermissionMapper.deleteIotUserEditRight(personId, THING_MODEL, thing.getName(), PERMISSION_CHANGE);
                        }
                    }
                    log.debug("成功删除权限条数：" + deleteCount);
                }
            }
            if (isDeleteable != null) {
                if (isDeleteable) {
                    if (!permissionList.contains(PERMISSION_DELETE)) {
                        sysIotRuntimePermissionMapper.addIotUserEditRight(personId, tableName, thingId, PERMISSION_DELETE, false);
                        if (isTemplate) {
                            //如果是修改物模型模板，则需要修改继承它的设备实例
                            for (Thing thing : things) {
                                //增加实例的删除权限
                                sysIotRuntimePermissionMapper.addIotUserEditRight(personId, THING_MODEL, thing.getName(), PERMISSION_DELETE, true);
                            }
                        }
                    } else {
                        log.error("添加权限错误。权限：" + PERMISSION_DELETE + " 已存在");
                        throw new JeecgBootException("添加权限错误：该权限已存在");
                    }
                } else {
                    log.debug("删除用户权限：" + personId + ":" + tableName + ":" + thingId + ":" + PERMISSION_DELETE);
                    int deleteCount = sysIotRuntimePermissionMapper.deleteIotUserEditRight(personId, tableName, thingId,
                            PERMISSION_DELETE);

                    if (isTemplate) {
                        //如果是修改物模型模板，则需要修改继承它的设备实例
                        for (Thing thing : things) {
                            //删除实例的删除权限
                            sysIotRuntimePermissionMapper.deleteIotUserEditRight(personId, THING_MODEL, thing.getName(), PERMISSION_DELETE);
                        }
                    }
                    log.debug("成功删除权限条数：" + deleteCount);
                }
            }
        } else {
            List<String> permissionList = sysIotRuntimePermissionMapper.getIotRoleEditRight(personId, tableName, thingId);
            if (isChangeable != null) {
                if (isChangeable) {
                    if (!permissionList.contains(PERMISSION_CHANGE)) {
                        sysIotRuntimePermissionMapper.addIotRoleEditRight(personId, tableName, thingId, PERMISSION_CHANGE, false);
                        if (isTemplate) {
                            //如果是修改物模型模板，则需要修改继承它的设备实例
                            for (Thing thing : things) {
                                //增加实例的修改权限
                                sysIotRuntimePermissionMapper.addIotRoleEditRight(personId, THING_MODEL, thing.getName(), PERMISSION_CHANGE, true);
                            }
                        }
                    } else {
                        log.error("添加权限错误。权限：" + PERMISSION_CHANGE + " 已存在");
                        throw new JeecgBootException("添加权限错误：该权限已存在");
                    }
                } else {
                    log.debug("删除组权限：" + personId + ":" + tableName + ":" + thingId + ":" + PERMISSION_CHANGE);
                    int deleteCount = sysIotRuntimePermissionMapper.deleteIotRoleEditRight(personId, tableName, thingId,
                            PERMISSION_CHANGE);
                    if (isTemplate) {
                        //如果是修改物模型模板，则需要修改继承它的设备实例
                        for (Thing thing : things) {
                            //删除实例的修改权限
                            sysIotRuntimePermissionMapper.deleteIotRoleEditRight(personId, THING_MODEL, thing.getName(), PERMISSION_CHANGE);
                        }
                    }
                    log.debug("成功删除权限条数：" + deleteCount);
                }
            }
            if (isDeleteable != null) {
                if (isDeleteable) {
                    if (!permissionList.contains(PERMISSION_DELETE)) {
                        sysIotRuntimePermissionMapper.addIotRoleEditRight(personId, tableName, thingId, PERMISSION_DELETE, false);
                        if (isTemplate) {
                            //如果是修改物模型模板，则需要修改继承它的设备实例
                            for (Thing thing : things) {
                                //增加实例的删除权限
                                sysIotRuntimePermissionMapper.addIotRoleEditRight(personId, THING_MODEL, thing.getName(), PERMISSION_DELETE, true);
                            }
                        }
                    } else {
                        log.error("添加权限错误。权限：" + PERMISSION_DELETE + " 已存在");
                        throw new JeecgBootException("添加权限错误：该权限已存在");
                    }
                } else {
                    log.debug("删除组权限：" + personId + ":" + tableName + ":" + thingId + ":" + PERMISSION_DELETE);
                    int deleteCount = sysIotRuntimePermissionMapper.deleteIotRoleEditRight(personId, tableName, thingId, PERMISSION_DELETE);
                    if (isTemplate) {
                        //如果是修改物模型模板，则需要修改继承它的设备实例
                        for (Thing thing : things) {
                            //删除实例的删除权限
                            sysIotRuntimePermissionMapper.deleteIotRoleEditRight(personId, THING_MODEL, thing.getName(), PERMISSION_DELETE);
                        }
                    }
                    log.debug("成功删除权限条数：" + deleteCount);
                }
            }
        }
        return bFlag;
    }
}
