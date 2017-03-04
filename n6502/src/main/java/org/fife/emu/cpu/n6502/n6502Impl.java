package org.fife.emu.cpu.n6502;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.fife.emu.*;
import org.fife.emu.cpu.*;


/**
 * Implementation of a 6502 CPU.  This CPU is what the NES CPU
 * was based on.
 *
 * @author Robert Futrell
 * @version 1.0
 */
@SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "CPU name is OK for class name")
@SuppressWarnings({"checkstyle:TypeName", "checkstyle:MethodName", "checkstyle:WhitespaeAround"})
public class n6502Impl extends AbstractCpu implements n6502, Serializable {

	private static final int MIN_DEBUG_OUT_INDEX = 0;

	private static final long serialVersionUID = 2920750437770524090L;

	/*
	 * JLS 14.21 states that most optimizing compilers (such as javac)
	 * will optimize away "if" statements where conditional is always
	 * false.
	 */
	private static final boolean DEBUG_OPCODE_COUNTS = false;
	private static final boolean DEBUG_COUNT_EXECUTED_INSTRUCTIONS = true;

	protected int flagC;
	protected int flagI;
	protected int flagD;
	protected int flagB;
	protected int flagV;
	protected int flagNZ; // Use 9 bits (not 8!), 0-6=>Z, 7=>N&Z, 8=>N

	protected int a;  // 8 bits
	protected int x;  // 8 bits
	protected int y;  // 8 bits

	protected boolean halted;

	private long[] opcodeCounts;
	private long executedInstructionCount;
	protected long totCycles;
	protected int endCycles;

	private Debug6502State stateLogger;
	private boolean logState;

	private int debugOutIndex;
	@SuppressFBWarnings(value = "UWF_UNWRITTEN_FIELD", justification = "Just for debugging purposes")
	private PrintWriter debugOut;
	private long debugExecutedInstructionCount;

	private static final int STACK_BOTTOM = 0x100;   // 0x100-0x1ff

	private static final int N_BIT = 0x80;

	/**
	 * Constructor.
	 *
	 * @param context    The CPU context.
	 * @param clockSpeed The clock speed of this CPU.
	 */
	@SuppressFBWarnings(value = "NM_CLASS_NAMING_CONVENTION", justification = "CPU name is OK for class name")
	public n6502Impl(CpuContext context, float clockSpeed) {

		super(context, clockSpeed);

		if (DEBUG_OPCODE_COUNTS) {
			opcodeCounts = new long[256];
		}

		//reset();
		stateLogger = new Debug6502State(this);

	}

	/**
	 * Utility method that returns the byte at PC as an
	 * absolute indexed address.
	 *
	 * @param index The index.
	 * @return The address.
	 */
	private int _absIndexed(int index) {
		int temp = context.readWord(pc);
		pc += 2;
		int address = temp + index;
		crossingPageBoundary(temp, address);
		return address;
	}

	/**
	 * Utility method that returns the indirect indexed
	 * address pointed to by PC and Y.
	 *
	 * @return The address.
	 * @see #_preIndIndX()
	 */
	private int _postIndIndY() {
		int temp = context.readWord(context.readByte(pc++));
		crossingPageBoundary(temp, temp + y);
		//return (temp+y)&0xffff;
		return temp + y;
	}

	/**
	 * Utility method that returns the indexed indirect
	 * address pointed to by PC and X.
	 *
	 * @return The address.
	 * @see #_postIndIndY()
	 */
	private int _preIndIndX() {
		int data = (context.readByte(pc++) + x) & 0xff;
		return context.readWord(data);
	}

	/**
	 * Utility method that returns the byte at PC as a
	 * zero-page address.
	 *
	 * @return The address.
	 * @see #_zeroPageIndexed(int)
	 */
	private int _zeroPage() {
		return context.readByte(pc++);
	}

	/**
	 * Utility method that returns the byte at PC as a
	 * zero-page indexed address.
	 *
	 * @return The address.
	 * @see #_zeroPage()
	 */
	private int _zeroPageIndexed(int index) {
		return (context.readByte(pc++) + index) & 0xff;
	}

	/**
	 * Performs an absolute-index-addressed read (LDA, LDX,
	 * LDY, EOR, AND, ORA, ADC, SBC, CMP, BIT, LAX, LAE,
	 * SHS, NOP).
	 *
	 * @param index The index.
	 * @return The byte read.
	 */
	private int absoluteIndex_Read(int index) {
		return context.readByte(_absIndexed(index));
	}

	/**
	 * Performs an absolute-index-addressed write
	 * (STA, STX, STY, SHA, SHX, SHY).
	 *
	 * @param index The index.
	 * @param b     The byte to write.
	 */
	private void absoluteIndex_Write(int index, int b) {
		context.writeByte(_absIndexed(index), b);
	}

	/**
	 * Performs an absolute-addressed read (LDA, LDX,
	 * LDY, EOR, AND, ORA, ADC, SBC, CMP, BIT, LAX, NOP).
	 *
	 * @return The byte read.
	 */
	private int absolute_Read() {
		int val = context.readByte(context.readWord(pc));
		pc += 2;
		return val;
	}

	/**
	 * Performs an absolute-addressed write (STA, STX,
	 * STY, SAX).
	 *
	 * @param b The byte to write.
	 */
	private void absolute_Write(int b) {
		context.writeByte(context.readWord(pc), b);
		pc += 2;
	}

	/**
	 * If two memory addresses cross a page boundary, the remaining
	 * cycles count is decremented.
	 *
	 * @param address1 The first address.
	 * @param address2 The second address.
	 */
	private void crossingPageBoundary(int address1, int address2) {
		cycles += ((address1 ^ address2) & 0x100) >> 8;
	}

	/**
	 * Executes an ADC instruction.  All flags are updated
	 * appropriately.
	 *
	 * @param val The value to ADC to the A register.
	 */
	protected void doADC(int val) {
// TODO: Implement BCD mode.
//		if (flagD>0) {
//			throw new InternalError("ADC instruction in BCD mode (not implemented)");
//		}
//		else {
		int temp = a + val + flagC;
		flagC = (temp & 0x100) >> 8;
		flagV = ((~(a ^ val)) & (a ^ temp) & 0x80) >> 7;
		flagNZ = a = temp & 0xff;
//		}
	}

	/**
	 * Executes an AND instruction.  All flags are updated
	 * appropriately.
	 *
	 * @param val The value to logically AND to the A
	 *            register.
	 */
	private void doAND(int val) {
		flagNZ = a &= val;
	}

	/**
	 * Performs an ASL instruction.  Flags are updated
	 * appropriately.
	 *
	 * @param b The byte to ASL.
	 * @return The modified byte.
	 */
	private int doASL(int b) {
		flagC = (b >> 7) & 0x01;
		return flagNZ = (b << 1) & 0xff;
	}

	/**
	 * Performs an ASL instruction that operates on memory.
	 * Flags are updated appropriately.
	 *
	 * @param address The address in memory to ASL.
	 */
	private void doASLMem(int address) {
		int val = context.readByte(address);
		context.writeByte(address, val); // Write back
		val = doASL(val);
		context.writeByte(address, val);
	}

