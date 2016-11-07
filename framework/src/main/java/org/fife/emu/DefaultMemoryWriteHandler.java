package org.fife.emu;


/**
 * Default implementation of a memory write handler.  This class
 * simply write a byte to the <code>CpuContext</code>'s memory.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class DefaultMemoryWriteHandler implements MemoryWriteHandler {

	/**
	 * The CPU context whose memory we are accessing.
	 */
	protected AbstractCpuContext context;

	/**
	 * Constructor.
	 *
	 * @param context The CPU context whose memory we will access.
	 */
	public DefaultMemoryWriteHandler(AbstractCpuContext context) {
		this.context = context;
	}

	/**
	 * Writes a write to the specified address, doing any special
	 * handling if necessary.
	 *
	 * @param address The address to write to.
	 * @param b The byte to write.
	 */
	public void write(int address, int b) {
		context.memory[address] = b;
	}

}