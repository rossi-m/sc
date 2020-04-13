package com.leyou.page.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;

@FeignClient(value = "item-service")

public interface BrandClient extends BrandApi {
}
