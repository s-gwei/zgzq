package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.system.entity.ThingTemplate;

import java.util.List;

/**
 * @author xduan
 * @version 2020/3/4
 * 物模型模板查询接口
 */
@Mapper
public interface ThingTemplateMapper extends BaseMapper<ThingTemplate> {

    /**
     * 根据类型查询模板名
     * @param type
     * @return
     */
    @Select("select name from iot_thingtemplate_model where type = #{type}")
    List<String> findByType(@Param("type") Integer type);
}
