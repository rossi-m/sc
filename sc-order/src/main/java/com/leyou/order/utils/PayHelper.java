package com.leyou.order.utils;

import com.github.wxpay.sdk.WXPay;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.order.config.PayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static com.github.wxpay.sdk.WXPayConstants.*;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class PayHelper {
    @Autowired
    private WXPay wxPay;
    @Autowired
    private PayConfig config;
    public String createOrder(Long orderId, Long totalPay, String desc) {
        try {
        // 准备请求参数
        HashMap<String, String> data = new HashMap<>();
        //商品描述
        data.put("body", desc);
        //订单号
        data.put("out_trade_no", orderId.toString());
        //金额，单位是分
        data.put("total_fee", totalPay.toString());
        //调用微信支付的终端ip
        data.put("spbill_create_ip", "127.0.0.1");
        //回调地址
        data.put("notify_url",config.getNotifyUrl());
        //交易类型为扫码支付
        data.put("trade_type", "NATIVE");


        //利用wxPay工具，完成下单
        Map<String, String> result = wxPay.unifiedOrder(data);
        //判断通信和业务标识
            isSuccess(result);

            //下单成功，获取支付链接
        String url= result.get("code_url");
        return url;
        } catch (Exception e) {
            log.error("【微信下单】下单失败，订单号:{}", orderId, e);
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
        }
    }

   public void isSuccess(Map<String, String> result) {
        //判断通信标识
        String returnCode=result.get("return_code");
        if (FAIL.equals(returnCode)){
               //通信失败
               log.error("【微信下单】微信下单通信失败，错误原因：{}",result.get("return_msg"));  //return_msg 固定的
               throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
           }
        //判断业务标识
        String resultCode=result.get("result_code");
        if (FAIL.equals(returnCode)){
            //通信失败
            log.error("【微信下单】微信下单业务失败，错误码：{}",result.get("err_code"),result.get("error_code_des"));
            throw new LyException(ExceptionEnum.WX_PAY_ORDER_FAIL);
    }
    }

    public void isValidSign(String sign) {
        //重新生成签名
    }
}
