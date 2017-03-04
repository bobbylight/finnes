package org.fife.emu;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serializable;


/**
 * Base implementation of a CPU context.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class AbstractCpuContext implements CpuContext, Serializable {

	/**
	 * Memory.
	 */
	protected int[] memory;

	/**
	 * Handlers for memory read accesses.
	 */
	protected MemoryReadHandler[] memoryReadHandlers;

	/**
	 * Handlers for memory writes.
	 */
	protected MemoryWriteHandler[] memoryWriteHandlers;

	/**
	 * Configures the memory read handlers used.  By default a
	 * standard memory read handler that just reads from this
	 * context's memory array is installed for all addresses.
	 * Subclasses can override this method and install custom
	 * read handlers to handle things like memory-mapped ports.
	 *
	 * @see #configureMemoryWriteHandlers()
	 */
	protected void configureMemoryReadHandlers() {
		int count = memory.length;
		memoryReadHandlers = new MemoryReadHandler[count];
		MemoryReadHandler readHandler = new DefaultMemoryReadHandler(this);
		for (int i = 0; i < count; i++) {
			memoryReadHandlers[i] = readHandler;
		}
	}

	/**
	 * Configures the memory write handlers used.  By default a
	 * standard memory write handler that just writes to this
	 * context's memory array is installed for all addresses.
	 * Subclasses can override this method and install custom
	 * write handlers to handle things like memory-mapped ports.
	 *
	 * @see #configureMemoryReadHandlers()
	 */
	protected void configureMemoryWriteHandlers() {
		int count = memory.length;
		memoryWriteHandlers = new MemoryWriteHandler[count];
		MemoryWriteHandler writeHandler = new DefaultMemoryWriteHandler(this);
		for (int i = 0; i < count; i++) {
			memoryWriteHandlers[i] = writeHandler;
		}
	}

	/**
	 * Returns the main memory for this CPU context.
	 *
	 * @return Main memory.  This is an array of bytes.
	 */
	@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Memory array returned for performance reasons")
	public int[] getMemory() {
		return memory;
	}

	/**
	 * Loads the specified ROM into memory.  Note that this CPU
	 * will not use a copy of this ROM; it will use the passed-in
	 * array itself as main memory.
	 *
	 * @param rom The ROM.
	 */
	public void loadROM(int[] rom) {

		int size = rom.length;
		memory = new int[size];
		System.arraycopy(rom,0, memory,0, size);

		configureMemoryReadHandlers();
		configureMemoryWriteHandlers();

	}

	/**
	 * Returns a byte from memory.
	 *
	 * @param address The address from which to read.
	 * @return The byte at the specified address in memory.
	 * @see #writeByte(int, int)
	 */
	public int readByte(int address) {
		return memoryReadHandlers[address].read(address);
	}

	/**
	 * Returns a word from memory.
	 *
	 * @param address The address from which to read.
	 * @return The word of memory read.
	 * @see #writeWord(int, int)
	 */
	public int readWord(int address) {
		int val = memoryReadHandlers[address].read(address);
		address++;
		return val | (memoryReadHandlers[address].read(address) << 8);
	}

	/**
	 * Writes the specified byte at the specified address.
	 *
	 * @param address The address at which to write.
	 * @param value The byte to write.
	 * @see #readByte(int)
	 * @see #writeWord(int, int)
	 */
	public void writeByte(int address, int value) {
		value &= 0xff;
		memoryWriteHandlers[address].write(address, value);
	}

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
	public void writeWord(int address, int value) {
		memoryWriteHandlers[address].write(address, value & 0xff);
		address++;
		memoryWriteHandlers[address].write(address, (value >> 8) & 0xff);
	}

}