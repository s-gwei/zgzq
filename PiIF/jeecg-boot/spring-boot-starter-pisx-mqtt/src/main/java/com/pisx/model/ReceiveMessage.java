package com.pisx.model;

import lombok.Data;

/**
 * @Author:boliu
 * @Date:Created in 10:51 AM 2020/3/11
 * @Description:接收到的消息
 */
@Data
public class ReceiveMessage {

    /**
     * 第一列的列名
     */
    private String id;

    /**
     * 第一列对应的value
     */
    private Object v;

    /**
     * q boolean
     */
    private Boolean q;

    /**
     * 时间戳
     */
    private Long t;
}
