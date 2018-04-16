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

class MapDataRepositoryImpl(private val mApi: ApiInterface) : MapDataRepository {

    override fun getMapData(): Single<List<PointModel>> {
        return get(BarCode("41", "42"))
    }

    private val fetcher = { apiService: ApiInterface, id: Int ->
        apiService.getPoints().compose(
                GsonTransformerFactory.createObjectToSourceTransformer<List<PointModel>>(Gson())
        )
    }
    private val persister = SourcePersisterFactory.create(App.Companion.cacheDirect)

    val type = object : TypeToken<List<PointModel>>() {}.type!!
    private val parser = GsonParserFactory.createSourceParser<List<PointModel>>(Gson(), type)

    private val myStore =
            StoreBuilder.parsedWithKey<BarCode, BufferedSource, List<PointModel>>()
                    .fetcher { barcode -> fetcher(mApi, barcode.key.toInt()) }
                    .persister(persister)
                    .parser(parser)
                    .open()

    fun get(barcode: BarCode) = myStore.get(barcode)

    fun fetch(barcode: BarCode) = myStore.fetch(barcode)


}