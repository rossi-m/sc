package com.leyou.order.service;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.dto.AddressDTO;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.enums.OrderStatusEnums;
import com.leyou.order.interceptor.UserInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStautsMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.utils.PayHelper;
import com.leyou.pojo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStautsMapper orderStautsMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private PayHelper payHelper;


    @Autowired
    private GoodsClient goodsClient;

    @Transactional
    public Long createOrder(OrderDTO orderDTO) {

        //1 新增订单
        Order order=new Order();
        //1.1 订单编号，基本信息
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());
        //1.2 用户信息
        UserInfo user= UserInterceptor.get();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);

        //1.3 收货人地址

        AddressDTO addr = AddressClient.findById(orderDTO.getAddressId());
        order.setReceiver(addr.getName());
        order.setReceiverMobile(addr.getPhone());
        order.setReceiverState(addr.getState());
        order.setReceiverCity(addr.getCity());
        order.setReceiverDistrict(addr.getDistrict());
        order.setReceiverAddress(addr.getAddress());
        order.setReceiverZip(addr.getZipCode());

        //1.4 金额
        //把cartDTO转为一个map，key是sku的id，值是num
        Map<Long, Integer> numMap = orderDTO.getCarts().stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        //获取所有sku的id
       Set<Long> ids = numMap.keySet();
        //根据id查询
        List<Sku> skus = goodsClient.querySkuBySkuId(new ArrayList<>(ids));

        //准备orderDetail集合
        List<OrderDetail> details=new ArrayList<>();
        long totalPay=0L;
        for (Sku sku:skus){
            //计算商品总价
            totalPay+=sku.getPrice()*numMap.get(sku.getId());
            //封装orderdetail
            OrderDetail detail=new OrderDetail();
            detail.setImage(StringUtils.substringBefore(sku.getImages(),","));
            detail.setNum(numMap.get(sku.getId()));
            detail.setOrderId(orderId);
            detail.setOwnSpec(sku.getOwnSpec());
            detail.setPrice(sku.getPrice());
            detail.setSkuId(sku.getId());
            detail.setTitle(sku.getTitle());
            details.add(detail);

        }
        order.setTotalPay(totalPay);
        //实付金额：总金额+邮费-优惠金额
        order.setTotalPay(totalPay+order.getPostFee()-0);

        //1.5 order写入数据库
        int count=orderMapper.insertSelective(order);
        if (count !=1){
            log.error("【创建订单】创建订单失败，orderId:{}:",orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);

        }
        //2 新增订单详情
        count=orderDetailMapper.insertList(details);
        if (count !=details.size()){
            log.error("【创建订单】创建订单失败，orderId:{}:",orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);

        }

        //3 新增订单状态
        OrderStatus orderStatus=new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnums.UN_PAY.value());
        count=orderStautsMapper.insertSelective(orderStatus);

        //4 减库存,获取getCarts是个json数据
        List<CartDTO> cartDTOS = orderDTO.getCarts();
        goodsClient.decreaseStock(cartDTOS);

        return orderId;
    }

    public Order queryOrderById(Long id) {
        //查询订单
        Order order = orderMapper.selectByPrimaryKey(id);
        if(order == null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        //查询订单详情
        OrderDetail detail=new OrderDetail();
        detail.setOrderId(id);
        List<OrderDetail> details = orderDetailMapper.select(detail);
        if (CollectionUtils.isEmpty(details)){
            throw new LyException(ExceptionEnum.ORDER_DETAIL_NOT_FOUNT);
        }
        //查询订单状态
        OrderStatus orderStatus = orderStautsMapper.selectByPrimaryKey(id);
        if (order==null){
            throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUNT);
        }
        order.setStatus(orderStatus.getStatus());

        return null ;
    }

    public String createPayUrl(Long orderId) {
        //查询订单
        Order order = queryOrderById(orderId);
        //判断订单状态
        Integer status = order.getStatus();
        if (status!=OrderStatusEnums.UN_PAY.value()){
            //订单状态异常
            log.error("订单状态有误");
            throw new LyException(ExceptionEnum.ORDER_STATUS_ERROR);


        }
        //支付金额
        Long actualPay = order.getActualPay();
        //商品描述一个订单有多个商品就有多个订单详情，就用第一个详情
        OrderDetail detail = order.getOrderDetails().get(0);
        String desc = detail.getTitle();

       return  payHelper.createOrder(orderId,actualPay,desc);    //订单id，订单金额，订单详情
    }

    public void handleNotify(Map<String, String> result) {
        //数据校验
        payHelper.isSuccess(result);

        //校验签名
        payHelper.isValidSign(result.get("sign"));

    }
}
