#include <jni.h>
#include "com_example_qjb_yinnao_AubioKit.h"

#ifndef _Included_com_example_qjb_yinnao_AubioKit
#define _Included_com_example_qjb_yinnao_AubioKit
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_qjb_yinnao_AubioKit
 * Method:    args_init
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL Java_com_example_qjb_yinnao_AubioKit_args_1init(JNIEnv *, jclass, jint, jint, jint, jint)
{
}

/*
 * Class:     com_example_qjb_yinnao_AubioKit
 * Method:    mfcc_compte
 * Signature: ([F)[F
 */
JNIEXPORT jfloatArray JNICALL Java_com_example_qjb_yinnao_AubioKit_mfcc_1compte(JNIEnv *, jclass, jfloatArray)
{

}

/*
 * Class:     com_example_qjb_yinnao_AubioKit
 * Method:    clean_mf
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_qjb_yinnao_AubioKit_clean_1mf(JNIEnv *, jclass)
{

}

#ifdef __cplusplus
}
#endif
#endif
