package com.hack.kind.lvivbikemap.data.api

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET

interface ApiInterface {

    @GET("points")
  //  fun getPoints(): Single<List<PointModel>>
    fun getPoints(): Single<ResponseBody>
}
