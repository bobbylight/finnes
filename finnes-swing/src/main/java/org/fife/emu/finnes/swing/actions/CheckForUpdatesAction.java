package org.fife.emu.finnes.swing.actions;

import org.fife.emu.finnes.swing.SwingFinnes;
import org.fife.ui.app.AppAction;

import java.awt.event.ActionEvent;


/**
 * An action that checks a simple web service for the latest version of this application.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class CheckForUpdatesAction extends AppAction<SwingFinnes> {

	/**
	 * The URL to contact to see if there is a newer RText release.
	 */
	private static final String CHECK_URL =
		"http://fifesoft.com/rtext/latest.properties?clientVersion=" + SwingFinnes.VERSION_STRING;

	/**
	 * Where the user is directed to download the latest version.
	 */
	private static final String DOWNLOAD_URL = "https://github.com/bobbylight/finnes";

	/**
	 * Constructor.
	 *
	 * @param owner The parent application.
	 */
	CheckForUpdatesAction(SwingFinnes owner) {
		super(owner, "Action.CheckForUpdates");
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO
	}

}