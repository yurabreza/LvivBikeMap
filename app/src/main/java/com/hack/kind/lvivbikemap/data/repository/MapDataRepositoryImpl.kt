package com.hack.kind.lvivbikemap.data.repository

import com.hack.kind.lvivbikemap.data.api.ApiInterface
import com.hack.kind.lvivbikemap.domain.model.PointModel
import com.hack.kind.lvivbikemap.domain.repository.MapDataRepository
import io.reactivex.Single

class MapDataRepositoryImpl(private val mApi: ApiInterface) : MapDataRepository {

    override fun getMapData(): Single<List<PointModel>> {
        return mApi.getPoints()
    }
}