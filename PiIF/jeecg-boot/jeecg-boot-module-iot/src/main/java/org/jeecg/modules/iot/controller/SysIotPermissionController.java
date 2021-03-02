package org.jeecg.modules.iot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.iot.entity.SysDepart;
import org.jeecg.modules.iot.entity.ThingDepart;
import org.jeecg.modules.iot.entity.ThingtemplateDepart;
import org.jeecg.modules.iot.service.IThingDepartService;
import org.jeecg.modules.iot.service.IThingTemplateDepartService;
import org.jeecg.modules.iot.vo.ThingDepartPermissionVO;
import org.jeecg.modules.iot.vo.ThingDepartVO;
import org.jeecg.modules.iot.vo.ThingtemplateDepartVO;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.service.IThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 物和物模板权限控制
 *
 * @author 张泽革
 * @create 2020/3/5
 */
@Slf4j
@RestController
@RequestMapping("/iot")
public class SysIotPermissionController {
    @Autowired
    private IThingTemplateDepartService sysThingTemplateDepartService;

    @Autowired
    private IThingDepartService sysThingDepartService;

    @Autowired
    private IThingService thingService;

    /**
     * 给指定物模板添加部门
     *
     * @param thingTemplateDepartVO 物模板和部门的中间实体类
     * @return
     */
    @ApiOperation("给指定物模板添加部门")
    @RequestMapping(value = "/addThingTemplateDepart", method = RequestMethod.POST)
    public Result<String> addThingTemplateDepart(@RequestBody ThingtemplateDepartVO thingTemplateDepartVO) {
        Result<String> result = new Result<String>();
        try {
            //todo 权限继承需要做
            //获取部门id和物模板id
            String departId = thingTemplateDepartVO.getDepartId();
            String thingtemplateId = thingTemplateDepartVO.getThingtemplateId();
            //新建实体类
            ThingtemplateDepart thingTemplateDepart = new ThingtemplateDepart(thingtemplateId, departId);
            //查询是否已经存在
            QueryWrapper<ThingtemplateDepart> queryWrapper = new QueryWrapper<ThingtemplateDepart>();
            queryWrapper.eq("thingtemplate_id", thingtemplateId).eq("depart_id", departId);
            ThingtemplateDepart one = sysThingTemplateDepartService.getOne(queryWrapper);
            if (one == null) {
                sysThingTemplateDepartService.save(thingTemplateDepart);
            }
            //查询继承的设备
            QueryWrapper<Thing> qw = new QueryWrapper<Thing>();
            qw.eq("thingTemplate", thingtemplateId);
            List<Thing> list = thingService.list(qw);
            for (Thing thing : list) {
                ThingDepart thingDepart = new ThingDepart(thing.getName(), departId);
                thingDepart.setIsExtend(true);
                //给继承的设备设置可见性权限
                sysThingDepartService.save(thingDepart);
            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     * 删除指定物模板的部门关系
     *
     * @param thingtemplateId 物模板id
     * @param departId        部门id
     */
    @ApiOperation("删除指定物模板的部门关系")
    @RequestMapping(value = "/deleteDepartInThingtemplate", method = RequestMethod.DELETE)
    public Result<ThingtemplateDepart> deleteDepartInThingtemplate(@RequestParam(name = "thingtemplateId") String thingtemplateId,
                                                                   @RequestParam(name = "departId", required = true) String departId
    ) {
        Result<ThingtemplateDepart> result = new Result<ThingtemplateDepart>();
        try {
            QueryWrapper<ThingtemplateDepart> queryWrapper = new QueryWrapper<ThingtemplateDepart>();
            queryWrapper.eq("thingtemplate_id", thingtemplateId).eq("depart_id", departId);
            sysThingTemplateDepartService.remove(queryWrapper);
            //查询继承的设备
            QueryWrapper<Thing> qw = new QueryWrapper<Thing>();
            qw.eq("thingTemplate", thingtemplateId);
            List<Thing> list = thingService.list(qw);
            for (Thing thing : list) {
                //移除设备继承的可见性权限
                QueryWrapper<ThingDepart> qw2 = new QueryWrapper<ThingDepart>();
                qw2.eq("thing_id", thing.getName()).eq("depart_id", departId);
                sysThingDepartService.remove(qw2);
            }
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 测试查询指定物模板的部门
     *
     * @param pageNo          页码，默认为1
     * @param pageSize        每页显示数量，默认为10
     * @param thingtemplateId 物模板id
     * @return
     */
    @ApiOperation("测试查询指定物模板的部门")
    @RequestMapping(value = "/thingtemplateDepartList", method = RequestMethod.GET)
    public Result<IPage<SysDepart>> thingtemplateDepartList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                            @RequestParam(name = "thingtemplateId") String thingtemplateId) {
        Result<IPage<SysDepart>> result = new Result<IPage<SysDepart>>();
        Page<SysDepart> page = new Page<SysDepart>(pageNo, pageSize);
        IPage<SysDepart> pageList = sysThingTemplateDepartService.getDepartsBythingtemplateId(page, thingtemplateId);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    /**
     * 给指定物实例添加部门
     *
     * @param
     * @return
     */
    @ApiOperation("给指定物实例添加部门")
    @RequestMapping(value = "/addThingDepart", method = RequestMethod.POST)
    public Result<String> addThingDepart(@RequestBody ThingDepartVO thingDepartVO) {
        Result<String> result = new Result<String>();
        try {
            //获取部门id和物模板id
            String departId = thingDepartVO.getDepartId();
            String thingId = thingDepartVO.getThingId();
            //新建实体类
            ThingDepart thingDepart = new ThingDepart(thingId, departId);
            //查询是否已经存在
            QueryWrapper<ThingDepart> queryWrapper = new QueryWrapper<ThingDepart>();
            queryWrapper.eq("thing_id", thingId).eq("depart_id", departId);
            ThingDepart one = sysThingDepartService.getOne(queryWrapper);
            if (one == null) {
                sysThingDepartService.save(thingDepart);
            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     * 删除指定物实例的部门关系
     *
     * @param thingId  物实例id
     * @param departId 部门id
     */
    @ApiOperation("删除指定物实例的部门关系")
    @RequestMapping(value = "/deleteDepartInThing", method = RequestMethod.DELETE)
    public Result<ThingDepart> deleteDepartInThing(@RequestParam(name = "thingId") String thingId,
                                                   @RequestParam(name = "departId", required = true) String departId
    ) {
        Result<ThingDepart> result = new Result<ThingDepart>();
        try {

            QueryWrapper<ThingDepart> queryWrapper = new QueryWrapper<ThingDepart>();
            queryWrapper.eq("thing_id", thingId).eq("depart_id", departId);
            sysThingDepartService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 测试查询指定物实例的部门
     *
     * @param thingId 物实例id
     * @return
     */
    @ApiOperation("测试查询指定物实例的部门")
    @RequestMapping(value = "/thingDepartList", method = RequestMethod.GET)
    public Result<IPage<ThingDepartPermissionVO>> thingDepartList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                  @RequestParam(name = "thingId") String thingId) {
        Result<IPage<ThingDepartPermissionVO>> result = new Result<IPage<ThingDepartPermissionVO>>();
        Page<ThingDepartPermissionVO> page = new Page<ThingDepartPermissionVO>(pageNo, pageSize);
        IPage<ThingDepartPermissionVO> pageVo = sysThingDepartService.getDepartsBythingId(page, thingId);
        result.setSuccess(true);
        result.setResult(pageVo);
        return result;
    }

    /**
     * 根据用户id查询物实例的集合
     * 当成方法用于查询时的权限控制
     *
     * @param userId 用户id
     * @return
     */
    //@ApiOperation("根据用户id查询物实例的集合")
    @RequestMapping(value = "/thingListByUserId", method = RequestMethod.GET)
    public List<String> thingListByUserId(@RequestParam(name = "userId") String userId) {
        //所有可见的物实例id集合
        List<String> thingIds = new ArrayList<>();
        //关联的所有部门集合
        List<String> departIds = new ArrayList<>();
        //根据userId查询departId集合，里面会有父部门
        List<String> departs = sysThingDepartService.getDepartIdByUserId(userId);
        //排除父部门的集合
        List<String> departsExp = exceptParent(departs);
        if (departsExp.size() > 0) {
            for (String depart : departsExp) {
                departIds.add(depart);
                departIds = getChildDepart(departIds, depart);
            }
        }
        departIds = init(departIds);
        if (departIds.size() > 0) {
            for (String departId : departIds) {
                List<String> things = sysThingDepartService.getThingIdByDepartId(departId);
                if (things.size() > 0) {
                    thingIds.addAll(things);
                }
            }
        }
        thingIds = init(thingIds);
        return thingIds;
    }


    /**
     * 根据用户id查询物模板的集合
     * 当成方法用于查询时的权限控制
     *
     * @param userId 用户id
     * @return
     */
    //@ApiOperation("根据用户id查询物模板的集合")
    @RequestMapping(value = "/thingtemplateListByUserId", method = RequestMethod.GET)
    public List<String> thingtemplateListByUserId(@RequestParam(name = "userId") String userId) {
        //所有可见的物模板id集合
        List<String> thingtemplateIds = new ArrayList<>();
        //关联的所有部门集合
        List<String> departIds = new ArrayList<>();
        //根据userId查询departId集合
        List<String> departs = sysThingDepartService.getDepartIdByUserId(userId);
        //排除父部门的集合
        List<String> departsExp = exceptParent(departs);
        if (departsExp.size() > 0) {
            for (String depart : departsExp) {
                departIds.add(depart);
                departIds = getChildDepart(departIds, depart);
            }
        }
        departIds = init(departIds);
        if (departIds.size() > 0) {
            for (String departId : departIds) {
                List<String> thingtemplates = sysThingTemplateDepartService.getThingtemplateIdByDepartId(departId);
                if (thingtemplates.size() > 0) {
                    thingtemplateIds.addAll(thingtemplates);
                }
            }
        }
        thingtemplateIds = init(thingtemplateIds);
        return thingtemplateIds;
    }

    /**
     * 排除父部门
     */
    private List<String> exceptParent(List<String> departs) {
        List<String> list = new ArrayList<>();
        if (departs.size() > 0) {
            //遍历部门id
            for (String departId : departs) {
                //获得每个id的子部门id集合
                List<String> childIds = sysThingDepartService.queryChildDepart(departId);
                //标识是否为叶子节点
                boolean flag = true;
                for (String childId : childIds) {
                    //如果该部门的子部门在departs中，则说明该部门不是叶子节点
                    if (departs.contains(childId)) {
                        flag = false;
                    }
                }
                if (flag == true) {
                    list.add(departId);
                }
            }
        }
        return list;
    }

    /**
     * id去重
     *
     * @param thingIds
     */
    private List<String> init(List<String> thingIds) {
        List<String> list = new ArrayList<>();
        for (String thingId : thingIds) {
            if (!list.contains(thingId)) {
                list.add(thingId);
            }
        }
        return list;
    }

    /**
     * 查询子部门的集合
     *
     * @param departIds
     * @param depart
     * @return
     */
    private List<String> getChildDepart(List<String> departIds, String depart) {
        List<String> list = sysThingDepartService.queryChildDepart(depart);
        if (list.size() > 0) {
            for (String departId : list) {
                departIds.add(departId);
                getChildDepart(departIds, departId);
            }
        }
        return departIds;
    }


}
