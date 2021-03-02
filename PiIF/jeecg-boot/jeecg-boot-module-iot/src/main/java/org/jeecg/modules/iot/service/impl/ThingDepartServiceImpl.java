package org.jeecg.modules.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.iot.entity.ThingDepart;
import org.jeecg.modules.iot.mapper.ThingDepartMapper;
import org.jeecg.modules.iot.service.IThingDepartService;
import org.jeecg.modules.iot.vo.ThingDepartPermissionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 张泽革
 * @create 2020/3/5
 */
@Service
public class ThingDepartServiceImpl extends ServiceImpl<ThingDepartMapper, ThingDepart> implements IThingDepartService {
    @Autowired
    ThingDepartMapper thingDepartMapper;

    @Override
    public IPage<ThingDepartPermissionVO> getDepartsBythingId(Page<ThingDepartPermissionVO> page, String thingId) {
        return thingDepartMapper.getDepartsBythingId(page, thingId);
    }

    @Override
    public List<String> getDepartIdByUserId(String userId) {
        return thingDepartMapper.getDepartIdByUserId(userId);
    }

    @Override
    public List<String> queryChildDepart(String depart) {
        return thingDepartMapper.queryChildDepart(depart);
    }

    @Override
    public List<String> getThingIdByDepartId(String departId) {
        return thingDepartMapper.getThingIdByDepartId(departId);
    }

}
