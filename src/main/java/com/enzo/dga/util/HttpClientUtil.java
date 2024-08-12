package com.enzo.dga.util;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HttpClientUtil {

    private static OkHttpClient httpClient = new OkHttpClient();

    public static String get(String url) {
        try {
            // 创建Request对象
            Request.Builder builder = new Request.Builder();
            Request request = builder
                    .get()
                    .url(url)
                    .build();

            // 创建Call对象
            Call call = httpClient.newCall(request);

            // 发送请求
            Response response = call.execute();

            // 从响应中获取结果
            String result = response.body().string();

            // 返回结果
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("发送请求失败...");
        }
    }

    public static void main(String[] args) {
        String result = get("http://fastfood102:18080/api/v1/applications/application_1723080522679_0007");
        System.out.println(result);
    }
}
