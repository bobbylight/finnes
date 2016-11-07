package org.fife.emu.cpu;


/**
 * Gets notified when bytes are written to one of a cpu's output ports.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface OutputPortReader {

	/**
	 * Called when the value of an output port changes.
	 *
	 * @param port The port number.
	 * @param oldVal The old value of the port.
	 * @param newVal The new value of the port.
	 */
	void outputPortValueChanged(int port, int oldVal, int newVal);

}