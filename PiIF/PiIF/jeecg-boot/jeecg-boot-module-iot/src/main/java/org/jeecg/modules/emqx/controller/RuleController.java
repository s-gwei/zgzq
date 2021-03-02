package org.jeecg.modules.emqx.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.emqx.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xduan
 * @version 2020/3/27
 * @Desc 规则和资源相关接口
 */
@RestController
@Slf4j
@Api("规则和资源相关接口")
@RequestMapping("/emqx")
public class RuleController {
    @Value("${emqx.baseurl}")
    private String baseurl;

    /**
     * 创建规则
     *
     * @param rule 规则json对象 示例如下
     *             {
     *             "name": "test-rule",
     *             "for": "message.publish",
     *             "rawsql": "select * from \"t/a\"",
     *             "actions": [{
     *             "name": "built_in:inspect_action",
     *             "params": {
     *             "a": 1
     *             }
     *             }],
     *             "description": "test-rule"
     *             }
     * @return 创建成功的规则对象
     */
    @RequestMapping(value = "/addRule", method = RequestMethod.POST)
    @ApiOperation("创建规则")
    public Result<?> addRule(@RequestBody JSONObject rule) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/rules
         *
         */
        String url = baseurl + "api/v4/rules";
        Object data = null;
        try {
            String result = HttpClientUtil.doPostJSON(url, JSON.toJSONString(rule));
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
     * 查询规则
     *
     * @return
     */
    @RequestMapping(value = "/getRulesById", method = RequestMethod.GET)
    @ApiOperation("查询规则")
    public Result<?> getRulesById(String ruleId) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/rules/${rule_id}
         *
         */
        String url = baseurl + "api/v4/rules/" + ruleId;
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
     * 获取当前规则列表
     *
     * @return
     */
    @RequestMapping(value = "/getRules", method = RequestMethod.GET)
    @ApiOperation("获取当前规则列表")
    public Result<?> getRules() {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/rules
         *
         */
        String url = baseurl + "api/v4/rules";
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
     * 删除规则
     *
     * @param ruleId
     * @return
     */
    @RequestMapping(value = "/deleteRuleById", method = RequestMethod.DELETE)
    @ApiOperation("删除规则")
    public Result<?> deleteRuleById(String ruleId) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/rules/${rule_id}
         *
         */
        String url = baseurl + "api/v4/rules/" + ruleId;
        try {
            String result = HttpClientUtil.doDelete(url);
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
        return Result.ok(ruleId + " 删除成功");
    }

    /**
     * 获取当前动作列表
     *
     * @return
     */
    @RequestMapping(value = "/getActions", method = RequestMethod.GET)
    @ApiOperation("获取当前动作列表")
    public Result<?> getActions() {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/actions
         *
         */
        String url = baseurl + "api/v4/actions";
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
     * 查询动作
     *
     * @param name 动作名称
     * @return
     */
    @RequestMapping(value = "/getActionByName", method = RequestMethod.GET)
    @ApiOperation("查询动作")
    public Result<?> getActionByName(String name) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/actions/${name}
         *
         */
        String url = baseurl + "api/v4/actions/" + name;
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
     * 获取当前资源类型列表
     *
     * @return
     */
    @RequestMapping(value = "/getResourceTypes", method = RequestMethod.GET)
    @ApiOperation("获取当前资源类型列表")
    public Result<?> getResourceTypes() {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/resource_types/
         *
         */
        String url = baseurl + "api/v4/resource_types/";
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
     * 查询资源类型
     *
     * @param type
     * @return
     */
    @RequestMapping(value = "/getResourceTypeByType", method = RequestMethod.GET)
    @ApiOperation("查询资源类型")
    public Result<?> getResourceTypeByType(String type) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/resource_types/${type}
         *
         */
        String url = baseurl + "api/v4/resource_types/" + type;
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
     * 获取某种类型的资源
     *
     * @param type
     * @return 返回该类型的所有资源
     */
    @RequestMapping(value = "/getResourceByType", method = RequestMethod.GET)
    @ApiOperation("获取某种类型的资源")
    public Result<?> getResourceByType(String type) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/resource_types/${type}/resources
         *
         */
        String url = baseurl + "api/v4/resource_types/" + type + "/resources";
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
     * 获取某种类型的动作
     *
     * @param type
     * @return
     */
    @RequestMapping(value = "/getActionByType", method = RequestMethod.GET)
    @ApiOperation("获取某种类型的动作")
    public Result<?> getActionByType(String type) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/resource_types/${type}/actions
         *
         */
        String url = baseurl + "api/v4/resource_types/" + type + "/actions";
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
     * 新建资源
     *
     * @param resource 示例：
     *                 {
     *                 "name": "test-resource",
     *                 "type": "built_in",
     *                 "config": {
     *                 "a": 1
     *                 },
     *                 "description": "test-resource"
     *                 }
     * @return
     */
    @RequestMapping(value = "/addResource", method = RequestMethod.POST)
    @ApiOperation("新建资源")
    public Result<?> addResource(@RequestBody JSONObject resource) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/resources
         *
         */
        String url = baseurl + "api/v4/resources/";
        Object data = null;
        try {
            String result = HttpClientUtil.doPostJSON(url, JSON.toJSONString(resource));
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
     * 获取资源列表
     *
     * @return
     */
    @RequestMapping(value = "/getResources", method = RequestMethod.GET)
    @ApiOperation("获取资源列表")
    public Result<?> getResources() {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/resources
         *
         */
        String url = baseurl + "api/v4/resources/";
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
     * 查询资源
     *
     * @param resourceId 资源Id
     * @return
     */
    @RequestMapping(value = "/getResourcesById", method = RequestMethod.GET)
    @ApiOperation("查询资源")
    public Result<?> getResourcesById(String resourceId) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/resources/:resource_id
         *
         */
        String url = baseurl + "api/v4/resources/" + resourceId;
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
     * 删除资源
     *
     * @param resourceId 资源Id
     * @return
     */
    @RequestMapping(value = "/deleteResourcesById", method = RequestMethod.DELETE)
    @ApiOperation("删除资源")
    public Result<?> deleteResourcesById(String resourceId) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/resources/:resource_id
         *
         */
        String url = baseurl + "api/v4/resources/" + resourceId;
        try {
            String result = HttpClientUtil.doDelete(url);
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
        return Result.ok(resourceId + "删除成功");
    }
}
