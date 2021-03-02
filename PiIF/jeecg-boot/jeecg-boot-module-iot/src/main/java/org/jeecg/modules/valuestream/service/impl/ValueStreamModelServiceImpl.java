package org.jeecg.modules.valuestream.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.datashape.entity.IotTableColumn;
import org.jeecg.modules.influxdb.config.InfluxDbUtils;
import org.jeecg.modules.iot.util.PageUtil;
import org.jeecg.modules.system.service.IPermissionService;
import org.jeecg.modules.system.service.ISysVisiblePermissionService;
import org.jeecg.modules.valuestream.entity.IotValuestreamModel;
import org.jeecg.modules.valuestream.entity.ValueStreamColumn;
import org.jeecg.modules.valuestream.entity.ValueStreamModelVO;
import org.jeecg.modules.valuestream.mapper.ValueStreamModelMapper;
import org.jeecg.modules.valuestream.service.IValueStreamModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author:wzp
 * @Date:Created in 9:45 AM 2020/3/20
 * @Description:StreamModelServiceImpl
 */
@Service
public class ValueStreamModelServiceImpl extends ServiceImpl<ValueStreamModelMapper, IotValuestreamModel> implements IValueStreamModelService {

    @Autowired
    private ValueStreamModelMapper valueStreamModelMapper;

    @Autowired
    private InfluxDbUtils influxDbUtils;

    @Resource
    private IPermissionService permissionService;

    @Resource
    private ISysVisiblePermissionService visiblePermissionService;

    private static final String VALUESTREAM = "iot_valuestream_model";



//    /**
//     * 根据表名和列名
//     *
//     * @param tableName 表名
//     * @param column    列名
//     * @return IotValuestreamModel1
//     */
//    @Override
//    public IotValuestreamModel1 findExistColumnByTableNameAndColumn(String tableName, String column) {
//        return valuestreamModelMapper1.findExistColumnByTableNameAndColumn(tableName, column);
//    }
//
//    /**
//     * 更新修改信息
//     *
//     * @param existValueStreamModelIot
//     */
//    @Override
//    public void updateConfigurationChanges(IotValuestreamModel1 existValueStreamModelIot) {
//        valuestreamModelMapper1.updateConfigurationChanges(existValueStreamModelIot);
//    }
    //    /**
//     * influxdb列添加数据
//     *
//     * @param tableName   表名
//     * @param column      添加数据的列
//     * @param columnValue 具体数据
//     * @param valueType   列值的类型
//     * @param user        添加人
//     * @return Result
//     */
//    @Override
//    public Result<Object> updateColumnValue(String tableName, String column, String columnValue, String valueType, String user) {
//        try {
//            //查询修改列是否已经存在
//            IotValuestreamModel1 existValueStreamModelIot = valuestreamModelService.findExistColumnByTableNameAndColumn(tableName, column);
//            //不存在,创建
//            if (existValueStreamModelIot == null) {
//                IotValuestreamModel1 iotValuestreamModel1 = new IotValuestreamModel1();
//                HashMap<String, Object> map = new HashMap<>();
//                map.put("type", "insertValue");
//                map.put("user", user);
//                map.put("times", System.currentTimeMillis());
//                iotValuestreamModel1.setConfigurationchanges(JSON.toJSONString(map));
//                iotValuestreamModel1.setName(tableName);
//                iotValuestreamModel1.setFielddefinitions(column);
//                valuestreamModelService.save(iotValuestreamModel1);
//                //存在,更新修改信息
//            } else {
//                HashMap<String, Object> map = new HashMap<>();
//                map.put("type", "insertValue");
//                map.put("user", user);
//                map.put("times", System.currentTimeMillis());
//                existValueStreamModelIot.setConfigurationchanges(JSON.toJSONString(map));
//                valuestreamModelService.updateConfigurationChanges(existValueStreamModelIot);
//            }
//            //通过spring容器拿到influx对象
//            InfluxDB influxDB = influxDbUtils.getInfluxDB();
//            //根据参数类型,进行存储
//            if ("FLOAT".equals(valueType) || "DOUBLE".equals(valueType)
//            ) {
//                influxDB.write(Point.measurement(tableName)
//                        .time(System.currentTimeMillis() + 28800000, TimeUnit.MILLISECONDS)
//                        .addField(column, Double.valueOf(columnValue))
//                        .addField("user", user)
//                        .build());
//            } else if ("STRING".equals(valueType)) {
//                influxDB.write(Point.measurement(tableName)
//                        .time(System.currentTimeMillis() + 28800000, TimeUnit.MILLISECONDS)
//                        .addField(column, columnValue)
//                        .addField("user", user)
//                        .build());
//            } else if ("BYTE".equals(valueType) || "SHORT".equals(valueType)
//                    || "LONG".equals(valueType) || "INTEGER".equals(valueType) || "BIGINTEGER".equals(valueType)) {
//                influxDB.write(Point.measurement(tableName)
//                        .time(System.currentTimeMillis() + 28800000, TimeUnit.MILLISECONDS)
//                        .addField(column, Integer.valueOf(columnValue))
//                        .addField("user", user)
//                        .build());
//            }
//            return Result.ok("设置成功!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.error("设置失败,请联系管理员");
//        }
//    }

//    /**
//     * 查询列的历史数据
//     *
//     * @param tableName 表名
//     * @param column    列名
//     * @return Result
//     */
//    @Override
//    public Result<Object> findUpdateColumnValues(String tableName, String column) {
//        InfluxDB influxDB = influxDbUtils.getInfluxDB();
//        QueryResult results = influxDB.query(new Query("select " + column + " from " + "\"" + tableName + "\""));
//        ArrayList<Map<String, Object>> hashMaps = new ArrayList<>();
//        for (QueryResult.Result result : results.getResults()) {
//            List<QueryResult.Series> series = result.getSeries();
//            for (QueryResult.Series serie : series) {
//                //列名
//                List columns = serie.getColumns();
//                //值集合
//                List<List<Object>> values = serie.getValues();
//                for (int i = 0; i < values.size(); ++i) {
//                    Map<String, Object> dataMap = new HashMap<>(columns.size());
//                    for (int j = 0; j < columns.size(); ++j) {
//                        dataMap.put((String) columns.get(j), values.get(i).get(j));
//                    }
//                    hashMaps.add(dataMap);
//                }
//            }
//        }
//        return Result.ok(hashMaps);
//    }

