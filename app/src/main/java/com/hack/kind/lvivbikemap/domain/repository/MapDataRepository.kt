package com.hack.kind.lvivbikemap.domain.repository

import com.hack.kind.lvivbikemap.domain.model.PointModel
import io.reactivex.Single

interface MapDataRepository {
    fun getMapData(): Single<List<PointModel>>
}