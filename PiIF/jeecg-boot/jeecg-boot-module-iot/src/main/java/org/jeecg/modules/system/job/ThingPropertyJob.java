package org.jeecg.modules.system.job;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.system.entity.Thing;
import org.jeecg.modules.system.service.IThingService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 定时同步redis中设备属性值到mysql
 * @author: boliu
 * @date: 2020年09月09日 11:29
 */
@Slf4j
public class ThingPropertyJob implements Job {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IThingService thingService;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        List<Thing> thingList = thingService.list();
        int thingListSize = thingList.size();
        for (int i = 0; i < thingListSize; i++) {
            Thing thing = thingList.get(i);
            String thingName = thing.getName();
            //获取redis中的键值
            Map<Object, Object> thingPropertyValueCacheMap = redisUtil.hmget(thingName);
            Map<String, String> thingPropertyValueMap = new HashMap<>();
            thingPropertyValueCacheMap.forEach((k, v) -> {
                thingPropertyValueMap.put(k.toString(), v.toString());
            });
            //将redis中的键值更新到mysql中
            thingService.updatePropertyValue(thingName, "admin", thingPropertyValueMap);
        }
    }
}
