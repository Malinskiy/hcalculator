package com.malinskiy.hcalculator.primitives;

import com.malinskiy.hcalculator.Scarab;

import java.util.Arrays;

/**
 * Homomorphic boolean class
 * There exist 2 operations already: xor, and;
 * For this boolean system of functions to implement all other boolean function
 * we need to add 1-function, that is constant 1
 * Such a system will be complete according to Post's theorem
 */
public class HBoolean implements BooleanInterface {
	private String value;
	private static HBoolean One = new HBoolean(true);

	public HBoolean() {
	}

	public HBoolean(boolean value) {
		set(value);
	}

	public HBoolean(HBoolean aHBoolean) {
		this.value = new String(aHBoolean.value);
	}

	public boolean equals(Object obj) {
		if (obj instanceof HBoolean) {
			return value.equals(((HBoolean) obj).value);
		}
		return false;
	}

	public HBoolean not() {
		return not(this);
	}

	@Override
	public <T extends BooleanInterface> T xor(T... x) {
		HBoolean[] tempArray = Arrays.copyOf(x, x.length + 1, HBoolean[].class);
		tempArray[x.length] = this;
		return (T)xor(tempArray);
	}

	@Override
	public <T extends BooleanInterface> T and(T... x) {
		HBoolean[] tempArray = Arrays.copyOf(x, x.length + 1, HBoolean[].class);
		tempArray[x.length] = this;
		return (T) and(tempArray);
	}

	@Override
	public <T extends BooleanInterface> T or(T... x) {
		HBoolean[] tempArray = Arrays.copyOf(x, x.length + 1, HBoolean[].class);
		tempArray[x.length] = this;
		return (T) or(tempArray);
	}

	@Override
	public <T extends BooleanInterface> T set(T c) {
		value = new String(((HBoolean)c).value);
		return (T) this;
	}

	@Override
	public <T extends BooleanInterface> T set(boolean value) {
		this.value = value ? Scarab.encrypt(1) : Scarab.encrypt(0);
		return (T) this;
	}

	@Override
	public boolean get() {
		return Scarab.decrypt(value) == 1 ? true : false;
	}

	/**
	 * x (+) x = !x
	 *
	 * @param x
	 * @return
	 */
	public static HBoolean not(HBoolean x) {
		return new HBoolean(Scarab.add(x.value, One.value));
	}

	public static HBoolean xor(HBoolean... x) {
		if (x.length <= 1) {
			throw new IllegalArgumentException();
		} else {
			HBoolean result = new HBoolean(x[0]);
			for (int i = 1; i < x.length; i++) {
				result = new HBoolean(Scarab.add(result.value, x[i].value));
			}
			return result;
		}
	}

	public static HBoolean and(HBoolean... x) {
		if (x.length <= 1) {
			throw new IllegalArgumentException();
		} else {
			HBoolean result = new HBoolean(x[0]);
			for (int i = 1; i < x.length; i++) {
				result = new HBoolean(Scarab.multiply(result.value, x[i].value));
			}
			return result;
		}
	}

	/**
	 * !(!x + !y)
	 *
	 * @param x
	 * @return
	 */
	public static HBoolean or(HBoolean... x) {
		if (x.length <= 1) {
			throw new IllegalArgumentException();
		} else {
			HBoolean result = new HBoolean(x[0]);
			for (int i = 1; i < x.length; i++) {
				result = not(and(not(result), not(x[i])));
			}
			return result;
		}
	}

	private HBoolean(String homomorphicValue) {
		this.value = homomorphicValue;
	}
}