package com.example.spaceflightnews.data;

import com.example.spaceflightnews.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit module for network operations.
 */
public class NetworkModule {
    private static final String BASE_URL = "https://api.spaceflightnewsapi.net/v4/";
    private static Retrofit mRetrofit = null;

    public static SpaceFlightApiService getApiService() {
        if (mRetrofit == null) {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                okHttpClientBuilder.addInterceptor(loggingInterceptor);
            }

            OkHttpClient okHttpClient = okHttpClientBuilder.build();

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit.create(SpaceFlightApiService.class);
    }
}
