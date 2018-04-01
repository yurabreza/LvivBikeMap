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
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.hack.kind.lvivbikemap.AboutFragment
import com.hack.kind.lvivbikemap.FeedbackFragment
import com.hack.kind.lvivbikemap.FilterFragment
import com.hack.kind.lvivbikemap.R
import com.hack.kind.lvivbikemap.data.api.FeedbackRequest
import com.hack.kind.lvivbikemap.data.api.FeedbackResponse
import com.hack.kind.lvivbikemap.domain.model.CategoryType
import com.hack.kind.lvivbikemap.domain.model.ParkingMarker
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

    @Inject
    lateinit var presenterProvider: Provider<MapPresenter>

    @InjectPresenter(type = PresenterType.LOCAL)
    lateinit var presenter: MapPresenter

    @ProvidePresenter(type = PresenterType.LOCAL)
    fun providePresenter(): MapPresenter {
        return presenterProvider.get()
    }

    private lateinit var clusterManager: ClusterManager<ParkingMarker>
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val parkings = ArrayList<ParkingMarker>()

    private val rentalMarkers = ArrayList<MarkerOptions>()
    private val sharingMarkers = ArrayList<MarkerOptions>()
    private val repairMarkers = ArrayList<MarkerOptions>()
    private val usefulMarkers = ArrayList<MarkerOptions>()
    private val interestsMarkers = ArrayList<MarkerOptions>()
    private val pathsMarkers = ArrayList<MarkerOptions>()

    private var allPoints: ArrayList<PointModel>? = null

    private val allMarkerArrays = arrayListOf(rentalMarkers, sharingMarkers, repairMarkers,
            usefulMarkers, interestsMarkers, pathsMarkers)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setupDrawer()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun addFragment(frag: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, frag, tag).addToBackStack(null).commit()
        fragmentContainer.visibility = View.VISIBLE
        updateToolbar(frag)
    }

    private fun updateToolbar(frag: Fragment) {
        when (frag::class.java.simpleName) {
            "FeedbackFragment" -> toolbarTitle.text = getString(R.string.menu_send_feedback)
            "AboutFragment" -> toolbarTitle.text = getString(R.string.menu_about_info)
            "FilterFragment" -> toolbarTitle.text = getString(R.string.menu_filter)
            "SupportMapFragment" -> toolbarTitle.text = getString(R.string.app_name)
        }
    }

    private fun getPointsFromApi() {
        presenter.getMapData()
    }

    private fun setupDrawer() {
        val drawer = DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        PrimaryDrawerItem().withIdentifier(MENU_ID_FILTER).withIcon(R.drawable.ic_filter_list_black_24dp).withName(getString(R.string.menu_filter)),
                        PrimaryDrawerItem().withIdentifier(MENU_ID_BUILD_ROUTE).withIcon(R.drawable.ic_motorcycle_black_24dp).withName(getString(R.string.menu_build_route)),
                        PrimaryDrawerItem().withIdentifier(MENU_ID_ADD_MARKER).withIcon(R.drawable.ic_pin_drop_black_24dp).withName(getString(R.string.menu_add_marker)),
                        PrimaryDrawerItem().withIdentifier(MENU_ID_EVENTS).withIcon(R.drawable.ic_event_black_24dp).withName(getString(R.string.menu_events)),
                        PrimaryDrawerItem().withIdentifier(MENU_ID_FEED).withIcon(R.drawable.ic_dns_black_24dp).withName(getString(R.string.menu_feed)),
                        PrimaryDrawerItem().withIdentifier(MENU_ID_SEND_FEEDBACK).withIcon(R.drawable.ic_feedback_black_24dp).withName(getString(R.string.menu_send_feedback)),
                        PrimaryDrawerItem().withIdentifier(MENU_ID_ABOUT_INFO).withIcon(R.drawable.ic_info_black_24dp).withName(getString(R.string.menu_about_info)))
                .withOnDrawerItemClickListener(this)
                .build()

        menuIv.setOnClickListener { drawer.openDrawer() }
    }

    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>?): Boolean {
        when (drawerItem?.identifier) {
            MENU_ID_FILTER -> {
                addFragment(getFilterFrag(), FilterFragment::class.java.simpleName)
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

    private fun getFilterFrag(): Fragment {
        val frag = FilterFragment.newInstance(this)
        FilterFragment.bikeRepairChecked = allMarkerArrays.contains(repairMarkers)
        FilterFragment.bikeSharingChecked = allMarkerArrays.contains(sharingMarkers)
        FilterFragment.rentalChecked = allMarkerArrays.contains(rentalMarkers)
        FilterFragment.usefulChecked = allMarkerArrays.contains(usefulMarkers)
        FilterFragment.interestChecked = allMarkerArrays.contains(interestsMarkers)
        FilterFragment.pathChecked = allMarkerArrays.contains(pathsMarkers)
        return frag
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getPointsFromApi()
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
        allPoints = ArrayList(pointsList)
        sortMarkers(pointsList)
        drawMarkers()
        allPoints?.forEach {
            if (it.feature.properties.category.id == CategoryType.parking) {
                parkings.add(ParkingMarker(it))
            }
        }
        initCluster(parkings)
    }

    private fun drawMarkers() {
        map.clear()
        allMarkerArrays.forEach { array -> array.forEach { it -> addMarkerToMap(it) } }
    }

    private fun sortMarkers(pointsList: List<PointModel>) {
        pointsList.forEach {
            when (it.feature.properties.category.id) {
                CategoryType.interests -> interestsMarkers.add(createMarker(it))
                CategoryType.parking -> pathsMarkers.add(createMarker(it))
                CategoryType.path -> pathsMarkers.add(createMarker(it))
                CategoryType.rental -> rentalMarkers.add(createMarker(it))
                CategoryType.repair -> repairMarkers.add(createMarker(it))
                CategoryType.sharing -> sharingMarkers.add(createMarker(it))
                CategoryType.stops -> usefulMarkers.add(createMarker(it))
            }
        }
    }

    private fun addMarkerToMap(it: MarkerOptions) {
        map.addMarker(it)
    }

    private fun createMarker(it: PointModel): MarkerOptions {
        return MarkerOptions()
                .position(LatLng(it.feature.geometry.coordinates.first()[1], it.feature.geometry.coordinates.first()[0]))
                .title(it.feature.properties.name).icon(getIcon(it))
    }


    private fun initCluster(parkings: ArrayList<ParkingMarker>) {
        clusterManager = ClusterManager(this, map)
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)
        map.setOnInfoWindowClickListener(clusterManager)
        clusterManager.addItems(parkings)
        clusterManager.cluster()
    }


    private fun getIcon(it: PointModel): BitmapDescriptor? {
        return when (it.feature.properties.category.id) {
            CategoryType.interests -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            CategoryType.parking -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            CategoryType.path -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
            CategoryType.rental -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
            CategoryType.repair -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            CategoryType.sharing -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            CategoryType.stops -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
            else -> {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
            }
        }
    }

    override fun onFiltersSelected(typeId: String, checked: Boolean) {
        Log.d(TAG, "string $typeId")
        when (typeId) {
            CategoryType.interests -> manageMarkerArrayVisibility(interestsMarkers, checked)
            CategoryType.path -> manageMarkerArrayVisibility(pathsMarkers, checked)
            CategoryType.rental -> manageMarkerArrayVisibility(rentalMarkers, checked)
            CategoryType.repair -> manageMarkerArrayVisibility(repairMarkers, checked)
            CategoryType.sharing -> manageMarkerArrayVisibility(sharingMarkers, checked)
            CategoryType.stops -> manageMarkerArrayVisibility(usefulMarkers, checked)

            CategoryType.parking -> {
                if (!checked && clusterManager != null) {
                    clusterManager.clearItems()
                } else {
                    clusterManager.addItems(parkings)
                }
                clusterManager.cluster()
            }
        }
        drawMarkers()
    }

    private fun manageMarkerArrayVisibility(list: ArrayList<MarkerOptions>, checked: Boolean) {
        if (checked && !allMarkerArrays.contains(list)) {
            allMarkerArrays.add(list)
        } else if (allMarkerArrays.contains(list)) {
            allMarkerArrays.remove(list)
        }
    }

    override fun showMapDataLoadingError(errorMsg: String) {
        Log.e("$TAG!!!", errorMsg)
        Toast.makeText(this, getString(R.string.points_load_error), Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        pb_loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        pb_loading.visibility = View.GONE
    }

    override fun showFeedbackSendSuccess(response: FeedbackResponse?) {
        Toast.makeText(this, getString(R.string.feedback_send_successfuly), Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        fragmentContainer.visibility = View.GONE
        toolbarTitle.text = getString(R.string.app_name)
    }

    override fun showFeedbackSendError(eMsg: String) {
        Log.d("$TAG!!!", eMsg)
        Toast.makeText(this, getString(R.string.feedback_send_error), Toast.LENGTH_SHORT).show()
    }

    override fun onFeedbackSend(feedback: FeedbackRequest) {
        Log.d("$TAG!!feedback", "$feedback")
        presenter.sendFeedback(feedback)
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
