package com.hack.kind.lvivbikemap.data.repository

import com.hack.kind.lvivbikemap.data.api.ApiInterface
import com.hack.kind.lvivbikemap.data.api.FeedbackRequest
import com.hack.kind.lvivbikemap.data.api.FeedbackResponse
import com.hack.kind.lvivbikemap.domain.repository.UserDataRepository
import io.reactivex.Single

class UserDataRepositoryImpl(val api:ApiInterface) : UserDataRepository {

    override fun sendFeedback(feedback: FeedbackRequest): Single<FeedbackResponse> {
       return api.postFeedback(feedback)
    }
}