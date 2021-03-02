package org.jeecg.modules.visualization.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.visualization.entity.IotVisualization;
import org.jeecg.modules.visualization.service.IVisualizationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author xduan
 * @version 2020/5/28
 */
@Slf4j
@RestController
@RequestMapping("/iot/visualization")
@Api("物可视相关接口")
public class VisualizationController {
    @Resource
    private IVisualizationService visualizationService;

    /**
     * 创建大屏可视化对象
     *
     * @param visualization
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增大屏可视化对象")
    public Result<?> add(@RequestBody IotVisualization visualization) {
        visualization = visualizationService.add(visualization);
        return Result.ok(visualization);
    }

    /**
     * 获取大屏可视化对象详情
     *
     * @param id
     * @return
     */
    @GetMapping("/getVisualization")
    @ApiOperation("获取大屏可视化对象详情")
    public Result<?> getVisualization(@RequestParam String id) {
        IotVisualization visualization = visualizationService.getById(id);
        return Result.ok(visualization);
    }

    @GetMapping("/list")
    @ApiOperation("获取大屏可视化对象列表")
    public Result<IPage<IotVisualization>> list(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<IPage<IotVisualization>> result = new Result<IPage<IotVisualization>>();
        IPage<IotVisualization> page = visualizationService.page(new Page<>(pageNo, pageSize));
        return result.successData(page);
    }

    /**
     * 修改大屏可视化对象
     *
     * @param visualization
     * @return
     */
    @PutMapping("/edit")
    @ApiOperation("修改大屏可视化对象")
    public Result<?> edit(@RequestBody IotVisualization visualization) {
        visualizationService.updateById(visualization);
        return Result.ok("修改成功");
    }
    @DeleteMapping("/delete")
    @ApiOperation("删除大屏可视化对象")
    public Result<?> delete(@RequestParam String id) {
        visualizationService.removeById(id);
        return Result.ok("删除成功");
    }

}
