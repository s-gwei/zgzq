package org.jeecg.modules.investigation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.investigation.constant.RelationshipConstant;
import org.jeecg.modules.investigation.entity.InvestigationParticipant;
import org.jeecg.modules.investigation.entity.InvestigationRelationship;
import org.jeecg.modules.investigation.mapper.ParticipantMapper;
import org.jeecg.modules.investigation.mapper.RelationshipMapper;
import org.jeecg.modules.investigation.service.IInvestigationRelationshipService;
import org.jeecg.modules.investigation.vo.RelationshipImportVO;
import org.jeecg.modules.investigation.vo.RelationshipVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/26
 * @desc 评价关系维护 服务类
 */
@Service
public class InvestigationRelationshipServiceImpl extends ServiceImpl<RelationshipMapper, InvestigationRelationship> implements IInvestigationRelationshipService {

    private static final String UNPUBLISHED = "未发布";
    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Resource
    private ParticipantMapper participantMapper;

    /**
     * 新增评价关系
     * 需要根据关系类型，处理权重
     *
     * @param relationship
     */
    @Override
    @Transactional
    public void add(InvestigationRelationship relationship) throws Exception {
        //关系类型
        String relationshipType = relationship.getRelationship();
        if (RelationshipConstant.INDIVIDUAL.equals(relationshipType) || RelationshipConstant.DIRECTOR.equals(relationshipType)) {
            //个人，总监类型 只能存在一个关系
            List<InvestigationRelationship> list = baseMapper.selectList(new QueryWrapper<InvestigationRelationship>()
                    .eq("assessor_user_id", relationship.getAssessorUserId())
                    .eq("relationship", relationshipType));
            if (list.size() > 0) {
                String type = "";
                if (RelationshipConstant.INDIVIDUAL.equals(relationshipType)) {
                    type = "个人";
                }
                if (RelationshipConstant.DIRECTOR.equals(relationshipType)) {
                    type = "总监";
                }
                throw new Exception("不能重复添加" + type + "关系");
            }
        }
        if (RelationshipConstant.PM.equals(relationshipType)) {
            //如果是pm类型，不做处理
            baseMapper.insert(relationship);
        } else {
            //如果不是pm类型,则所有的评价人的权重平分
            List<InvestigationRelationship> investigationRelationshipList = getInvestigationRelationships(relationship.getAssessorUserId(), relationshipType);
            //新增之后，当前类型的关系总数
            int count = investigationRelationshipList.size() + 1;
            DecimalFormat df = new DecimalFormat("0.00");
            //平均每个人的权重
            double value = Double.parseDouble(df.format((double) 100 / (count)));
            relationship.setQWeight(value);
            //新增关系
            int insert = baseMapper.insert(relationship);
            if (insert != 1) {
                //新增失败则不做后续处理
                return;
            }
            for (InvestigationRelationship investigationRelationship : investigationRelationshipList) {
                //修改数据库中已存在的关系权重
                investigationRelationship.setQWeight(value);
                baseMapper.updateById(investigationRelationship);
            }
        }
    }

    /**
     * 删除关系
     * 删除完成后需要对数据库中的关系权重进行处理
     *
     * @param relationshipId
     */
    @Override
    public void delete(String relationshipId) {
        InvestigationRelationship investigationRelationship = baseMapper.selectById(relationshipId);
        if (investigationRelationship == null) {
            return;
        }
        String relationshipType = investigationRelationship.getRelationship();
        if (RelationshipConstant.PM.equals(relationshipType)) {
            //如果是pm类型，不做处理
            baseMapper.deleteById(relationshipId);
        } else {
            //如果不是pm类型,则所有的评价人的权重平分
            List<InvestigationRelationship> investigationRelationshipList = getInvestigationRelationships(investigationRelationship.getAssessorUserId(), relationshipType);
            //删除之后，当前类型的关系总数
            int count = investigationRelationshipList.size() - 1;
            if (count <= 0) {
                //如果删除之后，当前类型的关系为0，则跳过后续处理
                baseMapper.deleteById(relationshipId);
                return;
            }
            DecimalFormat df = new DecimalFormat("0.00");
            //平均每个人的权重
            double value = Double.parseDouble(df.format((double) 100 / (count)));
            int i = baseMapper.deleteById(relationshipId);
            if (i != 1) {
                //删除失败则不做后续处理
                return;
            }
            for (InvestigationRelationship relationship : investigationRelationshipList) {
                //修改数据库中的关系权重
                relationship.setQWeight(value);
                baseMapper.updateById(relationship);
            }
        }
    }

