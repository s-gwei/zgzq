package org.jeecg.modules.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.iot.entity.SysDepart;
import org.jeecg.modules.iot.entity.ThingtemplateDepart;

import java.util.List;

/**
 * @author 张泽革
 * @create 2020/3/5
 */
public interface ThingTemplateDepartMapper extends BaseMapper<ThingtemplateDepart> {
    /**
     * 根据物模板id查询关联的部门
     *
     * @param page
     * @param thingtemplateId
     * @return
     */
    IPage<SysDepart> getDepartsBythingtemplateId(Page<SysDepart> page, @Param("thingtemplateId") String thingtemplateId);

    @Select("select thingtemplate_id from iot_thingtemplate_depart where depart_id=#{departId}")
    List<String> getThingtemplateIdByDepartId(@Param("departId") String departId);
}
