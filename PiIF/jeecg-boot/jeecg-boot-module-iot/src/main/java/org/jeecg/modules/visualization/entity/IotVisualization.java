package org.jeecg.modules.visualization.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xduan
 * @version 2020/5/27
 * @Desc 物可视模型
 */
@Data
@TableName("iot_visualization")
public class IotVisualization implements Serializable {
    private static final long serialVersionUID = -7809760451014141274L;

    /**
     * id主键
     */
    @TableId(type = IdType.UUID)
    private String id;

    /**
     * 背景图片地址
     */
    private String backgroundImage;

    /**
     * 背景颜色
     */
    private String background;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 标签数组 json格式
     */
    private String tags;

    /**
     * 高度
     */
    private int height;

    /**
     * 宽度
     */
    private int width;

    /**
     * 缩略图地址
     */
    private String thumb;

    /**
     * 继承的模板id
     */
    private String template;

    /**
     * 名称
     */
    private String name;

    /**
     * 栅格大小
     */
    private int grid;

    /**
     * 物实例的名称的数组 json
     */
    private String thingNames;

    /**
     * 权限关联字段 uuid 生成
     */
    private String objectPk;
    /**
     * 缩放模式
     */
    private String zoomMode;
    /**
     * 状态 预留
     */
    private String status;

    /**
     * 组件数据 （临时） json
     */
    private String chartArr;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;
}
