package org.jeecg.modules.investigation.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.investigation.constant.StatusConstant;
import org.jeecg.modules.investigation.entity.InvestigationEntity;
import org.jeecg.modules.investigation.model.Template;
import org.jeecg.modules.investigation.model.TemplateDetail;
import org.jeecg.modules.investigation.service.IInvestigationEntityService;
import org.jeecg.modules.investigation.vo.InvestigationScoreVO;
import org.jeecg.modules.investigation.vo.ReportVO;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/26
 */
@Slf4j
@RestController
@Api("调查问卷接口")
@RequestMapping("/investigation/entity")
public class InvestigationEntityController {

    @Resource
    private IInvestigationEntityService entityService;

    @Resource
    private ISysBaseAPI sysBaseAPI;

    /**
     * 根据活动id下发问卷
     *
     * @param id 活动id
     * @return
     */
    @GetMapping("/sendMessage")
    @ApiOperation("一键下发问卷")
    public Result<?> sendMessage(Integer id) throws Exception {
        entityService.sendMessage(id);
        return Result.ok("问卷下发成功");
    }

    @GetMapping("/getEntity")
    @ApiOperation("获取当个实例的详情")
    public Result<InvestigationEntity> getEntity(String entityId) throws UnknownHostException {
        Result<InvestigationEntity> result = new Result<InvestigationEntity>();
        InvestigationEntity entity = entityService.getById(entityId);
        result.setResult(entity);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @GetMapping("/getEntityListByUserId")
    @ApiOperation("根据当前用户id查询所有的需要评价的问卷实例")
    public Result<IPage<InvestigationEntity>> getEntityListByUserId(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                    String userId, String status, String qName) throws UnknownHostException {
        Result<IPage<InvestigationEntity>> result = new Result<IPage<InvestigationEntity>>();
        if (StringUtils.isEmpty(userId)) {
            return result.error500("userId 不能为空");
        }
        QueryWrapper<InvestigationEntity> qw = new QueryWrapper<InvestigationEntity>()
                .eq("evaluator_user_id", userId);
        if (StringUtils.hasText(status)) {
            qw.eq("status", status);
        }
        // 根据被评价人搜索
        if (StringUtils.hasText(qName)) {
            qw.like("assessor_realname", qName);
        }
        Page<InvestigationEntity> page = new Page<InvestigationEntity>(pageNo, pageSize);
        IPage<InvestigationEntity> entityIPage = entityService.page(page, qw);
        result.setResult(entityIPage);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @GetMapping("/getEntityList")
    @ApiOperation("获取所有的问卷实例")
    public Result<IPage<InvestigationEntity>> getEntityList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) throws UnknownHostException {
        Result<IPage<InvestigationEntity>> result = new Result<IPage<InvestigationEntity>>();
        Page<InvestigationEntity> page = new Page<InvestigationEntity>(pageNo, pageSize);
        IPage<InvestigationEntity> entityIPage = entityService.page(page);
        result.setResult(entityIPage);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    /**
     * @param entity   问卷实例
     * @param isCommit 是否提交，true，提交，问卷修改成完成状态；false ，不提交,问卷修改为编辑状态
     * @return
     */
    @PutMapping("/edit")
    @ApiOperation("填写问卷")
    public Result<?> edit(@RequestBody InvestigationEntity entity, boolean isCommit) {
        if (isCommit) {
            //计算得分
            Double score = calculateScore(entity);
            entity.setStatus(StatusConstant.COMPLETE);
            entity.setScore(score);
            entityService.updateById(entity);
            return Result.ok("提交成功");
        } else {
            entity.setStatus(StatusConstant.EDIT);
            entityService.updateById(entity);
            return Result.ok("保存成功");
        }
    }

    /**
     * 计算问卷的得分
     *
     * @param entity
     * @return
     */
    private Double calculateScore(InvestigationEntity entity) {
        String templateStr = entity.getTemplate();
        Template template = JSON.parseObject(templateStr, Template.class);
        List<TemplateDetail> lists = template.getLists();
        Double scoreValue = 0.0d;
        for (TemplateDetail templateDetail : lists) {
            String scoreStr = templateDetail.getScore();
            double score = Double.parseDouble(scoreStr);
            String weightingStr = templateDetail.getWeighting();
            double weighting = Double.parseDouble(weightingStr);
            scoreValue += ((score * weighting) / 100);
        }
        return scoreValue;
    }

    @GetMapping("/getScoreByUserId")
    @ApiOperation("根据用户id查询360评价得分详情")
    public Result<ReportVO> getScoreByUserId(String userId, Integer containerId) {
        Result<ReportVO> result = new Result<>();
        ReportVO reportVO = entityService.getReportVOByUserId(userId, containerId);
        result.setResult(reportVO);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @GetMapping("/getScoreByDepartId")
    @ApiOperation("根据部门id查询所有人员的360评价得分")
    public Result<JSONObject> getScoreByDepartId(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 String departId, Integer containerId) {
        Result<JSONObject> result = new Result<JSONObject>();
        JSONObject scoreByDepartId = entityService.getScoreByDepartId(pageNo, pageSize, departId, containerId);
        result.setResult(scoreByDepartId);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @GetMapping("/queryEntity")
    @ApiOperation("根据部门id和活动id查询所有人员的360评价实例")
    public Result<IPage<InvestigationEntity>> getEntity(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                        String departId, Integer containerId, String status, String evaluatorRealname, String assessorRealname) {
        Result<IPage<InvestigationEntity>> result = new Result<IPage<InvestigationEntity>>();
        Page<InvestigationEntity> page = new Page<>(pageNo, pageSize);
        //根据条件查询调查的实例
        IPage<InvestigationEntity> entityIPage = entityService.getByDepartIdAndContainerIdAndStatus(page, departId, containerId, status, evaluatorRealname, assessorRealname);
        result.setResult(entityIPage);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @GetMapping("/countEntity")
    @ApiOperation("根据部门id和活动id统计所有人员的360评价的状态")
    public Result<JSONObject> countEntity(String departId, Integer containerId) {
        Result<JSONObject> result = new Result<JSONObject>();
        //根据条件查询调查实例，统计状态
        JSONObject object = entityService.countByDepartIdAndContainerId(departId, containerId);
        result.setResult(object);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    /**
     * 导出excel,部门分数报表
     *
     * @param departId
     * @param containerId
     */
    @ApiOperation("导出报表")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(String departId, Integer containerId) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<InvestigationScoreVO> scoreVOList = entityService.getScoreByDepartId(departId, containerId);
        for (InvestigationScoreVO scoreVO : scoreVOList) {
            LoginUser user = sysBaseAPI.getUserById(scoreVO.getUserId());
            //修改返回的数据为工号
            scoreVO.setUserId(user.getWorkNo());
        }
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "部门评分");
        mv.addObject(NormalExcelConstants.CLASS, InvestigationScoreVO.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String realname = "";
        if (user != null) {
            realname = user.getRealname();
        }
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("部门评分详情", "导出人:" + realname, "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, scoreVOList);
        return mv;
    }

}
