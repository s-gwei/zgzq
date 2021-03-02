package org.jeecg.modules.datashape.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @Author:wzp
 * @Date:Created in 1:03 PM 2020/3/19
 * @Description:datashape
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("iot_datashape_model")
public class IotDatashapeModel implements Serializable {
    @TableId(type = IdType.NONE)
    private int id;

    private String name;

    private String avatar;

    private String basedatashape;

    private String classname;

    /**
     * 修改信息
     */
    private String configurationchanges;

    private String configurationtables;

    private String description;

    private String designtimepermissions;

    private String documentationcontent;

    /**
     * 列信息
     */
    private String fielddefinitions;

    private String homemashup;

    private Date lastmodifieddate;

    private String owner;

    private String projectname;

    private String runtimepermissions;

    private String tags;

    private String tenantid;

    private Integer type;

    private String visibilitypermissions;

    private String objectPk;

    @TableField(exist = false)
    private String object_pk;

    @TableField(exist = false)
    private Map tableMsg;
    @TableField(exist = false)
    private Collection columnMsg;

    private static final long serialVersionUID = 1L;
}