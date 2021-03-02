package org.jeecg.modules.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.service.IWorkflowRuntimeService;
import org.jeecg.modules.workflow.vo.ProcessInstanceVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author xduan
 * @version 2020/7/2
 */
@RestController
@Api("流程运行时相关接口")
@RequestMapping("/workflow/runtime")
@Slf4j
public class WorkflowRuntimeController {

    @Resource
    private IWorkflowRuntimeService workflowRuntimeService;

    @GetMapping(value = "/instanceList")
    @ApiOperation("获取流程实例列表")
    public Result<IPage<ProcessInstanceVO>> instanceList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                          String processName,String startUsername) {
        Result<IPage<ProcessInstanceVO>> result = new Result<>();
        IPage<ProcessInstanceVO> page = workflowRuntimeService.getInstanceList(pageNo, pageSize,processName,startUsername);
        return result.successData(page);
    }

    @GetMapping(value = "/suspended")
    @ApiOperation("挂起流程实例")
    public Result<?> suspended(@RequestParam String processInstanceId) {
        workflowRuntimeService.suspended(processInstanceId);
        return Result.ok("挂起成功");
    }

    @GetMapping(value = "/active")
    @ApiOperation("激活流程实例")
    public Result<?> active(@RequestParam String processInstanceId) {
        workflowRuntimeService.active(processInstanceId);
        return Result.ok("激活成功");
    }

    @GetMapping(value = "/close")
    @ApiOperation("关闭流程实例")
    public Result<?> close(@RequestParam String processInstanceId) {
        workflowRuntimeService.close(processInstanceId);
        return Result.ok("关闭成功");
    }

    @GetMapping(value = "/task/taskEntrust")
    @ApiOperation("委派任务")
    public Result<?> taskEntrust(@RequestParam String taskId,@RequestParam String username) {
        workflowRuntimeService.taskEntrust(taskId,username);
        return Result.ok("操作成功");
    }


    /**
     * 启动流程实例
     *
     * @param processId 流程设计对象的id
     * @return
     */
    @GetMapping(value = "/startProcess")
    @ApiOperation("启动流程")
    public Result<?> startProcess(@RequestParam String processId) {
        workflowRuntimeService.startProcess(processId);
        return Result.ok("启动成功");
    }
}
