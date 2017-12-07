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
import java.nio.ByteBuffer
import java.util.logging.Handler

class WavUtils(storagePath:String):Runnable {

    private val storagePath = storagePath
    private val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    private var wavFile: FileOutputStream? = null
    private val mediaPlayer: MediaPlayer? = null
    public var aubioKit:AubioKit? = null
    private var continuteNumClassTure = 0
    private var continuteNumClassFalse = 0
    private val startRecordThrehold = 10
    private val stopRecordThrehold  = 10
    private var enableRecord = false
    public  var mainThreadHandler:android.os.Handler? = null

    val bufferQueue = ArrayBlockingQueue<Pair<FloatArray,ByteArray>>(1024 * 1024)

    override fun run() {
        process()
    }
    fun process() {
        bufferQueue?.clear()
        while (true) {
            val pairBuffer = bufferQueue.take()
            val mfcc = pairBuffer?.first
            val byteArray   = pairBuffer?.second
            if(enableRecord) {
                write2Wav(byteArray!!)
            }
            val predictionResult = aubioKit?.predict(mfcc!!)
            if(predictionResult == 1) {
                continuteNumClassTure +=1
                continuteNumClassFalse = 0
            }
            else {
                continuteNumClassFalse +=1
                continuteNumClassTure   =0
            }
            if(continuteNumClassTure == startRecordThrehold) { // create wav file and start record
                if(!enableRecord) {
                    mainThreadHandler?.sendEmptyMessage(1)
                    openFile(newFileName())
                    enableRecord = true
                }
                continuteNumClassTure = 0
            }
            if(continuteNumClassTure == stopRecordThrehold) {
                if(enableRecord) {
                    mainThreadHandler?.sendEmptyMessage(0)
                    // stop record
                    closeFile()
                    enableRecord = false
                }
                continuteNumClassFalse = 0
            }
        }
    }

    fun newFileName():String {
        val mills = System.currentTimeMillis()
        val fileName = storagePath + format.format(mills) + ".wav"
        return fileName
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
        } catch (e: FileNotFoundException) {

        }
    }

    private fun fileSizeIsGreaterThanZero(fileName: String): Boolean {
        try {
            val file = File(fileName)
            val fileLength = file.length()
            return fileLength > 0
        } catch (e: FileNotFoundException) {
            return false
        }
    }

    fun play(fileName: String) {
        try {
            val file = File(fileName)
            if (file.exists()) {
                Wav(file).writeWavHeader()
                val mMediaPlayer = MediaPlayer()
                mMediaPlayer.setOnCompletionListener {  }
                mMediaPlayer.setDataSource(fileName)
                mMediaPlayer.prepare()
                mMediaPlayer.start()
                mMediaPlayer.release()
            }
        } catch (e:FileNotFoundException) {
        }
    }
}
