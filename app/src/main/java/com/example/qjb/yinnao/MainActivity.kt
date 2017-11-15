package com.example.qjb.yinnao

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.view.View.OnClickListener
import android.widget.ImageButton
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.view.BigImageView
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import java.util.*
import kotlin.concurrent.timerTask
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm
import be.tarsos.dsp.pitch.PitchProcessor
import android.widget.TextView
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.mfcc.MFCC
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator
import be.tarsos.dsp.util.FFMPEGDownloader



class MainActivity : AppCompatActivity(),OnClickListener {

    private var mBigImageView:BigImageView? = null
    private var mScrollView:ScrollView? = null
    private var mBtnTest:ImageButton? = null
    private var timer = Timer()
    private var mBtnTestStatus:Boolean = false
    private var textView:TextView? = null

    private fun doAddY() {
            mScrollView?.scrollTo(0, mScrollView?.scrollY!!.plus(1))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BigImageViewer.initialize(FrescoImageLoader.with(applicationContext));
        setContentView(R.layout.activity_main)

        mBigImageView = findViewById(R.id.mBigImageView)
        mScrollView   = findViewById(R.id.mScrollView)
        mBtnTest      = findViewById(R.id.btnTest)
        textView      = findViewById(R.id.textView)

        mBtnTest?.setOnClickListener(this)
        mBigImageView?.setOptimizeDisplay(false)
        mBigImageView?.showImage(Uri.parse("http://ww1.sinaimg.cn/mw690/005Fj2RDgw1f9mvl4pivvj30c82ougw3.jpg"))

        //TarosDSP()
        CacularMFCC()
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

    fun TarosDSP() {
        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)
        val pdh = PitchDetectionHandler { result, e ->
            val pitchInHz = result.pitch
            runOnUiThread {
                textView?.setText("" + pitchInHz)
                //val text = findViewById(R.id.textView1) as TextView
                //text.text = "" + pitchInHz
            }
        }
        val p = PitchProcessor(PitchEstimationAlgorithm.FFT_YIN, 22050f, 1024, pdh)
        dispatcher.addAudioProcessor(p)
        Thread(dispatcher, "Audio Dispatcher").start()
    }

    fun CacularMFCC() {
        val sampleRate = 44100
        val bufferSize = 2205
        val bufferOverlap = 1102
        //val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate,bufferSize,bufferOverlap)
        val dispatcher = AudioDispatcherFactory.fromPipe("/storage/emulated/0/Recorders/lxy.mp3",sampleRate,bufferSize,bufferOverlap)
        val mfcc = MFCC(bufferSize,sampleRate)
        dispatcher.addAudioProcessor(mfcc)
        dispatcher.addAudioProcessor(object : AudioProcessor {
            override fun processingFinished() {
            }
            override fun process(audioEvent: AudioEvent): Boolean {
                /*
                runOnUiThread {
                    textView?.setText("" + mfcc?.mfcc.size)
                }
                */
                System.out.println(mfcc?.mfcc[0])
                return true
            }
        })
        Thread(dispatcher, "Audio Dispatcher").start()
    }
}