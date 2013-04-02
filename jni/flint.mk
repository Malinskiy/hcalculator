LOCAL_PATH := $(call my-dir)

LOCAL_MODULE := flint
LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/lib/libflint.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libs/$(TARGET_ARCH_ABI)/include

include $(PREBUILT_STATIC_LIBRARY)
