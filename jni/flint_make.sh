#!/bin/bash
wget http://www.flintlib.org/flint-1.6.tgz
tar xvzf flint-1.6.tgz
mv flint-1.6 flint
rm flint-1.6.tgz

cd flint
patch -p1 -t -i ../flint.patch
source flint_env
FLINT_TUNE_ORIGINAL=$FLINT_TUNE

mkdir libs

for idx in "${!TARGETS[@]}"
do
	echo "Building for ${TARGETS[$idx]} with flags: ${TARGET_CFLAGS[$idx]}"
	ARCH="${TARGETS[$idx]}"
	CUSTOM_CFLAGS="${TARGET_CFLAGS[$idx]}"
	. ../"${TARGET_ENV[$idx]}"
	LDFLAGS="${TARGET_LDFLAGS[$idx]}"
	export FLINT_TUNE="$FLINT_TUNE_ORIGINAL $CUSTOM_CFLAGS"
	export FLINT_GMP_INCLUDE_DIR="../gmp/libs/$ARCH/include"
	export FLINT_GMP_LIB_DIR="../gmp/libs/$ARCH/lib"
	export FLINT_MPFR_INCLUDE_DIR="../mpfr/libs/$ARCH/include"
	export FLINT_MPFR_LIB_DIR="../mpfr/libs/$ARCH/lib"
	make libflint.a -j8 V=1 2>&1 | tee build.log
	mkdir -p libs/$ARCH/lib
	mkdir -p libs/$ARCH/include
	mv libflint.* libs/$ARCH/lib
	mv build.log libs/$ARCH
	cp *.h libs/$ARCH/include 
	mkdir -p libs/$ARCH/include/zn_poly/src
	cp zn_poly/src/*.h libs/$ARCH/include/zn_poly/src
	rm *.o
done

cp ../flint.mk Android.mk
cd ..
