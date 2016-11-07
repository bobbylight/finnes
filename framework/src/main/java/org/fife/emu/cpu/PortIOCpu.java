package org.fife.emu.cpu;

import java.io.*;


/**
 * A Cpu that implements this interface does some of its I/O
 * through ports.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface PortIOCpu extends Cpu, Serializable {

	/**
	 * Adds an output port reader (listener).
	 *
	 * @param r The reader.
	 * @see #removeOutputPortReader(OutputPortReader)
	 */
	void addOutputPortReader(OutputPortReader r);

	/**
	 * Reads the current byte value of the specified input port.
	 *
	 * @param port The port number.
	 * @return The byte value of the port.
	 * @see #readOutputPort(int)
	 */
	int readInputPort(int port);

	/**
	 * Reads the current byte value of the specified output port.
	 *
	 * @param port The port number.
	 * @return The byte value of the port.
	 * @see #readInputPort(int)
	 */
	int readOutputPort(int port);

	/**
	 * Removes the specified output port reader.
	 *
	 * @param r The output port reader to remove.
	 * @return Whether the reader was registered with this Cpu.
	 * @see #addOutputPortReader(OutputPortReader)
	 */
	boolean removeOutputPortReader(OutputPortReader r);

}