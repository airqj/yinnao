package com.example.qjb.yinnao

/**
 * Created by qinjianbo on 17-11-29.
 */

import android.media.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.io.FileNotFoundException

import com.example.qjb.yinnao.Wav

class WavUtils(storagePath:String) {

    private val storagePath = storagePath
    private val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    private var wavFile: FileOutputStream? = null
    private val mediaPlayer: MediaPlayer? = null

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
            if (file?.exists()) {
                Wav(file).writeWavHeader()
            }
        } catch (e:FileNotFoundException) {

        }
    }
}
