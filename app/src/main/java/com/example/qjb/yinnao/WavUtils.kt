package com.example.qjb.yinnao

/**
 * Created by qinjianbo on 17-11-29.
 */

import android.media.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.io.FileNotFoundException
import android.media.MediaPlayer
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
    private val mediaPlayer: MediaPlayer? = null
    public var aubioKit:AubioKit? = null
    private var continuteNumClassTure = 0
    private var continuteNumClassFalse = 0
    private val startRecordThrehold = 5
    private val stopRecordThrehold  = 10
    private var enableRecord = false
    private var fileName:String? = null
    private var playing = false
    public  var mainThreadHandler:android.os.Handler? = null

    val bufferQueue = ArrayBlockingQueue<Pair<FloatArray,ByteArray>>(1024)

    override fun run() {
        process()
    }
    fun process() {
        bufferQueue?.clear()
        var pairBuffer:Pair<FloatArray,ByteArray>? = null
        var mfcc:FloatArray? = null
        var byteArray:ByteArray? = null
        while (true) {
            pairBuffer = bufferQueue.take()
            mfcc = pairBuffer?.first
            byteArray   = pairBuffer?.second
            if(enableRecord) {
                write2Wav(byteArray!!)
            }
//            val predictionResult = aubioKit?.predict(mfcc!!)
            if(aubioKit?.predict(mfcc!!) == 1) {
                continuteNumClassTure +=1
                continuteNumClassFalse = 0
            }
            else {
                continuteNumClassFalse +=1
                continuteNumClassTure   =0
            }

            if(continuteNumClassTure == startRecordThrehold) { // create wav file and start record
                if(!enableRecord) {
                    mainThreadHandler?.sendEmptyMessage(Flag.RECORDING)
                    newFileName()
                    openFile(fileName!!)
                    enableRecord = true
                }
                continuteNumClassTure = 0
            }
            if(continuteNumClassFalse == stopRecordThrehold) {
                Log.i("WavUtils",System.currentTimeMillis().toString())
                mainThreadHandler?.sendEmptyMessage(Flag.STOPRECORD) // display stop text and set recordEnable to false
                if(enableRecord) { // if recording
                    // stop record
                    closeFile()
//                    bufferQueue.clear()
//                    enableRecord = false
//                    mainThreadHandler?.sendEmptyMessage(Flag.RECORDENABLE)
                    enableRecord = false
                    playing = true
                    play(fileName!!)
                }
                bufferQueue.clear()
                continuteNumClassFalse = 0
                if(!playing) {
                    mainThreadHandler?.sendEmptyMessage(Flag.ENABLERECORD) // set recordEnable to ture
                }
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

    fun play(fileName: String) {
       val mMediaPlayer = MediaPlayer()
       mMediaPlayer.setOnCompletionListener {
           mMediaPlayer.release()
           File(fileName).delete()
           playing = false
           mainThreadHandler?.sendEmptyMessage(Flag.ENABLERECORD)
       }
       mMediaPlayer.setDataSource(fileName)
       mMediaPlayer.prepare()
       mMediaPlayer.start()
    }
}
