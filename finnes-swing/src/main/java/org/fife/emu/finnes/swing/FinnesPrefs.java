package org.fife.emu.finnes.swing;

import org.fife.ui.app.GUIApplicationPrefs;

/**
 * Preferences loaded and saved by Finnes on startup and shutdown.
 */
public class FinnesPrefs extends GUIApplicationPrefs<SwingFinnes> {

	@Override
	public GUIApplicationPrefs<SwingFinnes> load() {
		return null;
	}

	@Override
	public GUIApplicationPrefs<SwingFinnes> populate(SwingFinnes app) {
		return null;
	}

	@Override
	public void save() {

	}

	@Override
	protected void setDefaults() {

	}
}
