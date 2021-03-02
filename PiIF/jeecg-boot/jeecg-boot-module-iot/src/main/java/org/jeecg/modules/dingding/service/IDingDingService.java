//package org.jeecg.modules.dingding.service;
//
//import org.jeecg.common.api.vo.Result;
//
///**
// * @Author:wzp
// * @Date:Created in 10:26 PM 2020/4/27
// * @Description:IDingDingService
// */
//public interface IDingDingService {
//
//    /**
//     * 获取钉钉考勤记录
//     *
//     * @param pageNo
//     * @param pageSize
//     * @param userName
//     * @param startTime
//     * @param endTime
//     * @param depart
//     * @return Result
//     */
//    Result<Object> getAttendanceRecord(Integer pageNo, Integer pageSize, String userName, Long startTime, Long endTime, String depart);
//
//    /**
//     * 获取每人每天2次的钉钉考勤记录
//     *
//     * @param pageNo
//     * @param pageSize
//     * @param userName
//     * @param startTime
//     * @param endTime
//     * @param depart
//     * @return Result
//     */
//    Result<Object> getOnAndOffRecords(Integer pageNo, Integer pageSize, String userName, Long startTime, Long endTime, String depart);
//}
