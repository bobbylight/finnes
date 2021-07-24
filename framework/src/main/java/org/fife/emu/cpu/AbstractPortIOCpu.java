package org.fife.emu.cpu;

import java.io.*;
import java.util.*;

import org.fife.emu.*;


/**
 * Base class for Cpu implementations that do some I/O through
 * ports.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class AbstractPortIOCpu extends AbstractCpu
									implements PortIOCpu, Serializable {

	private InputPortWriter inputPortWriter;
	private List<OutputPortReader> outputPortReaderList;
	protected int[] outputPorts;	// Output ports (256).

	/**
	 * Constructor.
	 *
	 * @param context The context for this CPU.
	 * @param clockSpeed The clock speed of this CPU.
	 * @param writer The input port writer.
	 * @param outputPortCount The number of output ports.
	 */
	public AbstractPortIOCpu(CpuContext context, float clockSpeed,
								InputPortWriter writer, int outputPortCount) {
		super(context, clockSpeed);
		outputPortReaderList = new ArrayList<>(1);
		this.inputPortWriter = writer;
		outputPorts = new int[outputPortCount];
	}

	/**
	 * Adds an output port reader (listener).
	 *
	 * @param r The reader.
	 * @see #removeOutputPortReader(OutputPortReader)
	 */
	public void addOutputPortReader(OutputPortReader r) {
		outputPortReaderList.add(r);
	}

	/**
	 * Reads the current byte value of the specified input port.
	 *
	 * @param port The port number.
	 * @return The byte value of the port.
	 * @see #readOutputPort(int)
	 */
	public int readInputPort(int port) {
		return inputPortWriter.getPortValue(port) & 0xff;
	}

	/**
	 * Reads the current byte value of the specified output port.
	 *
	 * @param port The port number.
	 * @return The byte value of the port.
	 * @see #readInputPort(int)
	 */
	public int readOutputPort(int port) {
		return outputPorts[port];
	}

	/**
	 * Removes the specified output port reader.
	 *
	 * @param r The output port reader to remove.
	 * @return Whether the reader was registered with this Cpu.
	 * @see #addOutputPortReader(OutputPortReader)
	 */
	public boolean removeOutputPortReader(OutputPortReader r) {
		return outputPortReaderList.remove(r);
	}

	/**
	 * Writes a byte to an output port.
	 *
	 * @param port The port to write to.
	 * @param value The byte value to write.
	 * @see #readInputPort(int)
	 * @see #readOutputPort(int)
	 */
	protected final void writeOutputPort(int port, int value) {

		// Set the new value.
		int old = outputPorts[port];
		value &= 0xff;
		outputPorts[port] = value;

		// Notify any listeners of the port value change.
		int count = outputPortReaderList.size();
		for (int i = 0; i < count; i++) {
			OutputPortReader r = outputPortReaderList.get(i);
			r.outputPortValueChanged(port, old, value);
		}

	}

}