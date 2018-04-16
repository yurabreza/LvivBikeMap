package com.hack.kind.lvivbikemap.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hack.kind.lvivbikemap.app.App
import com.hack.kind.lvivbikemap.data.api.ApiInterface
import com.hack.kind.lvivbikemap.domain.model.PointModel
import com.hack.kind.lvivbikemap.domain.repository.MapDataRepository
import com.nytimes.android.external.fs3.SourcePersisterFactory
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.middleware.GsonParserFactory
import com.nytimes.android.external.store3.middleware.GsonTransformerFactory
import io.reactivex.Single
import okio.BufferedSource

class MapDataRepositoryImpl(private val mApi: ApiInterface,private val gson: Gson) : MapDataRepository {

    val type = object : TypeToken<List<PointModel>>() {}.type!!

    override fun getMapData(): Single<List<PointModel>> {
        return get(BarCode(BARCODE_TYPE, BARCODE_KEY))
    }

    private val fetcher = { apiService: ApiInterface, id: Int ->
        apiService.getPoints().compose(
                GsonTransformerFactory.createObjectToSourceTransformer<List<PointModel>>(gson)
        )
    }

    private val myStore =
            StoreBuilder.parsedWithKey<BarCode, BufferedSource, List<PointModel>>()
                    .fetcher { barcode -> fetcher(mApi, barcode.key.toInt()) }
                    .persister(SourcePersisterFactory.create(App.Companion.cacheDirect))
                    .parser(GsonParserFactory.createSourceParser<List<PointModel>>(gson, type))
                    .open()

    private fun get(barcode: BarCode) = myStore.get(barcode)

    companion object {
        const val BARCODE_KEY = "41"
        const val BARCODE_TYPE = "42"
    }
}