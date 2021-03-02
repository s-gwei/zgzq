package org.jeecg.modules.datashape.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.datashape.entity.DataShapeModelVO;
import org.jeecg.modules.datashape.entity.IotDatashapeModel;
import org.jeecg.modules.datashape.entity.IotTableColumn;
import org.jeecg.modules.datashape.service.IDatashapeModelService;
import org.jeecg.modules.datashape.service.ITableMessageService;
import org.jeecg.modules.influxdb.config.InfluxDbUtils;
import org.jeecg.modules.iot.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:23 2020/3/4
 * @Description:influxdb表和列的crud
 */
@Service
@Transactional
public class TableMessageServiceImpl implements ITableMessageService {


    @Autowired
    private IDatashapeModelService datashapeModelService;

//    @Autowired
//    private InfluxDbUtils influxDbUtils;

    /**
     * 查询已经添加的表和列
     *
     * @param pageNo
     * @param pageSize
     * @return Result
     */
    @Override
    public Result<Object> findTableAndColumn(Integer pageNo, Integer pageSize) {
        List<IotDatashapeModel> list = datashapeModelService.findTableAndColumn();
        Collection<Object> values;
        for (IotDatashapeModel datashapeModel : list) {
            Map<String, Object> columnMsg = (Map<String, Object>) JSON.parse(datashapeModel.getFielddefinitions());
            Map<String, Object> tableMsg = (Map<String, Object>) JSON.parse(datashapeModel.getConfigurationchanges());
            if (columnMsg == null) {
                values = null;
            } else {
                values = columnMsg.values();
            }
            datashapeModel.setTableMsg(tableMsg);
            datashapeModel.setColumnMsg(values);
            datashapeModel.setFielddefinitions(null);
            datashapeModel.setConfigurationchanges(null);
        }
        Result<Object> result = new Result<>();
        IPage<IotDatashapeModel> paging = PageUtil.paging(pageNo, pageSize, list);
        result.setResult(paging);
        result.setSuccess(true);
        return result;
    }

    /**
     * 根据表名查询已经添加的表和列
     *
     * @param tableName 表名
     * @return Result
     */
    @Override
    public Result<Object> findTableAndColumnByTableName(String tableName) {
        IotDatashapeModel datashapeModel = datashapeModelService.findTableAndColumnByTableName(tableName);
        Map<String, Object> columnMsg = (Map<String, Object>) JSON.parse(datashapeModel.getFielddefinitions());
        Map<String, Object> tableMsg = (Map<String, Object>) JSON.parse(datashapeModel.getConfigurationchanges());
        Collection<Object> values;
        if (columnMsg == null) {
            values = null;
        } else {
            values = columnMsg.values();
        }
        datashapeModel.setTableMsg(tableMsg);
        datashapeModel.setColumnMsg(values);
        datashapeModel.setFielddefinitions(null);
        datashapeModel.setConfigurationchanges(null);
        Result<Object> result = new Result<>();
        result.setResult(datashapeModel);
        result.setSuccess(true);
        return result;
    }


