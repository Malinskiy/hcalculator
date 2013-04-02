#!/bin/bash
export NDKROOT="/opt/tadp/android-ndk-r8d"

export CC="$NDKROOT/toolchains/mipsel-linux-android-4.7/prebuilt/linux-x86/bin/mipsel-linux-android-gcc --sysroot=$NDKROOT/platforms/android-14/arch-mips"
export CXX="$NDKROOT/toolchains/mipsel-linux-android-4.7/prebuilt/linux-x86/bin/mipsel-linux-android-g++ --sysroot=$NDKROOT/platforms/android-14/arch-mips"
export AR="$NDKROOT/toolchains/mipsel-linux-android-4.7/prebuilt/linux-x86/bin/mipsel-linux-android-ar"
export SYSROOT="$NDKROOT/platforms/android-14/arch-mips"
export PATH="$NDKROOT/toolchains/mipsel-linux-android-4.7/prebuilt/linux-x86/bin":$PATH


