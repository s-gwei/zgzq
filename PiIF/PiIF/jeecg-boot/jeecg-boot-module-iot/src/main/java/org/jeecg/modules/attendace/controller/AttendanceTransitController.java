package org.jeecg.modules.attendace.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.attendace.entity.Worker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * @Author:wzp
 * @Date:Created in 3:31 PM 2020/4/21
 * @Description:上海门禁考勤中转类
 */
@RestController
@RequestMapping(value = "/attendanceTransit")
@Slf4j
public class AttendanceTransitController {

    //springboot自带的http/s的请求模板
    private RestTemplate restTemplate = new RestTemplate();

    //需要请求的url
    private final static String URL = "http://localhost:8983/api/data/get";

    /**
     * 接收从前端传过来的人员信息 并进行url的拼接
     *
     * @param worker
     * @return Result
     */
    @RequestMapping(value = "/getMsg", method = RequestMethod.POST)
    public Result getMsg(@RequestBody Worker worker) {
        String[] deptids = worker.getDeptid().replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .split(",");
        String params = "id=" + worker.getId()
                + "&do=" + worker.getDoType()
                + "&data=" + worker.getData()
                + "&ccid=" + worker.getCard()
                + "&name=" + worker.getName()
                + "&passwd=" + worker.getPasswd()
                + "&deptid=" + Arrays.toString(deptids)
                + "&auth=" + worker.getAuth()
                + "&faceexist=" + worker.getFaceexist()
                + "&card=" + worker.getCcid();
        String url = URL + "?" + params;
        //用springRestTemplate发送get请求
        ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
        log.info(res.getBody() + ",状态码:" + res.getStatusCodeValue());
        if (res.getStatusCodeValue() == 200) {
            return Result.ok("同步成功!");
        }
        return Result.error("同步失败,请联系管理员");
    }

}
