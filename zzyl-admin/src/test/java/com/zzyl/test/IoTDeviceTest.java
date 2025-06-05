package com.zzyl.test;

import cn.hutool.json.JSONUtil;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class IoTDeviceTest {


    @Autowired
    private IoTDAClient client;

    /**
     * 查询公共实例下的所有产品
     * @throws Exception
     */
    @Test
    public void selectProduceList() throws Exception {

        ListProductsRequest listProductsRequest = new ListProductsRequest();
        listProductsRequest.setLimit(50);
        ListProductsResponse response = client.listProducts(listProductsRequest);
        List<ProductSummary> products = response.getProducts();
        System.out.println(products);
        Page page = response.getPage();
        System.out.println(page.getMarker());
        System.out.println(page.getCount());
    }

    @Test
    public void testGetPro(){
        ShowDeviceShadowRequest showDeviceShadowRequest = new ShowDeviceShadowRequest();
        showDeviceShadowRequest.setDeviceId("67ad95860c504e29c72b2436_watch99");
        ShowDeviceShadowResponse showDeviceShadowResponse = client.showDeviceShadow(showDeviceShadowRequest);
        System.out.println(JSONUtil.toJsonStr(showDeviceShadowResponse));
    }

}