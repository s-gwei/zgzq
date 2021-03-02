package org.jeecg.modules.system.service.impl;

import org.jeecg.modules.system.entity.Item;
import org.jeecg.modules.system.repository.ItemRepository;
import org.jeecg.modules.system.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xduan
 * @version 2020/2/26
 * item的服务类
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    /**
     * 新增对象
     * @param item
     */
    @Override
    public void save(Item item) {
        //新增对象
        itemRepository.save(item);
    }
}
