package org.jeecg.modules.datashape.entity;

import lombok.Data;

/**
 * @Author:wzp
 * @Date:Created in 5:03 PM 2020/3/19
 * @Description:自定义列属性
 */
@Data
public class IotTableColumn implements Comparable<IotTableColumn> {

    /**
     * 列名
     */
    private String columnName;

    /**
     * 列值类型
     */
    private String columnType;

    /**
     * 列相关属性
     */
    private String aspects;

    /**
     * 列描述
     */
    private String description;

    /**
     * 列排序
     */
    private Integer columnOrder;


    @Override
    public int compareTo(IotTableColumn o) {
        return this.columnOrder.compareTo(o.getColumnOrder());
    }
}
