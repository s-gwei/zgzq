package org.jeecg.modules.investigation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.investigation.entity.InvestigationEntity;

import java.util.List;

/**
 * @author xduan
 * @version 2020/4/24
 * @Desc 调查问卷的实体 数据操作接口
 */
@Mapper
public interface EntityMapper extends BaseMapper<InvestigationEntity> {
    /**
     * 根据被评价人id,活动id,评价类型查询问卷实例
     *
     * @param relationship
     * @param containerId
     * @param assessorUserId
     * @return
     */
    @Select("select * from investigation_entity where assessor_user_id=#{assessorUserId} and container_id =#{containerId} and temp_id =(select id from investigation_template where relationship =#{relationship})")
    List<InvestigationEntity> getByTypeAndAssessorUserIdAndContainerId(@Param("relationship") String relationship
            , @Param("containerId") Integer containerId, @Param("assessorUserId") String assessorUserId);
}
