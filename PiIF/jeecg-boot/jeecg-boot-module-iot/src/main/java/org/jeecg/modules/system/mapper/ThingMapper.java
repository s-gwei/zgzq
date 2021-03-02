package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.system.entity.Thing;

import java.util.List;

/**
 * @author xduan
 * @version 2020/3/5
 */
@Mapper
public interface ThingMapper extends BaseMapper<Thing> {
    @Select("select * from iot_thing_model where thingTemplate = #{thingTemplate}")
    List<Thing> findThingByThingTemplate(@Param("thingTemplate") String thingTemplate);

    /**
     * 根据属性名称，查询设备
     * @param propertyName
     * @return
     */
    @Select("select * from iot_thing_model where JSON_CONTAINS_PATH(thingShape, 'all',  '$.propertyDefinitions.${propertyName}')")
    List<Thing> findThingByPropertyName(@Param("propertyName") String propertyName);
}
