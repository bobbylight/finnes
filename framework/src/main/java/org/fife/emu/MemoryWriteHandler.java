package org.fife.emu;


/**
 * Handles a write to a <code>CpuContext</code>'s memory.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface MemoryWriteHandler {

	/**
	 * Writes a write to the specified address, doing any special
	 * handling if necessary.
	 *
	 * @param address The address to write to.
	 * @param b The byte to write.
	 */
	void write(int address, int b);

}