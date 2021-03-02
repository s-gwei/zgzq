package org.jeecg.modules.investigation.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author xduan
 * @version 2020/3/18
 * HttpClientUtil 带有token验证
 */
@Slf4j
@Component
public class TokenHttpClientUtil {

    private static final String DEFAULT_CHARSET = "UTF-8";

   /* @Value("${jeecg.username}")
    private String username = "";
    @Value("${jeecg.syspassword}")
    private String syspassword = "";*/

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SpringContextUtils contextUtils;

    /**
     * @param url
     * @param params
     * @return 返回数据
     */
    public String doPostJSON(String url, String params) {

        String result = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        try {
            HttpServletRequest httpServletRequest = contextUtils.getHttpServletRequest();
            String token = httpServletRequest.getHeader("X-Access-Token");
          /*  //通过token绕过本身的权限控制，可以直接通过request对象获取前台传过来的token绕过权限
            String token = JwtUtil.sign(username, syspassword);
            // 设置token缓存有效时间
            redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
            redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);
*/
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
            httpPost.addHeader("X-Access-Token", token);
            if (StringUtils.isNotBlank(params)) {
                httpPost.setEntity(new StringEntity(params, Charset.forName(DEFAULT_CHARSET)));
            }
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());
                log.info("返回数据：" + result);
            } else {
                log.info("请求失败");
            }
        } catch (IOException e) {
            log.error("请求异常:" + e.toString());
        } finally {
            // 关闭连接，释放资源
            try {
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
                httpClient = null;
            }
        }
        return result;
    }

}