	/**
	 * Executes a BIT instruction.  All flags are updated
	 * appropriately.
	 *
	 * @param b The byte to test.
	 */
	private void doBIT(int b) {
		// Z flag set from result ("a&b"), while N flag is set
		// from just "b".
		flagNZ = ((b & a) > 0 ? 1 : 0) | ((b & 0x80) << 1);
		flagV = (b & 0x40) >> 6;            // "V" flag is bit 6.
	}

	/**
	 * Executes one of the branch instructions.  The PC and
	 * cycle cound are updated appropriately.
	 *
	 * @param test Whether the branch condition was met.
	 * @return The number of cycles used doing the branch.
	 */
	private void doBranch(boolean test) {
		if (test) {
			cycles += 3; // 2 cycles + 1 for branch
			int offset = (byte)context.readByte(pc++); // Force to -128,127.
			crossingPageBoundary(pc, pc + offset); // +1 if on different page
			pc += offset;
		} else {
			pc++; // Skip branch address.
			cycles += 2;
		}
	}

	/**
	 * Performs a BRK instruction.  The program counter and flags
	 * are updated appropriately.
	 */
	private void doBRK() {
		context.readByte(pc++); // Throw away
		pushWord(pc);
		flagB = 1; // Push P with flag B set.
		pushByte(getRegP());
		flagI = 1; // Flags B and I are set
		pc = context.readWord(0xfffe);
	}

	/**
	 * Compares the specified byte to the specified register value.
	 * All flags are updated appropriately.
	 *
	 * @param reg The value of a register (one of <code>a</code>,
	 *            <code>x</code> or <code>y</code>).
	 * @param b   The byte to compare with the register.
	 */
	private void doCMP(int reg, int b) {
		int result = reg - b;
		flagC = (~result >> 8) & 0x01;
		flagNZ = result & 0xff;
	}

	/**
	 * Performs a DEC instruction.  All flags are updated
	 * appropriately.
	 *
	 * @param b The byte to decrement.
	 * @return The decremented byte.
	 */
	private int doDEC(int b) {
		return flagNZ = (b - 1) & 0xff;
	}

	/**
	 * Performs an DEC instruction that operates on memory.
	 * Flags are updated appropriately.
	 *
	 * @param address The address in memory to DEC.
	 */
	private void doDECMem(int address) {
		int val = context.readByte(address);
		context.writeByte(address, val); // Write back
		val = doDEC(val);
		context.writeByte(address, val);
	}

	/**
	 * Performs an EOR (XOR) instruction.  All flags are
	 * updated appropriately.
	 *
	 * @param b The byte to EOR with the <code>a</code>
	 *          register.
	 */
	private void doEOR(int b) {
		flagNZ = a ^= b;
	}

	/**
	 * Performs a INC instruction.  All flags are updated
	 * appropriately.
	 *
	 * @param b The byte to increment.
	 * @return The incremented byte.
	 */
	private int doINC(int b) {
		return flagNZ = (b + 1) & 0xff;
	}

	/**
	 * Performs an INC instruction that operates on memory.
	 * Flags are updated appropriately.
	 *
	 * @param address The address in memory to INC.
	 */
	private void doINCMem(int address) {
		int val = context.readByte(address);
		context.writeByte(address, val); // Write back
		val = doINC(val);
		context.writeByte(address, val);
	}

	/**
	 * Performs an ISC (aka ISB, INS) instruction on
	 * memory (which is all it ever operates on).
	 * All flags are updated appropriately.
	 *
	 * @param address The memory address to increment.
	 */
	private void doISCMem(int address) {
		int val = context.readByte(address);
		context.writeByte(address, val); // Write back
		val = (val + 1) & 0xff;
		doSBC(val);
		context.writeByte(address, val);
	}

	/**
	 * Performs a KIL/JAM/HLT instruction.
	 */
	private void doKIL() {
		halted = true;
		cycles = 0;
	}

	/**
	 * Performs an LAX instruction.  All flags are updated
	 * appropriately.
	 *
	 * @param b The byte to load into the <code>a</code>
	 *          and <code>x</code> registers.
	 */
	private void doLAX(int b) {
		flagNZ = a = x = b;
	}

	/**
	 * Performs an LDA instruction.  All flags are updated
	 * appropriately.
	 *
	 * @param b The byte to load into the <code>a</code>
	 *          register.
	 */
	private void doLDA(int b) {
		flagNZ = a = b;
	}

	/**
	 * Performs an LDX instruction.  All flags are updated
	 * appropriately.
	 *
	 * @param b The byte to load into the <code>x</code>
	 *          register.
	 */
	private void doLDX(int b) {
		flagNZ = x = b;
	}

	/**
	 * Performs an LDY instruction.  All flags are updated
	 * appropriately.
	 *
	 * @param b The byte to load into the <code>y</code>
	 *          register.
	 */
	private void doLDY(int b) {
		flagNZ = y = b;
	}

	/**
	 * Performs an LSR instruction.  Flags are updated appropriately.
	 *
	 * @param b The byte to LSR.
	 * @return The modified byte.
	 */
	private int doLSR(int b) {
		flagC = b & 0x01;
		return flagNZ = b >> 1;
	}

	/**
	 * Performs an LSR instruction that operates on memory.
	 * Flags are updated appropriately.
	 *
	 * @param address The address in memory to LSR.
	 */
	private void doLSRMem(int address) {
		int val = context.readByte(address);
		context.writeByte(address, val); // Write back
		val = doLSR(val);
		context.writeByte(address, val);
	}

	/**
	 * Executes an ORA instruction.  All flags are updated
	 * appropriately.
	 *
	 * @param val The value to logically OR to the
	 *            <code>A</code> register.
	 */
	private void doORA(int val) {
		flagNZ = a |= val;
	}

	/**
	 * Performs an ROL instruction.  Flags are updated appropriately.
	 *
	 * @param b The byte to ROL.
	 * @return The modified byte.
	 */
	private int doROL(int b) {
		flagNZ = ((b << 1) & 0xff) | flagC;
		flagC = (b >> 7) & 0x01;
		return flagNZ;
	}

	/**
	 * Performs an ROL instruction that operates on memory.
	 * Flags are updated appropriately.
	 *
	 * @param address The address in memory to ROL.
	 */
	private void doROLMem(int address) {
		int val = context.readByte(address);
		context.writeByte(address, val); // Write back
		val = doROL(val);
		context.writeByte(address, val);
	}

	/**
	 * Performs an ROR instruction.  Flags are updated appropriately.
	 *
	 * @param b The byte to ROR.
	 * @return The modified byte.
	 */
	private int doROR(int b) {
		flagNZ = (b >> 1) | (flagC << 7);
		flagC = b & 0x01;
		return flagNZ;
	}

	/**
	 * Performs an ROR instruction that operates on memory.
	 * Flags are updated appropriately.
	 *
	 * @param address The address in memory to ROR.
	 */
	private void doRORMem(int address) {
		int val = context.readByte(address);
		context.writeByte(address, val); // Write back
		val = doROR(val);
		context.writeByte(address, val);
	}

	/**
	 * Performs an RTI instruction.  Status flags and the
	 * program counter are set appropriately.
	 */
	private void doRTI() {
		context.readByte(pc); // Throw away
		setRegP(popByte());
		pc = popWord();
	}

