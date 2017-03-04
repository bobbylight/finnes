package org.fife.emu.finnes.swing.actions;

import org.fife.emu.finnes.swing.FinnesPrefs;
import org.fife.emu.finnes.swing.SwingFinnes;

/**
 * Utility class for registering actions in the application.
 */
public final class ActionFactory implements ActionKeys {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ActionFactory() {
	}

	/**
	 * Installs the application's actions.
	 *
	 * @param finnes The application instance.
	 * @param prefs The application's preferences.
	 */
	public static void addActions(SwingFinnes finnes, FinnesPrefs prefs) {

		finnes.addAction(SwingFinnes.OPEN_ACTION_KEY, new OpenRomAction(finnes));
		finnes.addAction(SwingFinnes.EXIT_ACTION_KEY, new SwingFinnes.ExitAction<>(finnes, "Action.Exit"));

		finnes.addAction(SwingFinnes.HELP_ACTION_KEY, new SwingFinnes.HelpAction<>(finnes, "Action.Help"));
		finnes.addAction(SwingFinnes.ABOUT_ACTION_KEY, new SwingFinnes.AboutAction<>(finnes, "Action.About"));
		finnes.addAction(UPDATES_ACTION_KEY, new CheckForUpdatesAction(finnes));
		finnes.addAction(HOME_PAGE_ACTION_KEY, new HomePageAction(finnes));
	}
}
