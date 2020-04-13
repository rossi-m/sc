package com.leyou.search.repository;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {
    @Autowired
    ElasticsearchTemplate template;
    @Autowired
    GoodsRepository repository;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SearchService searchService;

    /**
     * 创建索引库
     */
    @Test
    public void testCreateIndex() {
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }

    @Test
    public void loadData() {
        //查询spu
        int page = 1;
        int row = 100;
        int size=0;
        do {
            PageResult<Spu> result = goodsClient.querySpuByPage(page, row, true, null);
            //获取当前页信息
            List<Spu> spuList = result.getItems();
            if (CollectionUtils.isEmpty(spuList)){
                break;
            }
            List<Goods> goods = spuList.stream().map(searchService::buildGoods).collect(Collectors.toList());
            //存入索引库
            repository.saveAll(goods);
            //翻页
            page++;
            size=spuList.size();
        }while (size==100); //等于一百说明差满了，如果没有查满100条数据，说明数据已经查完了
    }
}