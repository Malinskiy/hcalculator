#include <stdbool.h>
#include <android/log.h>
#include "integer-fhe.h"
#include "com_malinskiy_hcalculator_Scarab.h"
#include "test.h"

#define TAG "libscarab"

void throwJavaException(JNIEnv *env, const char *msg);

void mpz_set_jstr(JNIEnv *env, const jstring str, mpz_t var);

jstring mpz_get_jstr(JNIEnv *env, const mpz_t mpz);

void logd(const char *message);

void loge(const char *message);