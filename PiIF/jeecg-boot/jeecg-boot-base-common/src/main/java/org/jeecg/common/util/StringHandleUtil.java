package org.jeecg.common.util;

import java.util.Optional;

/**
 * @version:2020/3/13
 * @Author:wzp
 * @Date:Created in 11:54 2020/3/13
 * @Description:字符串工具类
 */
public class StringHandleUtil {

    /**
     * 验参方法
     *
     * @param object 参数
     * @param prompt 提示语
     */
    public static void validateParam(String prompt, Object... object) throws Exception {
        for (Object o : object) {
            Optional.ofNullable(o).orElseThrow(() -> new Exception(prompt));
        }
    }
}
