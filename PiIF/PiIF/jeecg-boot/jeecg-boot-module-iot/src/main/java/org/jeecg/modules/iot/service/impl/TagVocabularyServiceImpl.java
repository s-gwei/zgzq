package org.jeecg.modules.iot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.iot.entity.IotTagVocabulary;
import org.jeecg.modules.iot.mapper.TagVocabularyMapper;
import org.jeecg.modules.iot.service.ITagVocabularyService;
import org.jeecg.modules.iot.vo.VocabularyVO;
import org.jeecg.modules.system.mapper.DepartobjectPermissionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/10
 * @Desc 标签相关服务类
 */
@Service
public class TagVocabularyServiceImpl extends ServiceImpl<TagVocabularyMapper, IotTagVocabulary> implements ITagVocabularyService {

    @Resource
    private TagVocabularyMapper tagVocabularyMapper;

    @Resource
    private DepartobjectPermissionMapper visiblePermissionMapper;

    /**
     * 根据用户id查询可见的标签词汇
     *
     * @param departList
     * @param admin
     * @return
     */
    @Override
    public List<VocabularyVO> getVocabularyByUserId(List<String> departList, boolean admin) {
        List<VocabularyVO> result = new LinkedList<>();
        List<HashMap> vocabularys = null;
        if (admin) {
            //返回所有标签
            vocabularys = tagVocabularyMapper.getByDepartIds(null);
        } else {
            vocabularys = tagVocabularyMapper.getByDepartIds(departList);
        }
        for (HashMap hashMap : vocabularys) {
            //词汇id
            String id = (String) hashMap.get("id");
            if (id == null) {
                continue;
            }
            //标签名称
            String name = (String) hashMap.get("name");
            //词汇名
            String vocabulary = (String) hashMap.get("vocabulary");
            VocabularyVO vocabularyVO = new VocabularyVO();
            vocabularyVO.setId(id);
            //展示字符串
            vocabularyVO.setVocabularyStr(name + ":" + vocabulary);
            result.add(vocabularyVO);
        }
        return result;
    }
}
