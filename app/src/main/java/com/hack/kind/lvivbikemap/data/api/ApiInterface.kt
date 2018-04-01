package com.hack.kind.lvivbikemap.data.api

import com.hack.kind.lvivbikemap.domain.model.PointModel
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET

interface ApiInterface {

    @GET("points?android=true")
    fun getPoints(): Single<List<PointModel>>

}
