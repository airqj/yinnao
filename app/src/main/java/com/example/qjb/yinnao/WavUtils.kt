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

import com.example.qjb.yinnao.AubioKit

import java.util.concurrent.ArrayBlockingQueue

import com.example.qjb.yinnao.Wav

class WavUtils(storagePath:String) {

    private val storagePath = storagePath
    private val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    private var wavFile: FileOutputStream? = null
    private val mediaPlayer: MediaPlayer? = null
    public var aubioKit:AubioKit? = null
    private var continuteNumClassTure = 0
    private var continuteNumClassFalse = 0
    private val startRecordThrehold = 10
    private val stopRecordThrehold  = 10

    public val bufferQueue = ArrayBlockingQueue<FloatArray>(1024 * 1024)
    fun process():Int {
        while (true) {
            val audioBuffer = bufferQueue.take()
            if(aubioKit?.predict(audioBuffer)!! == 1) {
                continuteNumClassTure +=1
                continuteNumClassFalse = 0
            }
            else {
                continuteNumClassFalse +=1
                continuteNumClassTure   =0
            }
            if(continuteNumClassTure == startRecordThrehold) { // start to record
                newFileName()
            }
        }
        /*
        // if recording is setted,that record audio
        if(recording && !stopRecord) {
            wavUtil?.write2Wav(audioEvent.byteBuffer)
        }
        //val res = aubioKit?.predict(audioEvent?.floatBuffer)
        mfccBuffer = mfcc.mfcc
        preditionResult = aubioKit?.predict(mfccBuffer!!)
        //udpSender.send(mfccBuffer!!) //use to get data from phone
        if(preditionResult == 1 && !stopRecord) {
            continuteNumClassTrue += 1
            continuteNumClassFalse = 0
        }
        else if(preditionResult == 0 && !stopRecord){
            continuteNumClassFalse += 1
            continuteNumClassTrue   = 0
        }

        if(continuteNumClassFalse == stopThrehold || continuteNumClassTrue == startThrehold) {
            if(continuteNumClassTrue == startThrehold) {
                // start write to wav file
                if(recording == false) {
                    recording = true
                    fileName = wavUtil?.newFileName()
                    wavUtil?.openFile(fileName!!)
                    runOnUiThread({ textView?.setText("start record") })
                }
                // wavUtil?.write2Wav(audioEvent?.byteBuffer)
            }
            else {
                stopRecord = true
                runOnUiThread({textView?.setText("stop recording")})
                //start play wav file
                if(recording) {
                    wavUtil?.play(fileName!!)
                    recording = false
                }
                //wavUtil?.closeFile()
            }
            continuteNumClassTrue = 0
            continuteNumClassFalse = 0
        }
        */
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
