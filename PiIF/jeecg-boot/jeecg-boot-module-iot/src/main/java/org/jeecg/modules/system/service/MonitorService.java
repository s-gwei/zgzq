package org.jeecg.modules.system.service;

import java.util.Map;

/**
 * @author zhang ho jian
 * @date 2020/10/13
 * @time 17:15
 * @Description :监听设备属性
 */
public interface MonitorService {

   void  monitor(Map<String,Object> map ,String headingCode);
}
