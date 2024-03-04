package com.soya.launcher.http;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by ZTMIDGO 2023/1/22
 */
public interface ServiceHttp {
    @GET(Url.MOVICE_LIST)
    Call<ResponseBody> getMoviceList();

    @GET(Url.RECOMMEND_LIST)
    Call<ResponseBody> getRecommendList();

    @GET(Url.SEARCH_LIST)
    Call<ResponseBody> getSearchList();

    @GET(Url.HOT_CITY)
    Call<ResponseBody> getHotCitys();

    @GET(Url.SEARCH_CITY)
    Call<ResponseBody> searchCity(@QueryMap Map<String, String> map);

    @GET(Url.CITY_WEATHER)
    Call<ResponseBody> getCityWeather(@QueryMap Map<String, String> map);

    @GET(Url.CHECK_VERSION)
    Call<ResponseBody> checkVersion(@QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST(Url.APP_LIST)
    Call<ResponseBody> appList(@FieldMap Map<String, String> map, @QueryMap Map<String, String> query);

    @POST(Url.UID_PULL)
    Call<ResponseBody> uidPull(@Body Map<String, String> map);

    @GET(Url.PUSH_APPS)
    Call<ResponseBody> pushApps(@QueryMap Map<String, String> map);

    @GET(Url.HOME_MAIN_CONTENT)
    Call<ResponseBody> getHomeContents(@QueryMap Map<String, String> map);
}
