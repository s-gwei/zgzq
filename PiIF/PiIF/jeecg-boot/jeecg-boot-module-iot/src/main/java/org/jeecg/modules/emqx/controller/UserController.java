package org.jeecg.modules.emqx.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.emqx.util.HttpClientUtil;
import org.jeecg.modules.emqx.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author xduan
 * @version 2020/3/17
 * @Desc 集成emqx 用户接口
 */
@RestController
@Slf4j
@Api("emqx用户api")
@RequestMapping("/emqx")
public class UserController {

    @Value("${emqx.baseurl}")
    private String baseurl;

    /**
     * 新增用户
     * @param user
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ApiOperation("给emqx添加用户")
    public Result<?> addUser(@RequestBody User user) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/users
         *
         */
        String url = baseurl + "api/v4/users";
        try {
            String result = HttpClientUtil.doPostJSON(url, JSON.toJSONString(user));
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
        return Result.ok("新增成功");
    }

    /**
     * 修改用户
     * @param user
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    @ApiOperation("修改emqx用户信息")
    public Result<?> editUser(@RequestBody User user) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/users
         *
         */
        String url = baseurl + "api/v4/users/" + user.getUsername();
        try {
            String result = HttpClientUtil.doPutJSON(url, JSON.toJSONString(user));
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
        return Result.ok("修改成功");
    }

    /**
     * 获取所有用户
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ApiOperation("获取emqx系统user列表")
    public Result<?> getUserList() {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/users
         *
         */
        String url = baseurl + "api/v4/users";
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
     * 删除用户
     * @param username
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.DELETE)
    @ApiOperation("删除用户")
    public Result<?> deleteUser(@RequestParam(value = "username", required = true) String username) {
        /**
         * emqx中的接口地址
         * http://127.0.0.1:18083/api/v4/users
         *
         */
        String url = baseurl + "api/v4/users/" + username;
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

}
