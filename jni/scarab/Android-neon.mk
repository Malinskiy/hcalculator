LOCAL_PATH := $(call my-dir)

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
	  include $(CLEAR_VARS)

      LOCAL_MODULE     := scarab-neon
      LOCAL_SRC_FILES  := integer-fhe.c types.c util.c test.c android.c
      LOCAL_LDLIBS += -llog
      LOCAL_LDLIBS += -l$(LOCAL_PATH)/../flint/libs/armeabi-v7a-neon/lib/libflint.a \
      				-l$(LOCAL_PATH)/../mpfr/libs/armeabi-v7a-neon/lib/libmpfr.a \
      				-l$(LOCAL_PATH)/../gmp/libs/armeabi-v7a-neon/lib/libgmp.a
      LOCAL_C_INCLUDES := $(LOCAL_PATH)/../flint/libs/armeabi-v7a-neon/include \
      					$(LOCAL_PATH)/../mpfr/libs/armeabi-v7a-neon/include \
      					$(LOCAL_PATH)/../gmp/libs/armeabi-v7a-neon/include

      LOCAL_CFLAGS := -std=c99 -ffast-math -funroll-loops -O3
      LOCAL_ARM_NEON  := true

      include $(BUILD_SHARED_LIBRARY)
endif