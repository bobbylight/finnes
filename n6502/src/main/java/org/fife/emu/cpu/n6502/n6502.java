package org.fife.emu.cpu.n6502;

import java.io.Serializable;

import org.fife.emu.cpu.*;


/**
 * Interface for a 6502 CPU.  This CPU is what the NES CPU was based on.
 *
 * @author Robert Futrell
 * @version 1.0
 */
@SuppressWarnings("checkstyle:TypeName")
public interface n6502 extends Cpu, Serializable {


	/**
	 * Hook method that can be overridden and called by subclasses
	 * to implement functionality that occurs at the end of an
	 * emulated "frame."  The default implementation does nothing.
	 */
	void endTimeFrame();

	/**
	 * Performs an IRQ (maskable interrupt), if the
	 * interrupt disable flag is not set.
	 *
	 * @see #nmi()
	 */
	void irq();

	/**
	 * Performs an NMI (non-maskable interrupt).
	 *
	 * @see #irq()
	 */
	void nmi();

}