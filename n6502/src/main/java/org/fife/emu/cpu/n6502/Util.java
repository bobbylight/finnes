package org.fife.emu.cpu.n6502;

/**
 * Obligatory utility methods.
 */
public final class Util {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Util() {
	}

	public static int getBit(int flag, int bit) {
		return (flag >> bit) & 0x01;
	}

	public static boolean isBitSet(int flag, int bit) {
		return getBit(flag, bit) > 0;
	}

	public static String toHex(int b) {
		String str = Integer.toHexString(b).toUpperCase();
		if (str.length()<2) {
			str = "0" + str;
		}
		return str;
	}

	public static String toHexWord(int b) {
		String str = Integer.toHexString(b).toUpperCase();
		while (str.length()<4) {
			str = "0" + str;
		}
		return str;
	}

}