package com.hack.kind.lvivbikemap

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hack.kind.lvivbikemap.presentation.map.view.MapActivity
import kotlinx.android.synthetic.main.fragment_osm.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay


class OsmFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_osm, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
    }

    private fun initMap() {
        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))

        osmMap.setTileSource(TileSourceFactory.MAPNIK)
        osmMap.setBuiltInZoomControls(true)
        osmMap.setMultiTouchControls(true)

        enableRotation()

        gotoLviv()
    }

    private fun enableRotation() {
        val rotationGestureOverlay = RotationGestureOverlay(context, osmMap)
        rotationGestureOverlay.isEnabled = true
        osmMap.setMultiTouchControls(true)
        osmMap.overlays.add(rotationGestureOverlay)
    }

    private fun gotoLviv() {
        val mapController = osmMap.controller
        mapController.setZoom(12)
        val startPoint = GeoPoint(MapActivity.LVIV_LAT, MapActivity.LVIV_LNG)
        mapController.setCenter(startPoint)
    }

    override fun onResume() {
        super.onResume()
        osmMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        osmMap.onPause()
    }
}