package com.leyou.search.clietn;

import com.leyou.item.pojo.Category;
import com.leyou.search.client.CategoryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {
    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void queryCategoryByIds(){
        List<Category> list = categoryClient.queryCategoryByIds(Arrays.asList(1l, 2l, 3l));
        for (Category category : list) {
            System.out.println(category);
        }
    }

}
