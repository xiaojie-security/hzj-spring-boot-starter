package com.hzj.amap;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hzj.amap.core.webapi.domain.AMapReverseGeoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 高德逆地理编码响应实体兼容性测试。
 */
class AMapReverseGeoResponseTest {

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Test
    void mapsCamelCaseAddressComponent() {
        String json = "{\"regeocode\":{\"formatted_address\":\"江西省南昌市红谷滩区\","
                + "\"addressComponent\":{\"adcode\":\"360113\",\"district\":\"红谷滩区\"}}}";

        AMapReverseGeoResponse response = gson.fromJson(json, AMapReverseGeoResponse.class);

        assertNotNull(response.getRegeocode());
        assertNotNull(response.getRegeocode().getAddressComponent());
        assertEquals("江西省南昌市红谷滩区", response.getRegeocode().getFormattedAddress());
        assertEquals("360113", response.getRegeocode().getAddressComponent().getAdcode());
        assertEquals("红谷滩区", response.getRegeocode().getAddressComponent().getDistrict());
    }
}
