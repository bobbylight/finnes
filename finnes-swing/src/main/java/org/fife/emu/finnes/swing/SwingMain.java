package org.fife.emu.finnes.swing;

import javax.swing.*;

/**
 * Application entry point for the Swing UI.
 */
public final class SwingMain {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private SwingMain() {
	}

	/**
	 * Program entry point.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {
            try {
            	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            	e.printStackTrace();
			}
			new SwingFinnes();
        });
	}
}