package org.jeecg.modules.emqx.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.emqx.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xduan
 * @version 2020/3/26
 * @Desc emqx 管理相关的api 包括插件，监听等
 */
@RestController
@Slf4j
@Api("插件及监听相关的api")
@RequestMapping("/emqx")
public class ManageController {

    @Value("${emqx.baseurl}")
    private String baseurl;

    /**
     * 获取所有节点插件列表
     *
     * @return
     */
    @RequestMapping(value = "/getPlugins", method = RequestMethod.GET)
    @ApiOperation("获取所有节点插件列表")
    public Result<?> getPlugins() {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/plugins
         *
         */
        String url = baseurl + "api/v4/plugins";
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
                //返回错误信息
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
     * 获取指定节点插件列表
     *
     * @param node
     * @return
     */
    @RequestMapping(value = "/getPluginsByNode", method = RequestMethod.GET)
    @ApiOperation("获取指定节点插件列表")
    public Result<?> getPluginsByNode(String node) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/nodes/${node}/plugins/
         *
         */
        String url = baseurl + "api/v4/nodes/" + node + "/plugins/";
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
                //返回错误信息
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
     * 关闭节点指定插件
     *
     * @param node
     * @return
     */
    @RequestMapping(value = "/disablePlugins", method = RequestMethod.PUT)
    @ApiOperation("关闭节点指定插件")
    public Result<?> disablePlugins(String node, String plugin) {
        /**
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/nodes/${node}/plugins/${plugin}/unload
         *
         */
        String url = baseurl + "api/v4/nodes/" + node + "/plugins/" + plugin + "/unload";
        try {
            String result = HttpClientUtil.doPutJSON(url, null);
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject == null) {
                return Result.error("请求错误");
            }
            Object code = jsonObject.get("code");
            //没有code则请求错误
            if (code == null) {
                //返回错误信息
                return Result.error(jsonObject.toJSONString());
            }
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(plugin + "已关闭");
    }

    /**
     * @param node
     * @param plugin
     * @return
     */
    @RequestMapping(value = "/enablePlugins", method = RequestMethod.PUT)
    @ApiOperation("启用节点指定插件")
    public Result<?> enablePlugins(String node, String plugin) {
        /**
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/nodes/${node}/plugins/${plugin}/load
         *
         */
        String url = baseurl + "api/v4/nodes/" + node + "/plugins/" + plugin + "/load";
        try {
            String result = HttpClientUtil.doPutJSON(url, null);
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject == null) {
                return Result.error("请求错误");
            }
            Object code = jsonObject.get("code");
            //没有code则请求错误
            if (code == null) {
                //返回错误信息
                return Result.error(jsonObject.toJSONString());
            }
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(plugin + "启动成功");
    }

    /**
     * 重启节点指定插件
     *
     * @param node
     * @param plugin
     * @return
     */
    @RequestMapping(value = "/restartPlugins", method = RequestMethod.PUT)
    @ApiOperation("重启节点指定插件")
    public Result<?> restartPlugins(String node, String plugin) {
        /**
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/nodes/${node}/plugins/${plugin}/reload
         *
         */
        String url = baseurl + "api/v4/nodes/" + node + "/plugins/" + plugin + "/reload";
        try {
            String result = HttpClientUtil.doPutJSON(url, null);
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject == null) {
                return Result.error("请求错误");
            }
            Object code = jsonObject.get("code");
            //没有code则请求错误
            if (code == null) {
                //返回错误信息
                return Result.error(jsonObject.toJSONString());
            }
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(plugin + "重启成功");
    }

    /**
     * 获取集群监听器列表
     *
     * @return
     */
    @RequestMapping(value = "/getListeners", method = RequestMethod.GET)
    @ApiOperation("获取集群监听器列表")
    public Result<?> getListeners() {
        /**
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/listeners/
         *
         */
        String url = baseurl + "api/v4/listeners/";
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
                //返回错误信息
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
     * 获取节点监听器列表
     *
     * @return
     */
    @RequestMapping(value = "/getListenersByNode", method = RequestMethod.GET)
    @ApiOperation("获取节点监听器列表")
    public Result<?> getListenersByNode(String node) {
        /**
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/nodes/${node}/listeners
         *
         */
        String url = baseurl + "api/v4/nodes/" + node + "/listeners/";
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
                //返回错误信息
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
     * 获取集群收发报文统计¶
     *
     * @return
     */
    @RequestMapping(value = "/getMetrics", method = RequestMethod.GET)
    @ApiOperation("获取集群收发报文统计")
    public Result<?> getMetrics() {
        /**M
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/metrics/
         *
         */
        String url = baseurl + "/api/v4/metrics/";
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
                //返回错误信息
                return Result.error(jsonObject.toJSONString());
            }
            data = jsonObject.get("data");
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(data);
    }

    @RequestMapping(value = "/getMetricsByNode", method = RequestMethod.GET)
    @ApiOperation("获取节点收发报文统计")
    public Result<?> getMetricsByNode(String node) {
        /**M
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/nodes/${node}/metrics/
         *
         */
        String url = baseurl + "/api/v4/nodes/" + node + "/metrics/";
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
                //返回错误信息
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
     * 获取集群连接会话统计
     * @return
     */
    @RequestMapping(value = "/getStats", method = RequestMethod.GET)
    @ApiOperation("获取集群连接会话统计")
    public Result<?> getStats() {
        /**M
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/stats
         *
         */
        String url = baseurl + "/api/v4/stats";
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
                //返回错误信息
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
     * 获取节点连接会话统计
     * @param node
     * @return
     */
    @RequestMapping(value = "/getStatsByNode", method = RequestMethod.GET)
    @ApiOperation("获取节点连接会话统计")
    public Result<?> getStatsByNode(String node) {
        /**M
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/nodes/${node}/stats
         *
         */
        String url = baseurl + "/api/v4/nodes/" + node + "/stats";
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
                //返回错误信息
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
     * 获取集群当前告警信息
     *
     * @return
     */
    @RequestMapping(value = "/getAlarms", method = RequestMethod.GET)
    @ApiOperation("获取集群当前告警信息")
    public Result<?> getAlarms() {
        /**M
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/alarms/present
         *
         */
        String url = baseurl + "/api/v4/alarms/present";
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
                //返回错误信息
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
     * 获取节点当前告警信息
     * @param node
     * @return
     */
    @RequestMapping(value = "/getAlarmsNode", method = RequestMethod.GET)
    @ApiOperation("获取节点当前告警信息")
    public Result<?> getAlarmsNode(String node) {
        /**M
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/alarms/present/${node}
         *
         */
        String url = baseurl + "/api/v4/alarms/present/"+node;
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
                //返回错误信息
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
     * 获取集群历史告警信息
     * @return
     */
    @RequestMapping(value = "/getHistoryAlarms", method = RequestMethod.GET)
    @ApiOperation("获取集群历史告警信息")
    public Result<?> getHistoryAlarms() {
        /**M
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/alarms/history
         *
         */
        String url = baseurl + "/api/v4/alarms/history";
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
                //返回错误信息
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
     * 获取节点历史告警信息
     * @param node
     * @return
     */
    @RequestMapping(value = "/getHistoryAlarmsNode", method = RequestMethod.GET)
    @ApiOperation("获取节点历史告警信息")
    public Result<?> getHistoryAlarmsNode(String node) {
        /**M
         * emqx中的接口地址
         * PUT
         * http://127.0.0.1:18083/api/v4/alarms/history/${node}
         *
         */
        String url = baseurl + "/api/v4/alarms/history/"+node;
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
                //返回错误信息
                return Result.error(jsonObject.toJSONString());
            }
            data = jsonObject.get("data");
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
        return Result.ok(data);
    }
}
