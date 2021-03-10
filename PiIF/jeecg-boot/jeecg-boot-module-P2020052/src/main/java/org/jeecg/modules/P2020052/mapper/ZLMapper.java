package org.jeecg.modules.P2020052.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.P2020052.pojo.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ZLMapper {
    List<PiplanActivityVo> pertTable(@Param("activeId") String activeId);
}
