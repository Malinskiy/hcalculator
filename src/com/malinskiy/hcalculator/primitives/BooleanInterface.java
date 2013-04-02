package com.malinskiy.hcalculator.primitives;


public interface BooleanInterface {

	@Override
	public boolean equals(Object obj);

	public <T extends BooleanInterface> T not();

	public <T extends BooleanInterface> T xor(T... x);

	public <T extends BooleanInterface> T and(T... x);

	public <T extends BooleanInterface> T or(T... x);

	public <T extends BooleanInterface> T set(T c);

	public <T extends BooleanInterface> T set(boolean value);

	public boolean get();
}
