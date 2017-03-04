package org.fife.emu.finnes.swing.actions;

import org.fife.emu.finnes.swing.SwingFinnes;
import org.fife.ui.UIUtil;
import org.fife.ui.app.AppAction;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Action that opens a web browser to Finnes's home page.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class HomePageAction extends AppAction<SwingFinnes> {

	/**
	 * Constructor.
	 *
	 * @param owner The parent application.
	 */
	HomePageAction(SwingFinnes owner) {
		super(owner, "Action.HomePage");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!UIUtil.browse("https://github.com/bobbylight/finnes")) {
			SwingFinnes app = getApplication();
			UIManager.getLookAndFeel().provideErrorFeedback(app);
		}
	}
}