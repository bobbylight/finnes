package org.fife.emu.cpu.n6502;


/**
 * The set of instructions of the 6502 CPU.
 */
public interface InstructionSet6502 {

	int INST_ORA_PRE_INDEXED_INDIRECT = 0x01;
	int INST_ORA_ZERO_PAGE = 0x05;
	int INST_ASL_ZERO_PAGE = 0x06;
	int INST_PHP = 0x08;
	int INST_ORA_IMMEDIATE = 0x09;
	int INST_ASL_ACCUMULATOR = 0x0a;
	int INST_ORA_ABSOLUTE = 0x0d;
	int INST_BPL = 0x10;
	int INST_ORA_POST_INDEXED_INDIRECT = 0x11;
	int INST_ORA_ZERO_PAGE_INDEXED = 0x15;
	int INST_CLC = 0x18;
	int INST_ORA_INDEXED_Y = 0x19;
	int INST_ORA_INDEXED_X = 0x1d;
	int INST_JSR = 0x20;
	int INST_BIT_ZERO_PAGE = 0x24;
	int INST_ROL_ZERO_PAGE = 0x26;
	int INST_PLP = 0x28;
	int INST_ROL_ACCUMULATOR = 0x2a;
	int INST_BIT_ABSOLUTE = 0x2c;
	int INST_BMI = 0x30;
	int INST_SEC = 0x38;
	int INST_EOR_PRE_INDEXED_INDIRECT = 0x41;
	int INST_EOR_ZERO_PAGE = 0x45;
	int INST_PHA = 0X48;
	int INST_EOR_IMMEDIATE = 0x49;
	int INST_LSR_ACCUMULATOR = 0x4a;
	int INST_JMP_ABSOLUTE = 0x4c;
	int INST_EOR_ABSOLUTE = 0x4d;
	int INST_EOR_POST_INDEXED_INDIRECT = 0x51;
	int INST_EOR_INDEXED_Y = 0x59;
	int INST_EOR_ZERO_PAGE_INDEXED = 0x55;
	int INST_EOR_INDEXED_X = 0x5d;
	int INST_RTS = 0x60;
	int INST_ADC_PRE_INDEXED_INDIRECT = 0x61;
	int INST_ADC_ZERO_PAGE = 0x65;
	int INST_ROR_ZERO_PAGE = 0x66;
	int INST_PLA = 0x68;
	int INST_ADC_IMMEDIATE = 0x69;
	int INST_ROR_ACCUMULATOR = 0x6a;
	int INST_JMP_INDIRECT = 0x6c;
	int INST_ADC_ABSOLUTE = 0x6d;
	int INST_ROR_ABSOLUTE = 0x6e;
	int INST_ADC_POST_INDEXED_INDIRECT = 0x71;
	int INST_ADC_ZERO_PAGE_INDEXED = 0x75;
	int INST_ROR_ZERO_PAGE_INDEXED = 0x76;
	int INST_SEI = 0x78;
	int INST_ADC_INDEXED_Y = 0x79;
	int INST_ADC_INDEXED_X = 0x7d;
	int INST_ROR_INDEXED_X = 0x7e;
	int INST_STA_PRE_INDEXED_INDIRECT = 0x81;
	int INST_STY_ZERO_PAGE = 0x84;
	int INST_STA_ZERO_PAGE = 0x85;
	int INST_STX_ZERO_PAGE = 0x86;
	int INST_DEY = 0x88;
	int INST_TXA = 0x8a;
	int INST_STY_ABSOLUTE = 0x8c;
	int INST_STA_ABSOLUTE = 0x8d;
	int INST_STX_ABSOLUTE = 0x8e;
	int INST_BCC = 0x90;
	int INST_STA_POST_INDEXED_INDIRECT = 0x91;
	int INST_STY_ZERO_PAGE_INDEXED = 0x94;
	int INST_STA_ZERO_PAGE_INDEXED = 0x95;
	int INST_TYA = 0x98;
	int INST_STA_INDEXED_Y = 0x99;
	int INST_TXS_IMPLIED = 0x9a;
	int INST_STA_INDEXED_X = 0x9d;
	int INST_LDY_IMMEDIATE = 0xa0;
	int INST_LDA_PRE_INDEXED_INDIRECT = 0xa1;
	int INST_LDX_IMMEDIATE = 0xa2;
	int INST_LDY_ZERO_PAGE = 0xa4;
	int INST_LDA_ZERO_PAGE = 0xa5;
	int INST_LDX_ZERO_PAGE = 0xa6;
	int INST_TAY = 0xa8;
	int INST_LDA_IMMEDIATE = 0xa9;
	int INST_TAX = 0xaa;
	int INST_LDY_ABSOLUTE = 0xac;
	int INST_LDA_ABSOLUTE = 0xad;
	int INST_LDX_ABSOLUTE = 0xae;
	int INST_BCS = 0xb0;
	int INST_LDA_POST_INDEXED_INDIRECT = 0xb1;
	int INST_LDY_ZERO_PAGE_INDEXED = 0xb4;
	int INST_LDA_ZERO_PAGE_INDEXED = 0xb5;
	int INST_LDX_ZERO_PAGE_INDEXED = 0xb6;
	int INST_LDA_INDEXED_Y = 0xb9;
	int INST_TSX = 0xba;
	int INST_LDY_INDEXED_X = 0xbc;
	int INST_LDA_INDEXED_X = 0xbd;
	int INST_LDX_INDEXED_Y = 0xbe;
	int INST_CPY_IMMEDIATE = 0xc0;
	int INST_CMP_PRE_INDEXED_INDIRECT = 0xc1;
	int INST_CPY_ZERO_PAGE = 0xc4;
	int INST_CMP_ZERO_PAGE = 0xc5;
	int INST_INY = 0xc8;
	int INST_CMP_IMMEDIATE = 0xc9;
	int INST_DEX = 0xca;
	int INST_CPY_ABSOLUTE = 0xcc;
	int INST_CMP_ABSOLUTE = 0xcd;
	int INST_BNE = 0xd0;
	int INST_CMP_POST_INDEXED_INDIRECT = 0xd1;
	int INST_CMP_ZERO_PAGE_INDEXED = 0xd5;
	int INST_CLD = 0xd8;
	int INST_CMP_INDEXED_Y = 0xd9;
	int INST_CMP_INDEXED_X = 0xdd;
	int INST_CPX_IMMEDIATE = 0xe0;
	int INST_CPX_ZERO_PAGE = 0xe4;
	int INST_INC_ZERO_PAGE = 0xe6;
	int INST_INX = 0xe8;
	int INST_SBC_IMMEDIATE = 0xe9;
	int INST_CPX_ABSOLUTE = 0xec;
	int INST_INC_ABSOLUTE = 0xee;
	int INST_BEQ = 0xf0;
	int INST_INC_ZERO_PAGE_INDEXED = 0xf6;
	int INST_SED = 0xf8;
	int INST_SBC_INDEXED_Y = 0xf9;
	int INST_SBC_INDEXED_X = 0xfd;
	int INST_INC_INDEXED = 0xfe;

}
