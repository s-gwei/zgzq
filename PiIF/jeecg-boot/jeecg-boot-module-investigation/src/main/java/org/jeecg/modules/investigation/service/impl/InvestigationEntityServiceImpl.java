package org.jeecg.modules.investigation.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.PageUtil;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.investigation.constant.RelationshipConstant;
import org.jeecg.modules.investigation.constant.StatusConstant;
import org.jeecg.modules.investigation.entity.*;
import org.jeecg.modules.investigation.mapper.EntityMapper;
import org.jeecg.modules.investigation.mapper.ParticipantMapper;
import org.jeecg.modules.investigation.mapper.RelationshipMapper;
import org.jeecg.modules.investigation.mapper.TemplateMapper;
import org.jeecg.modules.investigation.model.Template;
import org.jeecg.modules.investigation.model.TemplateDetail;
import org.jeecg.modules.investigation.service.IInvestigationEntityService;
import org.jeecg.modules.investigation.util.TokenHttpClientUtil;
import org.jeecg.modules.investigation.vo.InvestigationScoreVO;
import org.jeecg.modules.investigation.vo.ReportScoreVO;
import org.jeecg.modules.investigation.vo.ReportVO;
import org.jeecg.modules.system.service.IPermissionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xduan
 * @version 2020/4/26\
 * @desc 调查问卷实体
 */
@Service
public class InvestigationEntityServiceImpl extends ServiceImpl<EntityMapper, InvestigationEntity> implements IInvestigationEntityService {

    private static final String UNPUBLISHED = "未发布";
    private static final String PUBLISHED = "已发布";

    @Resource
    private ParticipantMapper participantMapper;

    @Resource
    private RelationshipMapper relationshipMapper;

    @Resource
    private TemplateMapper templateMapper;

    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Resource
    private IPermissionService permissionService;

    @Resource
    private TokenHttpClientUtil tokenHttpClientUtil;

    /**
     * 360评价的详情页面的url，放在邮件中
     */
    @Value("${pisx.investigation.url}")
    private String urlEmail = "";
    /**
     * 邮件模板的编码
     */
    @Value("${pisx.investigation.templateCode}")
    private String templateCode = "";

    /**
     * 根据活动id创建问卷实例，并下发问卷
     *
     * @param id
     */
    @Override
    @Transactional
    public void sendMessage(Integer id) throws Exception {
        //查询所有未发布的参与者对象
        List<InvestigationParticipant> investigationParticipantList = getInvestigationParticipant(id);
        if (investigationParticipantList.isEmpty()) {
            throw new Exception("活动已经下发，不需要重复下发");
        }
        String msg = checkParticipant(investigationParticipantList);
        if (StringUtils.hasText(msg)) {
            throw new Exception(msg);
        }
        for (InvestigationParticipant investigationParticipant : investigationParticipantList) {
            //创建问卷实例
            List<InvestigationEntity> entityList = createEntity(investigationParticipant);
            if (entityList.size() == 0) {
                continue;
            }
            //设置参与者状态
            investigationParticipant.setStatus(PUBLISHED);
            participantMapper.updateById(investigationParticipant);
        }
        //下发邮件
        for (InvestigationParticipant participant : investigationParticipantList) {
            sendMail(id, participant.getUserId());
        }
    }

    /**
     * @param containId         容器Id
     * @param participantUserId 参与者的用户Id
     * @throws UnknownHostException
     */
    private void sendMail(Integer containId, String participantUserId) throws UnknownHostException {
        //查询当前活动未发送邮件的问卷实体
        List<InvestigationEntity> entityList = baseMapper.selectList(new QueryWrapper<InvestigationEntity>().lambda().eq(InvestigationEntity::getContainerId, containId).eq(InvestigationEntity::getAssessorUserId, participantUserId));
        //获取所有的评价人的id
        List<String> userIds = entityList.stream().map(InvestigationEntity::getEvaluatorUserId).collect(Collectors.toList());
        //去重
        userIds = userIds.stream().distinct().collect(Collectors.toList());
        String url = getUrl();
        for (String userId : userIds) {
            LoginUser user = sysBaseAPI.getUserById(userId);
            MsgParams param = createParam(user);
            tokenHttpClientUtil.doPostJSON(url, JSON.toJSONString(param));
        }
    }