    /**
     * 批量修改权重,用于pm类型关系的权重修改
     *
     * @param relationshipVOList
     */
    @Override
    @Transactional
    public void editQWeight(List<RelationshipVO> relationshipVOList) {
        for (RelationshipVO relationshipVO : relationshipVOList) {
            InvestigationRelationship relationship = baseMapper.selectById(relationshipVO.getId());
            relationship.setQWeight(relationshipVO.getQWeight());
            baseMapper.updateById(relationship);
        }
    }

    /**
     * 批量导入关系数据
     *
     * @param relationshipImportVOS
     * @param id
     */
    @Override
    @Transactional
    public void importRelationships(List<RelationshipImportVO> relationshipImportVOS, Integer id) throws Exception {
        List<String> assessorRealnames = new LinkedList<>();
        //遍历导入数据，获取所有的新增的被评价人姓名
        for (RelationshipImportVO relationshipImportVO : relationshipImportVOS) {
            String assessorRealname = relationshipImportVO.getAssessorRealname();
            if (!assessorRealnames.contains(assessorRealname)) {
                assessorRealnames.add(assessorRealname);
                addParticipant(assessorRealname, id);
            }
        }
        //删除被评价人已存在的关系
        QueryWrapper<InvestigationRelationship> qw = new QueryWrapper<>();
        qw.in("assessor_realname", assessorRealnames);
        baseMapper.delete(qw);
        //新增导入的关系
        for (RelationshipImportVO relationshipImportVO : relationshipImportVOS) {
            InvestigationRelationship relationship = createRelationship(relationshipImportVO);
            //新增关系
            add(relationship);
        }
    }

    /**
     * 新增参与者，如果已经存在，则跳过
     *
     * @param assessorRealname
     * @param id
     */
    private void addParticipant(String assessorRealname, Integer id) {
        LoginUser user = sysBaseAPI.getUserByRealname(assessorRealname);
        if (user == null) {
            return;
        }
        //查询用户是否已经存在于该活动的参与者中
        List<InvestigationParticipant> list = participantMapper.selectList(new QueryWrapper<InvestigationParticipant>()
                .eq("user_id", user.getId()).eq("container_id", id));
        if (list != null && list.size() > 0) {
            return;
        }
        InvestigationParticipant participant = new InvestigationParticipant();
        participant.setContainerId(id);
        participant.setUserId(user.getId());
        participant.setStatus(UNPUBLISHED);
        participantMapper.insert(participant);
    }

    /**
     * 创建关系对象
     *
     * @param relationshipImportVO
     * @return
     */
    private InvestigationRelationship createRelationship(RelationshipImportVO relationshipImportVO) throws Exception {
        InvestigationRelationship relationship = new InvestigationRelationship();
        //设置评价信息
        String assessorRealname = relationshipImportVO.getAssessorRealname();
        LoginUser assessorUser = sysBaseAPI.getUserByRealname(assessorRealname);
        if (assessorUser == null) {
            throw new Exception(assessorRealname + "不存在");
        }
        relationship.setAssessorRealname(assessorRealname);
        relationship.setAssessorUserId(assessorUser.getId());
        String evaluatorRealname = relationshipImportVO.getEvaluatorRealname();
        LoginUser evaluatorUser = sysBaseAPI.getUserByRealname(evaluatorRealname);
        if (evaluatorUser == null) {
            throw new Exception(evaluatorRealname + "不存在");
        }
        relationship.setEvaluatorRealname(evaluatorRealname);
        relationship.setEvaluatorUserId(evaluatorUser.getId());
        relationship.setEvaluatorUsername(evaluatorUser.getUsername());
        String qWeightStr = relationshipImportVO.getQWeight();
        if (StringUtils.isEmpty(qWeightStr)){
            throw new Exception("权重不能为空");
        }
        //处理权重
        double qWeight = Double.parseDouble(qWeightStr);
        relationship.setQWeight(qWeight * 100);
        //设置关系
        relationship.setRelationship(relationshipImportVO.getRelationship());
        return relationship;
    }

    /**
     * 根据类型和被评价人查询所有的关系实例
     *
     * @param assessorUserId   被评价人id
     * @param relationshipType 关系类型
     * @return
     */
    private List<InvestigationRelationship> getInvestigationRelationships(String assessorUserId, String relationshipType) {
        QueryWrapper<InvestigationRelationship> qw = new QueryWrapper<InvestigationRelationship>();
        //关系类型相同
        qw.eq("relationship", relationshipType);
        //被评价人相同
        qw.eq("assessor_user_id", assessorUserId);
        //所有的相同关系的类型
        return baseMapper.selectList(qw);
    }
}


