package org.jeecg.modules.system.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/22
 * 使用关系，父结构模型
 */
@Data
public class NodeTreeModel implements Serializable {

    private static final long serialVersionUID = -7111592322890430127L;

    private String name;

    private List<NodeTreeModel> nodes;



}
