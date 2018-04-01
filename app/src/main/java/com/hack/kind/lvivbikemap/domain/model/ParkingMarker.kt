package com.hack.kind.lvivbikemap.domain.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ParkingMarker(private val pointModel: PointModel) : ClusterItem {

    override fun getSnippet(): String {
        return pointModel.feature.properties.name
    }

    override fun getTitle(): String {
        return pointModel.feature.properties.name
    }

    override fun getPosition(): LatLng {
        return LatLng(pointModel.feature.geometry.coordinates.first()[1], pointModel.feature.geometry.coordinates.first()[0])
    }
}