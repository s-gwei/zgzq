//package org.jeecg.modules.dingding.util;
//
//import com.alibaba.fastjson.JSON;
//import com.dingtalk.api.DefaultDingTalkClient;
//import com.dingtalk.api.DingTalkClient;
//import com.dingtalk.api.request.*;
//import com.dingtalk.api.response.*;
//import com.google.common.collect.Lists;
//import com.taobao.api.ApiException;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.influxdb.dto.Point;
//import org.jeecg.common.util.DateUtils;
//import org.jeecg.common.util.SpringContextUtils;
//import org.jeecg.modules.dingding.entity.WorkerAttendanceRecord;
//import org.jeecg.modules.influxdb.config.InfluxDbUtils;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Author:wzp
// * @Date:Created in 11:32 AM 2020/4/26
// * @Description:获取钉钉打卡记录+存储到influxdb
// */
//@Slf4j
//@Component
//public class DingdingAttendanceRecords {
//
//
//    //dingdingAPP_KEY
//    private static final String APP_KEY = "dingbdna58p18ofanm39";
//
//    //dingdingAPP_SECRET
//    private static final String APP_SECRET = "zaWbRNKFCeqGUdzjluBQAfrJdgYiSSZP7qbaBaBwh4eT5DybGfZOF_y6Hew3syIn";
//
//    private static DefaultDingTalkClient client;
//
//
//    /**
//     * 获取全部考勤数据+每天上下班考勤数据
//     *
//     * @throws ApiException
//     */
//    @Scheduled(cron = "0 0 0 */1 * ?")
//    public void getAttendanceData() throws ApiException {
//        //token
//        client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
//        String accessToken = getToken(client);
//        //所有的部门
//        client = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list");
//        OapiDepartmentListResponse allDeparts = getAllDeparts(client, accessToken);
//        List<OapiDepartmentListResponse.Department> department1 = allDeparts.getDepartment();
//        //所有的员工id
//        client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/simplelist");
//        Set<String> userIds = getUserIds(client, accessToken, department1);
//        //一次最多只能查50个人
//        List<List<String>> split = split(new ArrayList<>(userIds), 50);
//        //全部打卡记录
//        client = new DefaultDingTalkClient("https://oapi.dingtalk.com/attendance/listRecord");
//        List<OapiAttendanceListRecordResponse.Recordresult> allRecordResults = new ArrayList<>();
//        for (List<String> userListCount : split) {
//            allRecordResults.addAll(getUserAllAttendanceRecord(client, accessToken, userListCount));
//        }
//        //每天上下班打卡记录
//        client = new DefaultDingTalkClient("https://oapi.dingtalk.com/attendance/list");
//        List<OapiAttendanceListResponse.Recordresult> onAndOffRecordResults = new ArrayList<>();
//        for (List<String> userListCount : split) {
//            onAndOffRecordResults.addAll(getUserOnAndOffAttendanceRecord(client, accessToken, userListCount));
//        }
//        //转成需要的格式
//        client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get");
//        List<WorkerAttendanceRecord> allAttendanceRecords = getAllWorkerAttendanceRecords(client, accessToken, allRecordResults);
//        List<WorkerAttendanceRecord> onAndOffAttendanceRecords = getOnAndOffAttendanceRecords(client, accessToken, onAndOffRecordResults);
////        //每天每人2条记录存influxdb
////        List<WorkerAttendanceRecord> userRecord = new ArrayList<>();
////        Map<String, List<WorkerAttendanceRecord>> map = new HashMap<>();
////        for (WorkerAttendanceRecord attendanceRecord : workerAttendanceRecords) {
////            if (map.containsKey(attendanceRecord.getUserId())) {
////                userRecord.add(attendanceRecord);
////                userRecord.sort(Comparator.comparing(WorkerAttendanceRecord::getCheckTime));
////                map.put(attendanceRecord.getUserId(), userRecord);
////            } else {
////                userRecord = new ArrayList<>();
////                userRecord.add(attendanceRecord);
////                userRecord.sort(Comparator.comparing(WorkerAttendanceRecord::getCheckTime));
////                map.put(attendanceRecord.getUserId(), userRecord);
////            }
////        }
//        //
////        for (String userId : map.keySet()) {
////            List<WorkerAttendanceRecord> oneRecords = map.get(userId);
////            userOnlyTwoRecord.add(oneRecords.get(0));
////            userOnlyTwoRecord.add(oneRecords.get(oneRecords.size() - 1));
////        }
//        log.info("全部打卡记录:" + JSON.toJSONString(allAttendanceRecords));
//        log.info("=====================================");
//        log.info("每天上下班打卡记录:" + JSON.toJSONString(onAndOffAttendanceRecords));
//        //每人每天上下班记录存influxdb
//        storeInfluxDB("dayAttendanceRecord", onAndOffAttendanceRecords);
//        //所有记录存influxdb 处理部门格式
//        storeInfluxDB("allAttendanceRecord", allAttendanceRecords);
//    }
//
//    /**
//     * WorkerAttendanceRecord的每人每天打卡记录
//     *
//     * @param client
//     * @param accessToken
//     * @param onAndOffRecordResults
//     * @return List
//     * @throws ApiException
//     */
//    private List<WorkerAttendanceRecord> getOnAndOffAttendanceRecords(DefaultDingTalkClient client, String accessToken, List<OapiAttendanceListResponse.Recordresult> onAndOffRecordResults) throws ApiException {
//        ArrayList<WorkerAttendanceRecord> workerAttendanceRecords = new ArrayList<>();
//        WorkerAttendanceRecord workerAttendanceRecord;
//        for (OapiAttendanceListResponse.Recordresult record : onAndOffRecordResults) {
//            workerAttendanceRecord = new WorkerAttendanceRecord();
//            workerAttendanceRecord.setId(record.getId());
//            workerAttendanceRecord.setUserId(record.getUserId());
//            workerAttendanceRecord.setCheckTime(record.getUserCheckTime());
//            workerAttendanceRecord.setUserName(getUserName(client, record.getUserId(), accessToken));
//            workerAttendanceRecord.setDepart(getUserDeparts(client, record.getUserId(), accessToken));
//            workerAttendanceRecord.setTimeResult(record.getTimeResult());
//            workerAttendanceRecord.setCheckType(record.getCheckType());
//            workerAttendanceRecords.add(workerAttendanceRecord);
//        }
//        return workerAttendanceRecords;
//
//    }
//
//    /**
//     * WorkerAttendanceRecord的全部打卡记录
//     *
//     * @param client
//     * @param accessToken
//     * @param allRecordResults
//     * @return List
//     * @throws ApiException
//     */
//    private List<WorkerAttendanceRecord> getAllWorkerAttendanceRecords(DefaultDingTalkClient client, String accessToken, List<OapiAttendanceListRecordResponse.Recordresult> allRecordResults) throws ApiException {
//        ArrayList<WorkerAttendanceRecord> workerAttendanceRecords = new ArrayList<>();
//        WorkerAttendanceRecord workerAttendanceRecord;
//        for (OapiAttendanceListRecordResponse.Recordresult record : allRecordResults) {
//            workerAttendanceRecord = new WorkerAttendanceRecord();
//            workerAttendanceRecord.setId(record.getId());
//            workerAttendanceRecord.setUserId(record.getUserId());
//            workerAttendanceRecord.setCheckTime(record.getUserCheckTime());
//            workerAttendanceRecord.setUserName(getUserName(client, record.getUserId(), accessToken));
//            workerAttendanceRecord.setDepart(getUserDeparts(client, record.getUserId(), accessToken));
//            workerAttendanceRecord.setTimeResult(record.getTimeResult());
//            workerAttendanceRecord.setCheckType(record.getCheckType());
//            workerAttendanceRecords.add(workerAttendanceRecord);
//        }
//        return workerAttendanceRecords;
//    }
//
//    /**
//     * 存influxdb
//     *
//     * @param dbName
//     * @param workerAttendanceRecords
//     */
//    private void storeInfluxDB(String dbName, List<WorkerAttendanceRecord> workerAttendanceRecords) {
//        InfluxDbUtils influxDbUtils = SpringContextUtils.getBean(InfluxDbUtils.class);
//        for (WorkerAttendanceRecord attendanceRecord : workerAttendanceRecords) {
//            String departMsg = "";
//            for (int i = 0; i < attendanceRecord.getDepart().size(); i++) {
//                if (i == attendanceRecord.getDepart().size() - 1) {
//                    departMsg += attendanceRecord.getDepart().get(i);
//                } else {
//                    departMsg += attendanceRecord.getDepart().get(i) + ",";
//                }
//            }
//            //处理数据
//            String checkTime = DateUtils.formatDate(attendanceRecord.getCheckTime(), "yyyy-MM-dd HH:mm:ss");
//            String timeResult = "";
//            if (!StringUtils.isEmpty(attendanceRecord.getTimeResult())) {
//                switch (attendanceRecord.getTimeResult()) {
//                    case "Normal":
//                        timeResult = "正常";
//                        break;
//                    case "Early":
//                        timeResult = "早退";
//                        break;
//                    case "Late":
//                        timeResult = "迟到";
//                        break;
//                    case "SeriousLate":
//                        timeResult = "严重迟到";
//                        break;
//                    case "Absenteeism":
//                        timeResult = "旷工迟到";
//                        break;
//                    case "NotSigned":
//                        timeResult = "未打卡";
//                        break;
//                }
//            }
//            String avatar = attendanceRecord.getAvatar();
//            if ("".equals(attendanceRecord.getAvatar()) || attendanceRecord.getAvatar() == null) {
//                avatar = "";
//            }
//            influxDbUtils.getInfluxDB().write(Point.measurement(dbName)
//                    .time(attendanceRecord.getCheckTime().getTime() + 28800000, TimeUnit.MILLISECONDS)
//                    .addField("id", attendanceRecord.getId())
//                    .addField("userId", attendanceRecord.getUserId())
//                    .addField("userName", attendanceRecord.getUserName())
//                    .addField("depart", departMsg)
//                    .addField("checkTime", checkTime)
//                    .addField("checkType", StringUtils.isEmpty(attendanceRecord.getCheckType()) ? "" : attendanceRecord.getCheckType())
//                    .addField("timeResult", timeResult)
//                    .addField("avatar", avatar)
//                    .build());
//            log.info(workerAttendanceRecords.toString());
//        }
//    }
//
//    /**
//     * 获取用户名称
//     *
//     * @param client
//     * @param userId
//     * @param accessToken
//     * @return String
//     * @throws ApiException
//     */
//    private String getUserName(DefaultDingTalkClient client, String userId, String accessToken) throws ApiException {
//        OapiUserGetRequest request = new OapiUserGetRequest();
//        request.setUserid(userId);
//        request.setHttpMethod("GET");
//        OapiUserGetResponse response = client.execute(request, accessToken);
//        return response.getName();
//    }
//
//    /**
//     * 获取当前用户的所属部门
//     *
//     * @param client
//     * @param userId
//     * @param accessToken
//     * @return List
//     * @throws ApiException
//     */
//    private List<String> getUserDeparts(DefaultDingTalkClient client, String userId, String accessToken) throws
//            ApiException {
//        ArrayList<String> departs = new ArrayList<>();
//        OapiUserGetRequest request = new OapiUserGetRequest();
//        request.setUserid(userId);
//        request.setHttpMethod("GET");
//        OapiUserGetResponse response = client.execute(request, accessToken);
//        List<Long> departments = response.getDepartment();
//        client = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/get");
//        OapiDepartmentGetRequest request1 = new OapiDepartmentGetRequest();
//        for (Long department : departments) {
//            request1.setId(department + "");
//            request1.setHttpMethod("GET");
//            OapiDepartmentGetResponse response1 = client.execute(request1, accessToken);
//            departs.add(response1.getName());
//        }
//
//        return departs;
//    }
//
//    /**
//     * 获取token
//     *
//     * @param client
//     * @return String
//     * @throws ApiException
//     */
//    private String getToken(DefaultDingTalkClient client) throws ApiException {
//        //拿token
//        OapiGettokenRequest request = new OapiGettokenRequest();
//        request.setAppkey(APP_KEY);
//        request.setAppsecret(APP_SECRET);
//        request.setHttpMethod("GET");
//        OapiGettokenResponse response = client.execute(request);
//        return response.getAccessToken();
//    }
//
//
//    /**
//     * 拆分集合
//     *
//     * @param <T>           泛型对象
//     * @param resList       需要拆分的集合
//     * @param subListLength 每个子集合的元素个数
//     * @return 返回拆分后的各个集合组成的列表
//     * 代码里面用到了guava和common的结合工具类
//     **/
//    public <T> List<List<T>> split(List<T> resList, int subListLength) {
//        if (CollectionUtils.isEmpty(resList) || subListLength <= 0) {
//            return Lists.newArrayList();
//        }
//        List<List<T>> ret = Lists.newArrayList();
//        int size = resList.size();
//        if (size <= subListLength) {
//            // 数据量不足 subListLength 指定的大小
//            ret.add(resList);
//        } else {
//            int pre = size / subListLength;
//            int last = size % subListLength;
//            // 前面pre个集合，每个大小都是 subListLength 个元素
//            for (int i = 0; i < pre; i++) {
//                List<T> itemList = Lists.newArrayList();
//                for (int j = 0; j < subListLength; j++) {
//                    itemList.add(resList.get(i * subListLength + j));
//                }
//                ret.add(itemList);
//            }
//            // last的进行处理
//            if (last > 0) {
//                List<T> itemList = Lists.newArrayList();
//                for (int i = 0; i < last; i++) {
//                    itemList.add(resList.get(pre * subListLength + i));
//                }
//                ret.add(itemList);
//            }
//        }
//        return ret;
//    }
//
//    /**
//     * 获取全部的打卡记录
//     *
//     * @param client
//     * @param accessToken
//     * @param subUserIds
//     * @return List
//     * @throws ApiException
//     */
//    private List<OapiAttendanceListRecordResponse.Recordresult> getUserAllAttendanceRecord(DingTalkClient client, String
//            accessToken, List<String> subUserIds) throws ApiException {
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.DATE, -1);
//        Date time = c.getTime();
//        OapiAttendanceListRecordRequest request = new OapiAttendanceListRecordRequest();
//        request.setCheckDateFrom(DateUtils.datetimeFormat.format(time));
//        request.setCheckDateTo(DateUtils.datetimeFormat.format(new Date()));
//        request.setUserIds(subUserIds);
//        OapiAttendanceListRecordResponse execute = client.execute(request, accessToken);
//        return execute.getRecordresult();
//    }
//
//    /**
//     * 获取每天上下班的打卡记录
//     *
//     * @param client
//     * @param accessToken
//     * @param subUserIds
//     * @return List
//     * @throws ApiException
//     */
//    private List<OapiAttendanceListResponse.Recordresult> getUserOnAndOffAttendanceRecord(DingTalkClient client, String
//            accessToken, List<String> subUserIds) throws ApiException {
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.DATE, -1);
//        Date time = c.getTime();
//        OapiAttendanceListRequest request = new OapiAttendanceListRequest();
//        request.setWorkDateFrom(DateUtils.datetimeFormat.format(time));
//        request.setWorkDateTo(DateUtils.datetimeFormat.format(new Date()));
//        request.setUserIdList(subUserIds);
//        request.setOffset(0L);
//        request.setLimit(50L);
//        OapiAttendanceListResponse response = client.execute(request, accessToken);
//        return response.getRecordresult();
//    }
//
//    /**
//     * 获取公司所有的部门
//     *
//     * @param client
//     * @param accessToken
//     * @return OapiDepartmentListResponse
//     * @throws ApiException
//     */
//    private OapiDepartmentListResponse getAllDeparts(DefaultDingTalkClient client, String accessToken) throws
//            ApiException {
//        OapiDepartmentListRequest req = new OapiDepartmentListRequest();
//        req.setHttpMethod("GET");
//        return client.execute(req, accessToken);
//    }
//
//    /**
//     * 获取所有职员的id
//     *
//     * @param client
//     * @param accessToken
//     * @param department
//     * @return Set
//     * @throws ApiException
//     */
//    private Set<String> getUserIds(DefaultDingTalkClient client, String
//            accessToken, List<OapiDepartmentListResponse.Department> department
//    ) throws ApiException {
//        Set<String> set = new HashSet<>();
//        OapiUserSimplelistRequest request = new OapiUserSimplelistRequest();
//        for (OapiDepartmentListResponse.Department department1 : department) {
//            request.setDepartmentId(department1.getId());
//            request.setOffset(0L);
//            request.setSize(100L);
//            request.setHttpMethod("GET");
//            OapiUserSimplelistResponse response = client.execute(request, accessToken);
//            for (OapiUserSimplelistResponse.Userlist userlist : response.getUserlist()) {
//                set.add(userlist.getUserid());
//            }
//        }
//        return set;
//    }
//
//
//}
//
