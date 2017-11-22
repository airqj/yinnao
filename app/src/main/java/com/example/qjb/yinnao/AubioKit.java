package com.example.qjb.yinnao;

public class AubioKit {
    static{
        System.loadLibrary("aubio");
    }
    //int args_init(uint_t win_s_jni, uint_t n_filters_jni, uint_t n_coefs_jni,uint_t samplerate_jni);
    //float * mfcc_compute(float audio_buffer[]);
    //void clean_mf();
    public native static int args_init(int win_s_jni,int n_filters_jni,int n_coefs_jni,int samplerate_jni);
    public native static float[] mfcc_compte(float[] audioBuffer);
    public native static void clean_mf();
}