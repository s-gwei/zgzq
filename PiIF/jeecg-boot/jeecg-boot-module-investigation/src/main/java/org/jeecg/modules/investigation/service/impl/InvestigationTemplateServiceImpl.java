package org.jeecg.modules.investigation.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.investigation.entity.InvestigationTemplate;
import org.jeecg.modules.investigation.mapper.TemplateMapper;
import org.jeecg.modules.investigation.model.Template;
import org.jeecg.modules.investigation.service.IInvestigationTemplateService;
import org.springframework.stereotype.Service;

/**
 * @author xduan
 * @version 2020/4/26
 * @desc 活动 服务类
 */
@Service
public class InvestigationTemplateServiceImpl extends ServiceImpl<TemplateMapper, InvestigationTemplate> implements IInvestigationTemplateService {
    /**
     * 修改问卷内容
     *
     * @param template
     * @param id
     */
    @Override
    public void editTemplate(Template template, Integer id) {
        //模板对象
        InvestigationTemplate entity = baseMapper.selectById(id);
        entity.setTemplate(JSON.toJSONString(template));
        baseMapper.updateById(entity);
    }
}
