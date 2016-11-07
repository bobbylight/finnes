package org.fife.emu.cpu;

import java.beans.*;
import java.io.*;

import org.fife.emu.*;


/**
 * Base implementation of a CPU.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class AbstractCpu implements Cpu, Serializable {

	/**
	 * The context in which this CPU is running.
	 */
	protected CpuContext context;

	/**
	 * The number of cycles left to run during this execution
	 * cycle.
	 */
	protected int cycles;

	/**
	 * The program counter.
	 */
	protected int pc;

	/**
	 * The stack pointer.
	 */
	protected int sp;

	/**
	 * The clock speed of this CPU, in Hz.
	 */
	protected float clockSpeed;

	private PropertyChangeSupport propertyChangeSupport;

	/**
	 * Constructor.
	 *
	 * @param context The context for this CPU.
	 * @param clockSpeed The clock speed of this CPU.
	 */
	public AbstractCpu(CpuContext context, float clockSpeed) {
		this.context = context;
		propertyChangeSupport = new PropertyChangeSupport(this);
		setClockSpeed(clockSpeed);
		cycles = 0;
	}

	/**
	 * Adds a property change listener to this CPU.
	 *
	 * @param listener The listener to add.
	 * @see #removePropertyChangeListener(PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Burns the given number of cycles.  This can be called by
	 * other parts of the emulated system (such as the CPU context)
	 * when an operation is performed that burns extra CPU cycles.
	 *
	 * @param cycles The number of cycles to burn.
	 * @return The number of cycles this CPU has left to execute.
	 * @see #getCycles()
	 */
	public int burnCycles(int cycles) {
		this.cycles -= cycles;
		return this.cycles;
	}

	/**
	 * Notifies all listeners of a float property change.
	 *
	 * @param name The name of the property.
	 * @param oldVal The old value of the property.
	 * @param newVal The new value of the property.
	 */
	protected void firePropertyChange(String name, float oldVal, float newVal) {
		Float old = new Float(oldVal);
		Float n   = new Float(newVal);
		propertyChangeSupport.firePropertyChange(name, old, n);
	}

	/**
	 * Returns the clock speed of this CPU.
	 *
	 * @return The clock speed of this CPU, in Hz.
	 * @see #setClockSpeed(float)
	 */
	public float getClockSpeed() {
		return clockSpeed;
	}

	/**
	 * Returns the context in which this CPU is running.  This context
	 * represents the machine in which this CPU is running.
	 *
	 * @return The CPU context.
	 * @see org.fife.emu.CpuContext
	 */
	public CpuContext getCpuContext() {
		return context;
	}

	/**
	 * Returns the number of cycles left for this CPU to
	 * run in this execution cycle.
	 *
	 * @return The number of cycles left to run.
	 * @see #burnCycles(int)
	 */
	public int getCycles() {
		return cycles;
	}

	/**
	 * Returns the program counter.
	 *
	 * @return The program counter.
	 */
	public int getPC() {
		return pc;
	}

	/**
	 * Returns the stack pointer.
	 *
	 * @return The stack pointer.
	 */
	public int getSP() {
		return sp;
	}

	/**
	 * Removes the specified property change listener from this CPU.
	 *
	 * @param listener The listener to remove.
	 * @see #addPropertyChangeListener(PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Sets the clock speed of this CPU.  This method does not usually
	 * have to be called since the clock speed for a CPU is often set
	 * in its constructor.<p>
	 *
	 * This method fires a property change event of type
	 * <code>{@link org.fife.emu.cpu.Cpu#PROPERTY_CLOCK_SPEED}</code>.
	 *
	 * @param speed The clock speed of this CPU, in Hz.
	 * @see #getClockSpeed
	 */
	public void setClockSpeed(float speed) {
		if (speed!=this.clockSpeed) {
			float old = this.clockSpeed;
			this.clockSpeed = speed;
			firePropertyChange(PROPERTY_CLOCK_SPEED, old, speed);
		}
	}

}