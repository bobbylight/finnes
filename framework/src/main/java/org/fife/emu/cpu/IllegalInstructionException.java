package org.fife.emu.cpu;


/**
 * An exception that is thrown when an illegal opcode is found in ROM.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class IllegalInstructionException
							extends UnemulatedInstructionException {

	private static final long serialVersionUID = -6027470223802588384L;

	/**
	 * Constructor.
	 *
	 * @param opcode The instruction encountered that is illegal.
	 */
	public IllegalInstructionException(int opcode) {
		super(opcode);
	}

	/**
	 * Constructor.
	 *
	 * @param opcode The instruction encountered that is illegal.
	 * @param msg A description of the exception.
	 */
	public IllegalInstructionException(int opcode, String msg) {
		super(opcode, msg);
	}

	/**
	 * Returns a description of this exception.
	 *
	 * @return A description of this exception.
	 */
	public String getMessage() {
		String msg = "Illegal instruction: " + getHexStringUWord(getOpcode());
		if (super.getMessage() != null) {
			msg += " (" + super.getMessage() + ")";
		}
		return msg;
	}

}