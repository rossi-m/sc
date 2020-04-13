package com.leyou.cart.controller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 *
 */
@Controller
public class CartController {

    @Autowired
    private CartService cartService;


    /**
     * 添加购物车
     * @param cart   json数据类型   num 和 skuId 的json数据
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Cart>> queryCart(){
        List<Cart>carts=cartService.queryCart();
        if (CollectionUtils.isEmpty(carts)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carts);
    }

    /**
     * 修改购物车的商品数量
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping
    public ResponseEntity<Void> updateCartNum(@RequestParam("id")Long skuId,@RequestParam("num")Integer num){
        cartService.updateCartNum(skuId,num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
