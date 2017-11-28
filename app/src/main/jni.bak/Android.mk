LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := aubio
LOCAL_SRC_FILES := $(LOCAL_PATH)/libaubio.so 
APP_BUILD_SCRIPT := $(LOCAL_PATH)/Android.mk
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := aubioinvoke
LOCAL_SRC_FILES += com_example_qjb_yinnao_AubioKit.cpp

LOCAL_C_INCLUDES := $(LOCAL_PATH)/aubio
LOCAL_LDLIBS := -llog -lz
LOCAL_SHARED_LIBRARIES := aubio
include $(BUILD_SHARED_LIBRARY)
