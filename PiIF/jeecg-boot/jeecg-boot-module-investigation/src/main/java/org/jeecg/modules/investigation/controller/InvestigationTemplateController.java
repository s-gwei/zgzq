package org.jeecg.modules.investigation.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.investigation.entity.InvestigationTemplate;
import org.jeecg.modules.investigation.model.Template;
import org.jeecg.modules.investigation.service.IInvestigationTemplateService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xduan
 * @version 2020/5/6
 * 问卷模板的接口
 */
@Slf4j
@RestController
@Api("问卷模板接口")
@RequestMapping("/investigation/template")
public class InvestigationTemplateController {

    @Resource
    private IInvestigationTemplateService templateService;

    @GetMapping("/list")
    @ApiOperation("获取所有模板")
    public Result<List<InvestigationTemplate>> list() {
        Result<List<InvestigationTemplate>> result = new Result<>();
        List<InvestigationTemplate> list = templateService.list();
        result.setResult(list);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @PutMapping("/edit")
    @ApiOperation("修改模板基础信息")
    public Result<?> edit(@RequestBody InvestigationTemplate investigationTemplate) {
        templateService.updateById(investigationTemplate);
        return Result.ok("修改成功");
    }

    @GetMapping("/getById")
    @ApiOperation("获取单个模板的详情")
    public Result<?> getById(Integer id) {
        InvestigationTemplate template = templateService.getById(id);
        return Result.ok(template);
    }


    @PutMapping("/editTemplate")
    @ApiOperation("修改问卷模板评分详情")
    public Result<?> editTemplate(@RequestBody Template template, Integer id) {
        templateService.editTemplate(template, id);
        return Result.ok("修改成功");
    }

    @GetMapping("/getDetail")
    @ApiOperation("获取问卷模板评分详情")
    public Result<?> getDetail(Integer id) {
        InvestigationTemplate investigationTemplate = templateService.getById(id);
        String templateStr = investigationTemplate.getTemplate();
        Template template = JSON.parseObject(templateStr, Template.class);
        return Result.ok(template);
    }

}
