package com.hack.kind.lvivbikemap.presentation.map.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.hack.kind.lvivbikemap.AboutFragment
import com.hack.kind.lvivbikemap.FeedbackFragment
import com.hack.kind.lvivbikemap.FilterFragment
import com.hack.kind.lvivbikemap.R
import com.hack.kind.lvivbikemap.data.api.FeedbackRequest
import com.hack.kind.lvivbikemap.data.api.FeedbackResponse
import com.hack.kind.lvivbikemap.domain.model.PointModel
import com.hack.kind.lvivbikemap.presentation.map.presenter.MapPresenter
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_map.*
import javax.inject.Inject
import javax.inject.Provider

class MapActivity : MvpAppCompatActivity(), OnMapReadyCallback, Drawer.OnDrawerItemClickListener, FilterFragment.FiltersSelectedListener, FeedbackFragment.FeedbackSendListener, MapView {

    override fun onFeedbackSend(feedback: FeedbackRequest) {
        Log.d("$TAG!!feedback", "$feedback")
        presenter.sendFeedback(feedback)
    }

    override fun onFiltersSelected() {
        //    TODO() //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var presenterProvider: Provider<MapPresenter>

    @InjectPresenter(type = PresenterType.LOCAL)
    lateinit var presenter: MapPresenter

    @ProvidePresenter(type = PresenterType.LOCAL)
    fun providePresenter(): MapPresenter {
        return presenterProvider.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setupDrawer()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getPointsFromApi()
    }

    private fun addFragment(frag: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, frag, tag).addToBackStack(null).commit()
    }

    private fun getPointsFromApi() {
        presenter.getMapData()
    }

    private fun setupDrawer() {
        val drawer = DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        PrimaryDrawerItem().withIdentifier(MENU_ID_FILTER).withIcon(R.drawable.ic_list_black_24dp).withName(getString(R.string.menu_filter)),
                        PrimaryDrawerItem().withIdentifier(MENU_ID_BUILD_ROUTE).withIcon(R.drawable.ic_motorcycle_black_24dp).withName(getString(R.string.menu_build_route)),
                        PrimaryDrawerItem().withIdentifier(MENU_ID_ADD_MARKER).withName(getString(R.string.menu_add_marker)),
                        PrimaryDrawerItem().withIdentifier(MENU_ID_EVENTS).withIcon(R.drawable.ic_event_black_24dp).withName(getString(R.string.menu_events)),
                        PrimaryDrawerItem().withIdentifier(MENU_ID_FEED).withName(getString(R.string.menu_feed)),
                        PrimaryDrawerItem().withIdentifier(MENU_ID_SEND_FEEDBACK).withIcon(R.drawable.ic_feedback_black_24dp).withName(getString(R.string.menu_send_feedback)),
                        PrimaryDrawerItem().withIdentifier(MENU_ID_ABOUT_INFO).withIcon(R.drawable.ic_info_black_24dp).withName(getString(R.string.menu_about_info)))
                .withOnDrawerItemClickListener(this)
                .build()

        menuIv.setOnClickListener { drawer.openDrawer() }
    }

    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>?): Boolean {
        when (drawerItem?.identifier) {
            MENU_ID_FILTER -> {
                addFragment(FilterFragment.newInstance(this), FilterFragment::class.java.simpleName)
            }
            MENU_ID_BUILD_ROUTE -> {
            }
            MENU_ID_ADD_MARKER -> {
            }
            MENU_ID_EVENTS -> {
            }
            MENU_ID_FEED -> {
            }
            MENU_ID_SEND_FEEDBACK -> {
                addFragment(FeedbackFragment.newInstance(this), FeedbackFragment::class.java.simpleName)
            }
            MENU_ID_ABOUT_INFO -> {
                addFragment(AboutFragment.newInstance(), AboutFragment::class.java.simpleName)
            }
        }
        return false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setupMapStyle()
        requestUserLocation()
    }

    private fun setupMapStyle() {
        try {
            val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun requestUserLocation() {
        RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(this::locationPermissionReceived)

    }

    @SuppressLint("MissingPermission")
    private fun locationPermissionReceived(granted: Boolean) {
        if (granted) {
            map.isMyLocationEnabled = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), CAMERA_ZOOM_LOCATION))
                } else {
                    gotoLviv()
                }
            }
        } else {
            gotoLviv()
        }
    }

    private fun gotoLviv() {
        val lviv = LatLng(LVIV_LAT, LVIV_LNG)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lviv, CAMERA_ZOOM_CITY))
    }

    override fun showMapData(pointsList: List<PointModel>) {
        Log.d("$TAG!!!", pointsList.toString())
        Toast.makeText(this, getString(R.string.points_load_successfuly), Toast.LENGTH_SHORT).show()
    }

    override fun showMapDataLoadingError(errorMsg: String) {
        Log.e("$TAG!!!", errorMsg)
        Toast.makeText(this, getString(R.string.points_load_error), Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        pb_loading.visibility=View.VISIBLE
    }

    override fun hideLoading() {
        pb_loading.visibility=View.GONE
    }

    override fun showFeedbackSendSuccess(response: FeedbackResponse?) {
        Toast.makeText(this, getString(R.string.feedback_send_successfuly), Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    override fun showFeedbackSendError(eMsg: String) {
        Log.d("$TAG!!!", eMsg)
        Toast.makeText(this, getString(R.string.feedback_send_error), Toast.LENGTH_SHORT).show()
    }

    companion object {
        val TAG = MapActivity::class.java.simpleName!!

        const val CAMERA_ZOOM_CITY = 12f
        const val CAMERA_ZOOM_LOCATION = 20f

        const val LVIV_LAT = 49.839683
        const val LVIV_LNG = 24.029717

        const val MENU_ID_FILTER = 1L
        const val MENU_ID_BUILD_ROUTE = 2L
        const val MENU_ID_ADD_MARKER = 3L
        const val MENU_ID_EVENTS = 4L
        const val MENU_ID_FEED = 5L
        const val MENU_ID_SEND_FEEDBACK = 6L
        const val MENU_ID_ABOUT_INFO = 7L
    }
}
