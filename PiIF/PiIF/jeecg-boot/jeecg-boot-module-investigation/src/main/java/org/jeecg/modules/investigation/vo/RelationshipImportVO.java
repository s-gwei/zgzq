package org.jeecg.modules.investigation.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/4/26
 * @desc 关系模型，用于关系导入
 */
@Data
public class RelationshipImportVO implements Serializable {

    @Excel(name = "评价关系")
    private String relationship;

    @Excel(name = "评价人")
    private String evaluatorRealname;


    @Excel(name = "被评价者")
    private String assessorRealname;

    @Excel(name = "评价权重")
    private String qWeight;

    private static final long serialVersionUID = 132912806263712203L;
}
