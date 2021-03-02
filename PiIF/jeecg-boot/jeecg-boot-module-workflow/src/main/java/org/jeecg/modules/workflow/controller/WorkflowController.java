package org.jeecg.modules.workflow.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.WorkflowProcess;
import org.jeecg.modules.workflow.service.IProcessService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xduan
 * @version 2020/6/22
 * @desc 流程定义的接口定义层
 */
@RestController
@Api("流程定义相关接口")
@RequestMapping("/workflow/design")
@Slf4j
public class WorkflowController {

    @Resource
    private IProcessService processService;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @PostMapping("/process")
    @ApiOperation("新建流程")
    public Result<?> add(@RequestBody WorkflowProcess process) {
        processService.save(process);
        return Result.ok("创建成功");
    }

    @GetMapping("/process/query")
    @ApiOperation("获取流程列表（包括条件查询）")
    public Result<IPage<WorkflowProcess>> list(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               String processName,
                                               String processKey,
                                               String processType) {
        Result<IPage<WorkflowProcess>> result = new Result<>();
        LambdaQueryWrapper<WorkflowProcess> qw = new QueryWrapper<WorkflowProcess>().lambda().orderByDesc(WorkflowProcess::getCreateTime);
        if (StringUtils.hasText(processName)) {
            qw.eq(WorkflowProcess::getProcessName, processName);
        }
        if (StringUtils.hasText(processKey)) {
            qw.eq(WorkflowProcess::getProcessKey, processKey);
        }
        if (StringUtils.hasText(processType)) {
            qw.eq(WorkflowProcess::getProcessType, processType);
        }
        IPage<WorkflowProcess> page = processService.page(new Page<>(pageNo, pageSize), qw);
        return result.successData(page);
    }

    @DeleteMapping("/process/delete")
    @ApiOperation("删除流程")
    public Result<?> deleteProcesses(String id) {
        processService.removeById(id);
        return Result.ok("删除成功");
    }

    @GetMapping("/process/detail")
    @ApiOperation("获取单个流程实例")
    public Result<?> getDetail(@RequestParam String id) {
        WorkflowProcess process = processService.getById(id);
        return Result.ok(process);
    }

    @GetMapping("/process/node")
    @ApiOperation("获取流程节点数据")
    public Result<?> getProcessNode(String id) {
        Assert.hasText(id, "id不能为空");
        JSONArray array = processService.getNode(id);
        return Result.ok(array);
    }

    @PutMapping("/process/deploy")
    @ApiOperation("部署流程")
    public Result<?> deployProcess(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return Result.error("id不能未空");
        }
        processService.deploy(id);
        return Result.ok("发布成功");
    }

    @PutMapping("/process/edit")
    @ApiOperation("修改流程")
    public Result<?> editProcess(@RequestBody WorkflowProcess process) {
        processService.updateById(process);
        return Result.ok("修改成功");
    }

    @PostMapping(value = "/process/upload")
    @ApiOperation("上传流程文件")
    public Result<?> upload(HttpServletRequest request, HttpServletResponse response) {
        //上传流程文件
        processService.uploadProcess(request);
        return Result.ok("上传成功");
    }

    @GetMapping(value = "/processDef/version")
    @ApiOperation("获取流程的历史版本记录")
    public Result<IPage<JSONObject>> versionList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 @RequestParam String processId) {
        Result<IPage<JSONObject>> result = new Result<>();
        IPage<JSONObject> versionList = processService.getVersionList(pageNo, pageSize, processId);
        return result.successData(versionList);
    }

    @GetMapping(value = "/processDef/processXml")
    @ApiOperation("获取流程xml信息")
    public Result<String> processXml(@RequestParam String processDefId) {
        Result<String> result = new Result<>();
        String processXml = processService.getProcessXml(processDefId);
        return result.successData(processXml);
    }

    @GetMapping(value = "/processDef/state")
    @ApiOperation("挂起流程")
    public Result<?> editProcessState(@RequestParam String processDefId) {
        processService.suspensionProcess(processDefId);
        return Result.ok("挂起成功");
    }

    @GetMapping(value = "/processDef/active")
    @ApiOperation("激活流程")
    public Result<?> activeProcessState(@RequestParam String processDefId) {
        processService.activeProcess(processDefId);
        return Result.ok("激活流程");
    }

    @DeleteMapping(value = "/processDef/delete")
    @ApiOperation("删除流程版本")
    public Result<?> delete(@RequestParam String processDefId) {
        processService.deleteProcess(processDefId);
        return Result.ok("删除成功");
    }



    /*@GetMapping(value = "/processDef/processImage")
    @ApiOperation("获取流程的预览图")
    public void processImage(@RequestParam String processDefId, HttpServletResponse response) {
        response.setContentType("image/jpeg");
        InputStream inputStream = processService.getProcessImage(processDefId);
        ServletOutputStream outputStream = null;
        try {
            byte[] b = new byte[1024];
            int len = -1;
            outputStream = response.getOutputStream();
            while ((len = inputStream.read(b, 0, 1024)) != -1) {
                outputStream.write(b, 0, len);
            }
        } catch (IOException e) {
            log.error("流程图获取异常");
        } finally {
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/


}
