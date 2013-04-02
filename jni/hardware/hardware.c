#include "com_malinskiy_hcalculator_NativeHardware.h"
#include <cpu-features.h>

JNIEXPORT jboolean JNICALL Java_com_malinskiy_hcalculator_NativeHardware_neonSupported(JNIEnv *env, jclass cl) {
	return (android_getCpuFamily() == ANDROID_CPU_FAMILY_ARM) && ((android_getCpuFeatures() & ANDROID_CPU_ARM_FEATURE_NEON) != 0 );
}