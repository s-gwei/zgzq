package org.jeecg.modules.iot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.iot.entity.IotProject;
import org.jeecg.modules.iot.mapper.ProjectMapper;
import org.jeecg.modules.iot.service.IProjectService;
import org.jeecg.modules.system.mapper.DepartobjectPermissionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author xduan
 * @version 2020/4/10
 * @Desc 项目相关服务类
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, IotProject> implements IProjectService {

    private static final String TABLENAME = "iot_project";
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private DepartobjectPermissionMapper permissionMapper;

    /**
     * 新建项目
     *
     * @param project
     */
    @Override
    public IotProject add(IotProject project) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        project.setCreateTime(timestamp);
        project.setUpdateTime(timestamp);
        //设置可见性权限的关联主键值
        project.setObjectPk(UUIDGenerator.generate());
        int insert = projectMapper.insert(project);
        return project;
    }

    /**
     * 删除项目
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        IotProject project = projectMapper.selectById(id);
        if (project == null) {
            return;
        }
        //删除相关的可见性权限
        permissionMapper.delete(null, project.getObjectPk(), TABLENAME);
        projectMapper.deleteById(id);
    }


}
