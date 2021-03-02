package org.jeecg.modules.system.repository;

import org.jeecg.modules.system.entity.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * item数据接口，继承Repository提供的一些子接口，就能具备各种基本的CRUD功能，
 * 类似springData-jpa
 * @author 段鑫扬
 * @version 2020/2/21
 */
@Repository
public interface ItemRepository extends ElasticsearchCrudRepository<Item,Long> {
    public List<Item> findItemsByBrandIsLike(String brandLike);

    Item findByTitle(String title);

    List<Item> findByPriceBetween(double price1, double price2);


    List<Item> findByTitleLike(String title);
}
