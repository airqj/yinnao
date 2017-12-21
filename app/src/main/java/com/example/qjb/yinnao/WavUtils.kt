package com.example.qjb.yinnao

/**
 * Created by qinjianbo on 17-11-29.
 */

import android.app.Notification
import android.media.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.io.FileNotFoundException
import android.media.MediaPlayer
import android.provider.MediaStore
import android.util.Log

import com.example.qjb.yinnao.AubioKit
import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter
import java.util.concurrent.ArrayBlockingQueue
import be.tarsos.dsp.AudioEvent
import com.example.qjb.yinnao.Wav
import com.example.qjb.yinnao.Flag
import java.nio.FloatBuffer

class WavUtils(storagePath:String):Runnable {

    private val storagePath = storagePath
    private val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    private var wavFile: FileOutputStream? = null
    private val mMediaPlayer = MediaPlayer()
    public var aubioKit:AubioKit? = null
    public var audioFileName:String? = null
    private var fileName:String? = null
    private var playing = false
    private var firstWrite = true
    public  var maxValue:Float = 0F
    public  var minValue:Float = 0F
    public  var mainThreadHandler:android.os.Handler? = null

    val bufferQueue = ArrayBlockingQueue<Pair<FloatArray,ByteArray>>(1024)
    val audioData = ArrayBlockingQueue<ByteArray>(1024)

    override fun run() {
        process()
    }

    fun process() {
        var byteArray:ByteArray? = null
        while (true) {
            byteArray = audioData.take()
            if(firstWrite) {
                newFileName()
                openFile(fileName!!)
                firstWrite = false
            }
            if(byteArray.isEmpty()) {
                Log.i("wavUtils play function",System.currentTimeMillis().toString())
                closeFile()
//                mainThreadHandler?.sendEmptyMessage(Flag.MEDIAPLAYERPLAYING)
                play()
            }
            else {
                write2Wav(byteArray)
            }

        }
    }

    fun newFileName() {
        val mills = System.currentTimeMillis()
        fileName = storagePath + format.format(mills) + ".wav"
    }

    fun openFile(fileName:String) {
        wavFile = FileOutputStream(fileName)
    }

    fun write2Wav(audioData: ByteArray) {
        wavFile?.write(audioData)
    }

    fun closeFile() {
        try {
            wavFile?.flush()
            wavFile?.close()
            val file = File(fileName)
            Wav(file).writeWavHeader()
        } catch (e: FileNotFoundException) {

        }
    }

    fun play() {
        if(audioFileName != null) {
            mMediaPlayer.reset()
            mMediaPlayer.setOnCompletionListener { playRecordFile() }
            mMediaPlayer.setDataSource(audioFileName)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
        }
        else {
            playRecordFile()
        }
    }

    fun playRecordFile() {
       mMediaPlayer.reset()
       mMediaPlayer.setOnCompletionListener {
           File(fileName).delete()
           firstWrite = true
           playing = false
           Log.i("play finished","Flag.ENABLEPROCESS")
       //    mainThreadHandler?.sendEmptyMessage(Flag.ENABLEPROCESS)
       }
       mMediaPlayer.setDataSource(fileName)
       mMediaPlayer.prepare()
       mMediaPlayer.start()
    }
}
