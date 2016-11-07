package org.fife.emu;


/**
 * Handles a read from a <code>CpuContext</code>'s memory.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface MemoryReadHandler {

	/**
	 * Reads a byte from the specified address, doing any special
	 * handling if necessary.
	 *
	 * @param address The address to read from.
	 * @return The byte read.
	 */
	int read(int address);

}