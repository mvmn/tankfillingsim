package x.mvmn.tankfillgame.swing;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ErrorHelper {

	public static void showError(Throwable t) {
		showError("Error occurred", t.getClass().getSimpleName() + " " + t.getMessage());
	}

	public static void showError(String title, String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
