package com.hack.kind.lvivbikemap.presentation.map.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
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
import com.hack.kind.lvivbikemap.*
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
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.osmdroid.bonuspack.kml.KmlDocument
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import javax.inject.Inject
import javax.inject.Provider

class MapActivity : MvpAppCompatActivity(), Drawer.OnDrawerItemClickListener,
        FilterFragment.FiltersSelectedListener, FeedbackFragment.FeedbackSendListener, MapView {

    @Inject
    lateinit var presenterProvider: Provider<MapPresenter>

    @InjectPresenter(type = PresenterType.LOCAL)
    lateinit var presenter: MapPresenter

    @ProvidePresenter(type = PresenterType.LOCAL)
    fun providePresenter(): MapPresenter {
        return presenterProvider.get()
    }

    //    private lateinit var clusterManager: ClusterManager<ParkingMarker>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val parkings = ArrayList<ParkingMarker>()

    private val rentalMarkers = ArrayList<Marker>()
    private val sharingMarkers = ArrayList<Marker>()
    private val repairMarkers = ArrayList<Marker>()
    private val usefulMarkers = ArrayList<Marker>()
    private val interestsMarkers = ArrayList<Marker>()
    private val pathsMarkers = ArrayList<Marker>()

    private var allPoints: ArrayList<PointModel>? = null

    private val allMarkerArrays = arrayListOf(rentalMarkers, sharingMarkers, repairMarkers,
            usefulMarkers, interestsMarkers, pathsMarkers)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(this::onRequestPermissionSuccess, Throwable::printStackTrace)
    }

    private fun onRequestPermissionSuccess(permissionGranted: Boolean) {
        if (permissionGranted) {
            setContentView(R.layout.activity_map)
            setupDrawer()
            initMap()
//                        getPointsFromApi()
            locationPermissionReceived(permissionGranted)
            addKml()
        }
    }

    private fun addKml() = launch(UI) {
        val kmlDocument = KmlDocument()
        async(CommonPool) { kmlDocument.parseKMLUrl(KML_LINK) }.await()
        val kmlOverlay = kmlDocument.mKmlRoot.buildOverlay(osmMap, null, null, kmlDocument) as FolderOverlay
        osmMap.overlays.add(kmlOverlay)
        osmMap.invalidate()
    }


    private fun addFragment(frag: Fragment, tag: String) {
        supportFragmentManager.popBackStack()
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

    lateinit var drawer: Drawer
    private fun setupDrawer() {
        drawer = DrawerBuilder()
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
            MENU_ID_FILTER -> addFragment(getFilterFrag(), FilterFragment::class.java.simpleName)
            MENU_ID_BUILD_ROUTE -> addFragment(SampleCacheDownloader(), SampleCacheDownloader::class.java.simpleName)
            MENU_ID_ADD_MARKER -> Unit
            MENU_ID_EVENTS -> Unit
            MENU_ID_FEED -> Unit
            MENU_ID_SEND_FEEDBACK -> addFragment(FeedbackFragment.newInstance(this), FeedbackFragment::class.java.simpleName)
            MENU_ID_ABOUT_INFO -> addFragment(AboutFragment.newInstance(), AboutFragment::class.java.simpleName)

        }
        return false
    }

    private fun initMap() {
        Marker.ENABLE_TEXT_LABELS_WHEN_NO_IMAGE = true
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        osmMap.setTileSource(TileSourceFactory.MAPNIK)
        osmMap.setBuiltInZoomControls(true)
        osmMap.setMultiTouchControls(true)

        enableRotation()

        gotoLviv()
    }

    private fun enableRotation() {
        val rotationGestureOverlay = RotationGestureOverlay(this, osmMap)
        rotationGestureOverlay.isEnabled = true
        osmMap.setMultiTouchControls(true)
        osmMap.overlays.add(rotationGestureOverlay)
    }

    override fun onResume() {
        super.onResume()
        osmMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        osmMap.onPause()
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

    private fun setupMapStyle() {
        try {
//            osmMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun locationPermissionReceived(granted: Boolean) {
        if (granted) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    osmMap.controller.setZoom(CAMERA_ZOOM_LOCATION)
                    osmMap.controller.setCenter(GeoPoint(it.latitude, it.longitude))
                } else {
                    gotoLviv()
                }
            }
        } else {
            gotoLviv()
        }
    }

    private fun gotoLviv() = gotoLocation(CAMERA_ZOOM_CITY.toDouble(), lvivGeo)

    private fun gotoLocation(zoom: Double, geoPoint: GeoPoint) {
        osmMap.controller.setZoom(zoom)
        osmMap.controller.setCenter(geoPoint)
    }


    override fun showMapData(pointsList: List<PointModel>) {
        allPoints = ArrayList(pointsList)
        sortMarkers(pointsList)
        drawMarkers()

//        parkings += allPoints!!.filter { it.feature.properties.category.id == CategoryType.parking }
//                .map(::ParkingMarker)

//        initCluster(parkings)
    }

    private fun drawMarkers() {
        osmMap.overlays.clear()
        allMarkerArrays.forEach { array -> array.forEach { it -> addMarkerToMap(it) } }
    }

    private fun sortMarkers(pointsList: List<PointModel>) {
        pointsList.forEach {
            when (it.feature.properties.category.id) {
                CategoryType.interests -> interestsMarkers.add(createMarker(it))
//                CategoryType.parking -> parkings.add(createMarker(it))
                CategoryType.path -> pathsMarkers.add(createMarker(it))
                CategoryType.rental -> rentalMarkers.add(createMarker(it))
                CategoryType.repair -> repairMarkers.add(createMarker(it))
                CategoryType.sharing -> sharingMarkers.add(createMarker(it))
                CategoryType.stops -> usefulMarkers.add(createMarker(it))
            }
        }
    }

    private fun addMarkerToMap(it: Marker) = osmMap.overlays.add(it)

    private fun createMarker(it: PointModel) = Marker(osmMap).apply {
        position = GeoPoint(it.feature.geometry.coordinates.first()[1], it.feature.geometry.coordinates.first()[0])
        snippet = it.feature.properties.category.name
        title = it.feature.properties.name
        setIcon(getIcon(it))
    }

