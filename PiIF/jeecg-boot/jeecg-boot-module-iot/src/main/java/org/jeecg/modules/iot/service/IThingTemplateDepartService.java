package org.jeecg.modules.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.iot.entity.SysDepart;
import org.jeecg.modules.iot.entity.ThingtemplateDepart;

import java.util.List;

/**
 * @author 张泽革
 * @create 2020/3/5
 */
public interface IThingTemplateDepartService extends IService<ThingtemplateDepart> {

    /**
     * 根据物模板id查询关联的部门
     *
     * @param page
     * @param thingtemplateId
     * @return
     */
    IPage<SysDepart> getDepartsBythingtemplateId(Page<SysDepart> page, String thingtemplateId);

    /**
     * 根据部门id查询所有的物模板id集合
     *
     * @param departId
     * @return
     */
    List<String> getThingtemplateIdByDepartId(String departId);
}
