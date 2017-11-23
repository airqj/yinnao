package com.example.qjb.yinnao;

public class AubioKit {
    static {
        System.loadLibrary("aubio");
        System.loadLibrary("aubioinvoke");
    }
    //int args_init(uint_t win_s_jni, uint_t n_filters_jni, uint_t n_coefs_jni,uint_t samplerate_jni);
    //float * mfcc_compute(float audio_buffer[]);
    //void clean_mf();
    public native int args_init(int win_s_jni,int n_filters_jni,int n_coefs_jni,int samplerate_jni);
    public native float[] mfcc_compute(float[] audioBuffer);
    public native void clean_mf();
}