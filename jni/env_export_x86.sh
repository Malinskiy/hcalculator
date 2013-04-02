#!/bin/bash
export NDKROOT="/opt/tadp/android-ndk-r8d"

export CC="$NDKROOT/toolchains/x86-4.7/prebuilt/linux-x86/bin/i686-linux-android-gcc --sysroot=$NDKROOT/platforms/android-14/arch-x86"
export CXX="$NDKROOT/toolchains/x86-4.7/prebuilt/linux-x86/bin/i686-linux-android-g++ --sysroot=$NDKROOT/platforms/android-14/arch-x86"
export AR="$NDKROOT/toolchains/x86-4.7/prebuilt/linux-x86/bin/i686-linux-android-ar"
export SYSROOT="$NDKROOT/platforms/android-14/arch-x86"
export PATH="$NDKROOT/toolchains/x86-4.7/prebuilt/linux-x86/bin":$PATH