    /**
     * 根据用户id，查询得分
     *
     * @param userId
     * @param containerId
     * @return
     */
    @Override
    public InvestigationScoreVO getScoreByUserId(String userId, Integer containerId) {
        List<InvestigationEntity> entityList = baseMapper.selectList(new QueryWrapper<InvestigationEntity>()
                .eq("assessor_user_id", userId).eq("container_id", containerId));
        InvestigationScoreVO scoreVO = new InvestigationScoreVO();
        if (entityList.size() == 0) {
            //没有参与调查
            return null;
        }
        scoreVO.setUserId(userId);
        Double scoreValue = 0.00d;
        //评分状态
        boolean flag = true;
        scoreVO.setRealname(entityList.get(0).getAssessorRealname());
        for (InvestigationEntity entity : entityList) {
            if (!StatusConstant.COMPLETE.equals(entity.getStatus())) {
                //不属于完成状态
                flag = false;
            }
            Double score = entity.getScore();
            if (score == null) {
                score = 0.00d;
            }
            Double qWeight = entity.getQWeight();
            scoreValue += (score * qWeight / 100);
        }
        //保留2位小数
        scoreValue = (double) Math.round(scoreValue * 100) / 100;
        scoreVO.setScore(scoreValue);

        if (flag) {
            scoreVO.setStatus("评分完成");
        } else {
            scoreVO.setScore(0.00d);
            scoreVO.setStatus("评分未完成");
        }
        return scoreVO;
    }

    /**
     * 根据部门id查询所有人员得分，包含下级部门的人员
     *
     * @param pageNo
     * @param pageSize
     * @param departId
     * @return
     */
    @Override
    public JSONObject getScoreByDepartId(Integer pageNo, Integer pageSize, String departId, Integer containerId) {
        List<InvestigationScoreVO> scoreVOList = getScoreByDepartId(departId, containerId);
        //部门总分
        Double totalScore = getCountScore(scoreVOList);
        IPage<InvestigationScoreVO> paging = PageUtil.paging(pageNo, pageSize, scoreVOList);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",paging);
        jsonObject.put("totalScore",totalScore);
        return jsonObject;
    }

    /**
     * 计算部门总分
     * @param scoreVOList
     */
    private Double getCountScore(List<InvestigationScoreVO> scoreVOList) {
        Double sum = 0.00d;
        for (InvestigationScoreVO scoreVO : scoreVOList) {
            sum =add(sum,scoreVO.getScore());
        }
        return sum;
    }

    /**
     * 根据部门id查询所有人员得分，包含下级部门的人员
     *
     * @param departId
     * @param containerId
     * @return
     */
    @Override
    public List<InvestigationScoreVO> getScoreByDepartId(String departId, Integer containerId) {
        List<String> userIdList = permissionService.getUserIdList(departId);
        List<InvestigationScoreVO> scoreVOList = new ArrayList<>(userIdList.size());

        for (String userId : userIdList) {
            InvestigationScoreVO scoreVO = getScoreByUserId(userId, containerId);
            if (scoreVO != null) {
                scoreVOList.add(scoreVO);
            }
        }
        return scoreVOList;
    }

    /**
     * 根据用户Id查询得分详情
     *
     * @param userId
     * @param containerId
     * @return
     */
    @Override
    public ReportVO getReportVOByUserId(String userId, Integer containerId) {
        InvestigationScoreVO scoreVO = getScoreByUserId(userId, containerId);
        if (scoreVO == null) {
            return new ReportVO();
        }
        ReportVO reportVO = new ReportVO();
        reportVO.setScore(scoreVO.getScore());
        reportVO.setRealName(scoreVO.getRealname());
        reportVO.setStatus(scoreVO.getStatus());

        List<ReportScoreVO> reportScoreVOS = new ArrayList<>(4);
        //分类查询实例
        //查询总监类型
        List<InvestigationEntity> directorList = baseMapper.getByTypeAndAssessorUserIdAndContainerId(RelationshipConstant.DIRECTOR, containerId, userId);
        ReportScoreVO directorReport = createReport(directorList, "总监评价");
        reportScoreVOS.add(0, directorReport);

        //pm类型
        List<InvestigationEntity> pmList = baseMapper.getByTypeAndAssessorUserIdAndContainerId(RelationshipConstant.PM, containerId, userId);
        ReportScoreVO pmReport = createReport(pmList, "PMS评价");
        reportScoreVOS.add(1, pmReport);

        //同行类型
        List<InvestigationEntity> peerList = baseMapper.getByTypeAndAssessorUserIdAndContainerId(RelationshipConstant.PEER, containerId, userId);
        ReportScoreVO peerReport = createReport(peerList, "同行评价");
        reportScoreVOS.add(2, peerReport);

        //个人类型
        List<InvestigationEntity> selfList = baseMapper.getByTypeAndAssessorUserIdAndContainerId(RelationshipConstant.INDIVIDUAL, containerId, userId);
        ReportScoreVO selfReport = createReport(selfList, "自我评估");
        reportScoreVOS.add(3, selfReport);

        reportVO.setList(reportScoreVOS);
        return reportVO;
    }

