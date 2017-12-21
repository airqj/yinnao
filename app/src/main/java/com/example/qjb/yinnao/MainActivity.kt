package com.example.qjb.yinnao

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.view.BigImageView
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import java.util.*
import kotlin.concurrent.timerTask
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.mfcc.MFCC
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import android.content.res.AssetManager
import android.os.*
import android.util.Log
import be.tarsos.dsp.AudioDispatcher
import org.jpmml.android.EvaluatorUtil
import com.example.qjb.yinnao.AubioKit
import com.example.qjb.yinnao.UDP
import com.example.qjb.yinnao.WavUtils
import com.example.qjb.yinnao.Flag
import java.io.File
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.view.KeyEvent
import android.widget.*
import com.lypeer.fcpermission.FcPermissions
import com.lypeer.fcpermission.impl.FcPermissionsCallbacks
import com.leon.lfilepickerlibrary.LFilePicker
import com.leon.lfilepickerlibrary.utils.Constant
import org.florescu.android.rangeseekbar.RangeSeekBar
import org.florescu.android.rangeseekbar.RangeSeekBar.OnRangeSeekBarChangeListener
import kotlin.math.max

class MainActivity : AppCompatActivity(),OnClickListener,FcPermissionsCallbacks {

    private var mBigImageView: BigImageView? = null
    private var mScrollView: ScrollView? = null
    private var mBtnTest: ImageButton? = null
    private var mImageBtnPlayStatus:ImageButton? = null
    private var mBtnFileSelector:Button? = null
    private var timer = Timer()
    private var mBtnTestStatus: Boolean = false
    private var textViewDisplay: TextView? = null
    private var rangeSeekBar:RangeSeekBar<Float>? = null
    private var wavUtil: WavUtils? = null
    private var recordEnable = false
    private var PermissionRecord = false
    private var audioFileName:String? = null
//    private var dispacher: AudioDispatcher? = null
    private var mAudioManager:AudioManager? = null
    val startThreadhold = 5
    val stopThreadHold = 10
    var continuteClassFalse = 0
    var continuteClassTure  = 0
    var processEnable = true

    private var fileName: String? = null
    private var stopRecord = false

    private fun doAddY() {
        mScrollView?.scrollTo(0, mScrollView?.scrollY!!.plus(1))
    }

