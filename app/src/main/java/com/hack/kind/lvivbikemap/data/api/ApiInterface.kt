package com.hack.kind.lvivbikemap.data.api

import com.cube.geojson.GeoJsonObject
import io.reactivex.Single
import retrofit2.http.GET

interface ApiInterface {

    @GET("points")
    fun getPoints(): Single<List<GeoJsonObject>>
//    fun getPoints(): Single<ResponseBody>
}
