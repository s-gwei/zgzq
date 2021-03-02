package org.jeecg.modules.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.iot.entity.IotProject;

/**
 * @author xduan
 * @version 2020/4/10
 */
public interface IProjectService extends IService<IotProject> {
    /**
     * 新建项目
     * @param project
     */
    IotProject add(IotProject project);

    /**
     * 删除项目
     * @param id
     */
    void delete(String id);
}
