package org.fife.emu.cpu;

import java.io.*;

import org.fife.emu.*;


/**
 * A CPU in an emulator.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface Cpu extends Serializable {

	/**
	 * Property fired when the clock speed of this CPU changes.
	 */
	String PROPERTY_CLOCK_SPEED		= "ClockSpeed";

	/**
	 * Burns the given number of cycles.  This can be called by
	 * other parts of the emulated system (such as the CPU context)
	 * when an operation is performed that burns extra CPU cycles.
	 *
	 * @param cycles The number of cycles to burn.
	 * @return The number of cycles this CPU has left to execute.
	 * @see #getCycles()
	 */
	int burnCycles(int cycles);

	/**
	 * Dumps the number of times each CPU instruction was executed
	 * to a file.<p>
	 *
	 * Most CPU cores will only honor this method if a debug flag
	 * is set, otherwise they will do nothing.
	 */
	void dumpOpcodeCounts();

	/**
	 * Runs this CPU for the given number of cycles.
	 *
	 * @param cycles The number of cycles to run.
	 * @return The number of cycles that the CPU burned over
	 *         <code>cycles</code>.  This will be a number
	 *         less than <code>1</code>.
	 */
	int execute(int cycles);

	/**
	 * Returns the clock speed of this CPU.
	 *
	 * @return The clock speed of this CPU, in Hz.
	 * @see #setClockSpeed(float)
	 */
	float getClockSpeed();

	/**
	 * Returns the context in which this CPU is running.  This
	 * context represents the machine in which this CPU is running.
	 *
	 * @return The CPU context.
	 * @see org.fife.emu.CpuContext
	 */
	CpuContext getCpuContext();

	/**
	 * Returns the number of cycles left for this CPU to
	 * run in this execution cycle.
	 *
	 * @return The number of cycles left to run.
	 * @see #burnCycles(int)
	 */
	int getCycles();

	/**
	 * Returns the program counter.
	 *
	 * @return The program counter.
	 */
	int getPC();

	/**
	 * Returns the stack pointer.
	 *
	 * @return The stack pointer.
	 */
	int getSP();

	/**
	 * Resets this CPU.
	 */
	void reset();

	/**
	 * Sets the clock speed of this CPU.  This method does not usually have
	 * to be called since the clock speed for a CPU is often set in its
	 * constructor.<p>
	 *
	 * This method fires a property change event of type
	 * <code>CLOCK_SPEED</code>.
	 *
	 * @param speed The clock speed of this CPU, in Hz.
	 * @see #getClockSpeed()
	 */
	void setClockSpeed(float speed);

	/**
	 * Stops this CPU.
	 */
	void stop();

}