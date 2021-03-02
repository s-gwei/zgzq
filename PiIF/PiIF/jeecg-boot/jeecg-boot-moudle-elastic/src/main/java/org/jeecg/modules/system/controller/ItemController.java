package org.jeecg.modules.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.system.entity.Item;
import org.jeecg.modules.system.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xduan
 * @version 2020/2/26
 */
@Slf4j
@RestController
@RequestMapping("/item")
@Api("item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 测试保存（创建）新的映射
     * @return
     */
    @PostMapping("/save")
    @ApiOperation("save")
    public Result<?> save(@RequestBody Item item){
        //Item item = new Item(1L,"小米10","手机","小米",3999.00,"http://image.baidu.com/13123.jpg");
        itemService.save(item);
        return Result.ok("保存成功");
    }
}
