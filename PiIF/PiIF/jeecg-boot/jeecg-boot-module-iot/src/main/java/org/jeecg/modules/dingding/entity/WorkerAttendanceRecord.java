//package org.jeecg.modules.dingding.entity;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import lombok.Data;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * @Author:wzp
// * @Date:Created in 9:52 AM 2020/4/27
// * @Description:用户考勤打卡记录
// */
//@Data
//public class WorkerAttendanceRecord implements Comparable<WorkerAttendanceRecord> {
//
//    /**
//     * 序号id
//     */
//    private Long id;
//
//    /**
//     * 头像
//     */
//    private String avatar;
//
//    /**
//     * 用户姓名
//     */
//    private String userName;
//
//    /**
//     * 用户id
//     */
//    private String userId;
//
//    /**
//     * 用户部门
//     */
//    private List<String> depart;
//
//    /**
//     * 打卡时间
//     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    private Date checkTime;
//
//    /**
//     * 打卡状态
//     */
//    private String checkType;
//
//    /**
//     * 打卡结果
//     */
//    private String timeResult;
//
//    @Override
//    public int compareTo(WorkerAttendanceRecord workerAttendanceRecord) {
//        return this.checkTime.compareTo(workerAttendanceRecord.getCheckTime());
//    }
//}
