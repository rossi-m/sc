package com.leyou.cart.service;

import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import com.leyou.pojo.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;
    public void addCart(Cart cart) {
        UserInfo userInfo = LoginInterceptor.get();
        //先查询
        //绑定用户
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(userInfo.getId().toString());
        String skuId = cart.getSkuId().toString();
        Integer num = cart.getNum();
        //判断userId用户的购物车中是否有商品skuId
        if (hashOperations.hasKey(skuId)){//确定绑定键上是否存在给定的哈希{@code key}。
            //判单是否有skuId，添加商品为skuId的数量
            String cartJson = hashOperations.get(skuId).toString();//相当于这种数据类型：Map<String , Map<String ,String >>skuid相当于第二个string
            cart = JsonUtils.toBean(cartJson, Cart.class);
            cart.setNum(num+cart.getNum());

        }else {
            //redis数据库没有商品信息，新增购物车商品
            cart.setUserId(userInfo.getId());
            //查询商品信息
           Sku sku = goodsClient.querySkuById(cart.getSkuId());
           cart.setImage(StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);
           cart.setOwnSpec(sku.getOwnSpec());
           cart.setTitle(sku.getTitle());
           cart.setNum(num);
        }
        hashOperations.put(skuId,JsonUtils.toString(cart));

    }

    public List<Cart> queryCart() {
        //获取用户的信息
        UserInfo userInfo = LoginInterceptor.get();
        //判断redis数据库是否有hash类型值。判断购物车是否有物品
        if (this.redisTemplate.hasKey(userInfo.getId().toString())){
            return null;
        }

        //查询
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(userInfo.getId().toString());
        //获取绑定键处哈希的项集（值）。
        List<Object> cartJsons = hashOperations.values();

        return cartJsons.stream().map(cartJson -> JsonUtils.toBean(cartJson.toString(),Cart.class)).collect(Collectors.toList());


    }

    public void updateCartNum(Long skuId, Integer num) {
        //获取用户
        UserInfo userInfo = LoginInterceptor.get();
        //获取操作
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(userInfo.getId().toString());
        if (!operations.hasKey(skuId.toString())){
            throw  new LyException(ExceptionEnum.CART_NOT_FOUND);
        }

        //查询
        Cart cart = JsonUtils.toBean(operations.get(skuId.toString()).toString(), Cart.class);
        cart.setNum(num);
        //把跟新了num的Cart对象存入到redis数据库
        operations.put(skuId.toString(),JsonUtils.toString(cart));

    }
}
