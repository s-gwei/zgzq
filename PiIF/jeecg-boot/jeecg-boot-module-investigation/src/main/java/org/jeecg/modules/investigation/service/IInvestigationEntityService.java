package org.jeecg.modules.investigation.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.investigation.entity.InvestigationEntity;
import org.jeecg.modules.investigation.vo.InvestigationScoreVO;
import org.jeecg.modules.investigation.vo.ReportVO;

import java.util.List;

/**
 * @author xduan
 * @version 2020/4/24
 * @desc 调查问卷实体的服务类
 */
public interface IInvestigationEntityService extends IService<InvestigationEntity> {
    /**
     * 根据活动id，创建对应的问卷实例，并发送邮件
     *
     * @param id
     * @throws Exception
     */
    void sendMessage(Integer id) throws Exception;

    /**
     * 根据用户id，查询得分
     *
     * @param userId
     * @param containerId
     * @return
     */
    InvestigationScoreVO getScoreByUserId(String userId, Integer containerId);

    /**
     * 根据部门id查询所有人员得分，包含下级部门的人员
     *
     * @param pageNo
     * @param pageSize
     * @param departId
     * @param containerId
     * @return
     */
    JSONObject getScoreByDepartId(Integer pageNo, Integer pageSize, String departId, Integer containerId);


    List<InvestigationScoreVO> getScoreByDepartId(String departId, Integer containerId);

    ReportVO getReportVOByUserId(String userId, Integer containerId);

    /**
     * 根据部门，活动，状态等查询问卷的实例
     * @param page
     * @param departId
     * @param containerId
     * @param status
     * @param name
     * @param assessorRealname
     * @return
     */
    IPage<InvestigationEntity> getByDepartIdAndContainerIdAndStatus(Page<InvestigationEntity> page, String departId, Integer containerId, String status, String name, String assessorRealname);

    /**
     * 统计各个状态的评价总数
     * @param departId
     * @param containerId
     * @return
     */
    JSONObject countByDepartIdAndContainerId(String departId, Integer containerId);
}
