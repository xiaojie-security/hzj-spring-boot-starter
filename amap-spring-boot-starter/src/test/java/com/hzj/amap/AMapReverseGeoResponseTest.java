package com.hzj.amap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzj.amap.core.webapi.adapter.AMapObjectMapperFactory;
import com.hzj.amap.core.webapi.domain.AMapReverseGeoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 高德逆地理编码响应实体兼容性测试。
 */
class AMapReverseGeoResponseTest {

    private final ObjectMapper objectMapper = AMapObjectMapperFactory.create();

    @Test
    void mapsCamelCaseAddressComponent() throws Exception {
        String json = "{\"regeocode\":{\"formatted_address\":\"江西省南昌市红谷滩区\","
                + "\"addressComponent\":{\"adcode\":\"360113\",\"district\":\"红谷滩区\","
                + "\"streetNumber\":{\"street\":\"会展路\"},"
                + "\"businessAreas\":[{\"name\":\"红谷滩中心区\"}]}}}";

        AMapReverseGeoResponse response = objectMapper.readValue(json, AMapReverseGeoResponse.class);

        assertNotNull(response.getRegeocode());
        assertNotNull(response.getRegeocode().getAddressComponent());
        assertEquals("江西省南昌市红谷滩区", response.getRegeocode().getFormattedAddress());
        assertEquals("360113", response.getRegeocode().getAddressComponent().getAdcode());
        assertEquals("红谷滩区", response.getRegeocode().getAddressComponent().getDistrict());
        assertEquals("会展路", response.getRegeocode().getAddressComponent().getStreetNumber().getStreet());
        assertEquals("红谷滩中心区", response.getRegeocode().getAddressComponent().getBusinessAreas().get(0).getName());
    }

    @Test
    void treatsMissingAddressComponentArrayAsNull() throws Exception {
        String json = "{\"regeocode\":{\"formatted_address\":\"江西省南昌市红谷滩区\","
                + "\"addressComponent\":[]}}";

        AMapReverseGeoResponse response = objectMapper.readValue(json, AMapReverseGeoResponse.class);

        assertNotNull(response.getRegeocode());
        assertEquals("江西省南昌市红谷滩区", response.getRegeocode().getFormattedAddress());
        assertNull(response.getRegeocode().getAddressComponent());
    }

    @Test
    void mapsReverseGeoDistrictAndTownshipArrays() throws Exception {
        String json = "{\"regeocode\":{\"formatted_address\":\"江西省南昌市红谷滩区\","
                + "\"addressComponent\":{\"district\":[\"红谷滩区\"],\"township\":[]}}}";

        AMapReverseGeoResponse response = objectMapper.readValue(json, AMapReverseGeoResponse.class);

        assertEquals("红谷滩区", response.getRegeocode().getAddressComponent().getDistrict());
        assertNull(response.getRegeocode().getAddressComponent().getTownship());
    }
}
