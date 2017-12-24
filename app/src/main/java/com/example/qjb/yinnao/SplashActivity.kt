package com.example.qjb.yinnao

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlin.concurrent.timerTask
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import java.util.*


/**
 * Created by qinjianbo on 17-12-23.
 */
class SplashActivity:Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_splash)
        Handler().postDelayed(Runnable {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        },3000)
    }
}