package com.hzj.amap;

import com.google.gson.Gson;
import com.hzj.amap.core.webapi.domain.AMapGeoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 高德地理编码响应兼容性测试。
 */
class AMapGeoResponseTest {

    private final Gson gson = new Gson();

    @Test
    void districtStringRemainsString() {
        AMapGeoResponse response = gson.fromJson(responseJson("\"海淀区\""), AMapGeoResponse.class);

        assertEquals("海淀区", response.getGeocodes().get(0).getDistrict());
    }

    @Test
    void districtArrayIsJoined() {
        AMapGeoResponse response = gson.fromJson(responseJson("[\"海淀区\",\"中关村\"]"), AMapGeoResponse.class);

        assertEquals("海淀区,中关村", response.getGeocodes().get(0).getDistrict());
    }

    @Test
    void districtNullRemainsNull() {
        AMapGeoResponse response = gson.fromJson(responseJson("null"), AMapGeoResponse.class);

        assertNull(response.getGeocodes().get(0).getDistrict());
    }

    @Test
    void emptyDistrictArrayBecomesNull() {
        AMapGeoResponse response = gson.fromJson(responseJson("[]"), AMapGeoResponse.class);

        assertNull(response.getGeocodes().get(0).getDistrict());
    }

    private String responseJson(String district) {
        return "{\"geocodes\":[{\"district\":" + district + "}]}";
    }
}
