package org.fife.emu.cpu.n6502;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.fife.emu.CpuContext;

/**
 * The set of instructions in the 6502 CPU.
 */
public class InstructionInfo6502 implements InstructionSet6502 {

	private Map<Integer, Instruction> instructions;


	public InstructionInfo6502() {
		instructions = new HashMap<>();
		populateInstructions();
	}


	public Instruction get(Integer instr) {
		return instructions.get(instr);
	}


	private void populateInstructions() {

		// Find: int INST_([A-Z]+)(_\w+)? = ([^;]+);
		// Replace with: instructions.put($3, new InstructionImpl(INST_$1$2, "$1"));

		instructions.put(0x01, new InstructionImpl(INST_ORA_PRE_INDEXED_INDIRECT, "ORA"));
		instructions.put(0x05, new InstructionImpl(INST_ORA_ZERO_PAGE, "ORA"));
		instructions.put(0x06, new InstructionImpl(INST_ASL_ZERO_PAGE, "ASL"));
		instructions.put(0x08, new InstructionImpl(INST_PHP, "PHP"));
		instructions.put(0x09, new InstructionImpl(INST_ORA_IMMEDIATE, "ORA"));
		instructions.put(0x0a, new InstructionImpl(INST_ASL_ACCUMULATOR, "ASL"));
		instructions.put(0x0d, new InstructionImpl(INST_ORA_ABSOLUTE, "ORA"));
		instructions.put(0x10, new InstructionImpl(INST_BPL, "BPL"));
		instructions.put(0x11, new InstructionImpl(INST_ORA_POST_INDEXED_INDIRECT, "ORA"));
		instructions.put(0x15, new InstructionImpl(INST_ORA_ZERO_PAGE_INDEXED, "ORA"));
		instructions.put(0x18, new InstructionImpl(INST_CLC, "CLC"));
		instructions.put(0x19, new InstructionImpl(INST_ORA_INDEXED_Y, "ORA"));
		instructions.put(0x1d, new InstructionImpl(INST_ORA_INDEXED_X, "ORA"));
		instructions.put(0x20, new InstructionImpl(INST_JSR, "JSR"));
		instructions.put(0x24, new InstructionImpl(INST_BIT_ZERO_PAGE, "BIT"));
		instructions.put(0x26, new InstructionImpl(INST_ROL_ZERO_PAGE, "ROL"));
		instructions.put(0x28, new InstructionImpl(INST_PLP, "PLP"));
		instructions.put(0x2a, new InstructionImpl(INST_ROL_ACCUMULATOR, "ROL"));
		instructions.put(0x2c, new InstructionImpl(INST_BIT_ABSOLUTE, "BIT"));
		instructions.put(0x30, new InstructionImpl(INST_BMI, "BMI"));
		instructions.put(0x38, new InstructionImpl(INST_SEC, "SEC"));
		instructions.put(0x41, new InstructionImpl(INST_EOR_PRE_INDEXED_INDIRECT, "EOR"));
		instructions.put(0x45, new InstructionImpl(INST_EOR_ZERO_PAGE, "EOR"));
		instructions.put(0X48, new InstructionImpl(INST_PHA, "PHA"));
		instructions.put(0x49, new InstructionImpl(INST_EOR_IMMEDIATE, "EOR"));
		instructions.put(0x4a, new InstructionImpl(INST_LSR_ACCUMULATOR, "LSR"));
		instructions.put(0x4c, new InstructionImpl(INST_JMP_ABSOLUTE, "JMP"));
		instructions.put(0x4d, new InstructionImpl(INST_EOR_ABSOLUTE, "EOR"));
		instructions.put(0x51, new InstructionImpl(INST_EOR_POST_INDEXED_INDIRECT, "EOR"));
		instructions.put(0x59, new InstructionImpl(INST_EOR_INDEXED_Y, "EOR"));
		instructions.put(0x55, new InstructionImpl(INST_EOR_ZERO_PAGE_INDEXED, "EOR"));
		instructions.put(0x5d, new InstructionImpl(INST_EOR_INDEXED_X, "EOR"));
		instructions.put(0x60, new InstructionImpl(INST_RTS, "RTS"));
		instructions.put(0x61, new InstructionImpl(INST_ADC_PRE_INDEXED_INDIRECT, "ADC"));
		instructions.put(0x65, new InstructionImpl(INST_ADC_ZERO_PAGE, "ADC"));
		instructions.put(0x66, new InstructionImpl(INST_ROR_ZERO_PAGE, "ROR"));
		instructions.put(0x68, new InstructionImpl(INST_PLA, "PLA"));
		instructions.put(0x69, new InstructionImpl(INST_ADC_IMMEDIATE, "ADC"));
		instructions.put(0x6a, new InstructionImpl(INST_ROR_ACCUMULATOR, "ROR"));
		instructions.put(0x6c, new InstructionImpl(INST_JMP_INDIRECT, "JMP"));
		instructions.put(0x6d, new InstructionImpl(INST_ADC_ABSOLUTE, "ADC"));
		instructions.put(0x6e, new InstructionImpl(INST_ROR_ABSOLUTE, "ROR"));
		instructions.put(0x71, new InstructionImpl(INST_ADC_POST_INDEXED_INDIRECT, "ADC"));
		instructions.put(0x75, new InstructionImpl(INST_ADC_ZERO_PAGE_INDEXED, "ADC"));
		instructions.put(0x76, new InstructionImpl(INST_ROR_ZERO_PAGE_INDEXED, "ROR"));
		instructions.put(0x78, new InstructionImpl(INST_SEI, "SEI"));
		instructions.put(0x79, new InstructionImpl(INST_ADC_INDEXED_Y, "ADC"));
		instructions.put(0x7d, new InstructionImpl(INST_ADC_INDEXED_X, "ADC"));
		instructions.put(0x7e, new InstructionImpl(INST_ROR_INDEXED_X, "ROR"));
		instructions.put(0x81, new InstructionImpl(INST_STA_PRE_INDEXED_INDIRECT, "STA"));
		instructions.put(0x84, new InstructionImpl(INST_STY_ZERO_PAGE, "STY"));
		instructions.put(0x85, new InstructionImpl(INST_STA_ZERO_PAGE, "STA"));
		instructions.put(0x86, new InstructionImpl(INST_STX_ZERO_PAGE, "STX"));
		instructions.put(0x88, new InstructionImpl(INST_DEY, "DEY"));
		instructions.put(0x8a, new InstructionImpl(INST_TXA, "TXA"));
		instructions.put(0x8c, new InstructionImpl(INST_STY_ABSOLUTE, "STY"));
		instructions.put(0x8d, new InstructionImpl(INST_STA_ABSOLUTE, "STA"));
		instructions.put(0x8e, new InstructionImpl(INST_STX_ABSOLUTE, "STX"));
		instructions.put(0x90, new InstructionImpl(INST_BCC, "BCC"));
		instructions.put(0x91, new InstructionImpl(INST_STA_POST_INDEXED_INDIRECT, "STA"));
		instructions.put(0x94, new InstructionImpl(INST_STY_ZERO_PAGE_INDEXED, "STY"));
		instructions.put(0x95, new InstructionImpl(INST_STA_ZERO_PAGE_INDEXED, "STA"));
		instructions.put(0x98, new InstructionImpl(INST_TYA, "TYA"));
		instructions.put(0x99, new InstructionImpl(INST_STA_INDEXED_Y, "STA"));
		instructions.put(0x9a, new InstructionImpl(INST_TXS_IMPLIED, "TXS"));
		instructions.put(0x9d, new InstructionImpl(INST_STA_INDEXED_X, "STA"));
		instructions.put(0xa0, new InstructionImpl(INST_LDY_IMMEDIATE, "LDY"));
		instructions.put(0xa1, new InstructionImpl(INST_LDA_PRE_INDEXED_INDIRECT, "LDA"));
		instructions.put(0xa2, new InstructionImpl(INST_LDX_IMMEDIATE, "LDX"));
		instructions.put(0xa4, new InstructionImpl(INST_LDY_ZERO_PAGE, "LDY"));
		instructions.put(0xa5, new InstructionImpl(INST_LDA_ZERO_PAGE, "LDA"));
		instructions.put(0xa6, new InstructionImpl(INST_LDX_ZERO_PAGE, "LDX"));
		instructions.put(0xa8, new InstructionImpl(INST_TAY, "TAY"));
		instructions.put(0xa9, new InstructionImpl(INST_LDA_IMMEDIATE, "LDA"));
		instructions.put(0xaa, new InstructionImpl(INST_TAX, "TAX"));
		instructions.put(0xac, new InstructionImpl(INST_LDY_ABSOLUTE, "LDY"));
		instructions.put(0xad, new InstructionImpl(INST_LDA_ABSOLUTE, "LDA"));
		instructions.put(0xae, new InstructionImpl(INST_LDX_ABSOLUTE, "LDX"));
		instructions.put(0xb0, new InstructionImpl(INST_BCS, "BCS"));
		instructions.put(0xb1, new InstructionImpl(INST_LDA_POST_INDEXED_INDIRECT, "LDA"));
		instructions.put(0xb4, new InstructionImpl(INST_LDY_ZERO_PAGE_INDEXED, "LDY"));
		instructions.put(0xb5, new InstructionImpl(INST_LDA_ZERO_PAGE_INDEXED, "LDA"));
		instructions.put(0xb6, new InstructionImpl(INST_LDX_ZERO_PAGE_INDEXED, "LDX"));
		instructions.put(0xb9, new InstructionImpl(INST_LDA_INDEXED_Y, "LDA"));
		instructions.put(0xba, new InstructionImpl(INST_TSX, "TSX"));
		instructions.put(0xbc, new InstructionImpl(INST_LDY_INDEXED_X, "LDY"));
		instructions.put(0xbd, new InstructionImpl(INST_LDA_INDEXED_X, "LDA"));
		instructions.put(0xbe, new InstructionImpl(INST_LDX_INDEXED_Y, "LDX"));
		instructions.put(0xc0, new InstructionImpl(INST_CPY_IMMEDIATE, "CPY"));
		instructions.put(0xc1, new InstructionImpl(INST_CMP_PRE_INDEXED_INDIRECT, "CMP"));
		instructions.put(0xc4, new InstructionImpl(INST_CPY_ZERO_PAGE, "CPY"));
		instructions.put(0xc5, new InstructionImpl(INST_CMP_ZERO_PAGE, "CMP"));
		instructions.put(0xc8, new InstructionImpl(INST_INY, "INY"));
		instructions.put(0xc9, new InstructionImpl(INST_CMP_IMMEDIATE, "CMP"));
		instructions.put(0xca, new InstructionImpl(INST_DEX, "DEX"));
		instructions.put(0xcc, new InstructionImpl(INST_CPY_ABSOLUTE, "CPY"));
		instructions.put(0xcd, new InstructionImpl(INST_CMP_ABSOLUTE, "CMP"));
		instructions.put(0xd0, new InstructionImpl(INST_BNE, "BNE"));
		instructions.put(0xd1, new InstructionImpl(INST_CMP_POST_INDEXED_INDIRECT, "CMP"));
		instructions.put(0xd5, new InstructionImpl(INST_CMP_ZERO_PAGE_INDEXED, "CMP"));
		instructions.put(0xd8, new InstructionImpl(INST_CLD, "CLD"));
		instructions.put(0xd9, new InstructionImpl(INST_CMP_INDEXED_Y, "CMP"));
		instructions.put(0xdd, new InstructionImpl(INST_CMP_INDEXED_X, "CMP"));
		instructions.put(0xe0, new InstructionImpl(INST_CPX_IMMEDIATE, "CPX"));
		instructions.put(0xe4, new InstructionImpl(INST_CPX_ZERO_PAGE, "CPX"));
		instructions.put(0xe6, new InstructionImpl(INST_INC_ZERO_PAGE, "INC"));
		instructions.put(0xe8, new InstructionImpl(INST_INX, "INX"));
		instructions.put(0xe9, new InstructionImpl(INST_SBC_IMMEDIATE, "SBC"));
		instructions.put(0xec, new InstructionImpl(INST_CPX_ABSOLUTE, "CPX"));
		instructions.put(0xee, new InstructionImpl(INST_INC_ABSOLUTE, "INC"));
		instructions.put(0xf0, new InstructionImpl(INST_BEQ, "BEQ"));
		instructions.put(0xf6, new InstructionImpl(INST_INC_ZERO_PAGE_INDEXED, "INC"));
		instructions.put(0xf8, new InstructionImpl(INST_SED, "SED"));
		instructions.put(0xf9, new InstructionImpl(INST_SBC_INDEXED_Y, "SBC"));
		instructions.put(0xfd, new InstructionImpl(INST_SBC_INDEXED_X, "SBC"));
		instructions.put(0xfe, new InstructionImpl(INST_INC_INDEXED, "INC"));

	}


