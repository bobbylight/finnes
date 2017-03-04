package org.fife.emu.cpu;


/**
 * An exception that is thrown when an instruction is found that
 * is not emulated.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class UnemulatedInstructionException
							extends UnsupportedOperationException {

	private static final long serialVersionUID = -2133014310628460179L;

	private int opcode;

	/**
	 * Constructor.
	 *
	 * @param opcode The instruction encountered that is not emulated.
	 */
	public UnemulatedInstructionException(int opcode) {
		this(opcode, null);
	}

	/**
	 * Constructor.
	 *
	 * @param opcode The instruction encountered that is not emulated.
	 * @param msg A description of the exception.
	 */
	public UnemulatedInstructionException(int opcode, String msg) {
		super(msg);
		this.opcode = opcode;
	}

	/**
	 * Returns a 4-character, string representation of the specified
	 * unsigned word.
	 *
	 * @param uword An unsigned word.
	 * @return A 4-character string representation (e.g. if <code>uword</code>
	 *         is <code>15</code>, this method returns <code>0x000f</code>).
	 */
	protected static String getHexStringUWord(int uword) {
		String str = Integer.toHexString(uword);
		while (str.length() < 4) {
			str = "0" + str;
		}
		return "0x" + str;
	}

	/**
	 * Returns a description of this exception.
	 *
	 * @return A description of this exception.
	 */
	public String getMessage() {
		String msg = "Unemulated instruction: " + getHexStringUWord(getOpcode());
		if (super.getMessage() != null) {
			msg += " (" + super.getMessage() + ")";
		}
		return msg;
	}

	/**
	 * Returns the opcode that caused this exception.
	 *
	 * @return The opcode.
	 */
	public int getOpcode() {
		return opcode;
	}

}