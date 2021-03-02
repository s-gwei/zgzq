package org.jeecg.modules.datashape.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.datashape.entity.DataShapeModelVO;
import org.jeecg.modules.datashape.entity.IotDatashapeModel;
import org.jeecg.modules.datashape.mapper.DatashapeModelMapper;
import org.jeecg.modules.datashape.service.IDatashapeModelService;
import org.jeecg.modules.system.service.IPermissionService;
import org.jeecg.modules.system.service.ISysVisiblePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @version:2020/3/4
 * @Author:wzp
 * @Date:Created in 15:23 2020/3/4
 * @Description: datashape_model serviceimpl
 */
@Service
public class DatashapeModelServiceImpl extends ServiceImpl<DatashapeModelMapper, IotDatashapeModel> implements IDatashapeModelService {
    //数据库里的表名
    private static final String DATASHAPE = "iot_datashape_model";

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
    public IPage<IotDatashapeModel> query(Page<IotDatashapeModel> page, String name, String description, String tags, String projectName, String userId) {
        // 根据条件拼sql
        QueryWrapper<IotDatashapeModel> queryWrapper = new QueryWrapper<>();
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
            List<String> objectPkByUserId = visiblePermissionService.getObjectPkByUserId(userId, DATASHAPE);
            if (objectPkByUserId.size() <= 0) {
                return page;
            }
            queryWrapper.in("object_pk", objectPkByUserId);
        }
        IPage<IotDatashapeModel> iPage = datashapeModelMapper.selectPage(page, queryWrapper);
        for (IotDatashapeModel record : iPage.getRecords()) {
            record.setObject_pk(record.getObjectPk());
            record.setObjectPk(null);
        }
        return iPage;


    }

    /**
     * 添加表信息和操作人信息
     *
     * @param subTableMessage 修改信息
     * @param tableMessage    列信息
     * @param tableName       表名
     */
    @Override
    public void addTableMessages(String subTableMessage, String tableMessage, String tableName) {
        datashapeModelMapper.addTableMessages(subTableMessage, tableMessage, tableName);
    }

    /**
     * 通过表名查询已有的列信息
     *
     * @param tableName 表名
     * @return IotDatashapeModel
     */
    @Override
    public IotDatashapeModel findExistColumns(String tableName) {
        return datashapeModelMapper.findExistColumns(tableName);
    }

    /**
     * 更新表新插入的列数据
     *
     * @param datashapeModel 表名
     */
    @Override
    public int updateTableColumnAndMsg(IotDatashapeModel datashapeModel) {
        return datashapeModelMapper.updateTableColumn(datashapeModel);
    }

    /**
     * 根据表名查询表的最新列结构
     *
     * @param tableName 表名
     * @return String
     */
    @Override
    public IotDatashapeModel findLastedTableColumn(String tableName) {
        return datashapeModelMapper.findLastedTableColumn(tableName);
    }

    /**
     * 查询已经添加的表和列
     *
     * @return Result
     */
    @Override
    public List<IotDatashapeModel> findTableAndColumn() {
        return datashapeModelMapper.findTableAndColumn();
    }

    /**
     * 根据表名查询已经添加的表和列
     *
     * @param tableName 表名
     * @return IotDatashapeModel
     */
    @Override
    public IotDatashapeModel findTableAndColumnByTableName(String tableName) {
        return datashapeModelMapper.findTableAndColumnByTableName(tableName);
    }

    /**
     * 修改datashape
     *
     * @param dataShapeModelVO
     * @return Result
     */
    @Override
    public Result<Object> updateTable(DataShapeModelVO dataShapeModelVO) {
        IotDatashapeModel datashapeModel = datashapeModelMapper.findById(dataShapeModelVO.getId());
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", "update");
        map.put("user", dataShapeModelVO.getUser());
        map.put("times", System.currentTimeMillis());
        datashapeModel.setName(dataShapeModelVO.getName());
        datashapeModel.setAvatar(dataShapeModelVO.getAvatar());
        datashapeModel.setConfigurationchanges(JSON.toJSONString(map));
        datashapeModel.setDescription(dataShapeModelVO.getDescription());
        datashapeModel.setDocumentationcontent(dataShapeModelVO.getDocumentationcontent());
        datashapeModel.setHomemashup(dataShapeModelVO.getHomemashup());
        datashapeModel.setProjectname(dataShapeModelVO.getProjectname());
        datashapeModel.setTags(dataShapeModelVO.getTags());
        int i = datashapeModelMapper.updateTable(datashapeModel);
        if (i > 0) {
            return Result.ok("修改dataShape成功!");
        } else {
            return Result.error("修改dataShape失败!");
        }
    }

    /**
     * 删除datashape
     *
     * @param tableName 表名
     * @param user      删除人
     * @return
     */
    @Override
    public Result<Object> deleteTable(String tableName, String user) {
        IotDatashapeModel deleteTable = datashapeModelMapper.findLastedTableColumn(tableName);
        IotDatashapeModel datashapeModel = datashapeModelMapper.findWithValueStreamModel(tableName);
        if (datashapeModel != null) {
            return Result.error("当前datashape正在被一个或者多个流引用,请先处理完成后再删除");
        } else {
            int i = datashapeModelMapper.deleteTable(deleteTable);
            if (i > 0) {
                return Result.ok("删除datashape成功!");
            } else {
                return Result.error("删除datashape失败!");
            }
        }

    }
}


