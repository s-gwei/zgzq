package org.jeecg.modules.investigation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.investigation.entity.InvestigationRelationship;
import org.jeecg.modules.investigation.service.IInvestigationRelationshipService;
import org.jeecg.modules.investigation.vo.RelationshipImportVO;
import org.jeecg.modules.investigation.vo.RelationshipVO;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author xduan
 * @version 2020/4/26
 */
@Slf4j
@RestController
@Api("评价关系维护接口")
@RequestMapping("/investigation/relationship")
public class InvestigationRelationshipController {

    @Resource
    private IInvestigationRelationshipService relationshipService;

    @GetMapping("/list")
    @ApiOperation("根据被评价人查询关系列表")
    public Result<IPage<InvestigationRelationship>> list(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         String assessorUserId) {
        Result<IPage<InvestigationRelationship>> result = new Result<IPage<InvestigationRelationship>>();
        if (StringUtils.isEmpty(assessorUserId)) {
            return result.error500("assessorUserId 不能为空");
        }
        QueryWrapper<InvestigationRelationship> qw = new QueryWrapper<InvestigationRelationship>();
        //根据被评价人查询
        qw.eq("assessor_user_id", assessorUserId);
        Page<InvestigationRelationship> page = new Page<InvestigationRelationship>(pageNo, pageSize);
        IPage<InvestigationRelationship> resultPage = relationshipService.page(page, qw);
        result.setResult(resultPage);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @PostMapping("/add")
    @ApiOperation("新增关系")
    public Result<?> add(@RequestBody InvestigationRelationship relationship) throws Exception {
        relationshipService.add(relationship);
        return Result.ok("新增成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除关系")
    public Result<?> delete(String relationshipId) {
        relationshipService.delete(relationshipId);
        return Result.ok("删除成功");
    }

    @DeleteMapping("/deleteByList")
    @ApiOperation("批量删除关系")
    public Result<?> deleteByList(String relationshipIds) {
        if (StringUtils.isEmpty(relationshipIds)) {
            return Result.error("参数为空");
        }
        List<String> list = Arrays.asList(relationshipIds.split(","));
        for (String relationshipId : list) {
            relationshipService.delete(relationshipId);
        }
        return Result.ok("删除成功");
    }

    /**
     * 修改权重，针对pm类型关系
     *
     * @param relationshipVOList
     * @return
     */
    @PutMapping("/editQWeight")
    @ApiOperation("批量修改权重")
    public Result<?> editQWeight(@RequestBody List<RelationshipVO> relationshipVOList) {
        relationshipService.editQWeight(relationshipVOList);
        return Result.ok("修改成功");
    }

    @PutMapping("/edit")
    @ApiOperation("修改关系")
    public Result<?> edit(@RequestBody InvestigationRelationship relationship) {
        relationshipService.updateById(relationship);
        return Result.ok("修改成功");
    }

    @GetMapping("/getById")
    @ApiOperation("获取关系实体")
    public Result<?> edit(String id) {
        InvestigationRelationship relationship = relationshipService.getById(id);
        return Result.ok(relationship);
    }

    /**
     * 通过excel导入关系数据
     *
     * @param request
     *
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    @ApiOperation("导入关系")
    public Result<?> importExcel(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        if (StringUtils.isEmpty(idStr)){
            return Result.error("缺少活动id");
        }
        //活动id
        Integer id = Integer.valueOf(idStr);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setHeadRows(1);
          //  params.setNeedSave(true);
            try {
                //解析excel
                List<RelationshipImportVO> relationshipImportVOS = ExcelImportUtil.importExcel(file.getInputStream(), RelationshipImportVO.class, params);
                relationshipService.importRelationships(relationshipImportVOS,id);
                return Result.ok("文件导入成功！数据行数：" + relationshipImportVOS.size());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return Result.error(e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }

   /* *//**
     * 导出关系模板
     *
     *
     *//*
    @ApiOperation("导出关系模板")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls() {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
       List<RelationshipImportVO>  relationshipImportVOS =createTemplate();
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "关系导入模板");
        mv.addObject(NormalExcelConstants.CLASS, RelationshipImportVO.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams());
        mv.addObject(NormalExcelConstants.DATA_LIST, relationshipImportVOS);
        return mv;
    }*/

   /* *//**
     * 创建模板数据
     * @return
     *//*
    private List<RelationshipImportVO> createTemplate() {
        List<RelationshipImportVO> list = new ArrayList<>();
        RelationshipImportVO relationshipImportVO = new RelationshipImportVO();
        relationshipImportVO.setAssessorRealname("何真元");
        relationshipImportVO.setEvaluatorRealname("何真元");
        relationshipImportVO.setQWeight("1.00");
        relationshipImportVO.setRelationship(RelationshipConstant.INDIVIDUAL);

        list.add(relationshipImportVO);

        RelationshipImportVO relationshipImportVO2 = new RelationshipImportVO();
        relationshipImportVO2.setAssessorRealname("何真元");
        relationshipImportVO2.setEvaluatorRealname("罗成");
        relationshipImportVO2.setQWeight("0.50");
        relationshipImportVO2.setRelationship(RelationshipConstant.PEER);
        list.add(relationshipImportVO2);

        RelationshipImportVO relationshipImportVO3 = new RelationshipImportVO();
        relationshipImportVO3.setAssessorRealname("何真元");
        relationshipImportVO3.setEvaluatorRealname("许正刚");
        relationshipImportVO3.setQWeight("0.80");
        relationshipImportVO3.setRelationship(RelationshipConstant.PM);
        list.add(relationshipImportVO3);

        RelationshipImportVO relationshipImportVO4= new RelationshipImportVO();
        relationshipImportVO4.setAssessorRealname("何真元");
        relationshipImportVO4.setEvaluatorRealname("高国军");
        relationshipImportVO4.setQWeight("1.00");
        relationshipImportVO4.setRelationship(RelationshipConstant.DIRECTOR);
        list.add(relationshipImportVO4);

        RelationshipImportVO relationshipImportVO5= new RelationshipImportVO();
        relationshipImportVO5.setAssessorRealname("何真元");
        relationshipImportVO5.setEvaluatorRealname("高国军");
        relationshipImportVO5.setQWeight("0.50");
        relationshipImportVO5.setRelationship(RelationshipConstant.PEER);
        list.add(relationshipImportVO5);

        RelationshipImportVO relationshipImportVO6= new RelationshipImportVO();
        relationshipImportVO6.setAssessorRealname("何真元");
        relationshipImportVO6.setEvaluatorRealname("罗成");
        relationshipImportVO6.setQWeight("0.20");
        relationshipImportVO6.setRelationship(RelationshipConstant.PM);
        list.add(relationshipImportVO6);
        return list;
    }*/
}
