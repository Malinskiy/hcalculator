package com.malinskiy.hcalculator.primitives;

/**
 * Integer class based on bit array
 * Warning: No overflow correction!
 * @param <T> Boolean class {NBoolean; HBoolean}
 */
public class HInteger<T extends BooleanInterface> {
	public static final int BIT_DEPTH = 16;
	private Class<T> bitClass;
	private BitArray bitArray;

	public HInteger(Class<T> bitClass) {
		this.bitClass = bitClass;
		bitArray = new BitArray(bitClass, BIT_DEPTH);
	}

	public HInteger(Class<T> bitClass, int x) {
		this(bitClass);
		set(x);
	}

	public HInteger(HInteger x) {
		this(x.bitClass);
		set(x);
	}

	private HInteger(Class<T> bitClass, BitArray bitArray) {
		this.bitClass = bitClass;
		this.bitArray = bitArray;
	}

	public void set(int x) {
		for (int i = 0; i < BIT_DEPTH; i++) {
			boolean value = ((x & (1 << i)) != 0);
			try {
				bitArray.set(i, bitClass.newInstance().set(value));
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public void set(HInteger x) {
		this.bitArray = new BitArray(x.bitArray);
	}

	public int get() {
		int result = 0;
		for (int i = 0; i < BIT_DEPTH; i++) {
			boolean value = bitArray.get(i).get();
			if (value) {
				result |= 1 << i;
			}
		}
		return result;
	}

	public HInteger add(HInteger x) {
		BitArray p = bitArray;
		BitArray m = x.bitArray;

		return addition(p, m);
	}

	private HInteger addition(BitArray p, BitArray m) {
		HInteger result = new HInteger(bitClass);
		BooleanInterface carry = null;

		for (int i = 0; i < BIT_DEPTH; i++) {
			BooleanInterface a = p.get(i);
			BooleanInterface b = m.get(i);
			if (i == 0) {
				result.bitArray.set(i, a.xor(b));
				carry = a.and(b);
			} else {
				result.bitArray.set(i, a.xor(b, carry));
				carry = a.and(b).xor(a.and(carry), b.and(carry));
			}
		}
		return result;
	}

	/**
	 * Avoid double subtraction (minus sign of the subtrahend)
	 * @param x
	 * @return
	 */
	public HInteger subtract(HInteger x) {
		x.bitArray.flip(BIT_DEPTH - 1);
		return add(x.getComplement());
	}

	public HInteger getComplement() {
		BooleanInterface ifNotPositive = bitArray.get(BIT_DEPTH - 1);
		BooleanInterface ifYesPositive = ifNotPositive.not();
		BitArray ifYesArray = bitArray;

		BitArray maybeZeroArray = new BitArray(bitClass, BIT_DEPTH);
		maybeZeroArray.setAll(ifYesPositive);
		maybeZeroArray.set(0, ifNotPositive);
		BitArray maybeOneArray = maybeZeroArray;

		BitArray inversed = bitArray.not();
		inversed.flip(BIT_DEPTH - 1);
		BitArray ifNotArray = addition(inversed, maybeOneArray).bitArray;

		return new HInteger(bitClass, ifYesArray.and(ifYesPositive).or(ifNotArray.and(ifNotPositive)));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BitArray) {
			HInteger x = (HInteger) obj;
			return bitArray.equals(x.bitArray);
		}
		return false;
	}

	/**
	 * Warning! decrypts value
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return String.valueOf(get());
	}
}