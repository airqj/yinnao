package com.example.qjb.yinnao

/**
 * Created by qinjianbo on 17-11-29.
 */

import android.media.AudioRecord
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import android.text.format.DateFormat
import android.media.MediaPlayer

private class Header() {
    /*
    * writeString(output, "RIFF"); // chunk id
        writeInt(output, 36 + rawData.length); // chunk size
        writeString(output, "WAVE"); // format
        writeString(output, "fmt "); // subchunk 1 id
        writeInt(output, 16); // subchunk 1 size
        writeShort(output, (short) 1); // audio format (1 = PCM)
        writeShort(output, (short) 1); // number of channels
        writeInt(output, Constants.RECORDER_SAMPLERATE); // sample rate
        writeInt(output, Constants.RECORDER_SAMPLERATE * 2); // byte rate
        writeShort(output, (short) 2); // block align
        writeShort(output, (short) 16); // bits per sample
        writeString(output, "data"); // subchunk 2 id
        writeInt(output, rawData.length); // subchunk 2 size
        */
    val flag = "RIFF"
}

class WavUtils(storagePath:String) {

    val mills = System.currentTimeMillis()
    val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    val fileName = storagePath + format.format(mills) + ".wav"
    var wavFile:FileOutputStream? = null
    val mediaPlayer:MediaPlayer?  = null

    fun openFile() {
        wavFile = FileOutputStream(fileName)
    }

    fun write2Wav(audioData: ByteArray) {
        wavFile?.write(audioData)
    }

    fun closeFile() {
        wavFile?.close()
    }

    fun fileSizeIsGreaterThanZero() {
    }

    fun play() {
        mediaPlayer?.setDataSource(fileName)
        mediaPlayer?.prepare()
        mediaPlayer?.start()

    }
}