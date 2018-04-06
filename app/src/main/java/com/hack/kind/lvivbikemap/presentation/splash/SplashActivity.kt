package com.hack.kind.lvivbikemap.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hack.kind.lvivbikemap.presentation.map.view.MapActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MapActivity::class.java))
        finish()
    }
}