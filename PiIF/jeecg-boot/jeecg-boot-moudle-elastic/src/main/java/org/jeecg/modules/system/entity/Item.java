package org.jeecg.modules.system.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author xduan
 * @version 2020/2/21
 * indexName 索引库名称
 * type 映射类型
 * 标记为文档类型
 * 注意：必须有无参构造方法
 */
@Data
@Document(indexName = "item", type = "docs")
public class Item implements Serializable {

    private static final long serialVersionUID = 5598979684862924875L;

    //主键
    @Id
    private Long id;
    /**
     * 标题
     * <p>
     * FieldType，可以是text、long、short、date、integer等
     * text：存储数据时候，会自动分词，并生成索引
     * keyword：存储数据时候，不会分词建立索引　　analyzer:分词器名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;
    // 分类
    @Field(type = FieldType.Keyword)
    private String category;
    // 品牌
    @Field(type = FieldType.Keyword)
    private String brand;
    // 价格
    @Field(type = FieldType.Double)
    private Double price;
    // 图片地址  index:是否索引
    @Field(index = false, type = FieldType.Keyword)
    private String images;

    public Item() {
    }

    public Item(Long id, String title, String category, String brand, Double price, String images) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.images = images;
    }
}
