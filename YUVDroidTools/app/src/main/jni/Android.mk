LOCAL_PATH := $(call my-dir)

LIBS_PATH:=$(LOCAL_PATH)/libs

#declare the prebuilt library

#Prebuild

include $(CLEAR_VARS)
LOCAL_MODULE := avcodec
LOCAL_SRC_FILES :=$(LIBS_PATH)/libavcodec.a  
include $(PREBUILT_STATIC_LIBRARY)    

include $(CLEAR_VARS)
LOCAL_MODULE := avswscale
LOCAL_SRC_FILES := $(LIBS_PATH)/libswscale.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avutil
LOCAL_SRC_FILES := $(LIBS_PATH)/libavutil.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avformat
LOCAL_SRC_FILES := $(LIBS_PATH)/libavformat.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := avswresample
LOCAL_SRC_FILES := $(LIBS_PATH)/libswresample.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := ffconverter
LOCAL_SRC_FILES := src/ff_converter.c src/converter.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/inc
LOCAL_LDLIBS := -llog -lm -lz 
LOCAL_WHOLE_STATIC_LIBRARIES:= avcodec avswscale avutil avformat avswresample

include $(BUILD_SHARED_LIBRARY)

