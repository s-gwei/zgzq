package org.jeecg.modules.investigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.investigation.entity.InvestigationRelationship;

/**
 * @author xduan
 * @version 2020/4/24
 * @Desc 关系评价维护表的数据操作接口
 */
@Mapper
public interface RelationshipMapper extends BaseMapper<InvestigationRelationship> {
}
