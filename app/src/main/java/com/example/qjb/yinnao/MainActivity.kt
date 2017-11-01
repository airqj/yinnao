package com.example.qjb.yinnao

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color.*
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageButton
import bolts.Task
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.view.BigImageView
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import java.util.*
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity(),OnClickListener {

    private var mBigImageView:BigImageView? = null
    private var mScrollView:ScrollView? = null
    private var mBtnTest:ImageButton? = null
    private var timer = Timer()
    private var mBtnTestStatus:Boolean = false

    fun doAddY() {
            mScrollView?.scrollTo(0, mScrollView?.scrollY!!.plus(1))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BigImageViewer.initialize(FrescoImageLoader.with(applicationContext));
        setContentView(R.layout.activity_main)

        mBigImageView = findViewById(R.id.mBigImageView)
        mScrollView   = findViewById(R.id.mScrollView)
        mBtnTest      = findViewById(R.id.btnTest)

        mBtnTest?.setOnClickListener(this)
        mBigImageView?.setOptimizeDisplay(false)
        mBigImageView?.showImage(Uri.parse("http://ww1.sinaimg.cn/mw690/005Fj2RDgw1f9mvl4pivvj30c82ougw3.jpg"))
    }

    override fun onClick(v: View?) {
        if(v?.id == mBtnTest?.id) {
            if(mBtnTestStatus == false) {
                timer.schedule(timerTask { doAddY() }, 1000, 100)
                mBtnTestStatus = true
            }
            else {
                timer.cancel()
                mBtnTestStatus = false
                timer = Timer()
            }
        }
    }
}