package org.jeecg.modules.valuestream.entity;

import lombok.Data;

/**
 * @Author:wzp
 * @Date:Created in 10:12 AM 2020/3/30
 * @Description:接收价值流修改/新增列相关内容
 */
@Data
public class ValueStreamColumn {

    /**
     * 操作人
     */
    private String user;

    /**
     * ValueStream名称
     */
    private String tableName;

    /**
     * 列
     */
    private Object column;

    /**
     * 更新列
     */
    private String updateColumn;
}
