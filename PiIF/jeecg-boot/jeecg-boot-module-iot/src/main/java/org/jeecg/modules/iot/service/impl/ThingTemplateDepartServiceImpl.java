package org.jeecg.modules.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.iot.entity.SysDepart;
import org.jeecg.modules.iot.entity.ThingtemplateDepart;
import org.jeecg.modules.iot.mapper.ThingTemplateDepartMapper;
import org.jeecg.modules.iot.service.IThingTemplateDepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 张泽革
 * @create 2020/3/5
 */
@Service
public class ThingTemplateDepartServiceImpl extends ServiceImpl<ThingTemplateDepartMapper, ThingtemplateDepart> implements IThingTemplateDepartService {
    @Autowired
    ThingTemplateDepartMapper thingTemplateDepartMapper;

    @Override
    public IPage<SysDepart> getDepartsBythingtemplateId(Page<SysDepart> page, String thingtemplateId) {
        return thingTemplateDepartMapper.getDepartsBythingtemplateId(page, thingtemplateId);
    }

    @Override
    public List<String> getThingtemplateIdByDepartId(String departId) {
        return thingTemplateDepartMapper.getThingtemplateIdByDepartId(departId);
    }
}
