package org.jeecg.modules.iot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.iot.entity.IotProject;
import org.jeecg.modules.iot.service.IProjectService;
import org.jeecg.modules.system.service.IPermissionService;
import org.jeecg.modules.system.service.ISysVisiblePermissionService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/14
 * @Desc 项目接口层
 */
@Slf4j
@RestController
@RequestMapping("/iot/project")
@Api("项目接口")
public class IotProjectController {
    //项目表 表名
    private static final String PROJECT = "iot_project";

    @Resource
    private IProjectService projectService;

    @Resource
    private IPermissionService permissionService;

    @Resource
    private ISysVisiblePermissionService visiblePermissionService;

    @PostMapping("/add")
    @ApiOperation("新增项目")
    public Result<?> add(@RequestBody IotProject project) {
        try {
            IotProject name = projectService.getOne(new QueryWrapper<IotProject>().eq("name", project.getName()));
            if (name != null) {
                return Result.error("项目名称不能重复");
            }
            project = projectService.add(project);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.getMessage());
        }
        return Result.ok(project);
    }

    @PutMapping("/editProject")
    @ApiOperation("修改项目")
    public Result<?> editProject(@RequestBody IotProject project) {
        projectService.updateById(project);
        return Result.ok("修改成功");
    }

    @DeleteMapping("/deleteProject")
    @ApiOperation("删除项目")
    public Result<?> deleteProject(String id) {
        projectService.delete(id);
        return Result.ok("删除成功");
    }

    @GetMapping("/getProject")
    @ApiOperation("根据id查询项目")
    public Result<IotProject> getProject(String id) {
        Result<IotProject> result = new Result<IotProject>();
        IotProject project = projectService.getById(id);
        result.setResult(project);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @DeleteMapping("/deleteProjectList")
    @ApiOperation("批量删除项目")
    public Result<?> deleteProjectList(String ids) {
        String[] split = ids.split(",");
        for (String id : split) {
            projectService.delete(id);
        }
        return Result.ok("删除成功");
    }

    @GetMapping("/query")
    @ApiOperation("多条件查询")
    public Result<IPage<IotProject>> query(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                           String userId, String projectName, String projectPk, String tagPk) {
        Result<IPage<IotProject>> result = new Result<IPage<IotProject>>();
        QueryWrapper<IotProject> qw = new QueryWrapper<IotProject>();
        if (StringUtils.hasText(projectName)) {
            qw.like("name", projectName);
        }
        if (StringUtils.hasText(projectPk)) {
            qw.eq("project_pk", projectPk);
        }
        if (StringUtils.hasText(tagPk)) {
            qw.like("tag_pk", tagPk);
        }
        if (!permissionService.isAdmin(userId)) {
            //不是管理员
            List<String> objectPks = visiblePermissionService.getObjectPkByUserId(userId, PROJECT);
            if (objectPks.size() > 0) {
                //可查看的对象大于o
                qw.in("object_pk", objectPks);
            } else {
                setResult(result, new Page<>());
                return result;
            }
        }
        Page<IotProject> page = new Page<IotProject>(pageNo, pageSize);
        IPage<IotProject> projectIPage = projectService.page(page, qw);
        setResult(result, projectIPage);
        return result;
    }

    private void setResult(Result<IPage<IotProject>> result, IPage<IotProject> list) {
        result.setResult(list);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
    }
}
