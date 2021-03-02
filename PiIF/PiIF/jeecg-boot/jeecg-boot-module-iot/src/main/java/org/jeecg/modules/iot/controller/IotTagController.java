package org.jeecg.modules.iot.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.iot.entity.IotTag;
import org.jeecg.modules.iot.entity.IotTagVocabulary;
import org.jeecg.modules.iot.service.ITagService;
import org.jeecg.modules.iot.service.ITagVocabularyService;
import org.jeecg.modules.iot.vo.VocabularyVO;
import org.jeecg.modules.system.service.IPermissionService;
import org.jeecg.modules.system.service.ISysVisiblePermissionService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/10
 */
@Slf4j
@RestController
@RequestMapping("/iot/tag")
@Api("标签接口")
public class IotTagController {
    private static final String TAG = "iot_tag";
    @Resource
    private ITagService tagService;
    @Resource
    private ITagVocabularyService tagVocabularyService;

    @Resource
    private IPermissionService permissionService;

    @Resource
    private ISysVisiblePermissionService visiblePermissionService;

    @PostMapping("/addTag")
    @ApiOperation("新增标签")
    public Result<IotTag> addTag(@RequestBody IotTag tag) {
        Result<IotTag> result = new Result<IotTag>();
        try {
            IotTag name = tagService.getOne(new QueryWrapper<IotTag>().eq("name", tag.getName()));
            if (name != null) {
                return result.error500("标签名称不能重复");
            }
            tag = tagService.add(tag);
        } catch (Exception e) {
            log.error(e.toString());
            return result.error500(e.getMessage());
        }
        result.setResult(tag);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @PutMapping("/editTag")
    @ApiOperation("修改标签")
    public Result<?> editTag(@RequestBody IotTag tag) {
        tagService.updateById(tag);
        return Result.ok("修改成功");
    }

    @DeleteMapping("/deleteTag")
    @ApiOperation("删除标签")
    public Result<?> deleteTag(String id) {
        tagService.delete(id);
        return Result.ok("删除成功");
    }

    @PostMapping("/addVocabulary")
    @ApiOperation("给标签添加词汇")
    public Result<?> addVocabulary(@RequestBody IotTagVocabulary vocabulary) {
        String vocabularyName = vocabulary.getVocabulary();
        if (StringUtils.isEmpty(vocabularyName)) {
            return Result.error("词汇名不能为空");
        }
        try {
            boolean save = tagVocabularyService.save(vocabulary);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.getMessage());
        }
        return Result.ok("新增成功");
    }

    @DeleteMapping("/deleteVocabulary")
    @ApiOperation("给标签删除单个词汇")
    public Result<?> deleteVocabulary(String id) {
        if (StringUtils.isEmpty(id)) {
            return Result.error("id不能为空");
        }
        try {
            tagVocabularyService.removeById(id);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.getMessage());
        }
        return Result.ok("删除成功");
    }

    @DeleteMapping("/deleteVocabularyList")
    @ApiOperation("批量删除词汇")
    public Result<?> deleteVocabularyList(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return Result.error("id不能为空");
        }
        try {
            String[] split = ids.split(",");
            tagVocabularyService.removeByIds(Arrays.asList(split));
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.getMessage());
        }
        return Result.ok("删除成功");
    }


    @GetMapping("/getVocabularyByTagId")
    @ApiOperation("获取标签的词汇")
    public Result<List<IotTagVocabulary>> getVocabularyByTagId(String tagId) {
        Result<List<IotTagVocabulary>> result = new Result<List<IotTagVocabulary>>();
        if (StringUtils.isEmpty(tagId)) {
            return result.error500("id不能为空");
        }
        try {
            List<IotTagVocabulary> vocabularyList = tagVocabularyService.list(new QueryWrapper<IotTagVocabulary>().eq("tag_id", tagId));
            result.setResult(vocabularyList);
            result.setSuccess(true);
            result.setCode(CommonConstant.SC_OK_200);
        } catch (Exception e) {
            log.error(e.toString());
            return result.error500(e.getMessage());
        }
        return result;
    }

    @GetMapping("/getTag")
    @ApiOperation("根据id查标签")
    public Result<IotTag> getTag(String id) {
        Result<IotTag> result = new Result<IotTag>();
        IotTag tag = tagService.getById(id);
        result.setResult(tag);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @DeleteMapping("/deleteTagList")
    @ApiOperation("批量删除标签")
    public Result<?> deleteTagList(String ids) {
        String[] split = ids.split(",");
        for (String id : split) {
            tagService.delete(id);
        }
        return Result.ok("删除成功");
    }

    @GetMapping("/getVocabularyByUserId")
    @ApiOperation("根据用户id查标签的词汇")
    public Result<List<VocabularyVO>> getVocabularyByUserId(String userId) {
        Result<List<VocabularyVO>> result = new Result<List<VocabularyVO>>();
        //判断是否是管理员
        boolean admin = permissionService.isAdmin(userId);
        //获取拥有的部门权限列表
        List<String> departList = permissionService.getDepartList(userId);
        List<VocabularyVO> list = tagVocabularyService.getVocabularyByUserId(departList, admin);
        result.setResult(list);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @GetMapping("/query")
    @ApiOperation("多条件查询")
    public Result<IPage<IotTag>> query(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                       String userId, String tagName, String projectPk, String tagPk) {
        Result<IPage<IotTag>> result = new Result<>();
        QueryWrapper<IotTag> qw = new QueryWrapper<IotTag>();
        if (StringUtils.hasText(tagName)) {
            qw.like("name", tagName);
        }
        if (StringUtils.hasText(projectPk)) {
            qw.eq("project_pk", projectPk);
        }
        if (StringUtils.hasText(tagPk)) {
            qw.like("tag_pk", tagPk);
        }
        if (!permissionService.isAdmin(userId)) {
            //不是管理员
            List<String> objectPks = visiblePermissionService.getObjectPkByUserId(userId, TAG);
            if (objectPks.size() > 0) {
                //可查看的对象大于o
                qw.in("object_pk", objectPks);
            } else {
                setResult(result, new Page<IotTag>());
                return result;
            }
        }
        Page<IotTag> page = new Page<IotTag>(pageNo, pageSize);
        IPage<IotTag> page1 = tagService.page(page, qw);
        setResult(result, page1);
        return result;
    }

    private void setResult(Result<IPage<IotTag>> result, IPage<IotTag> list) {
        result.setResult(list);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
    }
}