	/**
	 * Basic implementation of an {@link Instruction}.
	 */
	// TODO: This class is a *very* ugly hack to temporarily get better debug logging.
	// Clean me up!
	private static class InstructionImpl implements Instruction {

		private int opcode;
		private String name;
		//private int bytes;
		private int cycles;

		private static final Field[] INSTRUCTION_FIELDS = getInstructionFields();
		private boolean absolute, immediate, zeroPage, branch;

		private static Field[] getInstructionFields() {
			// TODO: This is a hack and terribly slow as well.  Do things better!
			try {
				Class clazz = Class.forName("org.fife.emu.cpu.n6502.InstructionSet6502");
				return clazz.getDeclaredFields();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		InstructionImpl(int opcode, String name) {
			this.name = name;
			this.opcode = opcode;

			try {
				for (int i = 0; i < INSTRUCTION_FIELDS.length; i++) {
					Field field = INSTRUCTION_FIELDS[i];
					if (opcode == field.getInt(null)) {
						String fieldName = field.getName();
						if (fieldName.endsWith("_ABSOLUTE")) {
							absolute = true;
							break;
						}
						else if (fieldName.endsWith("_IMMEDIATE")) {
							immediate = true;
							break;
						}
						else if (fieldName.endsWith("_ZERO_PAGE")) {
							zeroPage = true;
							break;
						}
						else if (fieldName.matches("INST_B\\w\\w")) {
							branch = true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public int getByteCount() {
			if (immediate) {
				return 2;
			}
			else if (zeroPage) {
				return 2;
			}
			else if (absolute) {
				return 3;
			}
			else if (branch) {
				return 2;
			}
			return 1;
		}

		public int getCycles() {
			return cycles;
		}

		public String getName() {
			return name;
		}

		public int getOpcode() {
			return opcode;
		}

		public String toString(int[] bytes, CpuContext context) {

			StringBuilder sb = new StringBuilder(getName());

			if (immediate) {
				sb.append(" #$").append(Util.toHex(bytes[1]));
			}
			else if (zeroPage) {
				sb.append(" $").append(Util.toHexWord(bytes[1]));
				int address = bytes[1];
				sb.append(" = #$").append(Util.toHex(context.readByteSafely(address)));
			}
			else if (absolute) {
				sb.append(" $").append(Util.toHex(bytes[2])).append(Util.toHex(bytes[1]));
				int address = bytes[1] | (bytes[2] << 8);
				sb.append(" = #$").append(Util.toHex(context.readByteSafely(address)));
			}
			else if (branch) {
				int pcAfterReadingInst = context.getCpu().getPC() + 2;
				sb.append(" $").append(Util.toHexWord(pcAfterReadingInst + (byte)bytes[1]));
			}
			return sb.toString();
		}

	}

}