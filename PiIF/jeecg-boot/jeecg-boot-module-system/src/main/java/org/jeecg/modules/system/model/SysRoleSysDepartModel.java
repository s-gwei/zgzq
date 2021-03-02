package org.jeecg.modules.system.model;

import lombok.Data;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysRole;

/**
 * 包含 SysUser 和 SysDepart 的 Model
 *
 * @author sunjianlei
 */
@Data
public class SysRoleSysDepartModel {

    private SysRole sysRole;
    private SysDepart sysDepart;

}
