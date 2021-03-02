package org.jeecg.modules.investigation.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xduan
 * @version 2020/4/27
 */
@Data
public class Template implements Serializable {

    private static final long serialVersionUID = 2556155254144663504L;
    private String id;

    private String desc;

    private String name;

    private String weight;

    private List<TemplateDetail> lists;
}
