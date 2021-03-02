package org.jeecg.modules.investigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.investigation.entity.InvestigationTemplate;

/**
 * @author xduan
 * @version 2020/4/24
 * @Desc 问卷调查模板的数据操作接口
 */
@Mapper
public interface TemplateMapper extends BaseMapper<InvestigationTemplate> {
}
