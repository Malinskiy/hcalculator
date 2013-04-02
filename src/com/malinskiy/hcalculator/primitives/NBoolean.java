package com.malinskiy.hcalculator.primitives;

public class NBoolean implements BooleanInterface {
	private Boolean value;

	public NBoolean() {
	}

	public NBoolean(boolean value) {
		set(value);
	}

	public NBoolean(NBoolean aNBoolean) {
		this.value = new Boolean(aNBoolean.value);
	}

	public boolean equals(Object obj) {
		if (obj instanceof NBoolean) {
			return value.equals(((NBoolean) obj).value);
		}
		return false;
	}

	@Override
	public NBoolean not() {
		return new NBoolean(!value);
	}

	@Override
	public <T extends BooleanInterface> T xor(T... x) {
		Boolean result = value;
		for(int i = 0; i < x.length; i++) {
			result ^= ((NBoolean)(x[i])).value;
		}
		return (T) (new NBoolean(result));
	}

	@Override
	public <T extends BooleanInterface> T and(T... x) {
		Boolean result = value;
		for (int i = 0; i < x.length; i++) {
			result &= ((NBoolean) (x[i])).value;
		}
		return (T) (new NBoolean(result));
	}

	@Override
	public <T extends BooleanInterface> T or(T... x) {
		Boolean result = value;
		for (int i = 0; i < x.length; i++) {
			result |= ((NBoolean) (x[i])).value;
		}
		return (T) (new NBoolean(result));
	}

	@Override
	public <T extends BooleanInterface> T set(T c) {
		this.value = ((NBoolean) c ).value;
		return (T) this;
	}

	@Override
	public <T extends BooleanInterface> T set(boolean value) {
		this.value = value;
		return (T) this;
	}

	@Override
	public boolean get() {
		return value;
	}
}
