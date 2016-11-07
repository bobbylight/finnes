package org.fife.emu.cpu;


/**
 * Implementations of this class write values to
 * <code>Cpu</code> input ports.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface InputPortWriter {

	/**
	 * Called when the Cpu reads from a port.
	 *
	 * @param port The port being read.
	 * @return The byte value read from the specified port.
	 */
	int getPortValue(int port);

}