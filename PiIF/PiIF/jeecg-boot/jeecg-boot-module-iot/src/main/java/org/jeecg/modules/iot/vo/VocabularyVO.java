package org.jeecg.modules.iot.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/16
 */
@Data
public class VocabularyVO implements Serializable {

    private static final long serialVersionUID = -3813890512893904361L;
    //词汇id
    private String id;

    //词汇展示字符串
    private String vocabularyStr;
}
