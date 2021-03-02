package org.jeecg.modules.stream.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author:wzp
 * @Date:Created in 3:50 PM 2020/4/15
 * @Description:混搭返回数据
 */
@Data
public class HomeMashUpVO {

    private List<Map<String, String>> columns;

    private IPage<Map<String, Object>> data;


}
