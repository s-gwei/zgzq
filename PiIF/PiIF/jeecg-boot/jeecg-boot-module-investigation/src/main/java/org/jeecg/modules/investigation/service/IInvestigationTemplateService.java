package org.jeecg.modules.investigation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.investigation.entity.InvestigationTemplate;
import org.jeecg.modules.investigation.model.Template;

/**
 * @author xduan
 * @version 2020/4/24
 * @desc 调查问卷模板的服务接口
 */
public interface IInvestigationTemplateService extends IService<InvestigationTemplate> {
    /**
     * 修改问卷内容
     * @param template
     * @param id
     */
    void editTemplate(Template template, Integer id);

}
