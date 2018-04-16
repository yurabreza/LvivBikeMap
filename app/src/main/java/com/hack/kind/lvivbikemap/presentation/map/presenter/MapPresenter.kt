package com.hack.kind.lvivbikemap.presentation.map.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.hack.kind.lvivbikemap.data.api.FeedbackRequest
import com.hack.kind.lvivbikemap.domain.repository.MapDataRepository
import com.hack.kind.lvivbikemap.domain.repository.UserDataRepository
import com.hack.kind.lvivbikemap.presentation.map.view.MapView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

@InjectViewState
class MapPresenter(private val mapRepo: MapDataRepository, private val userRepo: UserDataRepository) : MvpPresenter<MapView>() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun getMapData() {
        addDisposable(mapRepo.getMapData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showLoading() }
                .doAfterTerminate { viewState.hideLoading() }
                .subscribe({ response -> viewState.showMapData(response) },
                        { throwable -> viewState.showMapDataLoadingError(throwable.message!!) }))
    }

    fun sendFeedback(feedback: FeedbackRequest) {
        addDisposable(userRepo.sendFeedback(feedback)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showLoading() }
                .doAfterTerminate{ viewState.hideLoading() }
                .subscribe({ response -> viewState.showFeedbackSendSuccess(response) },
                        { throwable -> viewState.showFeedbackSendError(throwable.message!!) }))
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