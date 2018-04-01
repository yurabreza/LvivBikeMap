package com.hack.kind.lvivbikemap.data.api

import com.hack.kind.lvivbikemap.domain.model.PointModel
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @GET("points?android=true")
    fun getPoints(): Single<List<PointModel>>

    @POST("/feedback")
    fun postFeedback(@Body feedbackRequest: FeedbackRequest): Single<FeedbackResponse>

}
