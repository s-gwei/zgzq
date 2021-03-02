package org.jeecg.modules.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.iot.entity.ThingDepart;
import org.jeecg.modules.iot.vo.ThingDepartPermissionVO;

import java.util.List;

/**
 * @author 张泽革
 * @create 2020/3/5
 */
public interface IThingDepartService extends IService<ThingDepart> {

    /**
     * 根据物实例id查询关联的部门
     *
     * @param thingId
     * @return
     */
    IPage<ThingDepartPermissionVO> getDepartsBythingId(Page<ThingDepartPermissionVO> page, String thingId);

    /**
     * 根据userId查询departId
     */
    List<String> getDepartIdByUserId(String userId);

    /**
     * 查询子部门的集合
     *
     * @param depart
     */
    List<String> queryChildDepart(String depart);

    /**
     * 根据部门id查询所有的物id集合
     *
     * @param departId
     * @return
     */
    List<String> getThingIdByDepartId(String departId);


}
