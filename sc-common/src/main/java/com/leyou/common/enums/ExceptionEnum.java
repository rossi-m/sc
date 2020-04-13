package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    PRICE_CANNT_BE_NULL(400,"价格不能为空"),//相当于单例的实例化
    CATEGORY_NOT_FOUND(404,"商品信息没有查到"),
    BRAND_NOT_FOUND(404,"品牌信息没有查到"),
    SPEC_GROUP_NOT_FOUND(404,"商品规格组没有查到"),
    SPEC_PARAM_NOT_FOUND(404,"商品规格参数查询失败"),
    GOODS_NOT_FOUND(404,"商品不存在"),
    GOODS_DETAIL_NOT_FOUND(404,"商品详情不存在"),
    GOODS_SKU_NOT_FOUND(404,"商品sku不存在"),
    GOODS_STOCK_NOT_FOUND(404,"商品库存不存在"),
    BRAND_SAVE_ERROR(500,"添加品牌失败"),
    UPLOAD_ERROR(500,"文件上传失败"),
    INVALID_FILE_TYPE(500,"无效的文件类型"),
    GOOODS_SAVE_ERROR(500,"新增商品失败"),
    GOODS_UPDATE_ERROR(500,"更新商品失败"),
    GOODS_ID_CANNOT_BE_NULL(400,"商品id不能为空"),
   INVALID_USER_DATA_TYPE(400,"数据类型无效"),
    INVALID_VERIFY_CODE(400, "无效的验证码"),
    INVALID_USERNAME_PASSWORD(400, "用户名或密码错误"),
    UN_AUTHORIZE(403,"未授权用户"),
    CREATE_ORDER_ERROR(500,"购物车为空"),
   CART_NOT_FOUND(404,"购物车查找失败"),
    STOCK_NOT_ENOUGH(500,"库存不足"),
    ORDER_NOT_FOUND(404,"订单不存在"),
    ORDER_DETAIL_NOT_FOUNT(404,"订单详情不存在"),
    ORDER_STATUS_NOT_FOUNT(404,"订单状态不存在"),
    WX_PAY_ORDER_FAIL(500,"微信支付失败"),
    ORDER_STATUS_ERROR(500,"订单状态异常")
    ;
    private int code;
    private String msg;
}
