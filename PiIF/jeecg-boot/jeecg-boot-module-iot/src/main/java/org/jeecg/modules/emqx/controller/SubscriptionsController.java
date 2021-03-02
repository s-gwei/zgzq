package org.jeecg.modules.emqx.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.emqx.entity.Publish;
import org.jeecg.modules.emqx.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author xduan
 * @version 2020/3/24
 * @Desc 集成emqx 发布订阅等相关接口
 */
@RestController
@Slf4j
@Api("emqx订阅相关api")
@RequestMapping("/emqx")
public class SubscriptionsController {
    @Value("${emqx.baseurl}")
    private String baseurl;

    /**
     * 获取集群订阅信息
     *
     * @return
     */
    @RequestMapping(value = "/subscriptions", method = RequestMethod.GET)
    @ApiOperation("获取集群订阅信息")
    public Result<?> getSubscriptions(Integer _page, Integer _limit) {
        if (_page == null) {
            _page = 1;
        }
        if (_limit == null) {
            _limit = 100;
        }
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/subscriptions?_page=1&_limit=10000
         *
         */
        String url = baseurl + "api/v4/subscriptions?_page=" + _page + "&_limit=" + _limit;
        Object data = null;
        try {
            String result = HttpClientUtil.doGet(url);
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject == null) {
                return Result.error("请求错误");
            }
            Object code = jsonObject.get("code");
            //没有code则请求错误
            if (code == null) {
                return Result.error(jsonObject.toJSONString());
            }
            data = jsonObject.get("data");
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(data);
    }

