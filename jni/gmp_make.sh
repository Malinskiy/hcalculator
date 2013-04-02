#!/bin/bash
wget ftp://ftp.gmplib.org/pub/gmp-5.1.1/gmp-5.1.1.tar.bz2
tar xjf gmp-5.1.1.tar.bz2
mv gmp-5.1.1 gmp
rm gmp-5.1.1.tar.bz2

cd gmp
mkdir libs

for idx in "${!TARGETS[@]}"
do
	echo "Building for ${TARGETS[$idx]} with flags: ${TARGET_CFLAGS[$idx]}"
	ARCH="${TARGETS[$idx]}"
	CUSTOM_CFLAGS="${TARGET_CFLAGS[$idx]}"
	. ../"${TARGET_ENV[$idx]}"
	mkdir libs/$ARCH
	LDFLAGS="${TARGET_LDFLAGS[$idx]}"
	CFLAGS="-UHAVE_LOCALE_H $CUSTOM_CFLAGS" ./configure --host="${TARGET_HOST_NAME[$idx]}"-unknown-linux --prefix=$PWD/libs/$ARCH
	make -j8 V=1 2>&1 | tee build.log
	make install
	cp --preserve=links libs/$ARCH/lib libs/$ARCH/
	mv build.log libs/$ARCH
	make distclean
done

cp ../gmp.mk Android.mk
cd ../