    /**
     * 新增表
     *
     * @param dataShapeModelVO
     * @return Result
     */
    @Override
    public Result<Object> addTable(DataShapeModelVO dataShapeModelVO) {
        try {
            //重复判断
            IotDatashapeModel table = datashapeModelService.findLastedTableColumn(dataShapeModelVO.getName());
            if (table != null) {
                return Result.error("当前表已存在,请重新命名来创建新表");
            }
            //不存在新建表  datashape_model
            IotDatashapeModel insertTable = new IotDatashapeModel();
            HashMap<String, Object> map = new HashMap<>();
            map.put("type", "insert");
            map.put("user", dataShapeModelVO.getUser());
            map.put("times", System.currentTimeMillis());
            insertTable.setAvatar(dataShapeModelVO.getAvatar());
            insertTable.setDescription(dataShapeModelVO.getDescription());
            insertTable.setDocumentationcontent(dataShapeModelVO.getDocumentationcontent());
            insertTable.setHomemashup(dataShapeModelVO.getHomemashup());
            insertTable.setTags(dataShapeModelVO.getTags());
            insertTable.setProjectname(dataShapeModelVO.getProjectname());
            insertTable.setName(dataShapeModelVO.getName());
            insertTable.setObjectPk(UUID.randomUUID().toString());
            insertTable.setConfigurationchanges(JSON.toJSONString(map));
            boolean insert = datashapeModelService.save(insertTable);
            if (insert) {
                return Result.ok("添加表成功!");
            } else {
                return Result.error("添加表失败,请联系管理员!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("系统异常,请联系管理员");
    }

    /**
     * 新增列
     *
     * @param jsonData {"user":"admin",
     *                 "name":"东风",
     *                 "Week":{"name":"Week","aspects":{},"description":"","baseType":"STRING","ordinal":2}}
     * @return Result
     */
    @Override
    public Result<Object> addTableColumn(String jsonData) {
        try {
            //处理json数据
            JSONObject jsonObject = JSON.parseObject(jsonData);
            //添加人
            String user = (String) jsonObject.get("user");
            //添加表名
            String tableName = (String) jsonObject.get("name");
            jsonObject.remove("user");
            jsonObject.remove("name");
            //排除掉已经存在的列
            IotDatashapeModel iotDatashapeModel = datashapeModelService.findExistColumns(tableName);
            Map<String, Object> innerMap = jsonObject.getInnerMap();
            Set<String> lowerColumns = innerMap.keySet();
            Map<String, Object> exist;
            if (StringUtils.isEmpty(iotDatashapeModel.getFielddefinitions())) {
                exist = innerMap;
            } else {
                exist = (Map<String, Object>) JSON.parse(iotDatashapeModel.getFielddefinitions());
                //排除掉已经存在的列
                Set<String> existColumns = exist.keySet();
                lowerColumns.removeAll(existColumns);
                if (lowerColumns.size() > 0) {
                    exist.putAll(innerMap);
                } else {
                    return Result.error("您输入的列都已存在,请勿重复创建!");
                }
            }
            iotDatashapeModel.setFielddefinitions(JSON.toJSONString(exist));
            //存修改信息(json)列信息到mysql的datashape_model
            HashMap<String, Object> map = new HashMap<>();
            map.put("type", "insert");
            map.put("user", user);
            map.put("times", System.currentTimeMillis());
            datashapeModelService.addTableMessages(JSON.toJSONString(map), JSON.toJSONString(exist), tableName);
            insertInfluxDB(tableName, innerMap, user);
            return Result.ok("添加列成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统异常,请联系管理员");
        }
    }

    /**
     * 更新列
     *
     * @param jsonData {"user":"admin",
     *                 "name":"东风",
     *                 "updateColumn":"Day",
     *                 "Week":{"name":"Week","aspects":{},"description":"","baseType":"STRING","ordinal":2}}
     * @return Result
     */
    @Override
    public Result<Object> updateTableColumn(String jsonData) {
        try {
            //处理json数据
            JSONObject jsonObject = JSON.parseObject(jsonData);
            //修改人
            String user = (String) jsonObject.get("user");
            //修改表名
            String tableName = (String) jsonObject.get("name");
            //修改列
            String updateColumn = (String) jsonObject.get("updateColumn");
            jsonObject.remove("user");
            jsonObject.remove("name");
            jsonObject.remove("updateColumn");
            //最新的列信息
            Map<String, Object> innerMap = jsonObject.getInnerMap();
            //修改信息
            HashMap<String, Object> updateMsgMap = new HashMap<>();
            updateMsgMap.put("type", "update");
            updateMsgMap.put("user", user);
            updateMsgMap.put("times", System.currentTimeMillis());
            //查询最新的列信息,再更新
            IotDatashapeModel datashapeModel = datashapeModelService.findLastedTableColumn(tableName);
            Map<String, Object> exist = (Map<String, Object>) JSON.parse(datashapeModel.getFielddefinitions());
            exist.remove(updateColumn);
            exist.putAll(innerMap);
            datashapeModel.setConfigurationchanges(JSON.toJSONString(updateMsgMap));
            datashapeModel.setFielddefinitions(JSON.toJSONString(exist));
            int i = datashapeModelService.updateTableColumnAndMsg(datashapeModel);
            if (i > 0) {
                return Result.ok("修改列成功");
            } else {
                return Result.error("修改列失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修改失败,请联系管理员");
        }

    }


    /**
     * 删除列
     * @param tableName 表名
     * @param user 删除人
     * @param deleteColumn 删除列
     * @return Result
     */
    @Override
    public Result<Object> deleteTableColumn(String tableName, String user, String deleteColumn) {
        try {
            //修改信息
            HashMap<String, Object> updateMsgMap = new HashMap<>();
            updateMsgMap.put("type", "delete");
            updateMsgMap.put("user", user);
            updateMsgMap.put("times", System.currentTimeMillis());
            //查询最新的列信息,再更新
            IotDatashapeModel datashapeModel = datashapeModelService.findLastedTableColumn(tableName);
            Map<String, Object> exist = (Map<String, Object>) JSON.parse(datashapeModel.getFielddefinitions());
            exist.remove(deleteColumn);
            datashapeModel.setConfigurationchanges(JSON.toJSONString(updateMsgMap));
            datashapeModel.setFielddefinitions(JSON.toJSONString(exist));
            int i = datashapeModelService.updateTableColumnAndMsg(datashapeModel);
            if (i > 0) {
                return Result.ok("删除列成功!");
            } else {
                return Result.error("删除列失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统异常,请联系管理员");
        }
    }


    /**
     * 新增列插入influxdb
     *
     * @param tableName
     * @param innerMap
     */
    private void insertInfluxDB(String tableName, Map<String, Object> innerMap, String user) {
        //通过spring容器拿到influx对象
//        InfluxDB influxDB = influxDbUtils.getInfluxDB();
        Collection<Object> values = innerMap.values();
        //列信息转成实体类
        List<IotTableColumn> iotTableColumns = new ArrayList<>();
        IotTableColumn iotTableColumn;
        for (Object value : values) {
            iotTableColumn = new IotTableColumn();
            JSONObject object = (JSONObject) value;
            iotTableColumn.setColumnName((String) object.get("name"));
            iotTableColumn.setColumnType((String) object.get("baseType"));
            iotTableColumn.setColumnOrder((Integer) object.get("ordinal"));
            iotTableColumns.add(iotTableColumn);
        }
        iotTableColumns.sort(Comparator.comparing(IotTableColumn::getColumnOrder));
        //influxdb创建列
        for (IotTableColumn column : iotTableColumns) {
            if ("FLOAT".equals(column.getColumnType())
                    || "BYTE".equals(column.getColumnType())
                    || "SHORT".equals(column.getColumnType())
                    || "LONG".equals(column.getColumnType())
                    || "INTEGER".equals(column.getColumnType())
                    || "DOUBLE".equals(column.getColumnType())
                    || "BIGINTEGER".equals(column.getColumnType())) {
                influxDB.write(Point.measurement(tableName)
                        .time(System.currentTimeMillis() + 28800000, TimeUnit.MILLISECONDS)
                        .addField(column.getColumnName(), Double.MIN_VALUE)
                        .build());
            } else if ("STRING".equals(column.getColumnType())) {
                influxDB.write(Point.measurement(tableName)
                        .time(System.currentTimeMillis() + 28800000, TimeUnit.MILLISECONDS)
                        .addField(column.getColumnName(), "")
                        .build());
            }
        }
    }


    /**
     * 效验列值重复
     *
     * @param oldColumns
     * @param nowColumns
     * @param newColumns
     * @return String
     */
    private String checkUpdateOldColumns(Collection<String> oldColumns, Collection<String> nowColumns, Collection<String> newColumns) {
        String errMsg = "";
        if (!nowColumns.containsAll(oldColumns)) {
            oldColumns.removeAll(nowColumns);
            errMsg = "需要修改的列:";
            for (String oldColumnName : oldColumns) {
                errMsg += oldColumnName + " ";
            }
            errMsg += " 不存在,请创建后再修改" + "\\n";
        }
        if (nowColumns.containsAll(newColumns)) {
            newColumns.retainAll(nowColumns);
            errMsg = "您输入的修改列:";
            for (String newColumn : newColumns) {
                errMsg += newColumn + " ";
            }
            errMsg += " 已经存在,请更换列名";
        }
        return errMsg;
    }

    /**
     * 用修改的新列代替老列
     *
     * @param oldColumnNames 老列
     * @param newColumnNames 修改后的列
     * @param nowColumns     当前列
     * @return List
     */
    private List<String> getLastedTableColumns(List<String> oldColumnNames, List<String> newColumnNames, Collection<String> nowColumns) {
        HashSet<String> strings = new HashSet<>();
        List<String> newColumns = new ArrayList<>();
        for (String column : nowColumns) {
            for (int i = 0; i < oldColumnNames.size(); i++) {
                if (column.equals(oldColumnNames.get(i))) {
                    strings.add(newColumnNames.get(i));
                } else {
                    strings.add(column);
                }
            }
        }
        newColumns.addAll(strings);
        newColumns.removeAll(oldColumnNames);
        return newColumns;
    }

    /**
     * datashape_model存储数据
     *
     * @param tableName
     * @param jsonString
     * @param user
     * @param type
     * @return
     */
    private void operateDataShapeModel(String tableName, String jsonString, String user, String type) {
        IotDatashapeModel iotDatashapeModel = new IotDatashapeModel();
        HashMap<String, Object> updateMsgMap = new HashMap<>();
        updateMsgMap.put("type", type);
        updateMsgMap.put("user", user);
        updateMsgMap.put("times", System.currentTimeMillis());
        iotDatashapeModel.setConfigurationchanges(JSON.toJSONString(updateMsgMap));
        iotDatashapeModel.setFielddefinitions(jsonString);
        iotDatashapeModel.setName(tableName);
        iotDatashapeModel.setObjectPk(UUID.randomUUID().toString());
        datashapeModelService.save(iotDatashapeModel);
    }
}


