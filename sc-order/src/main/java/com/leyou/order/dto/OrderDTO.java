package com.leyou.order.dto;

import com.leyou.common.dto.CartDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderDTO {
    @NotNull
    private Long addressId;//收货人地址
    @NotNull
    private Integer paymentType; //付费类型
    @NotNull
    private List<CartDTO> carts; //订单详情
}
