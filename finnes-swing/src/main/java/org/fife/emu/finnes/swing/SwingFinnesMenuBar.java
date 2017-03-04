package org.fife.emu.finnes.swing;

import org.fife.ui.app.MenuBar;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * The menu bar for the Swing UI of application.
 */
public class SwingFinnesMenuBar extends MenuBar {

	private static final String MENU_FILE = "FileMenu";
	private static final String MENU_HELP = "HelpMenu";

	private SwingFinnes finnes;

	/**
	 * Creates a tool bar instance.
	 *
	 * @param finnes The parent application.
	 */
	public SwingFinnesMenuBar(SwingFinnes finnes) {

		this.finnes = finnes;
		ResourceBundle msg = finnes.getResourceBundle();

		// File menu
		JMenu fileMenu = createMenu(msg, "Menu.File");
		registerMenuByName(MENU_FILE, fileMenu);
		add(fileMenu);

		fileMenu.add(createMenuItem(finnes.getAction(SwingFinnes.OPEN_ACTION_KEY)));
		fileMenu.addSeparator();
		fileMenu.add(createMenuItem(finnes.getAction(SwingFinnes.EXIT_ACTION_KEY)));

		// Help menu
		JMenu helpMenu = createMenu(msg, "Menu.Help");
		registerMenuByName(MENU_HELP, helpMenu);
		add(helpMenu);

		helpMenu.add(createMenuItem(finnes.getAction(SwingFinnes.HELP_ACTION_KEY)));
		helpMenu.add(createMenuItem(finnes.getAction(SwingFinnes.HOME_PAGE_ACTION_KEY)));
		helpMenu.add(createMenuItem(finnes.getAction(SwingFinnes.UPDATES_ACTION_KEY)));
		helpMenu.addSeparator();
		helpMenu.add(createMenuItem(finnes.getAction(SwingFinnes.ABOUT_ACTION_KEY)));
	}
}
