package org.fife.emu;


/**
 * Default implementation of a memory read handler.  This class
 * simply reads a byte from the <code>CpuContext</code>'s memory.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class DefaultMemoryReadHandler implements MemoryReadHandler {

	/**
	 * The CPU context whose memory we are accessing.
	 */
	protected AbstractCpuContext context;

	/**
	 * Constructor.
	 *
	 * @param context The CPU context whose memory we will access.
	 */
	public DefaultMemoryReadHandler(AbstractCpuContext context) {
		this.context = context;
	}

	/**
	 * Reads a byte from the specified address.
	 *
	 * @param address The address to read from.
	 * @return The byte read.
	 */
	public int read(int address) {
		return context.memory[address];
	}

}