/*
 *  COPYRIGHT NOTICE
 *  Copyright (C) 2016, Jhuster <lujun.hust@gmail.com>
 *  https://github.com/Jhuster/YUVDroidTools
 *
 *  @license under the Apache License, Version 2.0
 *
 *  @file    ff_converter.c
 *
 *  @version 1.0
 *  @author  Jhuster
 *  @date    2016/06/30
 */
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
#include <libswscale/swscale.h>
#include "ff_converter.h"

typedef struct _ff_converter {
    struct SwsContext *context;
    AVFrame * src_frame; 
    AVFrame * dst_frame; 
    int src_width;
    int src_height;
    int src_fmt;
    int dst_width;
    int dst_height;
    int dst_fmt;
} ff_converter_t;

static int SUPPORTED_FMT_ARRAY[] = {
    AV_PIX_FMT_YUV420P,
    AV_PIX_FMT_YUV420P,
    AV_PIX_FMT_NV21,
    AV_PIX_FMT_NV12,    
    AV_PIX_FMT_YUYV422,
    AV_PIX_FMT_YVYU422,
    AV_PIX_FMT_YUV422P,
    AV_PIX_FMT_UYVY422,
    AV_PIX_FMT_GRAY8, 
    AV_PIX_FMT_RGB565,
    AV_PIX_FMT_RGB24,
    AV_PIX_FMT_ARGB,
    AV_PIX_FMT_ABGR,
    AV_PIX_FMT_RGBA,
};

static int SUPPORTED_QUALITY_ARRAY[] = {
    SWS_FAST_BILINEAR,
    SWS_BILINEAR,
    SWS_BICUBIC,
    SWS_X,
    SWS_POINT,
    SWS_AREA,
    SWS_BICUBLIN,
    SWS_GAUSS,
    SWS_SINC,
    SWS_LANCZOS,
    SWS_SPLINE
};

FF_CONVERTER ff_converter_open(int src_width, int src_height, int src_fmt, int dst_width, int dst_height, int dst_fmt, int alg)
{
    ff_converter_t * converter = (ff_converter_t *) malloc(sizeof(ff_converter_t));

    converter->context = sws_getContext(src_width, src_height, SUPPORTED_FMT_ARRAY[src_fmt], dst_width, dst_height,
                SUPPORTED_FMT_ARRAY[dst_fmt], SUPPORTED_QUALITY_ARRAY[alg], NULL, NULL, NULL);
    converter->src_frame = av_frame_alloc();
    converter->dst_frame = av_frame_alloc();

    converter->src_width  = src_width;
    converter->src_height = src_height;
    converter->src_fmt = SUPPORTED_FMT_ARRAY[src_fmt];
    converter->dst_width  = dst_width;
    converter->dst_height = dst_height;
    converter->dst_fmt = SUPPORTED_FMT_ARRAY[dst_fmt];

    return (FF_CONVERTER)converter;
}

void ff_converter_close(FF_CONVERTER fd)
{
    ff_converter_t * converter = (ff_converter_t *) fd;

    if(converter->context != NULL) {
        sws_freeContext(converter->context);
        converter->context = NULL;
    }

    if(converter->src_frame != NULL) {
        av_frame_free(&converter->src_frame);
        converter->src_frame = NULL;
    }

    if(converter->dst_frame != NULL) {
        av_frame_free(&converter->dst_frame);
        converter->dst_frame = NULL;
    }
}

int ff_converter_calc_size(int width, int height, int fmt) {
    return avpicture_get_size(fmt, width, height);
}

int ff_converter_calc_src_size(FF_CONVERTER fd)
{
    ff_converter_t * converter = (ff_converter_t *) fd;
    return ff_converter_calc_size(converter->src_width, converter->src_height, converter->src_fmt);
}

int ff_converter_calc_dst_size(FF_CONVERTER fd)
{
    ff_converter_t * converter = (ff_converter_t *) fd;
    return ff_converter_calc_size(converter->dst_width, converter->dst_height, converter->dst_fmt);
}

int ff_converter_process(FF_CONVERTER fd, unsigned char *src, int src_size, unsigned char *dst, int dst_size)
{    
    ff_converter_t * converter = (ff_converter_t *) fd;

    int src_bytes = ff_converter_calc_src_size(fd);
    int dst_bytes = ff_converter_calc_dst_size(fd);
    if (src_size < src_bytes || dst_size < dst_bytes) {
        return -1;
    }

    avpicture_fill((AVPicture *)converter->src_frame, src, converter->src_fmt ,
        converter->src_width, converter->src_height);

    avpicture_fill((AVPicture *)converter->dst_frame, dst, converter->dst_fmt ,
        converter->dst_width, converter->dst_height);

    sws_scale(converter->context, (const uint8_t* const*)converter->src_frame->data, converter->src_frame->linesize, 0 ,
                converter->src_height, converter->dst_frame->data, converter->dst_frame->linesize);

    return dst_bytes;
}

int ff_converter_process_once(int src_width, int src_height, int src_fmt, int dst_width, int dst_height, int dst_fmt, int alg,
        unsigned char *src, int src_size, unsigned char *dst, int dst_size)
{
    FF_CONVERTER converter = ff_converter_open(src_width, src_height, src_fmt, dst_width, dst_height, dst_fmt, alg);
    int ret = ff_converter_process(converter, src, src_size, dst, dst_size);
    ff_converter_close(converter);
    return ret;
}