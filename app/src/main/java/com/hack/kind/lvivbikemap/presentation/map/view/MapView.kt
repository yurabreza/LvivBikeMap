package com.hack.kind.lvivbikemap.presentation.map.view

import com.arellomobile.mvp.MvpView
import com.hack.kind.lvivbikemap.data.api.FeedbackResponse
import com.hack.kind.lvivbikemap.domain.model.PointModel

interface MapView : MvpView {
    fun showMapData(pointsList:List<PointModel>)
    fun showMapDataLoadingError(errorMsg: String)
    fun showLoading()
    fun hideLoading()
    fun showFeedbackSendSuccess(response: FeedbackResponse?)
    fun showFeedbackSendError(eMsg: String)
}