package org.fife.emu;

import org.fife.emu.cpu.*;


/**
 * Representation of an object containing a CPU and some memory,
 * such as an arcade game or a video game console.
 *
 * @author Robert Futrell
 * @version 0.1
 */
public interface CpuContext {

	/**
	 * Returns the CPU.
	 *
	 * @return The CPU.
	 */
	Cpu getCpu();

	/**
	 * Returns the main memory for this CPU context.
	 *
	 * @return Main memory.  This is an array of bytes.
	 */
	int[] getMemory();

	/**
	 * Loads the specified ROM into memory.  Note that this CPU
	 * will not use a copy of this ROM; it will use the passed-in
	 * array itself as main memory.
	 *
	 * @param rom The ROM.
	 */
	void loadROM(int[] rom);

	/**
	 * Returns a byte from memory.
	 *
	 * @param address The address from which to read.
	 * @return The byte at the specified address in memory.
	 * @see #writeByte(int, int)
	 */
	int readByte(int address);

	int readByteSafely(int address);

	/**
	 * Returns a word from memory.
	 *
	 * @param address The address from which to read.
	 * @return The word of memory read.
	 * @see #writeWord(int, int)
	 */
	int readWord(int address);

	/**
	 * Writes the specified byte at the specified address.
	 *
	 * @param address The address at which to write.
	 * @param value The byte to write.
	 * @see #readByte(int)
	 * @see #writeWord(int, int)
	 */
	void writeByte(int address, int value);

	/**
	 * Writes a word to memory.
	 *
	 * @param address The address at which to start writing (the bytes
	 *        at <code>address</code> and <code>address+1</code> will
	 *        be written to).
	 * @param value The 16-bit value to write.
	 * @see #readWord(int)
	 * @see #writeByte(int, int)
	 */
	void writeWord(int address, int value);

}