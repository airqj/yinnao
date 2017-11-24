package com.example.qjb.yinnao

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources

import org.dmg.pmml.False

import biz.k11i.xgboost.Predictor
import biz.k11i.xgboost.util.FVec
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import com.example.qjb.yinnao.ModelRF.predict



class AubioKit(i:InputStream) {

    internal var predictor = Predictor(i)
    //int args_init(uint_t win_s_jni, uint_t n_filters_jni, uint_t n_coefs_jni,uint_t samplerate_jni);
    //float * mfcc_compute(float audio_buffer[]);
    //void clean_mf();
    external fun args_init(win_s_jni: Int, n_filters_jni: Int, n_coefs_jni: Int, samplerate_jni: Int): Int
    external fun mfcc_compute(audioBuffer: FloatArray): FloatArray
    external fun clean_mf()

    fun predict(args: FloatArray): Int {
        val fVecDence = mfcc_compute(args)
        val feature = FVec.Transformer.fromArray(fVecDence,false)
        val prediction = predictor.predictSingle(feature)
        return Math.round(prediction).toInt()
    }

    companion object {
        init {
            System.loadLibrary("aubio")
            System.loadLibrary("aubioinvoke")
        }
    }
}