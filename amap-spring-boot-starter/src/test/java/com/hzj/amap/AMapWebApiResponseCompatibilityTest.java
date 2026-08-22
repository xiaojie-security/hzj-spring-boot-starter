package com.hzj.amap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzj.amap.core.webapi.adapter.AMapObjectMapperFactory;
import com.hzj.amap.core.webapi.domain.AMapGeoResponse;
import com.hzj.amap.core.webapi.domain.AMapRouteResponse;
import com.hzj.amap.core.webapi.domain.AMapWeatherResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 高德 Web API 多种返回形态兼容性测试。
 */
class AMapWebApiResponseCompatibilityTest {

    private final ObjectMapper objectMapper = AMapObjectMapperFactory.create();

    @Test
    void mapsGeocodeOptionalStringArrays() throws Exception {
        String json = "{\"count\":\"1\",\"geocodes\":[{"
                + "\"formatted_address\":\"江西省南昌市红谷滩区\","
                + "\"district\":[\"红谷滩区\"],\"township\":[],\"adcode\":\"360113\"}]}";

        AMapGeoResponse response = objectMapper.readValue(json, AMapGeoResponse.class);

        assertEquals(1, response.getCount());
        assertEquals("红谷滩区", response.getGeocodes().get(0).getDistrict());
        assertNull(response.getGeocodes().get(0).getTownship());
        assertEquals("江西省南昌市红谷滩区", response.getGeocodes().get(0).getFormattedAddress());
    }

    @Test
    void mapsRouteSnakeCaseFields() throws Exception {
        String json = "{\"route\":{\"paths\":[{\"toll_distance\":\"120\","
                + "\"steps\":[{\"assistant_action\":\"右转\"}]}]}}";

        AMapRouteResponse response = objectMapper.readValue(json, AMapRouteResponse.class);

        assertEquals("120", response.getRoute().getPaths().get(0).getTollDistance());
        assertEquals("右转", response.getRoute().getPaths().get(0).getSteps().get(0).getAssistantAction());
    }

    @Test
    void mapsWeatherOptionalStringArrays() throws Exception {
        String json = "{\"lives\":[{\"city\":\"南昌市\",\"temperature\":[],"
                + "\"humidity\":\"68\"}]}";

        AMapWeatherResponse response = objectMapper.readValue(json, AMapWeatherResponse.class);

        assertEquals("南昌市", response.getLives().get(0).getCity());
        assertNull(response.getLives().get(0).getTemperature());
        assertEquals("68", response.getLives().get(0).getHumidity());
    }
}
