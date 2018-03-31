package com.hack.kind.lvivbikemap.presentation.main.view

import android.os.Bundle
import android.util.Log
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.hack.kind.lvivbikemap.R
import com.hack.kind.lvivbikemap.presentation.main.presenter.MainPresenter
import dagger.android.AndroidInjection
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : MvpAppCompatActivity(), MainView {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    @Inject
    lateinit var presenterProvider: Provider<MainPresenter>

    @InjectPresenter(type = PresenterType.LOCAL)
    lateinit var presenter: MainPresenter

    @ProvidePresenter(type = PresenterType.LOCAL)
    fun providePresenter(): MainPresenter {
        return presenterProvider.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.getMapData()
    }

    override fun showMapData(pointsList: List<JSONObject>) {
        Log.d(TAG, pointsList.toString())
        // TODO implement
    }

    override fun showMapDataLoadingError(errorMsg: String) {
        // TODO implement
    }

    override fun showLoading() {
        // TODO implement
    }

    override fun hideLoading() {
        // TODO implement
    }

}
