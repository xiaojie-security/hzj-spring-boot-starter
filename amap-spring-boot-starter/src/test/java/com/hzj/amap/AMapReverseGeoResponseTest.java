package com.hzj.amap;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hzj.amap.core.webapi.adapter.EmptyArrayAsNullTypeAdapterFactory;
import com.hzj.amap.core.webapi.domain.AMapReverseGeoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 高德逆地理编码响应实体兼容性测试。
 */
class AMapReverseGeoResponseTest {

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapterFactory(new EmptyArrayAsNullTypeAdapterFactory())
            .create();

    @Test
    void mapsCamelCaseAddressComponent() {
        String json = "{\"regeocode\":{\"formatted_address\":\"江西省南昌市红谷滩区\","
                + "\"addressComponent\":{\"adcode\":\"360113\",\"district\":\"红谷滩区\","
                + "\"streetNumber\":{\"street\":\"会展路\"},"
                + "\"businessAreas\":[{\"name\":\"红谷滩中心区\"}]}}}";

        AMapReverseGeoResponse response = gson.fromJson(json, AMapReverseGeoResponse.class);

        assertNotNull(response.getRegeocode());
        assertNotNull(response.getRegeocode().getAddressComponent());
        assertEquals("江西省南昌市红谷滩区", response.getRegeocode().getFormattedAddress());
        assertEquals("360113", response.getRegeocode().getAddressComponent().getAdcode());
        assertEquals("红谷滩区", response.getRegeocode().getAddressComponent().getDistrict());
        assertEquals("会展路", response.getRegeocode().getAddressComponent().getStreetNumber().getStreet());
        assertEquals("红谷滩中心区", response.getRegeocode().getAddressComponent().getBusinessAreas().get(0).getName());
    }

    @Test
    void treatsMissingAddressComponentArrayAsNull() {
        String json = "{\"regeocode\":{\"formatted_address\":\"江西省南昌市红谷滩区\","
                + "\"addressComponent\":[]}}";

        AMapReverseGeoResponse response = gson.fromJson(json, AMapReverseGeoResponse.class);

        assertNotNull(response.getRegeocode());
        assertEquals("江西省南昌市红谷滩区", response.getRegeocode().getFormattedAddress());
        assertNull(response.getRegeocode().getAddressComponent());
    }
}
