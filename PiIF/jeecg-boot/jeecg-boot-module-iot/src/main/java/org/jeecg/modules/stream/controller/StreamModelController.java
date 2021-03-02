package org.jeecg.modules.stream.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.stream.entity.IotStreamModel;
import org.jeecg.modules.stream.entity.StreamModelVO;
import org.jeecg.modules.stream.service.IStreamModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:23 2020/3/4
 * @Description:streamModel的crud
 */
@RestController
@RequestMapping("/streamModel")
@Slf4j
public class StreamModelController {

    @Autowired
    private IStreamModelService streamModelService;


    /**
     * 根据条件模糊查询
     *
     * @param name
     * @param description
     * @param tags
     * @param projectName
     * @param userId      当前用户id
     * @return Result
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Result<IPage<IotStreamModel>> query(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               String name, String description, String tags, String projectName, String userId) {
        Result<IPage<IotStreamModel>> result = new Result<>();
        try {
            Page<IotStreamModel> page = new Page<>(pageNo, pageSize);
            IPage<IotStreamModel> streamModelIPage = streamModelService.query(page, name, description, tags, projectName, userId);
            result.setResult(streamModelIPage);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            result.error500(e.getMessage());
        }
        return result;
    }


    /**
     * 查询混搭信息
     * @param tableName stream表名称
     * @return Result
     */
    @RequestMapping(value = "/findMashUpData", method = RequestMethod.GET)
    public Result<Object> findMashUpData(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, String tableName) {
        Result<Object> result = new Result<>();
        try {
            result = streamModelService.findMashUpData(pageNo, pageSize, tableName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 查询StreamModel
     *
     * @param name stream表名
     * @return Result
     */
    @RequestMapping(value = "/findStreamByName", method = RequestMethod.GET)
    public Result<Object> findStreamById(String name) {
        Result<Object> result = new Result<>();
        try {
            result = streamModelService.findStreamById(name);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 新增streamModel
     *
     * @param streamModelVO
     * @return Result
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<Object> add(@RequestBody StreamModelVO streamModelVO) {
        Result<Object> result = new Result<>();
        try {
            result = streamModelService.add(streamModelVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }


    /**
     * 更新streamModel
     *
     * @param streamModelVO
     * @return Result
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result<Object> update(@RequestBody StreamModelVO streamModelVO) {
        Result<Object> result = new Result<>();
        try {
            result = streamModelService.updateStreamModel(streamModelVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 删除streamModel
     *
     * @param tableName 表名
     * @param user      删除人
     * @return Result
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result<Object> delete(String tableName, String user) {
        Result<Object> result = new Result<>();
        try {
            //删除streamModel
            result = streamModelService.deleteStreamModel(tableName, user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }



}
