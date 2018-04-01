package com.hack.kind.lvivbikemap

import com.cube.geojson.GeoJson
import com.cube.geojson.GeoJsonObject
import com.cube.geojson.LngLatAlt
import com.cube.geojson.gson.GeoJsonObjectAdapter
import com.cube.geojson.gson.LngLatAltAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hack.kind.lvivbikemap.data.api.ApiInterface
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


@Module
class MainActivityModule {

    @Provides
    @MainActivityScope
    fun provideClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                //        .addInterceptor(serverAuthInterceptor)
                .build()
    }

    @Provides
    @MainActivityScope
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
    }

    @Provides
    @MainActivityScope
    fun provideGson(): Gson {
//        gson.registerTypeAdapter(PointModel::class.java, PointAdapter())
        val builder = GsonBuilder()
        builder.registerTypeAdapter(GeoJsonObject::class.java, GeoJsonObjectAdapter())
        builder.registerTypeAdapter(LngLatAlt::class.java, LngLatAltAdapter())
        return GeoJson.getGson()
    }

    @Provides
    @MainActivityScope
    fun provideApi(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
}