    /**
     * 根据部门，活动，状态等查询问卷的实例
     *
     * @param page
     * @param departId
     * @param containerId
     * @param status
     * @param evaluatorRealname
     * @param assessorRealname
     * @return
     */
    @Override
    public IPage<InvestigationEntity> getByDepartIdAndContainerIdAndStatus(Page<InvestigationEntity> page, String departId, Integer containerId, String status, String evaluatorRealname, String assessorRealname) {
        //获取当前部门人员的id
        List<String> userIdList = permissionService.getUserIdList(departId);
        QueryWrapper<InvestigationEntity> qw = new QueryWrapper<InvestigationEntity>();
        qw.eq("container_id", containerId);
        if (StringUtils.hasText(status)) {
            qw.eq("status", status);
        }
        if (StringUtils.hasText(assessorRealname)) {
            //根据被评价人查询
            qw.like("assessor_realname", assessorRealname);
        }
        if (StringUtils.hasText(evaluatorRealname)) {
            //根据被评价人查询
            qw.like("evaluator_realname", evaluatorRealname);
        }
        qw.in("assessor_user_id", userIdList);
        //根据评价人排序
        qw.orderByAsc("evaluator_user_id");
        return baseMapper.selectPage(page, qw);
    }

    /**
     * 统计各个状态的评价总数
     *
     * @param departId
     * @param containerId
     * @return
     */
    @Override
    public JSONObject countByDepartIdAndContainerId(String departId, Integer containerId) {
        JSONObject result = new JSONObject();
        //获取当前部门人员的id
        List<String> userIdList = permissionService.getUserIdList(departId);
        //编辑状态数据
        Integer editCount = countEntity(userIdList, containerId, StatusConstant.EDIT);
        //完成状态
        Integer complateCount = countEntity(userIdList, containerId, StatusConstant.COMPLETE);
        //发布状态
        Integer publishedCount = countEntity(userIdList, containerId, StatusConstant.PUBLISHED);
        Integer count = countEntity(userIdList, containerId, null);
        result.put(StatusConstant.EDIT, editCount);
        result.put(StatusConstant.COMPLETE, complateCount);
        result.put(StatusConstant.PUBLISHED, publishedCount);
        result.put("count", count);
        return result;
    }

    /**
     * 根据状态统计数据,不传状态统计所有的
     *
     * @param userIdList
     * @param containerId
     * @param status
     * @return
     */
    private Integer countEntity(List<String> userIdList, Integer containerId, String status) {
        QueryWrapper<InvestigationEntity> qw = new QueryWrapper<InvestigationEntity>();
        qw.eq("container_id", containerId);
        if (StringUtils.hasText(status)) {
            qw.eq("status", status);
        }
        qw.in("assessor_user_id", userIdList);
        return baseMapper.selectCount(qw);
    }

    /**
     * 创建pm评价的报表
     *
     * @param pmList
     */
    private ReportScoreVO createReport(List<InvestigationEntity> pmList, String type) {
        InvestigationEntity entity = pmList.get(0);
        ReportScoreVO reportScoreVO = new ReportScoreVO();
        String templateStr = entity.getTemplate();
        StringBuilder evaluators = new StringBuilder();
        //设置参与评价人员
        for (InvestigationEntity investigationEntity : pmList) {
            String evaluatorRealname = investigationEntity.getEvaluatorRealname();
            if (StringUtils.hasText(evaluators.toString())) {
                evaluators.append(",");
            }
            evaluators.append(evaluatorRealname);
        }
        reportScoreVO.setEvaluators(evaluators.toString());
        Template template = JSON.parseObject(templateStr, Template.class);
        List<TemplateDetail> lists = template.getLists();
        for (int i = 0; i < lists.size(); i++) {
            //第多少项的评分细节
            TemplateDetail templateDetail = lists.get(i);
            String score = getScore(pmList, i);
            templateDetail.setScore(score);
            String detail = getDetail(pmList, i);
            templateDetail.setDetail(detail);
            String reason = getReason(pmList, i);
            templateDetail.setReason(reason);
        }
        reportScoreVO.setId(template.getId());
        reportScoreVO.setType(type);
        reportScoreVO.setDetails(lists);
        return reportScoreVO;
    }

