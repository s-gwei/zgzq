package org.jeecg.modules.visualization.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.visualization.entity.IotVisualization;

/**
 * @author xduan
 * @version 2020/4/10
 */
public interface IVisualizationService extends IService<IotVisualization> {

    /**
     * 新增大屏可视化对象
     * @param visualization
     * @return
     */
    IotVisualization add(IotVisualization visualization);



}
