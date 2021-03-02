package org.jeecg.modules.investigation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.investigation.entity.InvestigationRelationship;
import org.jeecg.modules.investigation.vo.RelationshipImportVO;
import org.jeecg.modules.investigation.vo.RelationshipVO;

import java.util.List;

/**
 * @author xduan
 * @version 2020/4/24
 * @desc 调查问卷关系评价维护的服务接口
 */
public interface IInvestigationRelationshipService extends IService<InvestigationRelationship> {
    /**
     * 新增评价关系
     * @param relationship
     */
    void add(InvestigationRelationship relationship) throws Exception;

    void delete(String relationshipId);

    /**
     * 批量修改权重
     * @param relationshipVOList
     */
    void editQWeight(List<RelationshipVO> relationshipVOList);

    /**
     * 批量导入关系数据
     * @param relationshipImportVOS
     * @param id
     */
    void importRelationships(List<RelationshipImportVO> relationshipImportVOS, Integer id) throws Exception;
}
