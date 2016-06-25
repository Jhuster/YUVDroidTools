/*
 *  COPYRIGHT NOTICE
 *  Copyright (C) 2016, Jhuster <lujun.hust@gmail.com>
 *  https://github.com/Jhuster/YUVDroidTools
 *
 *  @license under the Apache License, Version 2.0
 *
 *  @file    FFConverter.java
 *
 *  @version 1.0
 *  @author  Jhuster
 *  @date    2016/06/30
 */
package com.jhuster.yuvdroidtools;

public class FFConverter {

    // All the supported YUV format
    public static final int CV_FMT_YU12     = 0;
    public static final int CV_FMT_YV12     = 1;
    public static final int CV_FMT_NV21     = 2;
    public static final int CV_FMT_NV12     = 3;
    public static final int CV_FMT_YUYV422  = 4;
    public static final int CV_FMT_YVYU422  = 5;
    public static final int CV_FMT_YUV422P  = 6;
    public static final int CV_FMT_UYVY422  = 7;
    public static final int CV_FMT_GRAY8    = 8;
    public static final int CV_FMT_RGB565   = 9;
    public static final int CV_FMT_RGB888   = 10;
    public static final int CV_FMT_ARGB8888 = 11;
    public static final int CV_FMT_ABGR8888 = 12;
    public static final int CV_FMT_BGRA8888 = 13; // equals to Android Config.ARGB_8888

    // All the supported converte algorithm
    public static final int CV_ALG_FAST_BILINEAR   = 0;
    public static final int CV_ALG_BILINEAR        = 1;
    public static final int CV_ALG_BICUBIC         = 2;
    public static final int CV_ALG_X               = 3;
    public static final int CV_ALG_POINT           = 4;
    public static final int CV_ALG_AREA            = 5;
    public static final int CV_ALG_BICUBLIN        = 6;
    public static final int CV_ALG_GAUSS           = 7;
    public static final int CV_ALG_SINC            = 8;
    public static final int CV_ALG_LANCZOS         = 9;
    public static final int CV_ALG_SPLINE          = 10;

    private int mConverterFd = -1;

    private native int nativeOpen(int srcWidth, int srcHeight, int srcFmt, int dstWidth, int dstHeight, int dstFmt, int alg);
    private native void nativeClose(int fd);
    private native byte[] nativeProcess(int fd, byte[] data, int length);
    private static native byte[] nativeProcessOnce(int srcWidth, int srcHeight, int srcFmt, int dstWidth, int dstHeight, int dstFmt, int alg, byte[] data, int length);
    private static native int nativeCalcSize(int srcWidth, int srcHeight, int srcFmt);

    public boolean open(int srcWidth, int srcHeight, int srcFmt, int dstWidth, int dstHeight, int dstFmt, int alg) {
        if (mConverterFd == -1) {
            return false;
        }
        mConverterFd = nativeOpen(srcWidth, srcHeight, srcFmt, dstWidth, dstHeight, dstFmt, alg);
        return true;
    }

    public boolean close() {
        if (mConverterFd != -1) {
            return false;
        }
        nativeClose(mConverterFd);
        return true;
    }

    public byte[] process(byte[] data, int length) {
        if (mConverterFd == -1) {
            return null;
        }
        return nativeProcess(mConverterFd, data, length);
    }

    public static byte[] process(int srcWidth, int srcHeight, int srcFmt, int dstWidth, int dstHeight, int dstFmt, int alg, byte[] data, int length) {
        return nativeProcessOnce(srcWidth, srcHeight, srcFmt, dstWidth, dstHeight, dstFmt, alg, data, length);
    }

    public static int calcSize(int srcWidth, int srcHeight, int srcFmt) {
        return nativeCalcSize(srcWidth, srcHeight, srcFmt);
    }

    static {
        System.loadLibrary("ffconverter");
    }
}
