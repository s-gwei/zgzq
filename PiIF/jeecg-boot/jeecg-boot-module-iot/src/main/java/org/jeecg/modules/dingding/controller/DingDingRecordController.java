//package org.jeecg.modules.dingding.controller;
//
//import lombok.extern.slf4j.Slf4j;
//import org.jeecg.common.api.vo.Result;
//import org.jeecg.modules.dingding.service.IDingDingService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @Author:wzp
// * @Date:Created in 3:31 PM 2020/4/21
// * @Description:钉钉考勤记录查询controller
// */
//@RestController
//@RequestMapping(value = "/dingding")
//@Slf4j
//public class DingDingRecordController {
//
//    @Autowired
//    private IDingDingService dingDingService;
//
//    /**
//     * 获取所有钉钉考勤记录
//     *
//     * @param pageNo
//     * @param pageSize
//     * @param userName
//     * @param startTime
//     * @param endTime
//     * @return Result
//     */
//    @RequestMapping(value = "/getAttendanceRecord", method = RequestMethod.GET)
//    public Result getAttendanceRecord(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
//                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
//                                      String userName, Long startTime, Long endTime, String depart) {
//
//        Result<Object> result = new Result<>();
//        try {
//            result = dingDingService.getAttendanceRecord(pageNo, pageSize, userName, startTime, endTime, depart);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//        return result;
//    }
//
//    /**
//     * 获取每人每天2次的钉钉考勤记录
//     *
//     * @param pageNo
//     * @param pageSize
//     * @param userName
//     * @param startTime
//     * @param endTime
//     * @return Result
//     */
//    @RequestMapping(value = "/getOnAndOffRecords", method = RequestMethod.GET)
//    public Result getOnAndOffRecords(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
//                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
//                                     String userName, Long startTime, Long endTime, String depart) {
//
//        Result<Object> result = new Result<>();
//        try {
//            result = dingDingService.getOnAndOffRecords(pageNo, pageSize, userName, startTime, endTime, depart);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//        return result;
//    }
//
//}
