package org.fife.emu.finnes.swing;

import org.fife.emu.finnes.swing.actions.ActionFactory;
import org.fife.emu.finnes.swing.actions.ActionKeys;
import org.fife.help.HelpDialog;
import org.fife.ui.CustomizableToolBar;
import org.fife.ui.SplashScreen;
import org.fife.ui.StatusBar;
import org.fife.ui.app.AbstractGUIApplication;

import javax.swing.*;

/**
 * Application entry point for the Swing implementation of the Finnes emulator.
 */
public class SwingFinnes extends AbstractGUIApplication<FinnesPrefs> implements ActionKeys {

	public static final String VERSION_STRING = "0.1.0";

	@Override
	protected void createActions(FinnesPrefs prefs) {
		ActionFactory.addActions(this, prefs);
	}

	@Override
	protected JMenuBar createMenuBar(FinnesPrefs prefs) {
		return new SwingFinnesMenuBar(this);
	}

	@Override
	protected SplashScreen createSplashScreen() {
		return null;
	}

	@Override
	protected StatusBar createStatusBar(FinnesPrefs prefs) {
		return new StatusBar();
	}

	@Override
	protected CustomizableToolBar createToolBar(FinnesPrefs prefs) {
		return null;
	}

	@Override
	public HelpDialog getHelpDialog() {
		return null;
	}

	@Override
	protected String getPreferencesClassName() {
		return null;
	}

	@Override
	public String getResourceBundleClassName() {
		return "org.fife.emu.finnes.Resources";
	}

	@Override
	public String getVersionString() {
		return null;
	}

	@Override
	public void openFile(String fileName) {

	}

	@Override
	public void preferences() {

	}

	@Override
	protected void preDisplayInit(FinnesPrefs prefs, SplashScreen splashScreen) {

		setTitle(getString("App.Title.NoGame"));
	}

	@Override
	protected void preMenuBarInit(FinnesPrefs prefs, SplashScreen splashScreen) {

	}

	@Override
	protected void preStatusBarInit(FinnesPrefs prefs, SplashScreen splashScreen) {

	}

	@Override
	protected void preToolBarInit(FinnesPrefs prefs, SplashScreen splashScreen) {

	}
}
