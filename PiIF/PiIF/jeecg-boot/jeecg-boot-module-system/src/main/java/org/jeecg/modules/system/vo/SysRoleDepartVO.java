package org.jeecg.modules.system.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 角色关联部门
 * @Author: jeecg-boot
 * @Date:   2020-02-20
 * @Version: V1.0
 */
@Data
@TableName("sys_role_depart")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_role_depart对象", description="角色关联部门")
public class SysRoleDepartVO implements Serializable{
	private static final long serialVersionUID = 1L;
	/**部门id*/
	private String departId;
	/**角色id集合*/
	private List<String> roleIdList;

	public SysRoleDepartVO() {
		super();
	}

	public SysRoleDepartVO(String departId, List<String> roleIdList) {
		super();
		this.departId = departId;
		this.roleIdList = roleIdList;
	}
}
