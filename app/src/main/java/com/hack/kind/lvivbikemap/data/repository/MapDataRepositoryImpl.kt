package com.hack.kind.lvivbikemap.data.repository

import com.hack.kind.lvivbikemap.data.api.ApiInterface
import com.hack.kind.lvivbikemap.domain.repository.MapDataRepository
import io.reactivex.Single
import okhttp3.ResponseBody

class MapDataRepositoryImpl(private val mApi: ApiInterface) : MapDataRepository {

    override fun getMapData(): Single<ResponseBody> {
        return mApi.getPoints()
    }
}