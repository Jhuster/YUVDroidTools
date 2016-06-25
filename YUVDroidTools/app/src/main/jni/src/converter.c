/*
 *  COPYRIGHT NOTICE
 *  Copyright (C) 2016, Jhuster <lujun.hust@gmail.com>
 *  https://github.com/Jhuster/YUVDroidTools
 *
 *  @license under the Apache License, Version 2.0
 *
 *  @file    converter.c
 *
 *  @version 1.0
 *  @author  Jhuster
 *  @date    2016/06/30
 */
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include "ff_converter.h"

/*
 * Class:     com_jhuster_yuvdroidtools_FFConverter
 * Method:    nativeOpen
 * Signature: (IIIIIII)I
 */
JNIEXPORT jint JNICALL Java_com_jhuster_yuvdroidtools_FFConverter_nativeOpen(JNIEnv *env, jobject thiz, jint src_width, jint src_height, jint src_fmt, jint dst_width, jint dst_height, jint dst_fmt, jint alg)
{
    return ff_converter_open(src_width, src_height, src_fmt, dst_width, dst_height, dst_fmt, alg);
}

/*
 * Class:     com_jhuster_yuvdroidtools_FFConverter
 * Method:    nativeClose
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_jhuster_yuvdroidtools_FFConverter_nativeClose(JNIEnv *env, jobject thiz, jint fd)
{
    ff_converter_close(fd);
}

/*
 * Class:     com_jhuster_yuvdroidtools_FFConverter
 * Method:    nativeCalcSize
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_com_jhuster_yuvdroidtools_FFConverter_nativeCalcSize(JNIEnv *env, jclass thiz, jint width, jint height, jint fmt)
{
    return ff_converter_calc_size(width, height, fmt);
 }

/*
 * Class:     com_jhuster_yuvdroidtools_FFConverter
 * Method:    nativeProcess
 * Signature: (I[BI)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_jhuster_yuvdroidtools_FFConverter_nativeProcess(JNIEnv *env, jobject thiz, jint fd, jbyteArray buffer, jint length)
{
    int src_size = ff_converter_calc_src_size(fd);
    if (length < src_size) {
        return NULL;
    }

    unsigned char * p_src_buffer = (*env)->GetByteArrayElements(env, buffer, NULL);
    if (p_src_buffer == NULL) {
        return;
    }

    int dst_size = ff_converter_calc_dst_size(fd);
    unsigned char * p_dst_buffer = (unsigned char *) malloc(dst_size * sizeof(char));

    ff_converter_process(fd, p_src_buffer, length, p_dst_buffer, dst_size);

    (*env)->ReleaseByteArrayElements(env, buffer, p_src_buffer, 0);

    jbyteArray array = (*env)->NewByteArray(env, dst_size);
    (*env)->SetByteArrayRegion(env, array, 0, dst_size, p_dst_buffer);

    free(p_dst_buffer);

    return array;
}

/*
 * Class:     com_jhuster_yuvdroidtools_FFConverter
 * Method:    nativeProcessOnce
 * Signature: (IIIIIII[BI)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_jhuster_yuvdroidtools_FFConverter_nativeProcessOnce(JNIEnv *env, jclass thiz, jint src_width, jint src_height, jint src_fmt,
    jint dst_width, jint dst_height, jint dst_fmt, jint alg, jbyteArray buffer, jint length)
{
    int src_size = ff_converter_calc_size(src_width, src_height, src_fmt);
    if (length < src_size) {
        return NULL;
    }

    unsigned char * p_src_buffer = (*env)->GetByteArrayElements(env, buffer, NULL);
    if (p_src_buffer == NULL) {
        return;
    }

    int dst_size = ff_converter_calc_size(dst_width, dst_height, dst_fmt);
    unsigned char * p_dst_buffer = (unsigned char *) malloc(dst_size * sizeof(char));

    ff_converter_process_once(src_width, src_height, src_fmt, dst_width, dst_height, dst_fmt, alg, p_src_buffer, length, p_dst_buffer, dst_size);

    (*env)->ReleaseByteArrayElements(env, buffer, p_src_buffer, 0);

    jbyteArray array = (*env)->NewByteArray(env, dst_size);
    (*env)->SetByteArrayRegion(env, array, 0, dst_size, p_dst_buffer);

    free(p_dst_buffer);

    return array;
}
