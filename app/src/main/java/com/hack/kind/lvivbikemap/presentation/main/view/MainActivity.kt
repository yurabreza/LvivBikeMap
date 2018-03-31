package com.hack.kind.lvivbikemap.presentation.main.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.hack.kind.lvivbikemap.R
import com.hack.kind.lvivbikemap.data.api.ApiInterface
import com.hack.kind.lvivbikemap.presentation.main.presenter.MainPresenter
import dagger.android.AndroidInjection
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : AppCompatActivity(), MainView {

    @Inject
    lateinit var api: ApiInterface

    @Inject
    lateinit var presenterProvider: Provider<MainPresenter>

    @InjectPresenter(type = PresenterType.LOCAL)
    var presenter: MainPresenter? = null

    @ProvidePresenter(type = PresenterType.LOCAL)
    fun providePresenter(): MainPresenter {
        return presenterProvider.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter?.getMapData()
    }

    override fun showMapData(pointsList: List<JSONObject>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMapDataLoadingError(errorMsg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
