package com.hack.kind.lvivbikemap.data.api

import com.hack.kind.lvivbikemap.data.model.PointModel
import io.reactivex.Single
import retrofit2.http.GET

interface ApiInterface {

    @GET("/points")
    fun getPoints(): Single<List<PointModel>>
}
