package com.hack.kind.lvivbikemap.domain.repository

import com.hack.kind.lvivbikemap.data.api.FeedbackRequest
import com.hack.kind.lvivbikemap.data.api.FeedbackResponse
import io.reactivex.Single

interface UserDataRepository {
    fun sendFeedback(feedback:FeedbackRequest): Single<FeedbackResponse>
}