//    private fun initCluster(parkings: ArrayList<ParkingMarker>) {
//        clusterManager = ClusterManager(this, map)
//        map.setOnCameraIdleListener(clusterManager)
//        map.setOnMarkerClickListener(clusterManager)
//        map.setOnInfoWindowClickListener(clusterManager)
//        clusterManager.addItems(parkings)
//        clusterManager.cluster()
//    }


    private fun getIcon(it: PointModel): Drawable? {
        return when (it.feature.properties.category.id) {
            CategoryType.interests -> getDrawable(R.drawable.ic_places_of_interest)
            CategoryType.parking -> getDrawable(R.drawable.ic_parking)
//            CategoryType.path -> null
//            CategoryType.rental -> null
            CategoryType.repair -> getDrawable(R.drawable.ic_repair)
            CategoryType.sharing -> getDrawable(R.drawable.ic_bike_sharing)
            CategoryType.stops -> getDrawable(R.drawable.ic_useful_stops)
            else -> getDrawable(R.drawable.ic_pin_drop_black_24dp)

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

//            CategoryType.parking -> {
//                if (!checked && clusterManager != null) {
//                    clusterManager.clearItems()
//                } else {
//                    clusterManager.addItems(parkings)
//                }
//                clusterManager.cluster()
//            }
        }
        drawMarkers()
    }

    private fun manageMarkerArrayVisibility(list: ArrayList<Marker>, checked: Boolean) {
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
        pbLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        pbLoading.visibility = View.GONE
    }

    override fun showFeedbackSendSuccess(response: FeedbackResponse?) {
        Toast.makeText(this, getString(R.string.feedback_send_successfuly), Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
        } else {
            super.onBackPressed()
            fragmentContainer.visibility = View.GONE
            supportFragmentManager.popBackStack()
            toolbarTitle.text = getString(R.string.app_name)
        }
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

        //        const val KML_LINK = "http://mapsengine.google.com/map/kml?forcekml=1&mid=17CFIZP5JAJ7MHn1yImtXLz16kuY"
        const val KML_LINK = "http://www.google.com/maps/d/u/0/kml?forcekml=1&mid=17CFIZP5JAJ7MHn1yImtXLz16kuY"

        const val CAMERA_ZOOM_CITY = 12
        const val CAMERA_ZOOM_LOCATION = 20

        const val LVIV_LAT = 49.839683
        const val LVIV_LNG = 24.029717
        val lvivGeo = GeoPoint(LVIV_LAT, LVIV_LNG)

        const val MENU_ID_FILTER = 1L
        const val MENU_ID_BUILD_ROUTE = 2L
        const val MENU_ID_ADD_MARKER = 3L
        const val MENU_ID_EVENTS = 4L
        const val MENU_ID_FEED = 5L
        const val MENU_ID_SEND_FEEDBACK = 6L
        const val MENU_ID_ABOUT_INFO = 7L
    }
}
