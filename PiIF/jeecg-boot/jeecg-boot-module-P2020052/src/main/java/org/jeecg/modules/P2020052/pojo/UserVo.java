package org.jeecg.modules.P2020052.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {

    private String groupId;

    private String groupName;

    private String userId;

    private String userName;
}
