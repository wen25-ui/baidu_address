package com.graduation.platform.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.client.RestTemplate;

public class BaiduMapUtil {

    private static final String AK = "YOUR_BAIDU_MAP_AK"; // 替换为你的百度地图API密钥
    private static final String BASE_URL = "http://api.map.baidu.com/";

    private RestTemplate restTemplate;

    public BaiduMapUtil() {
        this.restTemplate = new RestTemplate();
    }

    public JsonObject getLocation(String address) {
        String url = BASE_URL + "geocoding/v3/?address=" + address + "&output=json&ak=" + AK;
        String response = restTemplate.getForObject(url, String.class);
        return JsonParser.parseString(response).getAsJsonObject();
    }

    public JsonObject getRoute(String origin, String destination) {
        String url = BASE_URL + "direction/v2/driving?origin=" + origin + "&destination=" + destination + "&ak=" + AK;
        String response = restTemplate.getForObject(url, String.class);
        return JsonParser.parseString(response).getAsJsonObject();
    }

    public JsonObject getDistance(String origin, String destination) {
        String url = BASE_URL + "routematrix/v2/driving?origins=" + origin + "&destinations=" + destination + "&ak=" + AK;
        String response = restTemplate.getForObject(url, String.class);
        return JsonParser.parseString(response).getAsJsonObject();
    }
}