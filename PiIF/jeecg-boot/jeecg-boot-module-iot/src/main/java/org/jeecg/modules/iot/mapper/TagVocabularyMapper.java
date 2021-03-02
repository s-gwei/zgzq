package org.jeecg.modules.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.iot.entity.IotTagVocabulary;

import java.util.HashMap;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/9
 * @Desc 标签词汇数据操作接口
 */
@Mapper
public interface TagVocabularyMapper extends BaseMapper<IotTagVocabulary> {

    /**
     * 查询所有的标签和词汇（权限控制）
     *
     * @param departList 如果传null，则查该表所有的数据
     * @return
     */
    List<HashMap> getByDepartIds(@Param("departList") List<String> departList);
}
