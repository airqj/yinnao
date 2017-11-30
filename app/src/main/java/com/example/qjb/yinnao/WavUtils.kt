package com.example.qjb.yinnao

/**
 * Created by qinjianbo on 17-11-29.
 */

import android.media.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import android.text.format.DateFormat
import org.dmg.pmml.False
import org.dmg.pmml.True
import java.io.FileInputStream
import java.io.FileNotFoundException

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

    private val mills = System.currentTimeMillis()
    private val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    private val fileName = storagePath + format.format(mills) + ".pcm"
    private var wavFile: FileOutputStream? = null
    private val mediaPlayer: MediaPlayer? = null

    fun openFile() {
        wavFile = FileOutputStream(fileName)
    }

    fun write2Wav(audioData: ByteArray) {
        wavFile?.write(audioData)
    }

    fun closeFile() {
        try {
            wavFile?.close()
        } catch (e: FileNotFoundException) {

        }
    }

    private fun fileSizeIsGreaterThanZero(): Boolean {
        try {
            val file = File(fileName)
            val fileLength = file.length()
            return fileLength > 0
        } catch (e: FileNotFoundException) {
            return false
        }
    }

    fun play() {
        if (fileSizeIsGreaterThanZero()) { // if file size > 0 ,play the file
            var buffer = ByteArray(512 * 1024)
            val intSize = AudioTrack.getMinBufferSize(44100,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT)
            val mAudioTrack = AudioTrack(AudioManager.STREAM_MUSIC,44100,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,intSize,AudioTrack.MODE_STREAM)
            val fis = FileInputStream(File(fileName))
            val fileLength = File(fileName).length()
            var readSize = 0
            while (readSize < fileLength) {
                val ret =  fis.read(buffer)
                if(ret > 0) {
                    mAudioTrack?.write(buffer,0,ret)
                    readSize += ret
                }
            }
            mAudioTrack.release()
        }
    }


}