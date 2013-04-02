#!/bin/bash
export TARGETS=(
'mips'
'x86' 
'armeabi-v7a-neon'
'armeabi-v7a'
'armeabi' 
)

export TARGET_ENV=(
'env_export_mips.sh'
'env_export_x86.sh'
'env_export_arm.sh'
'env_export_arm.sh'
'env_export_arm.sh'
)

export TARGET_HOST_NAME=(
'mips'
'i686'
'arm'
'arm'
'arm'
)

BASE_CFLAGS='-O3 -pedantic -fomit-frame-pointer -Wa,--noexecstack -ffunction-sections -funwind-tables -fstack-protector -fno-strict-aliasing -finline-limit=64'
export TARGET_CFLAGS=(
"-std=c99 -O3 -static -march=mips32"
"${BASE_CFLAGS} -march=i686"
"${BASE_CFLAGS} -march=armv7-a -mfpu=neon -mfloat-abi=softfp -ftree-vectorize -ftree-vectorizer-verbose=2"
"${BASE_CFLAGS} -march=armv7-a -mfloat-abi=softfp -mfpu=vfp" 
"${BASE_CFLAGS} -march=armv5te -mtune=xscale -msoft-float"
)

export TARGET_LDFLAGS=(
''
''
'-Wl,--fix-cortex-a8 -Wl,--no-undefined -Wl,-z,noexecstack -Wl,-z,relro -Wl,-z,now'
'-Wl,--fix-cortex-a8 -Wl,--no-undefined -Wl,-z,noexecstack -Wl,-z,relro -Wl,-z,now'
'-Wl,--fix-cortex-a8 -Wl,--no-undefined -Wl,-z,noexecstack -Wl,-z,relro -Wl,-z,now'
)

. ./gmp_make.sh
. ./mpfr_make.sh
. ./flint_make.sh
$NDKROOT/ndk-build clean
$NDKROOT/ndk-build
