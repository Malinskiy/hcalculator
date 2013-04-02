LOCAL_PATH := $(call my-dir)
LOCAL_PATH_COPY := $(LOCAL_PATH)
include $(CLEAR_VARS)

include $(LOCAL_PATH)/hardware/Android.mk
include $(CLEAR_VARS)
LOCAL_PATH := $(LOCAL_PATH_COPY)

include $(LOCAL_PATH)/gmp/Android.mk
include $(CLEAR_VARS)
LOCAL_PATH := $(LOCAL_PATH_COPY)

include $(LOCAL_PATH)/mpfr/Android.mk
include $(CLEAR_VARS)
LOCAL_PATH := $(LOCAL_PATH_COPY)

include $(LOCAL_PATH)/flint/Android.mk
include $(CLEAR_VARS)
LOCAL_PATH := $(LOCAL_PATH_COPY)

include $(LOCAL_PATH)/scarab/Android.mk
include $(CLEAR_VARS)
LOCAL_PATH := $(LOCAL_PATH_COPY)