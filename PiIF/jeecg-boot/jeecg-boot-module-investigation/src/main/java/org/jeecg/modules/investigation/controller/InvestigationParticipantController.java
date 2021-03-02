package org.jeecg.modules.investigation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.investigation.entity.InvestigationParticipant;
import org.jeecg.modules.investigation.service.IInvestigationParticipantService;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/26
 */
@Slf4j
@RestController
@Api("参与者接口")
@RequestMapping("/investigation/participant")
public class InvestigationParticipantController {

    @Resource
    private IInvestigationParticipantService participantService;

    @GetMapping("/list")
    @ApiOperation("根据活动id查询参与者")
    public Result<IPage<SysUser>> list(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                       String containerId, String realname) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        if (StringUtils.isEmpty(containerId)) {
            return result.error500("containerId 不能为空");
        }
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        IPage<SysUser> resultPage = participantService.findUserByContainerId(page, containerId, realname);
        result.setResult(resultPage);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @PostMapping("/add")
    @ApiOperation("给活动添加参与者")
    public Result<?> add(@RequestBody List<String> userIds, Integer containerId) {
        InvestigationParticipant investigationParticipant = new InvestigationParticipant();
        for (String userId : userIds) {
            QueryWrapper<InvestigationParticipant> qw = new QueryWrapper<InvestigationParticipant>()
                    .eq("user_id", userId).eq("container_id", containerId);
            List<InvestigationParticipant> list = participantService.list(qw);
            if (list.size() > 0) {
                //如果已经存在，则不新增参与者
                continue;
            }
            investigationParticipant.setStatus("未发布");
            investigationParticipant.setUserId(userId);
            investigationParticipant.setContainerId(containerId);
            participantService.save(investigationParticipant);
        }
        return Result.ok("新增成功");
    }


    @DeleteMapping("/delete")
    @ApiOperation("活动删除参与者")
    public Result<?> delete(String userId, Integer containerId) {
        QueryWrapper<InvestigationParticipant> queryWrapper = new QueryWrapper<InvestigationParticipant>()
                .eq("user_id", userId).eq("container_id", containerId);
        participantService.remove(queryWrapper);
        return Result.ok("删除成功");
    }

}
