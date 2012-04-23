package org.openflexo.br.view;

import java.io.File;

import org.openflexo.AdvancedPrefs;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoFrame;

public class JIRAURLCredentialsDialog {

	public static final File URL_FIB_FILE = new FileResource("Fib/JIRAURLCredentialsDialog.fib");

	private String login;
	private String password;

	public JIRAURLCredentialsDialog() {
		login = AdvancedPrefs.getBugReportUser();
		password = AdvancedPrefs.getBugReportPassword();
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrlLabel() {
		return "<html><a href=\"" + AdvancedPrefs.getBugReportUrl() + "\">" + AdvancedPrefs.getBugReportUrl() + "</a></html>";
	}

	public void openUrl() {
		ToolBox.openURL(AdvancedPrefs.getBugReportUrl());
	}

	public static boolean askLoginPassword() {
		JIRAURLCredentialsDialog credentialsDialog = new JIRAURLCredentialsDialog();
		FIBDialog<JIRAURLCredentialsDialog> dialog = FIBDialog.instanciateComponent(URL_FIB_FILE, credentialsDialog,
				FlexoFrame.getActiveFrame(), true, FlexoLocalization.getMainLocalizer());
		if (dialog.getStatus() == Status.VALIDATED) {
			AdvancedPrefs.setBugReportUser(credentialsDialog.login);
			AdvancedPrefs.setBugReportPassword(credentialsDialog.password);
			AdvancedPrefs.save();
			return true;
		}
		return false;
	}
}
