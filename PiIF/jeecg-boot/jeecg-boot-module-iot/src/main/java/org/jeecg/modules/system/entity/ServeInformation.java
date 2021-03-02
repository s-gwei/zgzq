package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zhang ho jian
 * @date 2020/9/25
 * @time 15:39
 * @Description : 服务信息实体类
 */
@Data
@TableName("iot_serve_information")
public class ServeInformation {

     //主键
    private String id ;
    //名称
    private String name ;
    //url地址
    private String url;
    //提示
    private  String hint;
    //标识
    private String type;
}
