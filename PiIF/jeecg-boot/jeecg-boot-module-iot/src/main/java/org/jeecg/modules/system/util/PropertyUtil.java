package org.jeecg.modules.system.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.datashape.entity.IotDatashapeModel;
import org.jeecg.modules.datashape.service.ITableMessageService;
import org.jeecg.modules.system.constant.TypeConstant;
import org.jeecg.modules.system.model.PropertyDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author xduan
 * @version 2020/5/18
 * @Desc 属性的校验工具
 */
@Component
public class PropertyUtil {

    @Resource
    private ITableMessageService tableMessageService;

    /**
     * 校验属性值，是否和当前的属性类型对应
     * 校验通过，返回一个空字符串，不通过返回信息
     *
     * @param propertyDefinition
     * @return
     */
    public String checkValue(PropertyDefinition propertyDefinition) {
        String baseType = propertyDefinition.getBaseType();
        String value = (String) propertyDefinition.getValue();
        if (StringUtils.isEmpty(value)) {
            //没有设置值，则不需要校验
            return "";
        }
        StringBuilder msg = new StringBuilder();
        //数据值校验不通过的信息
        msg.append("属性：‘").append(propertyDefinition.getName()).append("’ 的值和属性类型对应不上");
        try {
            if (TypeConstant.BOOLEAN.equals(baseType)) {
                //布尔类型
                boolean b = Boolean.parseBoolean(value);
            } else if (TypeConstant.NUMBER.equals(baseType) || TypeConstant.INTEGER.equals(baseType)) {
                double v = Double.parseDouble(value);
            } else if (TypeConstant.JSON.equals(baseType)) {
                //json类型
                JSONObject jsonObject = JSON.parseObject(value);
            } else if (TypeConstant.INFOTABLE.equals(baseType)) {
                //infoTable类型
                boolean b = checkInfoTable(propertyDefinition.getAspects().getDataShape(), value);
                if (!b) {
                    //数据对应不正确
                    return msg.toString();
                }
            }
        } catch (Exception e) {
            return msg.toString();
        }
        return "";
    }

    /**
     * 校验dataShape类型
     *
     * @param dataShape
     * @param value
     */
    private boolean checkInfoTable(String dataShape, String value) {
        Result<Object> result = tableMessageService.findTableAndColumnByTableName(dataShape);
        IotDatashapeModel datashapeModel = (IotDatashapeModel) result.getResult();
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        //属性值的json对象
        JSONObject jsonObject = JSON.parseObject(value);
        Collection columnMsg = datashapeModel.getColumnMsg();
        boolean flag = false;
        for (Object obj : columnMsg) {
            JSONObject json = (JSONObject) JSON.toJSON(obj);
            String name = (String) json.get("name");
            String baseType = (String) json.get("baseType");
            //dataShape 中列名对应的值
            Object o = jsonObject.get(name);
            if (TypeConstant.BOOLEAN.equals(baseType)) {
                //布尔类型
                flag = o instanceof Boolean;
            } else if (TypeConstant.NUMBER.equals(baseType) || TypeConstant.INTEGER.equals(baseType)) {
                //数字类型
                flag = o instanceof Integer || o instanceof Double;
            } else if (TypeConstant.STRING.equals(baseType)){
                //字符串
                flag= o instanceof String;
            }
        }
        return flag;
    }
}
