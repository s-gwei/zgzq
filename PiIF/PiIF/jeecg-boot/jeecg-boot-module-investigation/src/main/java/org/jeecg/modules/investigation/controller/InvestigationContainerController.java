package org.jeecg.modules.investigation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.investigation.entity.InvestigationContainer;
import org.jeecg.modules.investigation.service.IInvestigationContainerService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/28
 */
@Slf4j
@RestController
@Api("调查活动接口")
@RequestMapping("/investigation/container")
public class InvestigationContainerController {
    @Resource
    private IInvestigationContainerService containerService;

    @PostMapping("/add")
    @ApiOperation("新增活动")
    public Result<?> add(@RequestBody InvestigationContainer container) {
        container.setDelFlag(false);
        boolean save = containerService.save(container);
        return Result.ok(container);
    }

    @GetMapping("/list")
    @ApiOperation("活动列表")
    public Result<IPage<InvestigationContainer>> list(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      String name) {
        Result<IPage<InvestigationContainer>> result = new Result<>();
        Page<InvestigationContainer> page = new Page<>(pageNo, pageSize);
        //查询所有没被删除的活动
        QueryWrapper<InvestigationContainer> qw = new QueryWrapper<InvestigationContainer>().eq("del_flag", false);
        if (StringUtils.hasText(name)) {
            qw.like("name", name);
        }
        IPage<InvestigationContainer> resultPage = containerService.page(page, qw);
        result.setResult(resultPage);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @PutMapping("/edit")
    @ApiOperation("修改活动")
    public Result<?> list(@RequestBody InvestigationContainer container) {
        containerService.updateById(container);
        return Result.ok(container);
    }


    @DeleteMapping("/delete")
    @ApiOperation("删除活动")
    public Result<?> delete(Integer containerId) {
        InvestigationContainer container = containerService.getById(containerId);
        if (container == null) {
            return Result.ok("删除成功");
        }
        container.setDelFlag(true);
        containerService.updateById(container);
        return Result.ok("删除成功");
    }

    @DeleteMapping("/deleteList")
    @ApiOperation("批量删除活动")
    public Result<?> deleteList(String containerIds) {
        String[] split = containerIds.split(",");
        List<String> list = Arrays.asList(split);
        for (String id : list) {
            int containerId = Integer.parseInt(id);
            InvestigationContainer container = containerService.getById(containerId);
            if (container == null) {
                continue;
            }
            container.setDelFlag(true);
            containerService.updateById(container);
        }
        return Result.ok("删除成功");
    }

}
