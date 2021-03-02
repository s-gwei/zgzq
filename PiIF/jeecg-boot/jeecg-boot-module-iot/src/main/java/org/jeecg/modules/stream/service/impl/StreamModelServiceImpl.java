package org.jeecg.modules.stream.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.datashape.entity.IotDatashapeModel;
import org.jeecg.modules.datashape.mapper.DatashapeModelMapper;
import org.jeecg.modules.influxdb.config.InfluxDbUtils;
import org.jeecg.modules.iot.util.PageUtil;
import org.jeecg.modules.stream.entity.HomeMashUpVO;
import org.jeecg.modules.stream.entity.IotStreamModel;
import org.jeecg.modules.stream.entity.StreamModelVO;
import org.jeecg.modules.stream.mapper.StreamModelMapper;
import org.jeecg.modules.stream.service.IStreamModelService;
import org.jeecg.modules.system.service.IPermissionService;
import org.jeecg.modules.system.service.ISysVisiblePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author:wzp
 * @Date:Created in 9:45 AM 2020/3/20
 * @Description:StreamModelServiceImpl
 */
@Service
public class StreamModelServiceImpl extends ServiceImpl<StreamModelMapper, IotStreamModel> implements IStreamModelService {
    //数据库表名
    private static final String STREAM = "iot_stream_model";

    @Resource
    private StreamModelMapper streamModelMapper;

    @Autowired
    private InfluxDbUtils influxDbUtils;

    @Autowired
    private DatashapeModelMapper datashapeModelMapper;

    @Resource
    private IPermissionService permissionService;

