package org.jeecg.modules.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.iot.entity.IotTagVocabulary;
import org.jeecg.modules.iot.vo.VocabularyVO;

import java.util.List;

/**
 * @author xduan
 * @version 2020/4/10
 */
public interface ITagVocabularyService  extends IService<IotTagVocabulary> {
    /**
     * 根据用户id查询可见的标签词汇
     * @param departList
     * @param admin
     * @return
     */
    List<VocabularyVO> getVocabularyByUserId(List<String> departList, boolean admin);
}
