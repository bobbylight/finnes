package org.fife.emu.cpu.n6502;

import org.fife.emu.CpuContext;

/**
 * An instruction in the 6502 CPU.
 */
public interface Instruction {

	String getName();

	int getByteCount();

	int getCycles();

	int getOpcode();

	String toString(int[] bytes, CpuContext context);

}