    @Resource
    private ISysVisiblePermissionService visiblePermissionService;

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
    public IPage<IotStreamModel> query(Page<IotStreamModel> page, String name, String description, String tags, String projectName, String userId) {
        // 根据条件拼sql
        QueryWrapper<IotStreamModel> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            queryWrapper.like("name", name);
        }
        if (StringUtils.hasText(description)) {
            queryWrapper.like("description", description);
        }
        if (StringUtils.hasText(tags)) {
            queryWrapper.like("tags", tags);
        }
        if (StringUtils.hasText(projectName)) {
            queryWrapper.like("projectName", projectName);
        }
        boolean admin = permissionService.isAdmin(userId);
        if (!admin) {
            //  不是管理员，权限过滤 nameList 需要添加用户创建的模板
            List<String> objectPkByUserId = visiblePermissionService.getObjectPkByUserId(userId, STREAM);
            if (objectPkByUserId.size() <= 0) {
                return page;
            }
            queryWrapper.in("object_pk", objectPkByUserId);
        }
        IPage<IotStreamModel> iPage = streamModelMapper.selectPage(page, queryWrapper);
        for (IotStreamModel record : iPage.getRecords()) {
            record.setObject_pk(record.getObjectPk());
            record.setObjectPk(null);
        }
        return iPage;

    }

    /**
     * 查询混搭信息
     *
     * @param pageNo
     * @param pageSize
     * @param tableName
     * @return Result
     */
    @Override
    public Result<Object> findMashUpData(Integer pageNo, Integer pageSize, String tableName) {
        //查询出跟当前stream关联的datashape
        String datashape = streamModelMapper.findByStreamName(tableName).getDatashape();
        InfluxDB influxDB = influxDbUtils.getInfluxDB();
        QueryResult qr = influxDB.query(new Query("select * from " + "\"" + datashape + "\""));
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        for (QueryResult.Result result : qr.getResults()) {
            List<QueryResult.Series> series = result.getSeries();
            if (series == null) {
                return Result.error("系统错误,请联系管理员!");
            }
            for (QueryResult.Series serie : series) {
                List<List<Object>> values = serie.getValues();//字段字集合
                columns = serie.getColumns();//字段名
                // 封装查询结果
                for (int i = 0; i < values.size(); ++i) {
                    Map<String, Object> dataMap = new HashMap<>(columns.size());
                    for (int j = 0; j < columns.size(); ++j) {
                        dataMap.put(columns.get(j), values.get(i).get(j));
                    }
                    dataList.add(dataMap);
                }
            }
        }
        HomeMashUpVO homeMashUpVO = new HomeMashUpVO();
        List<Map<String, String>> maps = new ArrayList<>();
        Map<String, String> mashUpMap = new HashMap<>();
        //查datashape列名+类型
        IotDatashapeModel datashapeModel = datashapeModelMapper.findLastedTableColumn(datashape);
        Map<String, Object> columnMap = (Map<String, Object>) JSON.parse(datashapeModel.getFielddefinitions());
        for (String column : columns) {
            mashUpMap = new HashMap<>();
            if ("time".equals(column)) {
                mashUpMap.put("name", column);
                mashUpMap.put("type", "STRING");

            }
            Map<String, Object> columnData = (Map<String, Object>) columnMap.get(column);
            if (columnData != null) {
                String baseType = (String) columnData.get("baseType");
                mashUpMap.put("name", column);
                mashUpMap.put("type", baseType);

            }
            maps.add(mashUpMap);
        }
        Result<Object> result = new Result<>();
        IPage<Map<String, Object>> paging = PageUtil.paging(pageNo, pageSize, dataList);
        homeMashUpVO.setColumns(maps);
        homeMashUpVO.setData(paging);
        result.setResult(homeMashUpVO);
        result.setSuccess(true);
        return result;
    }

    /**
     * 查询StreamModel
     *
     * @param name stream表名
     * @return Result
     */
    @Override
    public Result<Object> findStreamById(String name) {
        IotStreamModel streamModel = streamModelMapper.findByStreamName(name);
        streamModel.setFielddefinitions(null);
        streamModel.setConfigurationchanges(null);
        return Result.ok(streamModel);
    }

    /**
     * 新增streamModel
     *
     * @param streamModelVO
     * @return Result
     */
    @Override
    public Result<Object> add(StreamModelVO streamModelVO) {
        Result<Object> result = new Result<>();
        try {
            //查询表是否已经存在
            IotStreamModel existStreamModel = streamModelMapper.findByStreamName(streamModelVO.getName());
            if (existStreamModel != null) {
                return Result.error("该表已经存在,请勿重复创建");
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("type", "insert");
            map.put("user", streamModelVO.getUser());
            map.put("times", System.currentTimeMillis());
            IotStreamModel iotStreamModel = new IotStreamModel();
            iotStreamModel.setName(streamModelVO.getName());
            iotStreamModel.setDescription(streamModelVO.getDescription());
            iotStreamModel.setProjectname(streamModelVO.getProjectname());
            iotStreamModel.setTags(streamModelVO.getTags());
            iotStreamModel.setImplementedshapes(streamModelVO.getImplementedshapes());
            iotStreamModel.setThingtemplate(streamModelVO.getThingtemplate());
            iotStreamModel.setEnabled(streamModelVO.isEnabled());
            iotStreamModel.setAvatar(streamModelVO.getAvatar());
            iotStreamModel.setConfigurationchanges(JSON.toJSONString(map));
            iotStreamModel.setIdentifier(streamModelVO.getIdentifier());
            iotStreamModel.setDocumentationcontent(streamModelVO.getDocumentationcontent());
            iotStreamModel.setPublished(streamModelVO.isPublished());
            iotStreamModel.setValuestream(streamModelVO.getValuestream());
            iotStreamModel.setHomemashup(streamModelVO.getHomemashup());
            iotStreamModel.setDatashape(streamModelVO.getDatashape());
            iotStreamModel.setObjectPk(UUID.randomUUID().toString());
            int insert = streamModelMapper.insert(iotStreamModel);
            if (insert > 0) {
                result = Result.ok("新增StreamModel成功!");
            } else {
                result = Result.error("新增StreamModel失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新streamModel
     *
     * @param streamModelVO
     * @return Result
     */
    @Override
    public Result<Object> updateStreamModel(StreamModelVO streamModelVO) {
        Result<Object> result = new Result<>();
        try {
            //查询表是否已经存在
            IotStreamModel existStreamModel = streamModelMapper.findByStreamName(streamModelVO.getName());
            if (existStreamModel == null) {
                return Result.error("该表不存在");
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("type", "update");
            map.put("user", streamModelVO.getUser());
            map.put("times", System.currentTimeMillis());
            existStreamModel.setDescription(streamModelVO.getDescription());
            existStreamModel.setProjectname(streamModelVO.getProjectname());
            existStreamModel.setTags(streamModelVO.getTags());
            existStreamModel.setImplementedshapes(streamModelVO.getImplementedshapes());
            existStreamModel.setThingtemplate(streamModelVO.getThingtemplate());
            existStreamModel.setEnabled(streamModelVO.isEnabled());
            existStreamModel.setAvatar(streamModelVO.getAvatar());
            existStreamModel.setConfigurationchanges(JSON.toJSONString(map));
            existStreamModel.setIdentifier(streamModelVO.getIdentifier());
            existStreamModel.setDocumentationcontent(streamModelVO.getDocumentationcontent());
            existStreamModel.setPublished(streamModelVO.isPublished());
            existStreamModel.setValuestream(streamModelVO.getValuestream());
            existStreamModel.setHomemashup(streamModelVO.getHomemashup());
            existStreamModel.setDatashape(streamModelVO.getDatashape());
            int update = streamModelMapper.updateStreamById(existStreamModel);
            if (update > 0) {
                result = Result.ok("修改StreamModel成功!");
            } else {
                result = Result.error("修改StreamModel失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除streamModel
     *
     * @param tableName 表名
     * @param user      删除人
     * @return Result
     */
    @Override
    public Result<Object> deleteStreamModel(String tableName, String user) {
        IotStreamModel streamModel = streamModelMapper.findByStreamName(tableName);
        if (streamModel == null) {
            return Result.error("当前删除表不存在");
        } else {
            int delete = streamModelMapper.deleteStreamModel(streamModel);
            if (delete > 0) {
                return Result.ok("删除streamModel成功!");
            } else {
                return Result.error("删除streamModel失败!");

            }
        }

    }


}
