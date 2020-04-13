package com.leyou.order.client;

import com.leyou.order.dto.AddressDTO;

import java.util.ArrayList;
import java.util.List;

public abstract class AddressClient {
    public static final List<AddressDTO> addressList=new ArrayList<AddressDTO>() {
        {
            AddressDTO address = new AddressDTO();
            address.setId(1l);
            address.setAddress("张家石");
            address.setCity("抚州");
            address.setDistrict("南城县");
            address.setName("毛哥");
            address.setPhone("15707943954");
            address.setState("上海");
            address.setZipCode("360360");
            address.setIsDefault(true);
            add(address);

            AddressDTO address2 = new AddressDTO();
            address.setId(2l);
            address.setAddress("石鹯村");
            address.setCity("深圳");
            address.setDistrict("龙岗区");
            address.setName("小阿毛");
            address.setPhone("15707943954");
            address.setState("广东");
            address.setZipCode("123123");
            address.setIsDefault(false);
            add(address);

        }
    };
    public static AddressDTO findById(Long id ){
        for (AddressDTO addressDTO: addressList){
            if (addressDTO.getId()==id)
                return addressDTO;
        }
        return null;

    }
}
