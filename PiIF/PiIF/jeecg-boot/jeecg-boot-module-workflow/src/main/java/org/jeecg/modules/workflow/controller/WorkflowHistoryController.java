package org.jeecg.modules.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.service.IWorkflowHistoryService;
import org.jeecg.modules.workflow.vo.ProcessInstanceVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author xduan
 * @version 2020/7/6
 * @Desc 流程历史记录
 */
@RestController
@Api("流程历史记录相关接口")
@RequestMapping("/workflow/history")
@Slf4j
public class WorkflowHistoryController {

    @Resource
    private IWorkflowHistoryService workflowHistoryService;

    @GetMapping(value = "/historyTaskList")
    @ApiOperation("获取流程历史任务列表")
    public Result<IPage<ProcessInstanceVO>> historyTaskList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         String processName, String processDefId) {
        Result<IPage<ProcessInstanceVO>> result = new Result<>();
        IPage<ProcessInstanceVO> page = workflowHistoryService.getHistoryTaskList(pageNo, pageSize,processName,processDefId);
        return result.successData(page);
    }

    @GetMapping(value = "/historyProcessInstanceList")
    @ApiOperation("获取历史流程列表")
    public Result<IPage<ProcessInstanceVO>> historyProcessInstanceList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                            String processName, String processDefId) {
        Result<IPage<ProcessInstanceVO>> result = new Result<>();
        IPage<ProcessInstanceVO> page = workflowHistoryService.getHistoryProcessInstanceList(pageNo, pageSize,processName,processDefId);
        return result.successData(page);
    }

}
