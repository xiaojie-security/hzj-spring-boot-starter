package com.hzj.amap;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hzj.amap.core.webapi.adapter.EmptyArrayAsNullTypeAdapterFactory;
import com.hzj.amap.core.webapi.adapter.FlexibleStringTypeAdapter;
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

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(String.class, new FlexibleStringTypeAdapter())
            .registerTypeAdapterFactory(new EmptyArrayAsNullTypeAdapterFactory())
            .create();

    @Test
    void mapsGeocodeOptionalStringArrays() {
        String json = "{\"count\":\"1\",\"geocodes\":[{"
                + "\"formatted_address\":\"江西省南昌市红谷滩区\","
                + "\"district\":[\"红谷滩区\"],\"township\":[],\"adcode\":\"360113\"}]}";

        AMapGeoResponse response = gson.fromJson(json, AMapGeoResponse.class);

        assertEquals(1, response.getCount());
        assertEquals("红谷滩区", response.getGeocodes().get(0).getDistrict());
        assertNull(response.getGeocodes().get(0).getTownship());
        assertEquals("江西省南昌市红谷滩区", response.getGeocodes().get(0).getFormattedAddress());
    }

    @Test
    void mapsRouteSnakeCaseFields() {
        String json = "{\"route\":{\"paths\":[{\"toll_distance\":\"120\","
                + "\"steps\":[{\"assistant_action\":\"右转\"}]}]}}";

        AMapRouteResponse response = gson.fromJson(json, AMapRouteResponse.class);

        assertEquals("120", response.getRoute().getPaths().get(0).getTollDistance());
        assertEquals("右转", response.getRoute().getPaths().get(0).getSteps().get(0).getAssistantAction());
    }

    @Test
    void mapsWeatherOptionalStringArrays() {
        String json = "{\"lives\":[{\"city\":\"南昌市\",\"temperature\":[],"
                + "\"humidity\":\"68\"}]}";

        AMapWeatherResponse response = gson.fromJson(json, AMapWeatherResponse.class);

        assertEquals("南昌市", response.getLives().get(0).getCity());
        assertNull(response.getLives().get(0).getTemperature());
        assertEquals("68", response.getLives().get(0).getHumidity());
    }
}
