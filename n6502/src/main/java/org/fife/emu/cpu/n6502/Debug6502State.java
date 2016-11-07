package org.fife.emu.cpu.n6502;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import org.fife.emu.CpuContext;


/**
 * Utility class to log debug information about the 6502 CPU state.
 */
class Debug6502State {

	private CpuContext nes;
	private n6502Impl cpu;
	private InstructionInfo6502 instrs;

	private int pc;
	private int[] bytes;
	private Instruction instruction;

	private PrintWriter w;


	Debug6502State(n6502 cpu) {
		this.cpu = (n6502Impl)cpu;
		this.nes = cpu.getCpuContext();
		bytes = new int[3];
		instrs = new InstructionInfo6502();
		w = new PrintWriter(System.out);
	}


	public void close() throws IOException {
		w.close();
	}


	private String debugStackInfo() {
		StringBuilder sb = new StringBuilder("[SP:").append(cpu.getSP());
		for (int i=0; i<3; i++) {
			sb.append(", ").append(Util.toHex(cpu.peekByte(i)));
		}
		return sb.append("]").toString();
	}


	private String getFlagStateStr() {
		char[] flags = "nvubdizc".toCharArray();
		for (int i=0; i<flags.length; i++) {
			if (cpu.getStatusFlag(flags.length-i-1)>0) {
				flags[i] = Character.toUpperCase(flags[i]);
			}
		}
		return new String(flags);
	}


	public void log() throws IOException {

		setInstruction();
		String inst = instruction!=null ? instruction.toString(bytes, nes) : "???";

		String b1 = Util.toHex(bytes[0]);
		String b2 = bytes[1]<0 ? "  " : Util.toHex(bytes[1]);
		String b3 = bytes[2]<0 ? "  " : Util.toHex(bytes[2]);
		inst = inst + ""; // TODO ...
//		while (inst.length() < "SEI                        ".length()) {
//			inst += " ";
//		}

		w.printf("A:%s X:%s Y:%s S:%s P:%s   $%04X:%s %s %s   %s L=%s %s\n",
			new Object[] {
				Util.toHex(cpu.a), Util.toHex(cpu.x), Util.toHex(cpu.y), Util.toHex(cpu.getSP()),
				getFlagStateStr(),
				new Integer(pc), b1, b2, b3,
				debugStackInfo(),
				"???", //Long.valueOf(((NES)nes).getPpu().getLatchClean()),
				inst
		});
	}


	public void log(String text) throws IOException {
		w.println(text);
	}


	private void setInstruction() {
		pc = nes.getCpu().getPC();
		int inst = nes.readByte(pc);
		instruction = instrs.get(new Integer(inst));

		bytes[0] = inst;
		bytes[1] = bytes[2] = -1;
		if (instruction != null) {
			for (int i=1; i<instruction.getByteCount(); i++) {
				bytes[i] = nes.readByte(pc+i);
			}
		}

	}


	public void setLog(Path file) {
		System.out.println("NOTE: Logging to file: " + file.toFile().getAbsolutePath());
		try {
			w = new PrintWriter(file.toFile());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			w = new PrintWriter(System.out);
		}
	}


}