    /**
     * 获取集群指定连接订阅信息
     *
     * @return
     */
    @RequestMapping(value = "/subscriptions/clientId", method = RequestMethod.GET)
    @ApiOperation("获取集群指定连接订阅信息")
    public Result<?> getSubscriptionsByClientId(@RequestParam String clientId) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/subscriptions/${clientid}
         *
         */
        String url = baseurl + "api/v4/subscriptions/" + clientId;
        Object data = null;
        try {
            String result = HttpClientUtil.doGet(url);
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject == null) {
                return Result.error("请求错误");
            }
            Object code = jsonObject.get("code");
            //没有code则请求错误
            if (code == null) {
                return Result.error(jsonObject.toJSONString());
            }
            data = jsonObject.get("data");
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(data);
    }

    /**
     * 获取节点订阅信息
     *
     * @return
     */
    @RequestMapping(value = "/node/subscriptions", method = RequestMethod.GET)
    @ApiOperation("获取节点订阅信息")
    public Result<?> getNodeSubscriptions(@RequestParam String node, Integer _page, Integer _limit) {
        if (_page == null) {
            _page = 1;
        }
        if (_limit == null) {
            _limit = 100;
        }
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/nodes/emqx@127.0.0.1/subscriptions?_page=1&_limit=10000
         *
         */
        String url = baseurl + "api/v4/nodes/" + node + "/subscriptions?_page=" + _page + "&_limit=" + _limit;
        Object data = null;
        try {
            String result = HttpClientUtil.doGet(url);
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject == null) {
                return Result.error("请求错误");
            }
            Object code = jsonObject.get("code");
            //没有code则请求错误
            if (code == null) {
                return Result.error(jsonObject.toJSONString());
            }
            data = jsonObject.get("data");
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(data);
    }

    /**
     * 获取节点指定连接订阅信息
     *
     * @param node
     * @param clientId
     * @return
     */
    @RequestMapping(value = "/node/subscriptions/clientId", method = RequestMethod.GET)
    @ApiOperation("获取节点指定连接订阅信息")
    public Result<?> getNodeSubscriptionsByClientId(@RequestParam String node
            , @RequestParam String clientId) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/ api/v4/nodes/${node}/subscriptions/${clientid}
         *
         */
        String url = baseurl + "api/v4/nodes/" + node + "/subscriptions/" + clientId;
        Object data = null;
        try {
            String result = HttpClientUtil.doGet(url);
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject == null) {
                return Result.error("请求错误");
            }
            Object code = jsonObject.get("code");
            //没有code则请求错误
            if (code == null) {
                return Result.error(jsonObject.toJSONString());
            }
            data = jsonObject.get("data");
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(data);
    }

    /**
     * 获取集群路由表
     *
     * @return
     */
    @RequestMapping(value = "/getRoutes", method = RequestMethod.GET)
    @ApiOperation("获取集群路由表")
    public Result<?> getRoutes() {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/routes
         *
         */
        String url = baseurl + "api/v4/routes";
        Object data = null;
        try {
            String result = HttpClientUtil.doGet(url);
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject == null) {
                return Result.error("请求错误");
            }
            Object code = jsonObject.get("code");
            //没有code则请求错误
            if (code == null) {
                return Result.error(jsonObject.toJSONString());
            }
            data = jsonObject.get("data");
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(data);
    }

    /**
     * 获取集群指定主题的路由信息
     *
     * @return
     */
    @RequestMapping(value = "/getRoutesByTopic", method = RequestMethod.GET)
    @ApiOperation("获取集群指定主题的路由信息")
    public Result<?> getRoutesByTopic(@RequestParam String topic) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/routes/${topic}
         *
         */
        String url = baseurl + "api/v4/routes/" + topic;
        Object data = null;
        try {
            String result = HttpClientUtil.doGet(url);
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject == null) {
                return Result.error("请求错误");
            }
            Object code = jsonObject.get("code");
            //没有code则请求错误
            if (code == null) {
                return Result.error(jsonObject.toJSONString());
            }
            data = jsonObject.get("data");
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(data);
    }

    /**
     * 发布消息
     * @param publish
     * @return
     */
    @RequestMapping(value = "/addPublish", method = RequestMethod.POST)
    @ApiOperation("发布消息")
    public Result<?> addPublish(@RequestBody Publish publish) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/ api/v4/mqtt/publish
         *
         */
        String url = baseurl + "api/v4/mqtt/publish";
        try {
            String result = HttpClientUtil.doPostJSON(url,JSON.toJSONString(publish));
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject == null) {
                return Result.error("请求错误");
            }
            Object code = jsonObject.get("code");
            //没有code则请求错误
            if (code == null) {
                return Result.error(jsonObject.toJSONString());
            }
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok("发布消息成功");
    }

    /**
     * 创建订阅
     * @param publish
     * @return
     */
    @RequestMapping(value = "/addSubscribe", method = RequestMethod.POST)
    @ApiOperation("创建订阅")
    public Result<?> addSubscribe(@RequestBody Publish publish) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/mqtt/subscribe
         *
         */
        String url = baseurl + "api/v4/mqtt/subscribe";
        try {
            String result = HttpClientUtil.doPostJSON(url,JSON.toJSONString(publish));
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject == null) {
                return Result.error("请求错误");
            }
            Object code = jsonObject.get("code");
            //没有code则请求错误
            if (code == null) {
                return Result.error(jsonObject.toJSONString());
            }
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(publish.getClientid()+"客户端 订阅'"+publish.getTopic()+"'成功 ");
    }

    /**
     * 取消订阅
     * @param publish
     * @return
     */
    @RequestMapping(value = "/Unsubscribe", method = RequestMethod.POST)
    @ApiOperation("取消订阅")
    public Result<?> Unsubscribe(@RequestBody Publish publish) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/mqtt/unsubscribe
         *
         */
        String url = baseurl + "api/v4/mqtt/subscribe";
        try {
            String result = HttpClientUtil.doPostJSON(url,JSON.toJSONString(publish));
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject == null) {
                return Result.error("请求错误");
            }
            Object code = jsonObject.get("code");
            //没有code则请求错误
            if (code == null) {
                return Result.error(jsonObject.toJSONString());
            }
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(publish.getClientid()+"客户端 取消订阅'"+publish.getTopic()+"'成功 ");
    }


}
