//package org.jeecg;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.RandomUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.File;
//import java.math.RoundingMode;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Description: 设备连接平台sdkdemo
// * @author: boliu
// * @date: 2020年10月19日 9:22
// */
//@Slf4j
//public class ThingLinkApp extends VirtualThing {
//
//    private String propertyDefinitions;
//
//    /**
//     * 定义方法名称
//     */
//    private static final String SUM_NUMBER = "add";
//
//    /**
//     * 设备唯一识别号
//     */
//    public static final String IDENTIFIER1 = "2c91808b754f321601754f3817330001";
//    public static final String IDENTIFIER2 = "2c91808b754f321601754f397f4e0002";
//
//
//    /**
//     * 测试数据
//     */
//    public static final String MESSAGECONTENT = "{\n" +
//            "    \"timestamp\":1600756505311,\n" +
//            "    \"values\":[\n" +
//            "        {\n" +
//            "            \"id\":\"电压\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756496041\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电流\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756496041\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电流\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756497037\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电压\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756497037\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电流\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756498042\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电压\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756498042\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电流\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756499042\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电压\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756499042\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电压\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756500035\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电流\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756500035\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电压\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756501036\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电流\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756501036\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电压\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756502036\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电流\",\n" +
//            "            \"v\":-{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756502036\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电压\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756503039\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电流\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756503039\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电流\",\n" +
//            "            \"v\":-{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756504041\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电压\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756504041\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电流\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756505039\n" +
//            "        },\n" +
//            "        {\n" +
//            "            \"id\":\"电压\",\n" +
//            "            \"v\":{},\n" +
//            "            \"q\":true,\n" +
//            "            \"t\":1600756505039\n" +
//            "        }\n" +
//            "    ]\n" +
//            "}";
//
//
//    public ThingLinkApp(String identifier) {
//        super(identifier);
//        this.propertyDefinitions = getThingShap().get("propertyDefinitions").toString();
//    }
//
//    public String getPropertyDefinitions() {
//        return propertyDefinitions;
//    }
//
//    public void setPropertyDefinitions(String propertyDefinitions) {
//        this.propertyDefinitions = propertyDefinitions;
//    }
//
//
//    /**
//     * 从设备获取属性发送到平台
//     *
//     * @param bind
//     * @return
//     */
//    @Override
//    public ThingData getProperties(String bind) {
//        HashMap<String, Object> data = new HashMap<>(16);
//        if ("true".equals(bind)) {
//            data.put("propertyDefinitions", this.propertyDefinitions);
//        }
//        return new ThingData(ErrorCode.SUCCESS, data);
//    }
//
//    /**
//     * 从平台获取属性设置到设备
//     *
//     * @param properties
//     * @return
//     */
//    @Override
//    public int setProperties(HashMap<String, Object> properties) {
//        return super.setProperties(properties);
//    }
//
//    /**
//     * 调用设备上的自定义方法
//     *
//     * @param methodName
//     * @param params
//     * @return
//     */
//    @Override
//    public ThingData callService(String methodName, HashMap<String, Object> params) {
//        log.debug("自定义方法名称:" + methodName);
//        HashMap<String, Object> map = new HashMap<>(16);
//        double a = Double.parseDouble(params.get("var1").toString());
//        double b = Double.parseDouble(params.get("var2").toString());
//        switch (methodName) {
//            case SUM_NUMBER:
//                String sum = "" + add(a, b);
//                map.put("result", sum);
//                break;
//            default:
//        }
//        return new ThingData(ErrorCode.SUCCESS, map);
//    }
//
//    /**
//     * 自定义方法
//     *
//     * @param a
//     * @param b
//     * @return
//     */
//    private double add(Double a, Double b) {
//        return a + b;
//    }
//
//
//    private JSONObject getThingShap() {
//        //通过绝对路径读取json文件
//        File file = FileUtil.file("device/json/thingShap.json");
//        return JSONUtil.readJSONObject(file, Charset.defaultCharset());
//    }
//
//    public static void main(String[] args) {
//
//
//        //实例化一个设备
//        ThingLinkApp thingSample1 = new ThingLinkApp(IDENTIFIER1);
//        ThingLinkApp thingSample2 = new ThingLinkApp(IDENTIFIER2);
//        ArrayList<ThingLinkApp> deviceList = new ArrayList<>();
//        if (ObjectUtil.isNotNull(thingSample1)) {
//            onlineDeviceList(thingSample1, deviceList);
//        }
//        if (ObjectUtil.isNotNull(thingSample2)) {
//            onlineDeviceList(thingSample2, deviceList);
//        }
//        for (ThingLinkApp device : deviceList) {
//            Map<String, Object> defaultPropertiesMap = new HashMap<>(16);
//            defaultPropertiesMap.put("propertyDefinitions", device.getPropertyDefinitions());
//            //上传默认属性到平台
//            device.reportDefaultProperties(defaultPropertiesMap);
//        }
//        while (true) {
//            for (ThingLinkApp device : deviceList) {
//                String data = StrUtil.format(MESSAGECONTENT
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP)
//                        , RandomUtil.randomDouble(0, 100, 2, RoundingMode.HALF_UP));
//                Map<String, Object> propertyDataMap = DataFormatter.formatData(data);
//                //上传设备属性数据
//                device.reportProperties(propertyDataMap);
//                try {
//                    Thread.sleep(5 * 1000);
//                } catch (InterruptedException e) {
//                    log.error("线程休眠时发生异常:｛｝ ", e);
//                }
//            }
//        }
//    }
//
//    /**
//     * 获取上线设备列表
//     *
//     * @param thingSample
//     * @param deviceList
//     */
//    private static void onlineDeviceList(ThingLinkApp thingSample, ArrayList<ThingLinkApp> deviceList) {
//        boolean flag = true;
//        while (flag) {
//            if (ErrorCode.SUCCESS == thingSample.online()) {
//                deviceList.add(thingSample);
//                flag = false;
//            }
//        }
//    }
//}
