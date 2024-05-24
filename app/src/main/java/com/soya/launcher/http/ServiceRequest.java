package com.soya.launcher.http;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.soya.launcher.utils.DeviceUuidFactory;
import com.google.gson.Gson;
import com.soya.launcher.App;
import com.soya.launcher.BuildConfig;
import com.soya.launcher.bean.City;
import com.soya.launcher.bean.MoviceList;
import com.soya.launcher.bean.WeatherData;
import com.soya.launcher.config.Config;
import com.soya.launcher.enums.ServiceStatus;
import com.soya.launcher.http.response.HomeResponse;
import com.soya.launcher.http.response.PushResponse;
import com.soya.launcher.http.response.VersionResponse;
import com.soya.launcher.http.ssl.CustomTrustManager;
import com.soya.launcher.http.ssl.SSLSocketClient;
import com.soya.launcher.manager.PreferencesManager;
import com.soya.launcher.utils.AndroidSystem;

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

public class ServiceRequest {
    private static final int DEFAULT_TIMEOUT = 30;
    private static final Gson GSON = new Gson();
    private final Retrofit retrofit;
    private final ServiceHttp request;

    public ServiceRequest(Context context){
        Map<String, String> header = Header.newMap(context);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
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
                .baseUrl(Url.BASE_URL)
                .client(client)
                .build();

        request = retrofit.create(ServiceHttp.class);
    }

    public Call<ResponseBody> getMoviceList(Callback<MoviceList[]> callback){
        Call<ResponseBody> call = request.getMoviceList();
        asyncRequest(call, MoviceList[].class, callback, "getMoviceList");
        return call;
    }

    public Call<ResponseBody> getHotCitys(Callback<City[]> callback){
        Call<ResponseBody> call = request.getHotCitys();
        asyncRequest(call, City[].class, callback, "getHotCitys");
        return call;
    }

    public Call<ResponseBody> searchCity(Callback<City[]> callback, String name){
        Map<String, String> map = new HashMap<>();
        map.put("key", name);
        Call<ResponseBody> call = request.searchCity(map);
        asyncRequest(call, City[].class, callback, "getHotCitys");
        return call;
    }

    public Call<ResponseBody> getCityWeather(Callback<WeatherData> callback, String name){
        Map<String, String> map = new HashMap<>();
        map.put("city", name);
        Call<ResponseBody> call = request.getCityWeather(map);
        asyncRequest(call, WeatherData.class, callback, "getCityWeather");
        return call;
    }

    public Call<ResponseBody> checkVersion(Callback<VersionResponse> callback){
        Map<String, String> map = new HashMap<>();
        map.put("appId", Config.APPID);
        map.put("channel", Config.CHANNEL);
        map.put("chihi_type", Config.CHIHI_TYPE);
        map.put("version", String.valueOf(BuildConfig.VERSION_CODE));
        map.put("sdk", String.valueOf(Build.VERSION.SDK_INT));
        map.put("uid", String.valueOf(DeviceUuidFactory.getUUID(App.getInstance())));
        map.put("model", Build.MODEL);
        map.put("brand", Build.BRAND);
        map.put("product", Build.PRODUCT);
        Call<ResponseBody> call = request.checkVersion(map);
        asyncRequest(call, VersionResponse.class, callback, "checkVersion");
        return call;
    }

    public Call<ResponseBody> pushApps(Callback<PushResponse> callback){
        Map<String, String> map = new HashMap<>();
        map.put("channel", Config.CHANNEL);
        Call<ResponseBody> call = request.pushApps(map);
        asyncRequest(call, PushResponse.class, callback, "pushApps");
        return call;
    }

    public Call<ResponseBody> getHomeContents(Callback<HomeResponse> callback){
        Map<String, String> map = new HashMap<>();
        map.put("channel", Config.CHANNEL);
        map.put("req_id", String.valueOf(PreferencesManager.getRecentlyModified()));
        Call<ResponseBody> call = request.getHomeContents(map);
        asyncRequest(call, HomeResponse.class, callback, "getHomeContents");
        return call;
    }


    public <T> void asyncRequest(Call<ResponseBody> call, Class<T> tClass, Callback<T> callback, String tag){

        if(call!=null){
            call.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    T data = null;
                    try {
                        String json = new String(response.body() != null ? response.body().bytes() : response.errorBody().bytes());
                        Log.e(tag, tag+": "+json);
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

    }

    private static <T> void byCallback(Call<ResponseBody> call, Callback<T> callback, T data, boolean isNetError){
        if (callback != null){
            if (isNetError){
                if(call!=null){
                    callback.onCallback(call, ServiceStatus.STATE_NET_WORK_ERROR, null);

                }
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