    val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg?.what) {
                Flag.STOPRECORD -> {recordEnable = false;textViewDisplay?.setText("停止录音")}
                Flag.RECORDING  -> {textViewDisplay?.setText("正在录音")}
                Flag.ENABLERECORD -> {recordEnable = true}
                Flag.PERMRECORDGRANTED -> {requestWriteExternalStorage()}
                Flag.PERMRECORDDENY -> {requestRecordPermission()}
                Flag.PERMWRITEEXTERNALSTORAGEDENY -> {requestWriteExternalStorage()}
                Flag.PERMWRITEEXTERNALSTORAGEGRANTED -> {
                    val dispather = createDispather()
                    Thread(wavUtil,"wavUtils").start()
                    Thread(dispather,"dispather").start()
                }
                Flag.MEDIAPLAYERPLAYING -> {
                    Log.i("mainActivity","playing")
                    textViewDisplay?.setText("正在播放")
                }
                Flag.ENABLEPROCESS -> {processEnable = true}
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mAudioManager?.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
        }
        else {
            mAudioManager?.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,AudioManager.FLAG_SHOW_UI)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BigImageViewer.initialize(FrescoImageLoader.with(applicationContext));
        setContentView(R.layout.activity_replay)

        /*
        // wait until permission Granted
        mBigImageView = findViewById(R.id.mBigImageView)
        mScrollView = findViewById(R.id.mScrollView)
        mBtnTest = findViewById(R.id.btnTest)
        textViewDisplay = findViewById(R.id.textViewDisplay)
        wavUtil = WavUtils(Environment.getExternalStorageDirectory().path + "/Recorders/")

        mBtnTest?.setOnClickListener(this)
        mBigImageView?.setOptimizeDisplay(false)
        mBigImageView?.showImage(Uri.parse("http://ww1.sinaimg.cn/mw690/005Fj2RDgw1f9mvl4pivvj30c82ougw3.jpg"))

        wavUtil?.mainThreadHandler = handler

        val ins = applicationContext.assets.open("model")
        wavUtil?.aubioKit = AubioKit(ins)
        wavUtil?.bufferQueue?.clear()
        requestRecordPermission()
        */
        mBtnFileSelector = findViewById(R.id.btnFileSelector)
        mImageBtnPlayStatus = findViewById(R.id.imgBtnPlayStatus)
        mBtnFileSelector?.setOnClickListener(this)
        mImageBtnPlayStatus?.setOnClickListener(this)
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        textViewDisplay = findViewById(R.id.textViewDisplay)
        rangeSeekBar    = findViewById(R.id.rangeSeekBar)
        rangeSeekBar?.setTextAboveThumbsColorResource(android.R.color.holo_red_dark);

        rangeSeekBar?.setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener<Float> { bar, minValue, maxValue ->
            wavUtil?.maxValue = maxValue
            wavUtil?.minValue = minValue
            Log.i("mainActivity: maxValue",maxValue.toString())
        })

        createDir()
        wavUtil = WavUtils(Environment.getExternalStorageDirectory().path + File.separator + "yinnao" + File.separator)
        wavUtil?.mainThreadHandler = handler
        val ins  = applicationContext.assets.open("model")
        wavUtil?.aubioKit = AubioKit(ins)
        wavUtil?.audioData?.clear()
        requestRecordPermission()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            mBtnFileSelector?.id -> {
                LFilePicker().withActivity(this).
                        withRequestCode(1000).
                        withTitle("选择音频文件").start()
            }
            mImageBtnPlayStatus?.id -> {
                val mMediaPlayer = MediaPlayer()
                mMediaPlayer.setDataSource(audioFileName)
                mMediaPlayer.prepare()
                mMediaPlayer.start()
            }
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == 1000) {
                val fileName = data?.getStringArrayListExtra(Constant.RESULT_INFO)!![0]
                wavUtil?.audioFileName = fileName
                runOnUiThread({
                    val mMediaPlayer = MediaPlayer()
                    mMediaPlayer.setDataSource(fileName)
                    mMediaPlayer.prepare()
                    val duration = (mMediaPlayer.duration / 1000 / 60).toInt().toFloat() +
                                    (mMediaPlayer.duration / 1000 %60).toInt() / 100F
                    Log.i("mainActivity:duration ",duration.toString())
                    rangeSeekBar?.setRangeValues(0F,duration)
                    mMediaPlayer.release()
                })
                mBtnFileSelector?.setText(fileName.split("/").last())
            }
        }
    }

    fun createDispather():AudioDispatcher {
        val sampleRate = 44100
        val bufferSize = 2205
        val bufferOverlap = 0
        val emptyByteArray:ByteArray = ByteArray(0)
        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, bufferOverlap)
        val mfcc = MFCC(bufferSize, sampleRate.toFloat(), 39, 40, 133.3334f, sampleRate.toFloat() / 2f);
        dispatcher.addAudioProcessor(mfcc)
        dispatcher.addAudioProcessor(object : AudioProcessor {
            override fun processingFinished() {
            }
            override fun process(audioEvent: AudioEvent): Boolean {
                if(processEnable) {
                    if (recordEnable) {
                        wavUtil?.audioData?.offer(audioEvent?.byteBuffer)
                    }
                    if (wavUtil?.aubioKit?.predict(mfcc?.mfcc) == 1) {
                        continuteClassTure += 1
                        continuteClassFalse = 0
                    } else {
                        continuteClassFalse += 1
                        continuteClassTure = 0
                    }
                    if (continuteClassTure == startThreadhold) {
                        recordEnable = true
                        continuteClassTure = 0
                        handler.sendEmptyMessage(Flag.RECORDING)
                    }
                    if (continuteClassFalse == stopThreadHold) {
                        handler.sendEmptyMessage(Flag.STOPRECORD)
                        if (recordEnable) {
                            processEnable = false  // stop process
                            // recordfinished,send message to play thread
                     //       handler.sendEmptyMessage(Flag.MEDIAPLAYERPLAYING)
                            Log.i("MainActi,emptyByteArray",System.currentTimeMillis().toString())
                            wavUtil?.audioData?.offer(emptyByteArray)
                        }
                        continuteClassFalse = 0
                    }
                }
                return true
            }
        })
        return dispatcher
    }

    fun createDir() {
        val dir = File(Environment.getExternalStorageDirectory().path + File.separator +"yinnao")
        if(!dir.exists()) {
            dir.mkdir()
        }
    }

    override fun onPermissionsDenied(p0: Int, perm: MutableList<String>?) {
        Toast.makeText(applicationContext, "不要拒绝权限请求,需要此权限才能运行", Toast.LENGTH_LONG).show()
        if(perm!![0] == Manifest.permission.RECORD_AUDIO) {
            handler.sendEmptyMessage(Flag.PERMRECORDDENY)
        }
        if(perm!![0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            handler.sendEmptyMessage(Flag.PERMWRITEEXTERNALSTORAGEDENY)
        }
        FcPermissions.checkDeniedPermissionsNeverAskAgain(this,"需要权限才能运行",R.string.setting,R.string.cancel,perm)
    }

    override fun onPermissionsGranted(p0: Int, p1: MutableList<String>?) {
        Toast.makeText(applicationContext,"成功获取权限",Toast.LENGTH_LONG).show()
        if(p1!![0] == Manifest.permission.RECORD_AUDIO) {
            handler?.sendEmptyMessage(Flag.PERMRECORDGRANTED)
        }
        if(p1!![0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            handler?.sendEmptyMessage(Flag.PERMWRITEEXTERNALSTORAGEGRANTED)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        FcPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun requestRecordPermission() {
        FcPermissions.requestPermissions(this,"请求录音机权限",FcPermissions.REQ_PER_CODE,Manifest.permission.RECORD_AUDIO)
    }

    private fun requestWriteExternalStorage() {
        FcPermissions.requestPermissions(this,"请求读取内存卡权限",FcPermissions.REQ_PER_CODE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}