    /**
     * 条件查询
     *
     * @param name
     * @param description
     * @param tags
     * @param projectName
     * @param userId
     * @return IPage
     */
    @Override
    public IPage<IotValuestreamModel> query(Page<IotValuestreamModel> page, String name, String description, String tags, String projectName, String userId) {
        // 根据条件拼sql
        QueryWrapper<IotValuestreamModel> queryWrapper = new QueryWrapper<>();
        if (org.springframework.util.StringUtils.hasText(name)) {
            queryWrapper.like("name", name);
        }
        if (org.springframework.util.StringUtils.hasText(description)) {
            queryWrapper.like("description", description);
        }
        if (org.springframework.util.StringUtils.hasText(tags)) {
            queryWrapper.like("tags", tags);
        }
        if (org.springframework.util.StringUtils.hasText(projectName)) {
            queryWrapper.like("projectName", projectName);
        }
        boolean admin = permissionService.isAdmin(userId);
        if (!admin) {
            //  不是管理员，权限过滤 nameList 需要添加用户创建的模板
            List<String> objectPkByUserId = visiblePermissionService.getObjectPkByUserId(userId, VALUESTREAM);
            if (objectPkByUserId.size() <= 0) {
                return page;
            }
            queryWrapper.in("object_pk", objectPkByUserId);
        }
        IPage<IotValuestreamModel> iPage = valueStreamModelMapper.selectPage(page, queryWrapper);
        for (IotValuestreamModel record : iPage.getRecords()) {
            record.setObject_pk(record.getObjectPk());
            record.setObjectPk(null);
        }
        return iPage;

    }

    /**
     * 查询已经添加的表和列
     *
     * @return Result
     */
    @Override
    public Result<Object> findTableAndColumn(Integer pageNo, Integer pageSize) {
        List<IotValuestreamModel> list = valueStreamModelMapper.findTableAndColumn();
        Collection<Object> values;
        for (IotValuestreamModel valuestreamModel : list) {
            Map<String, Object> columnMsg = (Map<String, Object>) JSON.parse(valuestreamModel.getFielddefinitions());
            Map<String, Object> tableMsg = (Map<String, Object>) JSON.parse(valuestreamModel.getConfigurationchanges());
            if (columnMsg == null) {
                values = null;
            } else {
                values = columnMsg.values();
            }
            valuestreamModel.setTableMsg(tableMsg);
            valuestreamModel.setColumnMsg(values);
            valuestreamModel.setFielddefinitions(null);
            valuestreamModel.setConfigurationchanges(null);
        }
        Result<Object> result = new Result<>();
        IPage<IotValuestreamModel> paging = PageUtil.paging(pageNo, pageSize, list);
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
        IotValuestreamModel datashapeModel = valueStreamModelMapper.findTableAndColumnByTableName(tableName);
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
        datashapeModel.setObjectPk(null);
        Result<Object> result = new Result<>();
        result.setResult(datashapeModel);
        result.setSuccess(true);
        return result;
    }