	/**
	 * Performs an SBC instruction.  All flags are
	 * updated appropriately.
	 *
	 * @param b The byte to subtract from the
	 *          <code>A</code> register.
	 */
	protected void doSBC(int b) {
// TODO: Implement BCD mode.
//		if (flagD>0) {
//			throw new InternalError("SBC instruction in bcd mode (not implemented)");
//		}
//		else {
		int temp = a - b - (flagC ^ 0x01);
		flagV = ((a ^ b) & (a ^ temp) & 0x80) >> 7;
		flagNZ = a = temp & 0xff;
		flagC = ((~temp) >> 8) & 0x01;
//		}
	}

	/**
	 * Performs an SLO instruction that operates on memory.
	 * All flags are updated appropriately.
	 *
	 * @param address The address in memory to SLO.
	 */
	private void doSLOMem(int address) {
		int val = context.readByte(address);
		context.writeByte(address, val); // Write the value back.
		flagC = (val >> 7) & 0x01;
		val = (val << 1) & 0xff;
		context.writeByte(address, val);
		flagNZ = a |= val;
	}

	/**
	 * Dumps the number of times each n6502 instruction was executed
	 * to a file.<p>
	 * <p>
	 * If {@link #DEBUG_OPCODE_COUNTS} is not set to
	 * <code>true</code>, this method will not dump out instruction
	 * counts.
	 */
	public void dumpOpcodeCounts() {
		try {
			PrintWriter out = new PrintWriter(new File("n6502_opcodeCounts.txt"),
				Charset.defaultCharset().name());
			if (!DEBUG_OPCODE_COUNTS) {
				out.println("Opcode counting was not enabled for the n6502");
				out.close();
				return;
			}
			for (int i = 0; i < 256; i++) {
				out.println(i + ":\t" + opcodeCounts[i]);
			}
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private int earliestIRQBefore(int time) {
		if (flagI == 0) {
			int irqTime = 99999;//apu.earliest_irq();
			if (irqTime < time) {
				time = irqTime;
			}
		}
		return time;
	}

	/**
	 * Hook method that can be overridden and called by subclasses
	 * to implement functionality that occurs at the end of an
	 * emulated "frame."  The default implementation does nothing.
	 */
	public void endTimeFrame() {
	}

	/**
	 * Runs this CPU for the given number of cycles.
	 *
	 * @param c The number of cycles to run.
	 * @return The number of cycles that the CPU burned over
	 * <code>cycles</code>.  This will be a number
	 * less than <code>1</code>.
	 */
	public int execute(int c) {
		return executeUntil(cycles + c);
	}

	protected int executeUntil(int until) {

		if (halted) {
			return 0;
		}

		while (cycles < until) {
			endCycles = until;
//			endCycles = earliestIRQBefore(until);
//			if (endCycles<=cycles) {
//				irq();
//				endCycles = until;
//			}

			execute();

		}

		return cycles;

	}

	@SuppressWarnings("checkstyle:MethodLength")
	protected void execute() {

		int addr;

		while (cycles < endCycles) {

			if (logState) {
				try {
					stateLogger.log();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}

			int opcode = context.readByte(pc++);

//if (org.fife.emu.finnes.Debug.DO_DEBUG) {
//if ((debugExecutedInstructionCount%200000)==0) {
//	if (++debugOutIndex>MIN_DEBUG_OUT_INDEX) {
//System.err.println("Yay - " + debugOutIndex);
//		if (debugOut!=null)
//			debugOut.close();
//		try {
//			debugOut = new PrintWriter(new BufferedWriter(new FileWriter("n6502" + debugOutIndex + ".txt")));
//		} catch (IOException ioe) { System.exit(0); }
//	}
//}
//if (debugOut!=null) {
//	org.fife.emu.finnes.hw.ppu.ppu2c02 ppu = ((org.fife.emu.finnes.hw.nes.NES)this.context).getPpu();
//	debugOut.println((pc-1) + " " + sp + " " + opcode + /*" " + cycles + */" | " +
//				a + " " + x + " " + y + " | " +
//				getFlagN() + " " + flagV + " " + flagD + " " + flagI + " " + getFlagZ() + " " + flagC + " | " +
//				ppu.getLatchClean() + " " + ppu.readByteSprRAM(1) + " " +
//				ppu.getRegisterClean(0x2000) + " " + ppu.getRegisterClean(0x2001) + " " + ppu.getRegisterClean(0x2002)
//	);
////	if ((executedInstructionCount%80)==0) {
//		debugOut.flush();
////	}
//}
//debugExecutedInstructionCount++;
//}
			if (DEBUG_OPCODE_COUNTS) {
				opcodeCounts[opcode]++;
			}
			if (DEBUG_COUNT_EXECUTED_INSTRUCTIONS) {
				executedInstructionCount++;
//if (executedInstructionCount%10000==0) {
//	System.out.println("executedInstructionCount == " + executedInstructionCount);
//}
			}

			switch (opcode) {

				case 0x00:    // BRK - Break
					doBRK();
					cycles += 7;
					break;

				case 0x01:    // ORA ($44,X) - Indirect,X
					doORA(indexedIndirect_Read());
					cycles += 6;
					break;

				case 0x02: // * KIL/JAM/HLT
				case 0x12:
				case 0x22:
				case 0x32:
				case 0x42:
				case 0x52:
				case 0x62:
				case 0x72:
				case 0x92:
				case 0xB2:
				case 0xD2:
				case 0xF2:
					doKIL();
					break;

				case 0x03: // * SLO ($44),Y - Indirect,Y
					doSLOMem(_preIndIndX());
					cycles += 8;
					break;

				case 0x04:    // * DOP/SKB $44 - Zero Page
				case 0x44:
				case 0x64:
					pc++;
					cycles += 3;
					break;

				case 0x05:    // ORA $44 - Zero Page
					doORA(zeroPage_Read());
					cycles += 3;
					break;

				case 0x06:    // ASL $44 - Zero page
					doASLMem(_zeroPage());
					cycles += 5;
					break;

				case 0x07: // * SLO - Zero Page
					doSLOMem(_zeroPage());
					cycles += 5;
					break;

				case 0x08:    // PHP - Immediate (PusH Processor status)
					context.readByte(pc); // Throw away
					pushByte(getRegP());
					cycles += 3;
					break;

				case 0x09:    // ORA #$44 - Immediate
					doORA(context.readByte(pc));
					pc++;
					cycles += 2;
					break;

				case 0x0A:    // ASL - SHL A
					a = doASL(a);
					cycles += 2;
					break;

				case 0x0B:
					throw new UnemulatedInstructionException(opcode);

				case 0x0C:    // * TOP/NOP/SKW - Absolute
					absolute_Read();
					cycles += 4;
					break;

				case 0x0D:    // ORA $4400 - Absolute
					doORA(absolute_Read());
					cycles += 4;
					break;

				case 0x0E:    // ASL $4400 - Absolute
					doASLMem(context.readWord(pc));
					pc += 2;
					cycles += 6;
					break;

				case 0x0F: // * SLO - Absolute
					doSLOMem(context.readWord(pc));
					pc += 2;
					cycles += 6;
					break;

				case 0x10:    // BPL - Branch on PLus
					doBranch(getFlagN() == 0);
					break;

				case 0x11:    // ORA ($44),Y - Indirect,Y
					doORA(indirectIndexed_Read()); // cycles-- if page boundary crossed.
					cycles += 5;
					break;

				// 0x12 handled previously

				case 0x13: // * SLO ($44),Y - Indirect,Y
					doSLOMem(_postIndIndY());
					cycles += 8;
					break;

				case 0x14:    // * DOP/SKB $44,X - Zero Page,X
				case 0x34:
				case 0x54:
				case 0x74:
				case 0xD4:
				case 0xF4:
					pc++;
					cycles += 4;
					break;

				case 0x15:    // ORA $44,X - Zero Page,X
					doORA(zeroPageIndexed_Read(x));
					cycles += 4;
					break;

				case 0x16:    // ASL $44,X - Zero Page,X
					doASLMem(_zeroPageIndexed(x));
					cycles += 6;
					break;

				case 0x17: // * SLO - Zero Page,X
					doSLOMem(_zeroPageIndexed(x));
					cycles += 6;
					break;

				case 0x18:    // CLC - CLear Carry flag
					flagC = 0;
					cycles += 2;
					break;

				case 0x19:    // ORA $4400,Y - Absolute,Y
					doORA(absoluteIndex_Read(y)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				case 0x1A:    // * NOP - Implied
				case 0x3A:
				case 0x5A:
				case 0x7A:
				case 0xDA:
				case 0xEA:
				case 0xFA:
					cycles += 2;
					break;

				case 0x1B: // * SLO - Absolute,Y
					doSLOMem(_absIndexed(y));
					cycles += 7;
					break;

				case 0x1C:    // * TOP/NOP/SKW - Absolute,X
				case 0x3C:
				case 0x5C:
				case 0x7C:
				case 0xDC:
				case 0xFC:
					// NOTE: Nestopia does not add 1 cycles for page boundaries...
					absoluteIndex_Read(x); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				case 0x1D:    // ORA $4400,X - Absolute,X
					doORA(absoluteIndex_Read(x)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				case 0x1E:    // ASL $4400,X - Absolute,X
					doASLMem(_absIndexed(x));
					cycles += 7;
					break;

				case 0x1F: // * SLO - Absolute,X
					doSLOMem(_absIndexed(x));
					cycles += 7;
					break;

				case 0x20:    // JSR - Jump to SubRoutine, Absolute
					pushWord(pc + 1);
					pc = context.readWord(pc);
					cycles += 6;
					break;

				case 0x21:    // AND ($44,X) - Indirect,X
					doAND(indexedIndirect_Read());
					cycles += 6;
					break;

				// 0x22 handled previously

				case 0x23:
					throw new UnemulatedInstructionException(opcode);

				case 0x24:    // BIT $44 - Zero Page
					doBIT(zeroPage_Read());
					cycles += 3;
					break;

				case 0x25:    // AND $44 - Zero page
					doAND(zeroPage_Read());
					cycles += 3;
					break;

				case 0x26:    // ROL $44 - Zero Page
					doROLMem(_zeroPage());
					cycles += 5;
					break;

				case 0x27:
					throw new UnemulatedInstructionException(opcode);

				case 0x28:    // PLP - Immediate (PuLl Processor status)
					context.readByte(pc); // Throw away
					setRegP(popByte());
					cycles += 4;
					break;

				case 0x29:    // AND #$44 - immediate
					doAND(context.readByte(pc));
					pc++;
					cycles += 2;
					break;

				case 0x2A:    // ROL A - Accumulator
					a = doROL(a);
					cycles += 2;
					break;

				case 0x2B:
					throw new UnemulatedInstructionException(opcode);

				case 0x2C:    // BIT $4400 - Absolute
					doBIT(absolute_Read());
					cycles += 4;
					break;

				case 0x2D:    // AND $4400 - Absolute
					doAND(absolute_Read());
					cycles += 4;
					break;

				case 0x2E:    // ROL $4400 - Absolute
					doROLMem(context.readWord(pc));
					pc += 2;
					cycles += 6;
					break;

				case 0x2F:
					throw new UnemulatedInstructionException(opcode);

				case 0x30:    // BMI - Branch on MInus
					doBranch(getFlagN() > 0);
					break;

				case 0x31:    // AND ($44),Y - Indirect,Y
					doAND(indirectIndexed_Read()); // cycles-- if page boundary crossed.
					cycles += 5;
					break;

				// 0x32 handled previously

				case 0x33:
					throw new UnemulatedInstructionException(opcode);

					// 0x34 handled previously

				case 0x35:    // AND $44,x - Zero Page,X
					doAND(zeroPageIndexed_Read(x));
					cycles += 4;
					break;

				case 0x36:    // ROL $44,X - Zero Page,X
					doROLMem(_zeroPageIndexed(x));
					cycles += 6;
					break;

				case 0x37:
					throw new UnemulatedInstructionException(opcode);

				case 0x38:    // SEC - SEt Carry flag)
					flagC = 1;
					cycles += 2;
					break;

				case 0x39:    // AND $4400,Y - Absolute,Y
					doAND(absoluteIndex_Read(y)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				// 0x3A handled previously

				case 0x3B:
					throw new UnemulatedInstructionException(opcode);

					// 0x3C handled previously

				case 0x3D:    // AND $4400,X - Absolute,X
					doAND(absoluteIndex_Read(x)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				case 0x3E:    // ROL $4400 - Absolute,X
					doROLMem(_absIndexed(x));
					cycles += 7;
					break;

				case 0x3F:
					throw new UnemulatedInstructionException(opcode);

				case 0x40:    // RTI - Implied
					doRTI();
					cycles += 6;
					break;

				case 0x41:    // EOR ($44,X) - Indirect,X
					doEOR(indexedIndirect_Read());
					cycles += 6;
					break;

				// 0x42 handled previously

				case 0x43:
					throw new UnemulatedInstructionException(opcode);

					// 0x44 handled previously

				case 0x45:    // EOR $44 - Zero Page
					doEOR(zeroPage_Read());
					cycles += 3;
					break;

				case 0x46:    // LSR $44 - Zero Page
					doLSRMem(_zeroPage());
					cycles += 5;
					break;

				case 0x47:
					throw new UnemulatedInstructionException(opcode);

				case 0x48:    // PHA - Immediate (PusH Accumulator)
					context.readByte(pc); // Throw away
					pushByte(a);
					cycles += 3;
					break;

				case 0x49:    // EOR #$44 - Immediate
					doEOR(context.readByte(pc));
					pc++;
					cycles += 2;
					break;

				case 0x4A:    // LSR A - Accumulator
					a = doLSR(a);
					cycles += 2;
					break;

				case 0x4B:
					throw new UnemulatedInstructionException(opcode);

				case 0x4C:    // JMP $5597 - Absolute
					pc = context.readWord(pc);
					cycles += 3;
					break;

				case 0x4D:    // EOR $4400 - Absolute
					doEOR(absolute_Read());
					cycles += 4;
					break;

				case 0x4E:    // LSR $4400 - Absolute
					doLSRMem(context.readWord(pc));
					pc += 2;
					cycles += 6;
					break;

				case 0x4F:
					throw new UnemulatedInstructionException(opcode);

				case 0x50:    // BVC - Branch on oVerflow Clear
					doBranch(flagV == 0);
					break;

				case 0x51:    // EOR ($44),Y - Indirect,Y
					doEOR(indirectIndexed_Read()); // cycles-- if page boundary crossed.
					cycles += 5;
					break;

				// 0x52 handled previously

				case 0x53:
					throw new UnemulatedInstructionException(opcode);

					// 0x54 handled previously

				case 0x55:    // EOR $44,X - Zero Page,X
					doEOR(zeroPageIndexed_Read(x));
					cycles += 4;
					break;

				case 0x56:    // LSR $44,X - Zero Page,X
					doLSRMem(_zeroPageIndexed(x));
					cycles += 6;
					break;

				case 0x57:
					throw new UnemulatedInstructionException(opcode);

				case 0x58:    // CLI - CLear Interrupt flag
					flagI = 0;
					cycles += 2;
					return; // Stop CPU immediately.

				case 0x59:    // EOR $4400,Y - Absolute,Y
					doEOR(absoluteIndex_Read(y)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				// 0x5A handled previously

				case 0x5B:
					throw new UnemulatedInstructionException(opcode);

					// 0x5C handled previously

				case 0x5D:    // EOR $4400,X - Absolute,X
					doEOR(absoluteIndex_Read(x)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				case 0x5E:    // LSR $4400,X - Absolute,X
					doLSRMem(_absIndexed(x));
					cycles += 7;
					break;

				case 0x5F:
					throw new UnemulatedInstructionException(opcode);

				case 0x60:    // RTS - Implied
					context.readByte(pc); // Throw away
					pc = popWord() + 1;
					cycles += 6;
					break;

				case 0x61:    // ADC ($44,X) - Indirect,X
					doADC(indexedIndirect_Read());
					cycles += 6;
					break;

				// 0x62 handled previously

				case 0x63:
					throw new UnemulatedInstructionException(opcode);

					// 0x64 handled previously

				case 0x65:    // ADC $44 - Zero page
					doADC(zeroPage_Read());
					cycles += 3;
					break;

				case 0x66:    // ROR $44 - Zero Page
					doRORMem(_zeroPage());
					cycles += 5;
					break;

				case 0x67:
					throw new UnemulatedInstructionException(opcode);

				case 0x68:    // PLA - Immediate (puLl Accumulator)
					context.readByte(pc); // Throw away
					flagNZ = a = popByte();
					cycles += 4;
					break;

				case 0x69:    // ADC #$44 - immediate
					doADC(context.readByte(pc));
					pc++;
					cycles += 2;
					break;

				case 0x6A:    // ROR A - Accumulator
					a = doROR(a);
					cycles += 2;
					break;

				case 0x6B:
					throw new UnemulatedInstructionException(opcode);

				case 0x6C:    // JMP ($5597) - Indirect
					addr = context.readWord(pc);
					if ((addr & 0xff) == 0xff) {
						pc = context.readByte(addr) | (context.readByte(addr & 0xff00) << 8);
					} else {
						pc = context.readWord(addr);
					}
					cycles += 5;
					break;

				case 0x6D:    // ADC $4400 - Absolute
					doADC(absolute_Read());
					cycles += 4;
					break;

				case 0x6E:    // ROR $4400 - Absolute
					doRORMem(context.readWord(pc));
					pc += 2;
					cycles += 6;
					break;

				case 0x6F:
					throw new UnemulatedInstructionException(opcode);

				case 0x70:    // BVS - Branch on oVerflow Set
					doBranch(flagV > 0);
					break;

				case 0x71:    // ADC ($44),Y - Indirect,Y
					doADC(indirectIndexed_Read()); // cycles-- if page boundary crossed.
					cycles += 5;
					break;

				// 0x72 handled previously

				case 0x73:
					throw new UnemulatedInstructionException(opcode);

					// 0x74 handled previously

				case 0x75:    // ADC $44,X - Zero page, X
					doADC(zeroPageIndexed_Read(x));
					cycles += 4;
					break;

				case 0x76:    // ROR $44,X - Zero Page,X
					doRORMem(_zeroPageIndexed(x));
					cycles += 6;
					break;

				case 0x77:
					throw new UnemulatedInstructionException(opcode);

				case 0x78: // SEI - SEt Interrupt flag
					flagI = 1;
					cycles += 2;
					break;

				case 0x79:    // ADC $4400,Y - Absolute,Y
					doADC(absoluteIndex_Read(y)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				// 0x7A handled previously

				case 0x7B:
					throw new UnemulatedInstructionException(opcode);

					// 0x7C handled previously

				case 0x7D:    // ADC $4400,X - Absolute, X
					doADC(absoluteIndex_Read(x));
					cycles += 4;
					break;

				case 0x7E:    // ROR $4400,X - Absolute,X
					doRORMem(_absIndexed(x));
					cycles += 7;
					break;

				case 0x7F:
					throw new UnemulatedInstructionException(opcode);

				case 0x80:    // * DOP/SKB #$44 - Immediate
				case 0x82:
				case 0x89:
				case 0xC2:
				case 0xE2:
					pc++;
					cycles += 2;
					break;

				case 0x81:    // STA ($44,X) - Indirect,X
					indexedIndirect_Write(a);
					cycles += 6;
					break;

				// 0x82 handled previously

				case 0x83:
					throw new UnemulatedInstructionException(opcode);

				case 0x84:    // STY $44 - Zero Page
					zeroPage_Write(y);
					cycles += 3;
					break;

				case 0x85:    // STA $44 - Zero Page
					zeroPage_Write(a);
					cycles += 3;
					break;

				case 0x86:    // STX $44 - Zero Page
					zeroPage_Write(x);
					cycles += 3;
					break;

				case 0x87:
					throw new UnemulatedInstructionException(opcode);

				case 0x88:    // DEY - Immediate
					flagNZ = y = (y - 1) & 0xff;
					cycles += 2;
					break;

				// 0x89 handled previously

				case 0x8A:    // TXA - Immediate
					flagNZ = a = x;
					cycles += 2;
					break;

				case 0x8B:
					throw new UnemulatedInstructionException(opcode);

				case 0x8C:    // STY $4400 - Absolute
					absolute_Write(y);
					cycles += 4;
					break;

				case 0x8D:    // STA $4400 - Absolute
					absolute_Write(a);
					cycles += 4;
					break;

				case 0x8E:    // STX $4400 - Absolute
					absolute_Write(x);
					cycles += 4;
					break;

				case 0x8F:
					throw new UnemulatedInstructionException(opcode);

				case 0x90:    // BCC - Branch on Carry Clear
					doBranch(flagC == 0);
					break;

				case 0x91:    // STA ($44),Y - Indirect,Y
					indirectIndexed_Write(a);
					cycles += 6;
					break;

				// 0x92 handled previously

				case 0x93:
					throw new UnemulatedInstructionException(opcode);

				case 0x94:    // STY $44,X - Zero Page,X
					zeroPageIndexed_Write(x, y);
					cycles += 4;
					break;

				case 0x95:    // STA $44,X - Zero Page,X
					zeroPageIndexed_Write(x, a);
					cycles += 4;
					break;

				case 0x96:    // STX $44,Y - Zero Page,Y
					zeroPageIndexed_Write(y, x);
					cycles += 4;
					break;

				case 0x97:
					throw new UnemulatedInstructionException(opcode);

				case 0x98:    // TYA - Immediate
					flagNZ = a = y;
					cycles += 2;
					break;

				case 0x99:    // STA $4400,Y - Absolute,Y
					absoluteIndex_Write(y, a);
					cycles += 5;
					break;

				case 0x9A:    // TXS - Implied
					sp = x;
					cycles += 2;
					break;

				case 0x9B:
					throw new UnemulatedInstructionException(opcode);

				case 0x9C:
					throw new UnemulatedInstructionException(opcode);

				case 0x9D:    // STA $4400,X - Absolute,X
					absoluteIndex_Write(x, a);
					cycles += 5;
					break;

				case 0x9E:
					throw new UnemulatedInstructionException(opcode);

				case 0x9F:
					throw new UnemulatedInstructionException(opcode);

				case 0xA0:    // LDY #$44 - Immediate
					doLDY(context.readByte(pc));
					pc++;
					cycles += 2;
					break;

				case 0xA1:    // LDA ($44,X) - Indirect,X
					doLDA(indexedIndirect_Read());
					cycles += 6;
					break;

				case 0xA2:    // LDX #$44 - Immediate
					doLDX(context.readByte(pc));
					pc++;
					cycles += 2;
					break;

				case 0xA3:    // * LAX ($44,X) - Indirect,X
					doLAX(indexedIndirect_Read());
					cycles += 6;
					break;

				case 0xA4:    // LDY $44 - Zero Page
					doLDY(zeroPage_Read());
					cycles += 3;
					break;

				case 0xA5:    // LDA $44 - Zero Page
					doLDA(zeroPage_Read());
					cycles += 3;
					break;

				case 0xA6:    // LDX $44 - Zero Page
					doLDX(zeroPage_Read());
					cycles += 3;
					break;

				case 0xA7: // * LAX $44 - Zero Page
					doLAX(zeroPage_Read());
					cycles += 3;
					break;

				case 0xA8:    // TAY - Immediate
					flagNZ = y = a;
					cycles += 2;
					break;

				case 0xA9:    // LDA #$44 - Immediate
					doLDA(context.readByte(pc));
					pc++;
					cycles += 2;
					break;

				case 0xAA:    // TAX - Immediate
					flagNZ = x = a;
					cycles += 2;
					break;

				case 0xAB:
					throw new UnemulatedInstructionException(opcode);

				case 0xAC:    // LDY $4400 - Absolute
					doLDY(absolute_Read());
					cycles += 4;
					break;

				case 0xAD:    // LDA $4400 - Absolute
					doLDA(absolute_Read());
					cycles += 4;
					break;

				case 0xAE:    // LDX $4400 - Absolute
					doLDX(absolute_Read());
					cycles += 4;
					break;

				case 0xAF: // * LAX $4400 - Absolute
					doLAX(absolute_Read());
					cycles += 4;
					break;

				case 0xB0:    // BCS - Branch on Carry Set
					doBranch(flagC > 0);
					break;

				case 0xB1:    // LDA ($44),Y - Indirect,Y
					doLDA(indirectIndexed_Read()); // cycles-- if page boundary crossed.
					cycles += 5;
					break;

				// 0xB2 handled previously

				case 0xB3:    // * LAX ($44),Y - Indirect,Y
					doLAX(indirectIndexed_Read()); // cycles-- if page boundary crossed.
					cycles += 5;
					break;

				case 0xB4:    // LDY $44,X - Zero Page,X
					doLDY(zeroPageIndexed_Read(x));
					cycles += 4;
					break;

				case 0xB5:    // LDA $44,X - Zero Page,X
					doLDA(zeroPageIndexed_Read(x));
					cycles += 4;
					break;

				case 0xB6:    // LDX $44,Y - Zero Page,Y
					doLDX(zeroPageIndexed_Read(y));
					cycles += 4;
					break;

				case 0xB7: // * LAX $44 - Zero Page,Y
					doLAX(zeroPageIndexed_Read(y));
					cycles += 4;
					break;

				case 0xB8:    // CLV - CLear oVerflow flag
					flagV = 0;
					cycles += 2;
					break;

				case 0xB9:    // LDA $4400,Y - Absolute,Y
					doLDA(absoluteIndex_Read(y)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				case 0xBA:    // TSX
					flagNZ = x = sp;
					cycles += 2;
					break;

				case 0xBB: // * LAR/LAE/LAS arg,Y - Absolute,Y
					flagNZ = x = sp = a = absoluteIndex_Read(y) & sp;
					cycles += 4;
					break;

				case 0xBC:    // LDY $4400,X - Absolute,X
					doLDY(absoluteIndex_Read(x)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				case 0xBD:    // LDA $4400,X - Absolute,X
					doLDA(absoluteIndex_Read(x)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				case 0xBE:    // LDX $4400,Y - Absolute,Y
					doLDX(absoluteIndex_Read(y)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				case 0xBF: // * LAX $4400 - Absolute,Y
					doLAX(absoluteIndex_Read(y)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				case 0xC0:    // CPY #$44 - Immediate
					doCMP(y, context.readByte(pc));
					pc++;
					cycles += 2;
					break;

				case 0xC1:    // CMP ($44,X) - Indirect,X
					doCMP(a, indexedIndirect_Read());
					cycles += 6;
					break;

				// 0xC2 handled previously

				case 0xC3:
					throw new UnemulatedInstructionException(opcode);

				case 0xC4:    // CPY $44 - Zero Page
					doCMP(y, zeroPage_Read());
					cycles += 3;
					break;

				case 0xC5:    // CMP $44 - Zero Page
					doCMP(a, zeroPage_Read());
					cycles += 3;
					break;

				case 0xC6:    // DEC $44 - Zero Page
					doDECMem(_zeroPage());
					cycles += 5;
					break;

				case 0xC7:
					throw new UnemulatedInstructionException(opcode);

				case 0xC8:    // INY - Immediate
					flagNZ = y = (y + 1) & 0xff;
					cycles += 2;
					break;

				case 0xC9:    // CMP #$44 - Compare immediate
					doCMP(a, context.readByte(pc));
					pc++;
					cycles += 2;
					break;

				case 0xCA:    // DEX - Immediate
					flagNZ = x = (x - 1) & 0xff;
					cycles += 2;
					break;

				case 0xCB:
					throw new UnemulatedInstructionException(opcode);

				case 0xCC:    // CPY $4400 - Absolute
					doCMP(y, absolute_Read());
					cycles += 4;
					break;

				case 0xCD:    // CMP $4400 - Absolute
					doCMP(a, absolute_Read());
					cycles += 4;
					break;

				case 0xCE:    // DEC $4400 - Absolute
					doDECMem(context.readWord(pc));
					pc += 2;
					cycles += 6;
					break;

				case 0xCF:
					throw new UnemulatedInstructionException(opcode);

				case 0xD0:    // BNE - Branch on Not Equal
					doBranch(getFlagZ() == 0);
					break;

				case 0xD1:    // CMP ($44),Y - Indirect,Y
					doCMP(a, indirectIndexed_Read()); // cycles-- if page boundary crossed.
					cycles += 5;
					break;

				// 0xD2 handled previously

				case 0xD3:
					throw new UnemulatedInstructionException(opcode);

					// 0xD4 handled previously

				case 0xD5:    // CMP $44,X - Zero Page,X
					doCMP(a, zeroPageIndexed_Read(x));
					cycles += 4;
					break;

				case 0xD6:    // DEC $44,X - Zero Page,X
					doDECMem(_zeroPageIndexed(x));
					cycles += 6;
					break;

				case 0xD7:
					throw new UnemulatedInstructionException(opcode);

				case 0xD8:    // CLD - CLear Decimal flag
					flagD = 0;
					cycles += 2;
					break;

				case 0xD9:    // CMP $4400,Y - Absolute,Y
					doCMP(a, absoluteIndex_Read(y)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				// 0xDA handled previously

				case 0xDB:
					throw new UnemulatedInstructionException(opcode);

					// 0xDC handled previously

				case 0xDD:    // CMP $4400,X - Absolute,X
					doCMP(a, absoluteIndex_Read(x)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				case 0xDE:    // DEC $4400,X - Absolute,X
					doDECMem(_absIndexed(x));
					cycles += 7;
					break;

				case 0xDF:
					throw new UnemulatedInstructionException(opcode);

				case 0xE0:    // CPX #$44 - Immediate
					doCMP(x, context.readByte(pc));
					pc++;
					cycles += 2;
					break;

				case 0xE1:    // SBC ($44,X) - Indirect,X
					doSBC(indexedIndirect_Read());
					cycles += 6;
					break;

				// 0xE2 handled previously

				case 0xE3: // * ISC ($44,X) - Indirect,X
					doISCMem(_preIndIndX());
					cycles += 8;
					break;

				case 0xE4:    // CPX $44 - Zero Page
					doCMP(x, zeroPage_Read());
					cycles += 3;
					break;

				case 0xE5:    // SBC $44 - Zero Page
					doSBC(zeroPage_Read());
					cycles += 3;
					break;

				case 0xE6:    // INC $44 - Zero Page
					doINCMem(_zeroPage());
					cycles += 5;
					break;

				case 0xE7: // * ISC $44 - Zero Page
					doISCMem(_zeroPage());
					cycles += 5;
					break;

				case 0xE8:    // INX - Immediate
					flagNZ = x = (x + 1) & 0xff;
					cycles += 2;
					break;

				case 0xE9:    // SBC #$44 - Immediate
					doSBC(context.readByte(pc));
					pc++;
					cycles += 2;
					break;

				// 0xEA handled previously

				case 0xEB: // * SBC #$44 - Immediate
					doSBC(context.readByte(pc));
					pc++;
					cycles += 2;
					break;

				case 0xEC:    // CPX $4400 - Absolute
					doCMP(x, absolute_Read());
					cycles += 4;
					break;

				case 0xED:    // SBC $4400 - Absolute
					doSBC(absolute_Read());
					cycles += 4;
					break;

				case 0xEE:    // INC $4400 - Absolute
					doINCMem(context.readWord(pc));
					pc += 2;
					cycles += 6;
					break;

				case 0xEF: // * ISC $4400 - Absolute
					doISCMem(context.readWord(pc));
					pc += 2;
					cycles += 6;
					break;

				case 0xF0:    // BEQ - Branch on EQual
					doBranch(getFlagZ() > 0);
					break;

				case 0xF1:    // SBC ($44),Y - Indirect,Y
					doSBC(indirectIndexed_Read()); // cycles-- if page boundary crossed.
					cycles += 5;
					break;

				// 0xF2 handled previously

				case 0xF3: // * ISC ($44),Y - Indirect,Y
					doISCMem(_postIndIndY());
					cycles += 8;
					break;

				// 0xF4 handled previously

				case 0xF5:    // SBC $44,X - Zero Page,X
					doSBC(zeroPageIndexed_Read(x));
					cycles += 4;
					break;

				case 0xF6:    // INC $44,X - Zero Page,X
					doINCMem(_zeroPageIndexed(x));
					cycles += 6;
					break;

				case 0xF7: // * ISC $44,X - Zero Page,X
					doISCMem(_zeroPageIndexed(x));
					cycles += 6;
					break;

				case 0xF8:    // SED - SEt Decimal flag
					flagD = 1;
					cycles += 2;
					break;

				case 0xF9:    // SBC $4400,Y - Absolute,Y
					doSBC(absoluteIndex_Read(y)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				// 0xFA handled previously

				case 0xFB: // * ISC $4400,Y - Absolute,Y
					doISCMem(_absIndexed(y));
					cycles += 7;
					break;

				// 0xFC handled previously

				case 0xFD:    // SBC $4400,X - Absolute,X
					doSBC(absoluteIndex_Read(x)); // cycles-- if page boundary crossed.
					cycles += 4;
					break;

				case 0xFE:    // INC $4400,X - Absolute,X
					doINCMem(_absIndexed(x));
					cycles += 7;
					break;

				case 0xFF: // * ISC $4400,X - Absolute,X
					doISCMem(_absIndexed(x));
					cycles += 7;
					break;

			}

		}

	}

	/**
	 * Returns the B flag.
	 *
	 * @return The B flag.
	 */
	public int getFlagB() {
		return flagB;
	}

	/**
	 * Returns the C flag.
	 *
	 * @return The C flag.
	 */
	public int getFlagC() {
		return flagC;
	}

	/**
	 * Returns the D flag.
	 *
	 * @return The D flag.
	 */
	public int getFlagD() {
		return flagD;
	}

	/**
	 * Returns the I flag.
	 *
	 * @return The I flag.
	 */
	public int getFlagI() {
		return flagI;
	}

	/**
	 * Returns the N flag.
	 *
	 * @return The N flag.
	 */
	public int getFlagN() {
		// Sometimes bit 8 of flagNZ gets set as a side-effect of
		// operations, and that is what we must check for the sign
		// bit.  So we OR bits 7 and 8 together and just check both.
		return ((flagNZ | (flagNZ >> 1)) & N_BIT) >> 7;
	}

	/**
	 * Returns the V flag.
	 *
	 * @return The V flag.
	 */
	public int getFlagV() {
		return flagV;
	}

	/**
	 * Returns the Z flag.
	 *
	 * @return The Z flag.
	 */
	public int getFlagZ() {
		// We use flagNZ "opposite" of a Z flag, e.g., if
		// flagNZ==0 => Z flag is set,
		// flagNZ!=0 => flagZ is clear.
		// The first 8 bits of flagNZ are in essence a copy of
		// the results of the last operation done.  We must mask
		// to a byte as we use bit 8 of flagNZ in checking for the
		// sign bit, as sometimes our operations sets that bit.
		return (flagNZ & 0xff) == 0 ? 1 : 0;
	}

	/**
	 * Returns the value of the A register.
	 *
	 * @return The value of the A register.
	 * @see #getRegX()
	 * @see #getRegY()
	 */
	public int getRegA() {
		return a;
	}

	/**
	 * Returns the value of the P (processor status flags)
	 * register.
	 *
	 * @return The value of the P register.
	 * @see #setRegP(int)
	 */
	public int getRegP() {
		// NOTE: Flag Z is stored "opposite" as we use flaNZ the "opposite"
		// way of having a Z flag (e.g., it being "0" => Z flag is "set,"
		// it being "!= 0" => Z flag "clear."
		return flagC |
				/*getFlagZ()>0?0x02:0x00*/((flagNZ & 0xff) > 0 ? 0x00 : 0x02) |
			(flagI << 2) |
			(flagD << 3) |
			(flagB << 4) |
			(flagV << 6) |
				/*getFlagN()<<7;*/((flagNZ | (flagNZ >> 1)) & N_BIT);
	}

	/**
	 * Returns the value of the X register.
	 *
	 * @return The value of the X register.
	 * @see #getRegA()
	 * @see #getRegY()
	 */
	public int getRegX() {
		return x;
	}

	/**
	 * Returns the value of the Y register.
	 *
	 * @return The value of the Y register.
	 * @see #getRegA()
	 * @see #getRegX()
	 */
	public int getRegY() {
		return y;
	}

	public int getStatusFlag(int flag) {
		if (flag == 0) {
			return getFlagC();
		}
		if (flag == 1) {
			return getFlagZ();
		}
		if (flag == 2) {
			return getFlagI();
		}
		if (flag == 3) {
			return getFlagD();
		}
		if (flag == 4) {
			return getFlagB();
		}
		if (flag == 5) {
			return 0;
		}
		if (flag == 6) {
			return getFlagV();
		}
		if (flag == 7) {
			return getFlagN();
		}
		throw new IllegalArgumentException("Invalid flag value: " + flag);
	}


	/**
	 * Performs an indexed-indirect-addressed read
	 * (LDA, ORA, EOR, AND, ADC, CMP, SBC, LAX).
	 *
	 * @return The byte read.
	 */
	private int indexedIndirect_Read() {
		return context.readByte(_preIndIndX());
	}

	/**
	 * Performs an indexed-indirect-addressed write
	 * (STA, SAX).
	 *
	 * @param b The byte to write.
	 */
	private void indexedIndirect_Write(int b) {
		context.writeByte(_preIndIndX(), b);
	}

	/**
	 * Performs an indirect-indexed-addressed read
	 * (LDA, EOR, AND, ORA, ADC, SBC, CMP).
	 *
	 * @return The byte read.
	 */
	private int indirectIndexed_Read() {
		return context.readByte(_postIndIndY());
	}

	/**
	 * Performs an indirect-indexed-addressed write
	 * (STA, SHA).
	 *
	 * @param b The byte to write.
	 */
	private void indirectIndexed_Write(int b) {
		context.writeByte(_postIndIndY(), b);
	}

	/**
	 * Performs an IRQ (maskable interrupt), if the
	 * interrupt disable flag is not set.
	 *
	 * @see #nmi()
	 */
	public void irq() {
		if (flagI == 0) {
			pushWord(pc);
			pushByte(getRegP());
			flagI = 1;
			pc = context.readWord(0xfffe);
			cycles += 7;
		}
	}

	/**
	 * Performs an NMI (non-maskable interrupt).
	 *
	 * @see #irq()
	 */
	public void nmi() {
		pushWord(pc);
		pushByte(getRegP());
		flagI = 1;
		pc = context.readWord(0xfffa);
		cycles += 7;
	}

	/**
	 * Returns the next byte that would be popped from the stack, without
	 * modifying the stack pointer.  Useful for debugging.
	 *
	 * @param offs The offset into the stack, &gt;= 0.
	 * @return The byte.
	 * @see #popByte()
	 */
	public int peekByte(int offs) {
		offs = (sp + offs + 1) & 0xff;
		return context.readByteSafely(STACK_BOTTOM | offs);
	}


	/**
	 * Pops a byte off of the stack (addresses
	 * <code>0x100 - 0x1FF</code> in memory).
	 *
	 * @see #pushByte(int)
	 * @see #popWord()
	 */
	private int popByte() {
		sp = (sp + 1) & 0xff;
		return context.readByte(STACK_BOTTOM | sp);
	}

	/**
	 * Pops a word off of the stack (addresses
	 * <code>0x100 - 0x1FF</code> in memory).
	 *
	 * @see #pushWord(int)
	 * @see #popByte()
	 */
	private int popWord() {
		// TODO: This is always on the stack so we should be
		// able to directly access context.ram if we want to
		// cheat.
		sp = (sp + 1) & 0xff;
		int word = context.readByte(STACK_BOTTOM | sp);
		sp = (sp + 1) & 0xff;
		word |= (context.readByte(STACK_BOTTOM | sp) << 8);
		return word;
	}

	/**
	 * Pushes a byte onto the stack (addresses
	 * <code>0x100 - 0x1FF</code> in memory).
	 *
	 * @param b The byte to push.
	 * @see #popByte()
	 * @see #pushWord(int)
	 */
	private void pushByte(int b) {
		context.writeByte(STACK_BOTTOM + sp, b);
		sp = (sp - 1) & 0xff;
	}

	/**
	 * Pushes a word onto the stack (addresses
	 * <code>0x100 - 0x1FF</code> in memory).
	 *
	 * @param word The word to push.
	 * @see #popWord()
	 * @see #pushByte(int)
	 */
	private void pushWord(int word) {
		context.writeByte(STACK_BOTTOM + sp, (word >> 8) & 0xff);
		sp = (sp - 1) & 0xff;
		context.writeByte(STACK_BOTTOM + sp, word & 0xff);
		sp = (sp - 1) & 0xff;
	}

	/**
	 * Performs a reset.
	 */
	public void reset() {
		totCycles = 0;
		cycles = 0; // ???
		a = x = y = 0;
		flagI = 1;
		flagB = flagC = flagD = flagNZ = flagV = 0;
		pc = context.readWord(0xfffc);
		sp = 0xfd;//0xff; // "Stack" starts at 0x1ff and goes down to 0x100.
	}

	public void setLogState(boolean logState) {
		this.logState = logState;
		stateLogger.setLog(Paths.get(new java.io.File("output_oldfinnes.log").toURI()));
	}

	/**
	 * Sets the value of the P (processor status) register.
	 *
	 * @param p The new value for the register.
	 * @see #getRegP()
	 */
	public void setRegP(int p) {
		flagNZ = ((p & N_BIT) << 1) | (~p & 0x02);//((p&0x02)>>1);
		flagV = (p & 0x40) >> 6;
		flagB = (p & 0x10) >> 4;
		flagD = (p & 0x08) >> 3;
		flagI = (p & 0x04) >> 2;
		flagC = p & 0x01;
	}

	/**
	 * Stops this CPU.
	 */
	public void stop() {
		if (debugOut != null) {
			debugOut.close();
		}
	}

	/**
	 * Performs a zero-page-addressed read (LDA, LDX, LDY,
	 * EOR, AND, ORA, ADC, SBC, CMP, BIT, LAX, NOP).
	 *
	 * @return The byte read.
	 */
	private int zeroPage_Read() {
		return context.readByte(_zeroPage());
	}

	/**
	 * Performs a zero-page-addressed write (STA, STX, STY, SAX).
	 *
	 * @param b The byte to write.
	 */
	private void zeroPage_Write(int b) {
		context.writeByte(_zeroPage(), b);
	}

	/**
	 * Performs a zero-page-indexed-addressed read (LDA, LDX, LDY,
	 * EOR, AND, ORA, ADC, SBC, CMP, BIT, LAX, NOP).
	 *
	 * @param index The index.
	 * @return The byte read.
	 */
	private int zeroPageIndexed_Read(int index) {
		return context.readByte(_zeroPageIndexed(index));
	}

	/**
	 * Performs a zero-page-indexed-addressed write
	 * (STA, STX, STY, SAX).
	 *
	 * @param index The index.
	 * @param b     The byte to write.
	 */
	private void zeroPageIndexed_Write(int index, int b) {
		context.writeByte(_zeroPageIndexed(index), b);
	}

}