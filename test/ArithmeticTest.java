import com.malinskiy.hcalculator.primitives.HInteger;
import com.malinskiy.hcalculator.primitives.NBoolean;
import org.junit.Test;

import java.util.Random;

import static junit.framework.Assert.assertTrue;

/**
 * This test uses NBoolean (Native java Boolean) class to check the integrity of the HInteger
 */
public class ArithmeticTest {
	public final static int             TEST_COUNT_ARITH_OPS = 16;
	public final static int             TEST_COUNT_BIT_OPS   = 256;
	public static final Class<NBoolean> BOOLEAN_CLASS        = NBoolean.class;

	@Test
	public void addTest() {
		for (int i = 0; i < TEST_COUNT_ARITH_OPS; i++) {
			Random random = new Random(System.currentTimeMillis());
			int x = random.nextInt() / 4;
			int y = random.nextInt() / 4;
			HInteger hX = new HInteger(BOOLEAN_CLASS, x);
			HInteger hY = new HInteger(BOOLEAN_CLASS, y);
			HInteger hZ = hX.add(hY);
			System.out.println(x + " + " + y + " == " + hZ.get());
			assertTrue("Add test failed", hZ.get() == x + y);
		}
	}

	@Test
	public void subTest() {
		for (int i = 0; i < TEST_COUNT_ARITH_OPS; i++) {
			Random random = new Random(System.currentTimeMillis());
			int x = random.nextInt() / 4;
			int y = Math.abs(random.nextInt() / 4);
			HInteger hX = new HInteger(BOOLEAN_CLASS, x);
			HInteger hY = new HInteger(BOOLEAN_CLASS, y);
			HInteger hZ = hX.subtract(hY);
			System.out.println(x + " - " + y + " == " + hZ.get());
			assertTrue("Subtract test failed", hZ.get() == x - y);
		}
	}
}