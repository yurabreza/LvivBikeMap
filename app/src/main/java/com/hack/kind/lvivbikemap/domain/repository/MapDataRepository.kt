package com.hack.kind.lvivbikemap.domain.repository

import io.reactivex.Single
import okhttp3.ResponseBody

interface MapDataRepository {
    fun getMapData(): Single<ResponseBody>
}