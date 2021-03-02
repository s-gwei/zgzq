package org.jeecg.modules.emqx.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xduan
 * @version 2020/3/18
 * HttpClientUtil 带有basic验证
 */
@Slf4j
@Component
public class HttpClientUtil {

    private static final String DEFAULT_CHARSET = "UTF-8";

    /*
     *  获取带有权限的HttpClient连接
     *
     */
    private static CloseableHttpClient getHttpClient() {
        ApplicationContext applicationContext = SpringContextUtils.getApplicationContext();
        Environment environment = applicationContext.getEnvironment();
        String username = environment.getProperty("emqx.username");
        String password = environment.getProperty("emqx.password");
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        provider.setCredentials(AuthScope.ANY, credentials);
        return HttpClients.custom().setDefaultCredentialsProvider(provider).build();
    }

    /**
     * @param url
     * @return
     */
    public static String doGet(String url) {
        return doGet(url, null);
    }

    /**
     * @param url    请求路径
     * @param params 参数
     * @return 请求结果
     */
    public static String doGet(String url, Map<String, String> params) {

        // 返回结果
        String result = "";
        // 创建HttpClient对象
        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet = null;
        try {
            // 拼接参数,可以用URIBuilder,也可以直接拼接在？传值，拼在url后面，如下--httpGet = new
            // HttpGet(uri+"?id=123");
            URIBuilder uriBuilder = new URIBuilder(url);
            if (null != params && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    uriBuilder.addParameter(entry.getKey(), entry.getValue());
                    // 或者用
                    // 顺便说一下不同(setParameter会覆盖同名参数的值，addParameter则不会)
                    // uriBuilder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            URI uri = uriBuilder.build();
            // 创建get请求
            httpGet = new HttpGet(uri);
            log.info("访问路径：" + uri);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {// 返回200，请求成功
                // 结果返回
                result = EntityUtils.toString(response.getEntity());
                log.info("请求成功！，返回数据：" + result);
            } else {
                log.info("请求失败！");
            }
        } catch (Exception e) {
            log.info("请求失败!");
            log.error(e.toString());
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

    /**
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, Map<String, String> params) {
        String result = "";
        // 创建httpclient对象
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try { // 参数键值对
            if (null != params && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                NameValuePair pair = null;
                for (String key : params.keySet()) {
                    pair = new BasicNameValuePair(key, params.get(key));
                    pairs.add(pair);
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
                log.info("返回数据：>>>" + result);
            } else {
                log.info("请求失败！，url:" + url);
            }
        } catch (Exception e) {
            log.error("请求异常" + e.toString());
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

    /**
     * @param url
     * @param params
     * @return 返回数据
     */
    public static String doPostJSON(String url, String params) {
        String result = "";
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");
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

    /**
     * 发送put请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return String
     */
    public static String doPutJSON(String url, String params) {
        CloseableHttpClient httpClient = getHttpClient();
        String result = "";
        HttpPut put = new HttpPut(url);
        try {
            // 封装请求参数
            put.addHeader("Content-type", "application/json; charset=utf-8");
            put.setHeader("Accept", "application/json");
            if (StringUtils.isNotBlank(params)) {
                put.setEntity(new StringEntity(params, Charset.forName(DEFAULT_CHARSET)));
            }
            // 提交请求并以指定编码获取返回数据
            HttpResponse response = httpClient.execute(put);
            log.info("请求地址：" + url + "；响应状态：" + response.getStatusLine());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());
                log.info("返回数据：" + result);
            } else {
                log.info("请求失败");
            }
        } catch (IOException e) {
            log.error("请求异常", e.toString());
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

    /**
     * 发送delete请求
     *
     * @param url 请求地址
     * @return String
     */
    public static String doDelete(String url) {
        CloseableHttpClient httpClient = getHttpClient();
        String result = "";
        HttpDelete del = new HttpDelete(url);
        try {
            // 提交请求并以指定编码获取返回数据
            HttpResponse response = httpClient.execute(del);
            log.info("请求地址：" + url + "；响应状态：" + response.getStatusLine());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());
                log.info("返回数据：" + result);
            } else {
                log.info("请求失败");
            }
        } catch (IOException e) {
            log.error("请求异常", e.toString());
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
