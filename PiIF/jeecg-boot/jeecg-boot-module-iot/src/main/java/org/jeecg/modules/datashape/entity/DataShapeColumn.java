package org.jeecg.modules.datashape.entity;

import lombok.Data;

/**
 * @Author:wzp
 * @Date:Created in 10:12 AM 2020/3/30
 * @Description:datashape列信息
 */
@Data
public class DataShapeColumn {

    /**
     * 操作人
     */
    private String user;

    /**
     * datashape名称
     */
    private String tableName;

    /**
     * 列名称
     */
    private Object column;

    /**
     * 更新列名称
     */
    private String updateColumn;
}
