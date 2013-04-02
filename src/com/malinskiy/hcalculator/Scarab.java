package com.malinskiy.hcalculator;

import android.util.Log;
import com.malinskiy.hcalculator.primitives.HBoolean;
import com.malinskiy.hcalculator.primitives.HInteger;

import java.util.Random;

/**
 * JNI interface to scarab methods
 */
public class Scarab {
	static {
		if(NativeHardware.neonSupported()) {
			System.loadLibrary("scarab-neon");
		} else {
			System.loadLibrary("scarab");
		}
	}
	public static boolean isInitialized = false;

	private static final String TAG = "scarab";

	public static native boolean generateKeys();

	public static native String getPublicKey();

	public static native String getSecretKey();

	public static native String encrypt(int plain);

	public static native int decrypt(String cipher);

	public static native String recrypt(String cipher);

	public static native String add(String a, String b);

	public static native String multiply(String a, String b);

	public static native void free();

	public static native void test();

	public static boolean testXor() {
		Random random = new Random(System.currentTimeMillis());
		Integer X = random.nextBoolean() ? 1 : 0;
		Integer Y = random.nextBoolean() ? 1 : 0;
		Integer expectedZ = X ^ Y;

		String hX = encrypt(X);
		String hY = encrypt(Y);
		String hZ = add(hX, hY);

		Integer realZ = decrypt(hZ);

		Log.d(TAG, X + " + " + Y + " == " + realZ);
		return realZ.equals(expectedZ);
	}

	public static boolean testAnd() {
		Random random = new Random(System.currentTimeMillis());
		Integer X = random.nextBoolean() ? 1 : 0;
		Integer Y = random.nextBoolean() ? 1 : 0;
		Integer expectedZ = X & Y;

		String hX = encrypt(X);
		String hY = encrypt(Y);
		String hZ = multiply(hX, hY);

		Integer realZ = decrypt(hZ);
		Log.d(TAG, X + " & " + Y + " == " + realZ);
		return realZ.equals(expectedZ);
	}

	public static boolean testAdd() {
		Random random = new Random(System.currentTimeMillis());
		int x = random.nextInt() / 4;
		int y = random.nextInt() / 4;
		HInteger hX = new HInteger(HBoolean.class, x);
		HInteger hY = new HInteger(HBoolean.class, y);
		HInteger hZ = hX.add(hY);
		Log.d("scarab", x + " + " + y + " == " + hZ);
		return hZ.get() == x + y;
	}

	public static boolean testSub() {
		Random random = new Random(System.currentTimeMillis());
		int x = random.nextInt() / 4;
		int y = Math.abs(random.nextInt() / 4);
		HInteger hX = new HInteger(HBoolean.class, x);
		HInteger hY = new HInteger(HBoolean.class, y);
		HInteger hZ = hX.subtract(hY);
		Log.d("scarab", x + " - " + y + " == " + hZ);
		return hZ.get() == x - y;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		free();
	}
}
