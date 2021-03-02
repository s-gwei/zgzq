package org.jeecg.modules.attendace.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @Author:wzp
 * @Date:Created in 11:12 AM 2020/5/8
 * @Description:
 */
@Component
@Aspect
@Slf4j
public class DeleteUsersAspect {


    //springboot自带的http/s的请求模板
    private RestTemplate restTemplate = new RestTemplate();

    //需要请求的url
    private final static String URL = "http://localhost:8983/api/data/get";


    /**
     * 删除用户切点
     */
    @Pointcut("execution(public * org.jeecg.modules.system.controller.SysUserController.delete(..))")
    public void delete() {//切点映射，命名不规定
    }

    //在本类的login执行之前 {id:”1006”,do:”delete”,data:[”user”,”fingerprint”,”face”,”headpic”,”clockin”,”pic”],ccid:[13245,8784,54878]}
    @Before("delete()")
    public void beforeDelete(JoinPoint point) {
        //拿到删除人的id
        String id = "";
        for (Object object : point.getArgs()) {
            if (object instanceof String) {
                id = (String) object;
                break;
            }
        }
        if (!StringUtils.isEmpty(id)) {
            String url = URL + "?ccid=" + id + "&id=1006";
            //用springRestTemplate发送get请求
            ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
            log.info(res.getBody() + ",状态码:" + res.getStatusCodeValue());
        }
    }


    /**
     * 批量删除用户切点
     */
    @Pointcut("execution(public * org.jeecg.modules.system.controller.SysUserController.deleteBatch(..))")
    public void deleteBatch() {//切点映射，命名不规定
    }

    //在本类的login执行之前 {id:”1006”,do:”delete”,data:[”user”,”fingerprint”,”face”,”headpic”,”clockin”,”pic”],ccid:[13245,8784,54878]}
    @Before("deleteBatch()")
    public void beforeDeleteBatch(JoinPoint point) {
        //拿到删除人的id
        String ids = "";
        for (Object object : point.getArgs()) {
            if (object instanceof String) {
                ids = (String) object;
                break;
            }
        }
        if (!StringUtils.isEmpty(ids)) {
            String url = URL + "?ccid=" + ids + "&id=1006";
            //用springRestTemplate发送get请求
            ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
            log.info(res.getBody() + ",状态码:" + res.getStatusCodeValue());
        }
    }
}
