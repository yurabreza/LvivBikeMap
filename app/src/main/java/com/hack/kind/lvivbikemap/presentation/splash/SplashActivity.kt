package com.hack.kind.lvivbikemap.presentation.splash

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.hack.kind.lvivbikemap.presentation.map.view.MapActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import org.osmdroid.config.Configuration


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
    }

    override fun onResume() {
        super.onResume()
        RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe({
                    if (it) {
                        startActivity(Intent(this, MapActivity::class.java))
                        finish()
                    }
                }, Throwable::printStackTrace)
    }
}