    /**
     * 拼接理由
     *
     * @param pmList
     * @param i
     * @return
     */
    private String getReason(List<InvestigationEntity> pmList, int i) {
        StringBuilder reason = new StringBuilder();
        for (InvestigationEntity entity : pmList) {
            String templateStr = entity.getTemplate();
            Template template = JSON.parseObject(templateStr, Template.class);
            List<TemplateDetail> lists = template.getLists();
            TemplateDetail templateDetail = lists.get(i);
            String reason1 = templateDetail.getReason();
            if (StringUtils.isEmpty(reason1)) {
                reason1 = "";
            }
            String evaluatorRealname = entity.getEvaluatorRealname();
            reason.append(evaluatorRealname).append("评分理由：").append(reason1).append("。").append("\n");
        }
        return reason.toString();
    }

    /**
     * 获取评分细节
     *
     * @return
     */
    private String getDetail(List<InvestigationEntity> pmList, int i) {
        StringBuilder detail = new StringBuilder();
        for (InvestigationEntity entity : pmList) {
            String templateStr = entity.getTemplate();
            Template template = JSON.parseObject(templateStr, Template.class);
            List<TemplateDetail> lists = template.getLists();
            TemplateDetail templateDetail = lists.get(i);
            String score = templateDetail.getScore();
            if (StringUtils.isEmpty(score)) {
                score = "0";
            }
            String evaluatorRealname = entity.getEvaluatorRealname();
            detail.append(evaluatorRealname).append("评分：").append(score).append("\n");
        }
        return detail.toString();
    }

    /**
     * 获取第多少项的总分
     *
     * @param pmList
     * @param i
     * @return
     */
    private String getScore(List<InvestigationEntity> pmList, int i) {
        double scoreValue = 0.0d;
        for (InvestigationEntity entity : pmList) {
            String templateStr = entity.getTemplate();
            Template template = JSON.parseObject(templateStr, Template.class);
            List<TemplateDetail> lists = template.getLists();
            TemplateDetail templateDetail = lists.get(i);
            String score = templateDetail.getScore();
            if (StringUtils.isEmpty(score)) {
                score = "0";
            }
            double v = Double.parseDouble(score);
            Double qWeight = entity.getQWeight();
            scoreValue += (v * qWeight / 100);
        }
        DecimalFormat df = new DecimalFormat("######0.00");
        //保留2位小数
        String format = df.format(scoreValue);
        return format;
    }

    /**
     * 创建邮件接口的参数
     *
     * @param user
     */
    private MsgParams createParam(LoginUser user) {
        MsgParams msgParams = new MsgParams();
        //设置为邮件类型
        msgParams.setMsgType("2");
        //设置消息模板码
        msgParams.setTemplateCode(templateCode);
        //设置消息接受人
        msgParams.setReceiver(user.getEmail());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", user.getUsername());
        //拼接问卷调查的详情url
        String url = urlEmail;
        //拼接初始密码
        String workNo = user.getWorkNo();
        int i = Integer.parseInt(workNo);
        i = i * 5;
        //初始密码
        String password = "changeMe" + i;
        jsonObject.put("url", url);
        jsonObject.put("password", password);
        msgParams.setTestData(jsonObject.toJSONString());
        return msgParams;
    }

    /**
     * 获取本机的uri地址
     *
     * @return
     */
    private String getUrl() throws UnknownHostException {
        ApplicationContext application = SpringContextUtils.getApplicationContext();
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        String url = "http://" + ip + ":" + port + path + "/message/sysMessageTemplate/sendMsg";
        return url;
    }

