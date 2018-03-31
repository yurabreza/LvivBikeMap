package com.hack.kind.lvivbikemap

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_map.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException


class MapActivity : AppCompatActivity(), OnMapReadyCallback, Drawer.OnDrawerItemClickListener {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setupDrawer()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val s = run("http://ec2-52-57-78-207.eu-central-1.compute.amazonaws.com:3000/api/points")
        Log.d("MyTag", "my ${s}")
    }

    fun run(url: String): String? {

        val client = OkHttpClient()

        val request = Request.Builder()
                .url(url)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {

                val js = JSONArray(response?.body()?.string())
                Log.d("MyTag", "my $js")
//                Log.d("MyTag", "my ${response?.body()?.string()}")

            }

            override fun onFailure(call: Call?, e: IOException?) {

            }
        })
        return ""
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
            }
            MENU_ID_ABOUT_INFO -> {
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
