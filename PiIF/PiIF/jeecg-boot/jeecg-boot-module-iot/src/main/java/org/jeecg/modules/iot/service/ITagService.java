package org.jeecg.modules.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.iot.entity.IotTag;

/**
 * @author xduan
 * @version 2020/4/10
 */
public interface ITagService extends IService<IotTag> {
    /**
     * 新增标签
     * @param tag
     * @return
     */
    IotTag add(IotTag tag);

    /**
     * 根据id删除标签
     * @param id
     */
    void delete(String id);
}