    /**
     * 创建调查问卷实例
     *
     * @param investigationParticipant 参与者
     * @return
     */
    private List<InvestigationEntity> createEntity(InvestigationParticipant investigationParticipant) {

        Integer containerId = investigationParticipant.getContainerId();
        String userId = investigationParticipant.getUserId();
        //获取所有的对应的关系对象
        List<InvestigationRelationship> relationshipList = getInvestigationRelationship(userId);
        List<InvestigationEntity> entityList = new ArrayList<>(relationshipList.size());
        //创建实例对象
        for (InvestigationRelationship relationship : relationshipList) {
            InvestigationTemplate template = getTemplate(relationship.getRelationship());
            InvestigationEntity entity = new InvestigationEntity();
            entity.setAssessorUserId(relationship.getAssessorUserId());
            entity.setAssessorRealname(relationship.getAssessorRealname());
            entity.setContainerId(containerId);
            entity.setEvaluatorRealname(relationship.getEvaluatorRealname());
            entity.setEvaluatorUserId(relationship.getEvaluatorUserId());
            entity.setQWeight(relationship.getQWeight());

            entity.setWeight(template.getWeight());
            entity.setName(template.getName() + "-" + relationship.getEvaluatorRealname());
            entity.setDescription(template.getDescription());
            entity.setVersion(1);
            entity.setTempId(template.getId());
            entity.setTemplate(template.getTemplate());
            //修改每项评分的加权重
            setWeighting(entity);
            //设置状态为 发布
            entity.setStatus(StatusConstant.PUBLISHED);
            //创建实例
            baseMapper.insert(entity);
            entityList.add(entity);
        }
        return entityList;
    }

    /**
     * 修改每项评分的加权重
     *
     * @param entity
     */
    private void setWeighting(InvestigationEntity entity) {
        String templateStr = entity.getTemplate();
        Template template = JSON.parseObject(templateStr, Template.class);
        String weight = template.getWeight();
        Double weight1 = Double.parseDouble(weight);
        List<TemplateDetail> lists = template.getLists();
        for (TemplateDetail templateDetail : lists) {
            String weight2Str = templateDetail.getWeight();
            Double weight2 = Double.parseDouble(weight2Str);
            DecimalFormat df = new DecimalFormat("0.00");
            //加权重
            double value = Double.parseDouble(df.format((double) weight1 * weight2 / (100)));
            templateDetail.setWeighting(Double.toString(value));
        }
        entity.setTemplate(JSON.toJSONString(template));
    }


    /**
     * 根据关系类型查对应的模板
     *
     * @param relationship
     * @return
     */
    private InvestigationTemplate getTemplate(String relationship) {
        List<InvestigationTemplate> list = templateMapper.selectList(new QueryWrapper<InvestigationTemplate>().eq("relationship", relationship));
        if (list.size() == 0) {
            throw new RuntimeException("模板不存在");
        }
        return list.get(0);
    }

    /**
     * 根据被评价人用户id查询关系
     *
     * @param assessorUserId
     * @return
     */
    private List<InvestigationRelationship> getInvestigationRelationship(String assessorUserId) {
        QueryWrapper<InvestigationRelationship> iqw = new QueryWrapper<InvestigationRelationship>();
        iqw.eq("assessor_user_id", assessorUserId);
        return relationshipMapper.selectList(iqw);
    }

    /**
     * 获取所有未发布状态的参与者
     *
     * @param id
     * @return
     */
    private List<InvestigationParticipant> getInvestigationParticipant(Integer id) {
        QueryWrapper<InvestigationParticipant> pqw = new QueryWrapper<InvestigationParticipant>();
        pqw.eq("status", UNPUBLISHED)
                .eq("container_id", id);
        return participantMapper.selectList(pqw);
    }