    /**
     * 新增valueStreamModel
     *
     * @param valueStreamModelVO
     * @return Result
     */
    @Override
    public Result<Object> add(ValueStreamModelVO valueStreamModelVO) {
        Result<Object> result = new Result<>();
        try {
            //查询表是否已经存在
            IotValuestreamModel existValuestreamModel = valueStreamModelMapper.findByTableName(valueStreamModelVO.getName());
            if (existValuestreamModel != null) {
                return Result.error("该表已经存在,请勿重复创建");
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("type", "insert");
            map.put("user", valueStreamModelVO.getUser());
            map.put("times", System.currentTimeMillis());
            IotValuestreamModel iotValuestreamModel = new IotValuestreamModel();
            iotValuestreamModel.setName(valueStreamModelVO.getName());
            iotValuestreamModel.setConfigurationchanges(JSON.toJSONString(map));
            iotValuestreamModel.setDescription(valueStreamModelVO.getDescription());
            iotValuestreamModel.setDocumentationcontent(valueStreamModelVO.getDocumentationcontent());
            iotValuestreamModel.setEnabled(valueStreamModelVO.isEnabled());
            iotValuestreamModel.setHomemashup(valueStreamModelVO.getHomemashup());
            iotValuestreamModel.setIdentifier(valueStreamModelVO.getIdentifier());
            iotValuestreamModel.setImplementedshapes(valueStreamModelVO.getImplementedshapes());
            iotValuestreamModel.setAvatar(valueStreamModelVO.getAvatar());
            iotValuestreamModel.setProjectname(valueStreamModelVO.getProjectname());
            iotValuestreamModel.setObjectPk(UUID.randomUUID().toString());
            iotValuestreamModel.setPublished(valueStreamModelVO.isPublished());
            iotValuestreamModel.setTags(valueStreamModelVO.getTags());
            iotValuestreamModel.setThingtemplate(valueStreamModelVO.getThingtemplate());
            iotValuestreamModel.setValuestream(valueStreamModelVO.getValuestream());
            int insert = valueStreamModelMapper.insert(iotValuestreamModel);
            if (insert > 0) {
                result = Result.ok("新增valueStream成功!");
            } else {
                result = Result.error("新增valueStream失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 新增valueStreamModel列
     *
     * @param valueStreamColumn {"user":"admin",
     *                          "name":"东风",
     *                          "Day":{"name":"Day","aspects":{},"description":"","baseType":"STRING","ordinal":1}}
     * @return Result
     */
    @Override
    public Result<Object> addColumns(ValueStreamColumn valueStreamColumn) {
        //key是column  要转换成列名
        Map<String, Object> map = (Map<String, Object>) valueStreamColumn.getColumn();
        HashMap<String, Object> addColumn = new HashMap<>();
        addColumn.put((String) map.get("name"), map);
        Map<String, Object> exist;
        Set<String> lowerColumns = addColumn.keySet();
        //查询valuestreamModel
        IotValuestreamModel valuestreamModel = valueStreamModelMapper.findByTableName(valueStreamColumn.getTableName());
        //没有列存在,第一次添加列
        if (StringUtils.isEmpty(valuestreamModel.getFielddefinitions())) {
            exist = addColumn;
        } else {
            exist = (Map<String, Object>) JSON.parse(valuestreamModel.getFielddefinitions());
            //排除掉已经存在的列
            Set<String> existColumns = exist.keySet();
            lowerColumns.removeAll(existColumns);
            if (lowerColumns.size() > 0) {
                exist.putAll(addColumn);
            } else {
                return Result.error("您输入的列都已存在,请勿重复创建!");
            }
        }
        //存修改信息
        HashMap<String, Object> insertMsg = new HashMap<>();
        insertMsg.put("type", "insert");
        insertMsg.put("user", valueStreamColumn.getUser());
        insertMsg.put("times", System.currentTimeMillis());
        valuestreamModel.setConfigurationchanges(JSON.toJSONString(insertMsg));
        valuestreamModel.setFielddefinitions(JSON.toJSONString(exist));
        //influxdb
        insertInfluxDB(valueStreamColumn.getTableName(), exist, valueStreamColumn.getUser());
        int update = valueStreamModelMapper.updateValueStreamModel(valuestreamModel);
        if (update > 0) {
            return Result.ok("添加列成功!");
        } else {
            return Result.error("添加列失败,请联系管理员!");
        }
    }


    /**
     * 修改valueStreamModel
     *
     * @param valueStreamModelVO
     * @return Result
     */
    @Override
    public Result<Object> updateTable(ValueStreamModelVO valueStreamModelVO) {
        IotValuestreamModel valuestreamModel = valueStreamModelMapper.findById(valueStreamModelVO.getEntityId());
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", "update");
        map.put("user", valueStreamModelVO.getUser());
        map.put("times", System.currentTimeMillis());
        valuestreamModel.setName(valueStreamModelVO.getName());
        valuestreamModel.setAvatar(valueStreamModelVO.getAvatar());
        valuestreamModel.setConfigurationchanges(JSON.toJSONString(map));
        valuestreamModel.setDescription(valueStreamModelVO.getDescription());
        valuestreamModel.setDocumentationcontent(valueStreamModelVO.getDocumentationcontent());
        valuestreamModel.setHomemashup(valueStreamModelVO.getHomemashup());
        valuestreamModel.setProjectname(valueStreamModelVO.getProjectname());
        valuestreamModel.setTags(valueStreamModelVO.getTags());
        valuestreamModel.setEnabled(valueStreamModelVO.isEnabled());
        valuestreamModel.setPublished(valueStreamModelVO.isPublished());
        valuestreamModel.setImplementedshapes(valueStreamModelVO.getImplementedshapes());
        valuestreamModel.setValuestream(valueStreamModelVO.getValuestream());
        valuestreamModel.setHomemashup(valueStreamModelVO.getHomemashup());
        valuestreamModel.setIdentifier(valueStreamModelVO.getIdentifier());
        int i = valueStreamModelMapper.updateTable(valuestreamModel);
        if (i > 0) {
            return Result.ok("修改valueStream成功!");
        } else {
            return Result.error("修改valueStream失败!");
        }
    }

    /**
     * 修改valueStreamModel列属性
     *
     * @param valueStreamColumn
     * @return Result
     */
    @Override
    public Result<Object> updateTableColumn(ValueStreamColumn valueStreamColumn) {
        //key是column  要转换成列名
        Map<String, Object> map = (Map<String, Object>) valueStreamColumn.getColumn();
        HashMap<String, Object> addColumn = new HashMap<>();
        addColumn.put((String) map.get("name"), map);
        //查询valuestreamModel
        IotValuestreamModel valuestreamModel = valueStreamModelMapper.findByTableName(valueStreamColumn.getTableName());
        Map<String, Object> exist = (Map<String, Object>) JSON.parse(valuestreamModel.getFielddefinitions());
        exist.remove(valueStreamColumn.getUpdateColumn());
        exist.putAll(addColumn);
        //存修改信息
        HashMap<String, Object> insertMsg = new HashMap<>();
        insertMsg.put("type", "update");
        insertMsg.put("user", valueStreamColumn.getUser());
        insertMsg.put("times", System.currentTimeMillis());
        valuestreamModel.setConfigurationchanges(JSON.toJSONString(insertMsg));
        valuestreamModel.setFielddefinitions(JSON.toJSONString(exist));
        int update = valueStreamModelMapper.updateValueStreamModel(valuestreamModel);
        if (update > 0) {
            return Result.ok("添加列成功!");
        } else {
            return Result.error("添加列失败,请联系管理员!");
        }

    }

    /**
     * 删除列
     *
     * @param tableName    表名
     * @param user         删除人
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
            IotValuestreamModel valuestreamModel = valueStreamModelMapper.findByTableName(tableName);
            Map<String, Object> exist = (Map<String, Object>) JSON.parse(valuestreamModel.getFielddefinitions());
            exist.remove(deleteColumn);
            valuestreamModel.setConfigurationchanges(JSON.toJSONString(updateMsgMap));
            valuestreamModel.setFielddefinitions(JSON.toJSONString(exist));
            int i = valueStreamModelMapper.updateValueStreamModel(valuestreamModel);
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
     * @param tableName
     * @param user
     * @return
     */
    @Override
    public Result<Object> deleteTable(String tableName, String user) {
        IotValuestreamModel valuestreamModel = valueStreamModelMapper.findByTableName(tableName);
        if (valuestreamModel == null) {
            return Result.error("当前删除表不存在");
        } else {
            int delete = valueStreamModelMapper.deleteTable(valuestreamModel);
            if (delete > 0) {
                return Result.ok("删除valuestreamModel成功!");
            } else {
                return Result.error("删除valuestreamModel失败!");

            }
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
        InfluxDB influxDB = influxDbUtils.getInfluxDB();
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
}
