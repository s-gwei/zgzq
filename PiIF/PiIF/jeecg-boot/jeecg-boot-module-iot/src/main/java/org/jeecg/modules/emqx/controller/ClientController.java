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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xduan
 * @version 2020/3/17
 * @Desc 集成emqx客户端相关接口
 */
@RestController
@Slf4j
@Api("emqx客户端API")
@RequestMapping("/emqx")
public class ClientController {

    @Value("${emqx.baseurl}")
    private String baseurl;

    /**
     * 获取集群的所有客户端信息
     *
     * @param _page
     * @param _limit
     * @return
     */
    @RequestMapping(value = "/getClients", method = RequestMethod.GET)
    @ApiOperation("获取所有客户端的信息")
    public Result<?> getClients(Integer _page, Integer _limit) {

        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/clients
         *
         */
        String url = baseurl + "api/v4/clients";
        if (_page == null) {
            _page = 1;
        }
        if (_limit == null) {
            _limit = 100;
        }
        url = url + "?_page=" + _page + "&_limit=" + _limit;
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
     * 获取某个节点下的所有客户端信息
     *
     * @param node
     * @return
     */
    @RequestMapping(value = "/node/clients", method = RequestMethod.GET)
    @ApiOperation("获取单个节点下的客户端信息")
    public Result<?> getNodeClient(@RequestParam(value = "node", required = true) String node) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/nodes/emqx@127.0.0.1/clients
         *
         */
        String url = baseurl + "api/v4/nodes/" + node + "/clients";
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

    @RequestMapping(value = "/getClientById", method = RequestMethod.GET)
    @ApiOperation("通过客户端id获取集群指定客户端信息")
    public Result<?> getClientById(@RequestParam(value = "clientId", required = true) String clientId) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/clients/{clientid}
         *
         */
        String url = baseurl + "api/v4/clients/" + clientId;
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

    @RequestMapping(value = "/getNodeClientById", method = RequestMethod.GET)
    @ApiOperation("通过客户端id获取节点指定客户端信息")
    public Result<?> getNodeClientById(@RequestParam(value = "clientId", required = true) String clientId
            , @RequestParam(value = "node", required = true) String node) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/nodes/${node}/clients/${clientid}
         *
         */
        String url = baseurl + "api/v4/nodes/" + node + "/clients/" + clientId;
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

    @RequestMapping(value = "/getNodeClientByUsername", method = RequestMethod.GET)
    @ApiOperation("通过用户名获取节点指定客户端信息")
    public Result<?> getNodeClientByUsername(@RequestParam(value = "username", required = true) String username
            , @RequestParam(value = "node", required = true) String node) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/ api/v4/nodes/${nodes}/clients/username/${username}
         *
         */
        String url = baseurl + "api/v4/nodes/"+node+"/clients/username/" + username;
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

    @RequestMapping(value = "/getClientByUsername", method = RequestMethod.GET)
    @ApiOperation("通过用户名获取集群指定客户端信息")
    public Result<?> getClientByUsername(@RequestParam(value = "username", required = true) String username) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/ api/v4/clients/username/${username}
         *
         */
        String url = baseurl + "api/v4/clients/username/" + username;
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


    @RequestMapping(value = "/deleteClient", method = RequestMethod.DELETE)
    @ApiOperation("踢掉指定客户端")
    public Result<?> deleteClient(@RequestParam(value = "clientId", required = true) String clientId) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/ api/v4/clients/${clientid}
         *
         */
        String url = baseurl + "api/v4/clients/" + clientId;
        try {
            String result = HttpClientUtil.doDelete(url);
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
        return Result.ok("删除成功");
    }

    @RequestMapping(value = "/getACL", method = RequestMethod.GET)
    @ApiOperation("获取指定客户端 ACL 缓存")
    public Result<?> getACL(@RequestParam(value = "clientId", required = true) String clientId) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/clients/${clientid}/acl_cache
         *
         */
        String url = baseurl + "api/v4/clients/" + clientId+"acl_cache";
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

    @RequestMapping(value = "/deleteACL", method = RequestMethod.DELETE)
    @ApiOperation("清除指定客户端 ACL 缓存")
    public Result<?> deleteACL(@RequestParam(value = "clientId", required = true) String clientId) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/clients/${clientid}/acl_cache
         *
         */
        String url = baseurl + "api/v4/clients/" + clientId+"acl_cache";
        try {
            String result = HttpClientUtil.doDelete(url);
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
        return Result.ok("清除缓存成功");
    }

    /**
     *获取集群基本信息
     * @return
     */
    @RequestMapping(value = "/getBrokers", method = RequestMethod.GET)
    @ApiOperation("获取集群基本信息")
    public Result<?> getBrokers() {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/brokers/
         *
         */
        String url = baseurl + "api/v4/brokers/";
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
     *获取节点基本信息
     * @return
     */
    @RequestMapping(value = "/getBrokersByNode", method = RequestMethod.GET)
    @ApiOperation("获取节点基本信息")
    public Result<?> getBrokersByNode(String node) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/brokers/${node}
         *
         */
        String url = baseurl + "api/v4/brokers/"+node;
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
     *获取集群监控数据
     * @return
     */
    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
    @ApiOperation("获取集群监控数据")
    public Result<?> getInfo() {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/nodes/
         *
         */
        String url = baseurl + "api/v4/nodes/";
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
     *获取节点监控数据
     * @return
     */
    @RequestMapping(value = "/getNodeInfoByNode", method = RequestMethod.GET)
    @ApiOperation("获取节点监控数据")
    public Result<?> getNodeInfoByNode(String node) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/nodes/${node}
         *
         */
        String url = baseurl + "api/v4/nodes/"+node;
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

}
