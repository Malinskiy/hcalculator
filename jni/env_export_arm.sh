#!/bin/bash
export NDKROOT="/opt/tadp/android-ndk-r8d"

export CC="$NDKROOT/toolchains/arm-linux-androideabi-4.7/prebuilt/linux-x86/bin/arm-linux-androideabi-gcc --sysroot=$NDKROOT/platforms/android-14/arch-arm"
export CXX="$NDKROOT/toolchains/arm-linux-androideabi-4.7/prebuilt/linux-x86/bin/arm-linux-androideabi-g++ --sysroot=$NDKROOT/platforms/android-14/arch-arm"
export AR="$NDKROOT/toolchains/arm-linux-androideabi-4.7/prebuilt/linux-x86/bin/arm-linux-androideabi-ar"
export SYSROOT="$NDKROOT/platforms/android-14/arch-arm"
export PATH="$NDKROOT/toolchains/arm-linux-androideabi-4.7/prebuilt/linux-x86/bin":$PATH
