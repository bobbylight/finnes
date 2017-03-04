package org.fife.emu.finnes.swing.actions;

import org.fife.emu.finnes.swing.SwingFinnes;
import org.fife.ui.app.AppAction;
import org.fife.ui.rtextfilechooser.RTextFileChooser;
import org.fife.ui.rtextfilechooser.filters.ExtensionFileFilter;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.io.File;


/**
 * Lets the user select a ROM to open.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class OpenRomAction extends AppAction<SwingFinnes> {

	private RTextFileChooser chooser;

	/**
	 * Constructor.
	 *
	 * @param owner The parent application.
	 */
	OpenRomAction(SwingFinnes owner) {
		super(owner, "Action.OpenRom");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		SwingFinnes app = getApplication();
		if (chooser == null) {
			chooser = createFileChooser(app);
		}

		int rc = chooser.showOpenDialog(app);
		if (rc == RTextFileChooser.APPROVE_OPTION) {
			LoggerFactory.getLogger(OpenRomAction.class).info("Selected file: {}", chooser.getSelectedFile());
		}
	}

	private static RTextFileChooser createFileChooser(SwingFinnes app) {

		File startDirectory = new File("."); // TODO: Remember previously selected directory

		RTextFileChooser chooser = new RTextFileChooser(false, startDirectory);
		String desc = app.getString("FileFilter.NES");
		chooser.setFileFilter(new ExtensionFileFilter(desc, "nes", "zip"));
		return chooser;
	}
}