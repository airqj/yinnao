package com.example.qjb.yinnao

import android.net.Uri
import android.support.v7.app.AppCompatActivity
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
import android.content.res.AssetManager
import android.os.*
import be.tarsos.dsp.AudioDispatcher
import org.jpmml.android.EvaluatorUtil
import com.example.qjb.yinnao.ModelRF
import com.example.qjb.yinnao.AubioKit
import com.example.qjb.yinnao.UDP
import com.example.qjb.yinnao.WavUtils
import java.io.File

class MainActivity : AppCompatActivity(),OnClickListener {

    private var mBigImageView: BigImageView? = null
    private var mScrollView: ScrollView? = null
    private var mBtnTest: ImageButton? = null
    private var timer = Timer()
    private var mBtnTestStatus: Boolean = false
    private var textView: TextView? = null
    private var wavUtil: WavUtils? = null
//    private var dispacher: AudioDispatcher? = null

    private var fileName: String? = null
    private var stopRecord = false

    private fun doAddY() {
        mScrollView?.scrollTo(0, mScrollView?.scrollY!!.plus(1))
    }

    val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            Thread({ createDispather() }, "unknown").start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BigImageViewer.initialize(FrescoImageLoader.with(applicationContext));
        setContentView(R.layout.activity_main)

        mBigImageView = findViewById(R.id.mBigImageView)
        mScrollView = findViewById(R.id.mScrollView)
        mBtnTest = findViewById(R.id.btnTest)
        textView = findViewById(R.id.textView)
        wavUtil = WavUtils(Environment.getExternalStorageDirectory().path + "/Recorders/")

        mBtnTest?.setOnClickListener(this)
        mBigImageView?.setOptimizeDisplay(false)
        mBigImageView?.showImage(Uri.parse("http://ww1.sinaimg.cn/mw690/005Fj2RDgw1f9mvl4pivvj30c82ougw3.jpg"))

        val ins = applicationContext.assets.open("model")
        wavUtil?.aubioKit = AubioKit(ins)
        createDispather()
//        aubioKit?.args_init(win_s,n_filters,n_coefs,samplerate)
        //TarosDSP()
        Thread({ wavUtil}, "process").start()
//        Thread({dispacher},"process audio data").start()
        createDispather()
    }

    override fun onClick(v: View?) {
        if (v?.id == mBtnTest?.id) {
            stopRecord = true
            wavUtil?.play(fileName!!)
            /*
            if(mBtnTestStatus == false) {
                timer.schedule(timerTask { doAddY() }, 1000, 100)
                mBtnTestStatus = true
            }
            else {
                timer.cancel()
                mBtnTestStatus = false
                timer = Timer()
            }
            */
        }

    }

    fun TarosDSP() {
        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)
        val pdh = PitchDetectionHandler { result, _ ->
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


    fun createDispather() {
        val sampleRate = 44100
        val bufferSize = 2205
        val bufferOverlap = 0
        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, bufferOverlap)
        val mfcc = MFCC(bufferSize, sampleRate.toFloat(), 39, 40, 133.3334f, sampleRate.toFloat() / 2f);
        dispatcher.addAudioProcessor(mfcc)
        dispatcher.addAudioProcessor(object : AudioProcessor {
            override fun processingFinished() {
            }

            override fun process(audioEvent: AudioEvent): Boolean {
                wavUtil?.bufferQueue?.offer(Pair(audioEvent?.floatBuffer, audioEvent?.byteBuffer))
                return true
            }
        })
        Thread({ dispatcher }, "dispacher").start()
    }
}
