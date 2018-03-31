package com.hack.kind.lvivbikemap.presentation.main.view

import com.arellomobile.mvp.MvpView
import org.json.JSONObject

interface MainView : MvpView {
    fun showMapData(pointsList:List<JSONObject>)
    fun showMapDataLoadingError(errorMsg: String)
    fun showLoading()
    fun hideLoading()
}