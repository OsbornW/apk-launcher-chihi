package com.soya.launcher.http;

import android.content.Context;

import com.soya.launcher.bean.AppInfo;
import com.soya.launcher.bean.City;
import com.soya.launcher.bean.MoviceList;
import com.soya.launcher.bean.RecommendData;
import com.soya.launcher.bean.WeatherData;
import com.soya.launcher.http.response.VersionResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class HttpRequest {
    private static ServiceRequest request;
    private static RecommendServiceRequest recommendListRequest;
    private static AppServiceRequest appServiceRequest;
    private static UIDRequest uidRequest;
    public static void init(Context context){
        request = new ServiceRequest(context);
        recommendListRequest = new RecommendServiceRequest(context);
        appServiceRequest = new AppServiceRequest(context);
        uidRequest = new UIDRequest(context);
    }
    public static Call<ResponseBody> getMoviceList(ServiceRequest.Callback<MoviceList[]> callback){
        return request.getMoviceList(callback);
    }

    public static Call<ResponseBody> getRecommendList(RecommendServiceRequest.Callback<RecommendData> callback, int maxSize){
        return recommendListRequest.getRecommendList(callback, maxSize);
    }

    public static Call getAppList(AppServiceRequest.Callback callback, String userId, String appColumnId, String tag, String word, int page, int maxSize){
        return appServiceRequest.getSearchList(callback, userId, appColumnId, tag, word, page, maxSize);
    }

    public static Call<ResponseBody> getHotCitys(ServiceRequest.Callback<City[]> callback){
        return request.getHotCitys(callback);
    }

    public static Call<ResponseBody> searchCity(ServiceRequest.Callback<City[]> callback, String name){
        return request.searchCity(callback, name);
    }

    public static Call<ResponseBody> getCityWeather(ServiceRequest.Callback<WeatherData> callback, String name){
        return request.getCityWeather(callback, name);
    }

    public static Call<ResponseBody> checkVersion(ServiceRequest.Callback<VersionResponse> callback){
        return request.checkVersion(callback);
    }

    public static Call<ResponseBody> uidPull(AppInfo bean){
        return uidRequest.uidPull(bean);
    }
}
