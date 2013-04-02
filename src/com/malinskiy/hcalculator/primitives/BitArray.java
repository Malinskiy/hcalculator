package com.malinskiy.hcalculator.primitives;

import java.lang.reflect.Array;

public class BitArray<T extends BooleanInterface> {
	private T[] bits;
	private Class<T> bitClass;

	private BitArray() {
	}

	public BitArray(Class<T> tClass, int capacity) {
		super();
		if (capacity <= 0) {
			throw new ExceptionInInitializerError();
		} else {
			bitClass = tClass;
			bits = (T[]) Array.newInstance(tClass, capacity);
		}
	}

	public BitArray(BitArray aBitArray) {
		if (aBitArray == null || aBitArray.bits == null || aBitArray.bits.length == 0) {
			throw new ExceptionInInitializerError();
		} else {
			int capacity = aBitArray.bits.length;
			bits = (T[]) Array.newInstance(aBitArray.bitClass, capacity);
			for (int i = 0; i < capacity; i++) {
				try {
					bits[i] = bitClass.newInstance();
					bits[i].set(aBitArray.bits[i]);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void flip(int idx) {
		if (idx < 0 || idx >= bits.length) {
			throw new IllegalArgumentException();
		} else {
			bits[idx] = bits[idx].not();
		}
	}

	public void flip(int firstIdx, int lastIdx) {
		if (firstIdx < 0 || firstIdx > bits.length || lastIdx >= bits.length || firstIdx > lastIdx) {
			throw new IllegalArgumentException();
		} else {
			for (int i = firstIdx; i <= lastIdx; i++) {
				flip(i);
			}
		}
	}

	public void set(int idx, T value) {
		if (idx < 0 || idx >= bits.length) {
			throw new IllegalArgumentException();
		} else {
			bits[idx] = value;
		}
	}

	public void setAll(T value) {
		for(int i = 0; i < bits.length; i++) {
			set(i, value);
		}
	}

	public void clear(int idx) {
		if (idx < 0 || idx >= bits.length) {
			throw new IllegalArgumentException();
		} else {
			try {
				bits[idx] = bitClass.newInstance();
				bits[idx].set(false);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public T get(int idx) {
		if (idx < 0 || idx >= bits.length) {
			throw new IllegalArgumentException();
		} else {
			return bits[idx];
		}
	}

	public int length() {
		return bits.length;
	}

	public BitArray not() {
		BitArray result = new BitArray(bitClass, bits.length);
		for (int i = 0; i < bits.length; i++) {
			result.set(i, bits[i].not());
		}
		return result;
	}

	public BitArray xor(BitArray x) {
		if (x == null || x.bits == null || x.bits.length != bits.length) {
			throw new IllegalArgumentException();
		} else {
			BitArray result = new BitArray(bitClass, bits.length);
			for (int i = 0; i < bits.length; i++) {
				result.set(i, bits[i].xor(x.bits[i]));
			}
			return result;
		}
	}

	public BitArray and(BitArray x) {
		if (x == null || x.bits == null || x.bits.length != bits.length) {
			throw new IllegalArgumentException();
		} else {
			BitArray result = new BitArray(bitClass, bits.length);
			for (int i = 0; i < bits.length; i++) {
				result.set(i, bits[i].and(x.bits[i]));
			}
			return result;
		}
	}

	public BitArray and(T x) {
		if (x == null) {
			throw new IllegalArgumentException();
		} else {
			BitArray result = new BitArray(bitClass, bits.length);
			for (int i = 0; i < bits.length; i++) {
				result.set(i, bits[i].and(x));
			}
			return result;
		}
	}

	public BitArray or(BitArray x) {
		if (x == null || x.bits == null || x.bits.length != bits.length) {
			throw new IllegalArgumentException();
		} else {
			BitArray result = new BitArray(bitClass, bits.length);
			for (int i = 0; i < bits.length; i++) {
				result.set(i, bits[i].or(x.bits[i]));
			}
			return result;
		}
	}

	public BitArray lshift(int d) {
		if (d < 0) {
			throw new IllegalArgumentException();
		} else {
			for (int i = bits.length - 1; i >= 0; i--) {
				int beforeShiftIdx = i - d;
				if (beforeShiftIdx >= 0 && beforeShiftIdx <= bits.length) {
					bits[i] = bits[beforeShiftIdx];
				} else {
					try {
						bits[i] = bitClass.newInstance();
						bits[i].set(false);
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return this;
	}

	/**
	 * by one
	 *
	 * @return
	 */
	public BitArray lshift() {
		return lshift(1);
	}

	public BitArray rshift(int d) {
		if (d < 0) {
			throw new IllegalArgumentException();
		} else {
			for (int i = 0; i < bits.length; i++) {
				int beforeShiftIdx = i + d;
				if (beforeShiftIdx >= 0 && beforeShiftIdx <= bits.length) {
					bits[i] = bits[beforeShiftIdx];
				} else {
					try {
						bits[i] = bitClass.newInstance();
						bits[i].set(false);
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return this;
	}

	/**
	 * by one
	 *
	 * @return
	 */
	public BitArray rshift() {
		return rshift(1);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BitArray) {
			BitArray x = (BitArray) obj;
			for (int i = 0; i < bits.length; i++) {
				if (!bits[i].equals(x.bits))
					return false;
			}
			return true;
		}
		return false;
	}
}