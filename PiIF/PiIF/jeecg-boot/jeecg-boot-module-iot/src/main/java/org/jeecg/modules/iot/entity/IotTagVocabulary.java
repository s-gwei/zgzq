package org.jeecg.modules.iot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/9
 * @Desc 标签词汇实体
 */
@Data
@TableName("iot_tag_vocabulary")
public class IotTagVocabulary implements Serializable {
    private static final long serialVersionUID = 1480911401411634038L;
    @TableId(type = IdType.UUID)
    private String id;
    //词汇名
    private String vocabulary;
    //标签id
    private String tagId;
}
