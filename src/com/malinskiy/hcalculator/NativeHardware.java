package com.malinskiy.hcalculator;

public class NativeHardware {
	static {
		System.loadLibrary("hw");
	}

	public static native boolean neonSupported();
}