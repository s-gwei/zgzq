package org.jeecg.modules.P2020052.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.P2020052.pojo.PlanINVo;
import org.springframework.stereotype.Repository;


@Repository
public interface HR_INMapper {
    //List<PlanINVo> HR_INTable(@Param("activeId") String activeId);
    IPage<PlanINVo> selectINTable(Page page, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("group") String[] group, @Param("planId") String planId);

}
