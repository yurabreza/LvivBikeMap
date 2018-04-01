package com.hack.kind.lvivbikemap.presentation.main.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.hack.kind.lvivbikemap.domain.repository.MapDataRepository
import com.hack.kind.lvivbikemap.presentation.main.view.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

@InjectViewState
class MainPresenter(private val dataRepository: MapDataRepository) : MvpPresenter<MainView>() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun getMapData() {
        dataRepository.getMapData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({ disposable ->
                    addDisposable(disposable)
                    viewState.showLoading()
                })
                .doAfterTerminate({ viewState.hideLoading() })
                .subscribe({ response -> viewState.showMapData(response) },
                        { throwable -> viewState.showMapDataLoadingError(throwable.message!!) })
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