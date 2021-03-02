package org.jeecg.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;


/**
 * @author xduan
 * @version 2020/4/3
 * @Desc 分页助手
 */
public class PageUtil {

    /**
     * 分页
     *
     * @param pageNo   当前页码
     * @param pageSize 每页大小
     * @param data     需要分页的集合
     * @param <T>
     * @return
     */
    public static <T> IPage<T> paging(Integer pageNo, Integer pageSize, List<T> data) {
        if (pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize < 1) {
            pageSize = 0;
        }

        Page<T> page = new Page<T>(pageNo, pageSize);
        int total = data.size();
        page.setTotal(total);
        if (pageNo > page.getPages()) {
            //页码大于总页码，没有数据
            return page;
        }
        //数据起始索引
        int start = (pageNo - 1) * pageSize;
        int toIndex = start + pageSize;
        if (toIndex > data.size()) {
            toIndex = data.size();
        }
        //分页数据
        List<T> records = data.subList(start, toIndex);
        page.setRecords(records);
        return page;
    }
}
