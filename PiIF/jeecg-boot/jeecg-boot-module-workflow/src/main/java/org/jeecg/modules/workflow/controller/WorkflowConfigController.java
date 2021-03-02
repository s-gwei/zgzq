package org.jeecg.modules.workflow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.WorkflowForm;
import org.jeecg.modules.workflow.entity.WorkflowNodeConfig;
import org.jeecg.modules.workflow.service.IWorkflowFormService;
import org.jeecg.modules.workflow.service.IWorkflowNodeConfigService;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author xduan
 * @version 2020/6/29
 */
@RestController
@Api("流程配置相关接口")
@RequestMapping("/workflow/config")
@Slf4j
public class WorkflowConfigController {

    @Resource
    private IWorkflowNodeConfigService nodeConfigService;

    @Resource
    private IWorkflowFormService formService;

    @PostMapping("/node")
    @ApiOperation("新增流程节点配置")
    public Result<?> addNodeConfig(@RequestBody WorkflowNodeConfig nodeConfig) {
        boolean save = nodeConfigService.save(nodeConfig);
        return Result.ok("新增成功");
    }


    @GetMapping("/node/list")
    @ApiOperation("获取流程节点配置列表")
    public Result<IPage<WorkflowNodeConfig>> nodeList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      @RequestParam String processId) {
        Result<IPage<WorkflowNodeConfig>> result = new Result<IPage<WorkflowNodeConfig>>();
        Assert.hasText(processId, "processId not be empty ");
        LambdaQueryWrapper<WorkflowNodeConfig> qw = new QueryWrapper<WorkflowNodeConfig>()
                .lambda()
                .eq(WorkflowNodeConfig::getProcessId, processId);
        IPage<WorkflowNodeConfig> page = nodeConfigService.page(new Page<>(pageNo, pageSize),qw);
        return result.successData(page);
    }

    @DeleteMapping("/node/delete")
    @ApiOperation("删除流程节点配置")
    public Result<?> deleteNodeConfig(@RequestParam String id) {
        nodeConfigService.removeById(id);
        return Result.ok("删除成功");
    }


    @PostMapping("/form/")
    @ApiOperation("新增流程表单配置")
    public Result<?> addForm(@RequestBody WorkflowForm form) {
        boolean save = formService.save(form);
        return Result.ok("新增成功");
    }

    @GetMapping("/form/list")
    @ApiOperation("获取表单配置列表")
    public Result<IPage<WorkflowForm>> formList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                @RequestParam String processId) {
        Result<IPage<WorkflowForm>> result = new Result<IPage<WorkflowForm>>();
        Assert.hasText(processId, "processId not be empty ");
        LambdaQueryWrapper<WorkflowForm> qw = new QueryWrapper<WorkflowForm>()
                .lambda()
                .eq(WorkflowForm::getProcessId, processId);
        IPage<WorkflowForm> page = formService.page(new Page<>(pageNo, pageSize),qw);
        return result.successData(page);
    }


    @DeleteMapping("/form/delete")
    @ApiOperation("删除表单配置")
    public Result<?> deleteForm(@RequestParam String id) {
        formService.removeById(id);
        return Result.ok("删除成功");
    }

}
