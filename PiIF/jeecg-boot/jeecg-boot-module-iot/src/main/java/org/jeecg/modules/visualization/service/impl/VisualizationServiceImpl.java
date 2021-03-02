package org.jeecg.modules.visualization.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.system.service.IThingService;
import org.jeecg.modules.visualization.entity.IotVisualization;
import org.jeecg.modules.visualization.mapper.VisualizationMapper;
import org.jeecg.modules.visualization.service.IVisualizationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;

/**
 * @author xduan
 * @version 2020/4/10
 * @Desc 项目相关服务类
 */
@Service
public class VisualizationServiceImpl extends ServiceImpl<VisualizationMapper, IotVisualization> implements IVisualizationService {

    @Resource
    private IThingService thingService;

    /**
     * 新增大屏可视化对象
     *
     * @param visualization
     * @return
     */
    @Override
    public IotVisualization add(IotVisualization visualization) {
        visualization.setObjectPk(UUIDGenerator.generate());
        visualization.setCreateBy(TokenUtils.getLoginUser().getUsername());
        visualization.setCreateTime(new Date());
        int insert = baseMapper.insert(visualization);
        return visualization;
    }

    /**
     * 获取指定对象
     *
     * @param id
     * @return
     */
    @Override
    public IotVisualization getById(Serializable id) {
        IotVisualization visualization = super.getById(id);
       /* //实例名称数组 json对象
        String thingNames = visualization.getThingNames();
        List<String> list = JSON.parseArray(thingNames, String.class);
        if (list.isEmpty()){
            //没有组件直接返回
            return visualization;
        }*/
       /* //获取组件详细数据
        List<Thing> things = thingService.list(new QueryWrapper<Thing>().in("name", list));
        visualization.setThings(things);*/
        return visualization;
    }
}