    /**
     * 验证参与者的关系维护状态,如果关系维护不正常则抛出异常
     *
     * @param investigationParticipantList
     */
    private String checkParticipant(List<InvestigationParticipant> investigationParticipantList) {
        StringBuilder sb = new StringBuilder("");
        for (InvestigationParticipant investigationParticipant : investigationParticipantList) {
            String userId = investigationParticipant.getUserId();
            List<InvestigationRelationship> investigationRelationshipList = relationshipMapper.selectList(
                    new QueryWrapper<InvestigationRelationship>().eq("assessor_user_id", userId));
            if (investigationRelationshipList.size() == 0) {
                LoginUser user = sysBaseAPI.getUserById(userId);
                sb.append(user.getRealname()).append(",");
                continue;
            }
            //存储关系类型的容器
            Map<String, List<InvestigationRelationship>> relationshipMap = new HashMap<>();
            //关系类型处理
            for (InvestigationRelationship relationship : investigationRelationshipList) {
                String relationshipType = relationship.getRelationship();
                if (RelationshipConstant.DIRECTOR.equals(relationshipType)) {
                    //总监类型:
                    if (relationshipMap.get(RelationshipConstant.DIRECTOR) == null) {
                        List<InvestigationRelationship> list = new ArrayList<>();
                        list.add(relationship);
                        relationshipMap.put(RelationshipConstant.DIRECTOR, list);
                    } else {
                        List<InvestigationRelationship> list = relationshipMap.get(RelationshipConstant.DIRECTOR);
                        list.add(relationship);
                        relationshipMap.put(RelationshipConstant.DIRECTOR, list);
                    }
                }

                if (RelationshipConstant.PM.equals(relationshipType)) {
                    //项目经理类型:
                    if (relationshipMap.get(RelationshipConstant.PM) == null) {
                        List<InvestigationRelationship> list = new ArrayList<>();
                        list.add(relationship);
                        relationshipMap.put(RelationshipConstant.PM, list);
                    } else {
                        List<InvestigationRelationship> list = relationshipMap.get(RelationshipConstant.PM);
                        list.add(relationship);
                        relationshipMap.put(RelationshipConstant.PM, list);
                    }
                }

                if (RelationshipConstant.INDIVIDUAL.equals(relationshipType)) {
                    //个人类型:
                    if (relationshipMap.get(RelationshipConstant.INDIVIDUAL) == null) {
                        List<InvestigationRelationship> list = new ArrayList<>();
                        list.add(relationship);
                        relationshipMap.put(RelationshipConstant.INDIVIDUAL, list);
                    } else {
                        List<InvestigationRelationship> list = relationshipMap.get(RelationshipConstant.INDIVIDUAL);
                        list.add(relationship);
                        relationshipMap.put(RelationshipConstant.INDIVIDUAL, list);
                    }
                }

                if (RelationshipConstant.PEER.equals(relationshipType)) {
                    //同行类型:
                    if (relationshipMap.get(RelationshipConstant.PEER) == null) {
                        List<InvestigationRelationship> list = new ArrayList<>();
                        list.add(relationship);
                        relationshipMap.put(RelationshipConstant.PEER, list);
                    } else {
                        List<InvestigationRelationship> list = relationshipMap.get(RelationshipConstant.PEER);
                        list.add(relationship);
                        relationshipMap.put(RelationshipConstant.PEER, list);
                    }
                }
            }

            List<InvestigationRelationship> list = relationshipMap.get(RelationshipConstant.DIRECTOR);
            if (list == null || list.size() != 1) {
                sb.append(investigationRelationshipList.get(0).getAssessorRealname()).append(",");
                continue;
            }
            list = relationshipMap.get(RelationshipConstant.PM);
            if (list == null || list.size() == 0) {
                sb.append(investigationRelationshipList.get(0).getAssessorRealname()).append(",");
                continue;
            }
            //pm的权重总数
            double qWeight = 0;
            for (InvestigationRelationship relationship : list) {
                qWeight = add(qWeight, relationship.getQWeight());
            }
            if (qWeight != 100) {
                sb.append(investigationRelationshipList.get(0).getAssessorRealname()).append(",");
                continue;
            }

            list = relationshipMap.get(RelationshipConstant.INDIVIDUAL);
            if (list == null || list.size() != 1) {
                sb.append(investigationRelationshipList.get(0).getAssessorRealname()).append(",");
                continue;
            }

            list = relationshipMap.get(RelationshipConstant.PEER);
            if (list == null || list.size() == 0) {
                sb.append(investigationRelationshipList.get(0).getAssessorRealname()).append(",");
                continue;
            }
        }
        String msg = sb.toString();
        if (StringUtils.hasText(msg)) {
            //如果有信息
            return (msg + "的关系维护有问题，请处理");
        } else {
            return "";
        }
    }

    /**
     * 精确加法
     */
    public static double add(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.add(b2).doubleValue();
    }
}
