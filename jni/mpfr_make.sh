#!/bin/bash
wget http://www.mpfr.org/mpfr-current/mpfr-3.1.2.tar.gz
tar xvzf mpfr-3.1.2.tar.gz
mv mpfr-3.1.2 mpfr
rm mpfr-3.1.2.tar.gz

cd mpfr
mkdir libs

for idx in "${!TARGETS[@]}"
do
	echo "Building for ${TARGETS[$idx]} with flags: ${TARGET_CFLAGS[$idx]}"
	ARCH="${TARGETS[$idx]}"
	CUSTOM_CFLAGS="${TARGET_CFLAGS[$idx]}"
	LDFLAGS="${TARGET_LDFLAGS[$idx]}"	
	. ../"${TARGET_ENV[$idx]}"
	CFLAGS="-UHAVE_LOCALE_H $CUSTOM_CFLAGS -I"$PWD/../gmp/libs/$ARCH/include"" LDFLAGS="-L"$PWD/../gmp/libs/$ARCH/lib"" ./configure --host="${TARGET_HOST_NAME[$idx]}"-unknown-linux
	make check
	make -j8 V=1 2>&1 | tee build.log
	mkdir -p libs/$ARCH/lib
	mkdir -p libs/$ARCH/include
	cp --preserve=links src/.libs/libmpfr.* libs/$ARCH/lib/
	cp src/*.h libs/$ARCH/include
	mv build.log libs/$ARCH
	make distclean
done

cp ../mpfr.mk Android.mk
cd ../
