package com.hack.kind.lvivbikemap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.cocoahero.android.geojson.GeoJSON
import com.cocoahero.android.geojson.GeoJSONObject
import com.hack.kind.lvivbikemap.data.api.ApiInterface
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var api: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPointsFromApi()
    }

    private fun getPointsFromApi() {
        api.getPoints()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response -> parseGeoJson(response.string()) })
    }

    private fun parseGeoJson(response: String) {
        Log.d("My Points = ", " $response")
        val geoJSON: GeoJSONObject
        try {
            geoJSON = GeoJSON.parse(response)
            Log.d("My Parsed points!!! = ", "$geoJSON")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}
