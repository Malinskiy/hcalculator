LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE     := scarab
LOCAL_SRC_FILES  := integer-fhe.c types.c util.c test.c android.c
LOCAL_LDLIBS += -llog
LOCAL_SHARED_LIBRARIES := flint mpfr gmp

LOCAL_CFLAGS := -std=c99 -ffast-math -funroll-loops -O3

include $(BUILD_SHARED_LIBRARY)

include $(LOCAL_PATH)/Android-neon.mk