package com.soya.launcher.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.soya.launcher.bean.AppInfo;
import com.soya.launcher.enums.ServiceStatus;
import com.soya.launcher.http.response.AppListResponse;
import com.soya.launcher.http.ssl.CustomTrustManager;
import com.soya.launcher.http.ssl.SSLSocketClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UIDRequest {
    private static final int DEFAULT_TIMEOUT = 30;
    private static final Gson GSON = new Gson();
    private final Retrofit retrofit;
    private final ServiceHttp request;

    public UIDRequest(Context context){
        Map<String, String> header = Header.newMap(context);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), new CustomTrustManager())
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder();
                        for (Map.Entry<String, String> entry : header.entrySet()){
                            requestBuilder.addHeader(entry.getKey(), entry.getValue());
                        }
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Url.UID_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        request = retrofit.create(ServiceHttp.class);
    }

    public Call<ResponseBody> uidPull(AppInfo bean){
        Map<String, String> map = new HashMap<>();
        map.put("appName", bean.getAppName());
        map.put("appQdName", bean.getAppQdName());
        map.put("appVersion", bean.getAppVersion());
        map.put("mac", bean.getMac());
        map.put("ip", bean.getIp());
        map.put("sdkVersion", bean.getSdkVersion());
        map.put("imei", bean.getImei());
        map.put("randomStr", bean.getRandomStr());
        Call<ResponseBody> call = request.uidPull(map);
        asyncRequest(call, String.class, null, "uidPull");
        return call;
    }



    public <T> void asyncRequest(Call<ResponseBody> call, Class<T> tClass, Callback<T> callback, String tag){
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                T data = null;
                try {
                    String json = new String(response.body() != null ? response.body().bytes() : response.errorBody().bytes());
                    data = GSON.fromJson(json, tClass);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    byCallback(call, callback, data, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                byCallback(call, callback, null, true);
                t.printStackTrace();
            }
        });
    }

    private static <T> void byCallback(Call<ResponseBody> call, Callback<T> callback, T data, boolean isNetError){
        if (callback != null){
            if (isNetError){
                callback.onCallback(call, ServiceStatus.STATE_NET_WORK_ERROR, null);
            }else {
                if (data == null){
                    callback.onCallback(call, ServiceStatus.STATE_SERVICE_ERROR, null);
                }else {
                    callback.onCallback(call, ServiceStatus.STATE_SUCCESS, data);
                }
            }
        }
    }

    public interface Callback<T>{
        void onCallback(Call call, int status, T result);
    }
}
