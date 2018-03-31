package com.hack.kind.lvivbikemap.presentation.main.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.hack.kind.lvivbikemap.domain.repository.MapDataRepository
import com.hack.kind.lvivbikemap.presentation.main.view.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

@InjectViewState
class MainPresenter(private val dataRepository: MapDataRepository) : MvpPresenter<MainView>() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    public fun getMapData() {
        dataRepository.getMapData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({ disposable ->
                    addDisposable(disposable)
                    viewState.showLoading()
                })
                .doAfterTerminate({ viewState.hideLoading() })
                .subscribe({ response -> parseMapData(response) },
                        { throwable -> viewState.showMapDataLoadingError(throwable.message!!) })
    }

    private fun parseMapData(response: ResponseBody?) {
        val jsArray = JSONArray(response.toString())
        val list = ArrayList<JSONObject>()
        if (jsArray != null) {
            val len = jsArray.length()
            for (i in 0 until len) {
                list.add(jsArray.getJSONObject(i))
            }
        }
        viewState.showMapData(list)
    }

    private fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }
}