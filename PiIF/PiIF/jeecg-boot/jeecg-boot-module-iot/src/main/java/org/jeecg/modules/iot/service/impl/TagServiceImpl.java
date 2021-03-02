package org.jeecg.modules.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.iot.entity.IotTag;
import org.jeecg.modules.iot.entity.IotTagVocabulary;
import org.jeecg.modules.iot.mapper.TagMapper;
import org.jeecg.modules.iot.mapper.TagVocabularyMapper;
import org.jeecg.modules.iot.service.ITagService;
import org.jeecg.modules.system.mapper.DepartobjectPermissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author xduan
 * @version 2020/4/10
 * @Desc 标签相关服务类
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, IotTag> implements ITagService {
    private static final String TABLENAME = "iot_tag";
    @Resource
    private TagMapper tagMapper;

    @Resource
    private DepartobjectPermissionMapper permissionMapper;

    @Resource
    private TagVocabularyMapper tagVocabularyMapper;

    /**
     * 新增标签
     *
     * @param tag
     * @return
     */
    @Override
    public IotTag add(IotTag tag) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        tag.setCreateTime(timestamp);
        tag.setUpdateTime(timestamp);
        //设置可见性权限的关联主键值
        tag.setObjectPk(UUIDGenerator.generate());
        int insert = tagMapper.insert(tag);
        return tag;
    }

    /**
     * 根据id删除标签
     *
     * @param id
     */
    @Override
    @Transactional
    public void delete(String id) {
        IotTag iotTag = tagMapper.selectById(id);
        if (iotTag == null) {
            return;
        }
        //删除相关的可见性权限
        permissionMapper.delete(null, iotTag.getObjectPk(), TABLENAME);
        //删除词汇
        tagVocabularyMapper.delete(new QueryWrapper<IotTagVocabulary>().eq("tag_id", id));
        tagMapper.deleteById(id);
    }
}
