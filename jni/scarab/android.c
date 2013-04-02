#include "android.h"

fhe_pk_t public_key;
fhe_sk_t secret_key;
bool initialized = false;

void throwJavaException(JNIEnv *env, const char *msg)
{
    jclass c = (*env)->FindClass(env, "java/lang/RuntimeException");
    if (NULL == c)
    {
        //B plan: null pointer ...
        c = (*env)->FindClass(env, "java/lang/NullPointerException");
    }
    (*env)->ThrowNew(env, c, msg);
}

void mpz_set_jstr(JNIEnv *env, const jstring str, mpz_t var) {
	const char *textchars= (*env)->GetStringUTFChars(env, str, 0);
	mpz_set_str(var, textchars, 10);
    (*env)->ReleaseStringUTFChars(env, str, textchars);
}

jstring mpz_get_jstr(JNIEnv *env, const mpz_t mpz) {
	char *buf = mpz_get_str(NULL, 10, mpz);
    jstring jresult = (*env)->NewStringUTF(env, buf);
    free(buf);
	return jresult;
}

void logd(const char *message) {
	__android_log_print(ANDROID_LOG_DEBUG, TAG, "%s", message);
}

void loge(const char *message) {
	__android_log_print(ANDROID_LOG_ERROR, TAG, "%s", message);
}

JNIEXPORT jboolean JNICALL Java_com_malinskiy_hcalculator_Scarab_generateKeys
  (JNIEnv * env, jobject obj) {
        fhe_pk_init(public_key);
        fhe_sk_init(secret_key);
        fhe_keygen(public_key, secret_key);
        logd("keypair generation complete!");
		initialized = true;
        return JNI_TRUE;
  }

JNIEXPORT jstring JNICALL Java_com_malinskiy_hcalculator_Scarab_getPublicKey
  (JNIEnv * env, jobject obj) {
        char* str_public_key = fhe_pk_to_str(public_key);
        jstring jresult;
        if(str_public_key == NULL) {
			jresult = (*env)->NewStringUTF(env, "Failed to allocate enough memory");
			loge("Failed to allocate enough memory for public key string");
        } else {
			jresult = (*env)->NewStringUTF(env, str_public_key);

			#ifdef DEBUG
				logd("Pushing public key to VM: ");
				logd(str_public_key);
			#endif

            free(str_public_key);
        }
        return jresult;
  }

JNIEXPORT jstring JNICALL Java_com_malinskiy_hcalculator_Scarab_getSecretKey
  (JNIEnv * env, jobject obj) {
        char* str_secret_key = fhe_sk_to_str(secret_key);
        jstring jresult;
        if(str_secret_key == NULL) {
  		    jresult = (*env)->NewStringUTF(env, "Failed to allocate enough memory");
  			loge("Failed to allocate enough memory for private key string");
        } else {
  			jresult = (*env)->NewStringUTF(env, str_secret_key);
  			logd("Pushing private key to VM: ");
  			logd(str_secret_key);
            free(str_secret_key);
        }
        return jresult;
  }

JNIEXPORT jstring JNICALL Java_com_malinskiy_hcalculator_Scarab_encrypt
  (JNIEnv * env, jobject obj, jint plain) {
        if(initialized == false) {
            loge("Keys are NULL! Initialization failure?");
            return (*env)->NewStringUTF(env, "Error");
        }
        mpz_t ciphertext;
        mpz_init(ciphertext);

        fhe_encrypt(ciphertext, public_key, plain);
        jstring result = mpz_get_jstr(env, ciphertext);
        mpz_clear(ciphertext);
		return result;
  }

JNIEXPORT jint JNICALL Java_com_malinskiy_hcalculator_Scarab_decrypt
  (JNIEnv * env, jobject obj, jstring cipher) {

        #ifdef DEBUG
            logd("Entering decryption");
        #endif

        if(initialized == false) {
            loge("Keys are NULL! Initialization failure?");
            return -1;
        }
        mpz_t ciphertext;
        mpz_init(ciphertext);
        mpz_set_jstr(env, cipher, ciphertext);
		jint jresult = fhe_decrypt(ciphertext, secret_key);

		#ifdef DEBUG
			char log[128];
			char val[16];
			strcpy(log, "Decrypted ");
			sprintf(val,"%d",jresult);
			strcat(log, val);
			logd(log);
		#endif

		mpz_clear(ciphertext);
        return jresult;
  }

JNIEXPORT jstring JNICALL Java_com_malinskiy_hcalculator_Scarab_recrypt
  (JNIEnv * env, jobject obj, jstring cipher) {
        if(initialized == false) {
            loge("Keys are NULL! Initialization failure?");
            return (*env)->NewStringUTF(env, "Error");
        }
        mpz_t ciphertext;
        mpz_init(ciphertext);
        mpz_set_jstr(env, cipher, ciphertext);
		fhe_recrypt(ciphertext, public_key);
		jstring result = mpz_get_jstr(env, ciphertext);
		mpz_clear(ciphertext);
        return result;
  }

JNIEXPORT jstring JNICALL Java_com_malinskiy_hcalculator_Scarab_add
  (JNIEnv * env, jobject obj, jstring a, jstring b) {
        if(initialized == false) {
            loge("Keys are NULL! Initialization failure?");
            return (*env)->NewStringUTF(env, "Error");
        }
        mpz_t x, y, result;
        mpz_inits(x, y, result, NULL);
		mpz_set_jstr(env, a, x);
		mpz_set_jstr(env, b, y);

        fhe_add(result, x, y, public_key);
        jstring jresult = mpz_get_jstr(env, result);
        mpz_clears(x, y, result, NULL);
		return jresult;
  }

JNIEXPORT jstring JNICALL Java_com_malinskiy_hcalculator_Scarab_multiply
  (JNIEnv * env, jobject obj, jstring a, jstring b) {
        if(initialized == false) {
            loge("Keys are NULL! Initialization failure?");
            return (*env)->NewStringUTF(env, "Error");
        }
        mpz_t x, y, result;
        mpz_inits(x, y, result, NULL);
		mpz_set_jstr(env, a, x);
		mpz_set_jstr(env, b, y);

        fhe_mul(result, x, y, public_key);
        jstring jresult = mpz_get_jstr(env, result);

		mpz_clears(x, y, result, NULL);

		return jresult;
  }

JNIEXPORT void JNICALL Java_com_malinskiy_hcalculator_Scarab_free
  (JNIEnv * env, jclass obj) {
    fhe_pk_clear(public_key);
    fhe_sk_clear(secret_key);
  }

JNIEXPORT void JNICALL Java_com_malinskiy_hcalculator_Scarab_test
  (JNIEnv * env, jclass obj) {
    test_suite();
  }