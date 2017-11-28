#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include "aubio/aubio.h"
#include "com_example_qjb_yinnao_AubioKit.h"

//#ifndef _Included_com_example_qjb_yinnao_AubioKit
//#define _Included_com_example_qjb_yinnao_AubioKit

//public native int args_init(int win_s_jni,int n_filters_jni,int n_coefs_jni,int samplerate_jni);
//public native float[] mfcc_compute(float[] audioBuffer);

#define LOGI(...) \
                ((void)__android_log_print(ANDROID_LOG_INFO, "aubioKit::", __VA_ARGS__))

extern "C" {

jint JNI_OnLoad( JavaVM* vm, void* reserved )
{
    LOGI("Loading native library compiled at " __TIME__ " " __DATE__);
    return JNI_VERSION_1_6;
}

/*
 * Class:     com_example_qjb_yinnao_AubioKit
 * Method:    args_init
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL Java_com_example_qjb_yinnao_AubioKit_args_1init(JNIEnv *env, jclass jc, \
                                                                        jint win_s,jint n_filters,\
                                                                        jint n_coefs,jint samplerate)
{
    uint ret = args_init(win_s,n_filters,n_coefs,samplerate);
    return ret;
}

/*
 * Class:     com_example_qjb_yinnao_AubioKit
 * Method:    mfcc_compte
 * Signature: ([F)[F
 */
JNIEXPORT jfloatArray JNICALL Java_com_example_qjb_yinnao_AubioKit_mfcc_1compute(JNIEnv *env, jclass jc, jfloatArray audioBuffer)
{
    /*
    jfloat *temp = (env)->GetFloatArrayElements(audioBuffer,JNI_FALSE);
    float args[2205] = {0};
    uint j = 0;
    for(j=0;j< sizeof(args) / sizeof(args[0]);j++)
    {
        args[j] = temp[j];
    }
//    float *res = mfcc_compute(temp);
    */
    jfloatArray ret = (env)->NewFloatArray(39);
    jfloat *input = (env)->GetFloatArrayElements(audioBuffer,JNI_FALSE);
    float *temp = mfcc_compute(input);
    (env)->SetFloatArrayRegion(ret,0,39,temp);
    return ret;
}

/*
 * Class:     com_example_qjb_yinnao_AubioKit
 * Method:    clean_mf
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_qjb_yinnao_AubioKit_clean_1mf(JNIEnv *env, jclass jc)
{
        clean_mf();
}
}
//#endif
