package org.openflexo.br.view;

import java.io.File;

import org.openflexo.ApplicationContext;
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
	private final ApplicationContext applicationContext;

	public JIRAURLCredentialsDialog(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		login = applicationContext.getAdvancedPrefs().getBugReportUser();
		password = applicationContext.getAdvancedPrefs().getBugReportPassword();
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
		return "<html><a href=\"" + applicationContext.getAdvancedPrefs().getBugReportUrl() + "\">"
				+ applicationContext.getAdvancedPrefs().getBugReportUrl() + "</a></html>";
	}

	public void openUrl() {
		ToolBox.openURL(applicationContext.getAdvancedPrefs().getBugReportUrl());
	}

	public static boolean askLoginPassword(ApplicationContext applicationContext) {
		JIRAURLCredentialsDialog credentialsDialog = new JIRAURLCredentialsDialog(applicationContext);
		FIBDialog<JIRAURLCredentialsDialog> dialog = FIBDialog.instanciateAndShowDialog(URL_FIB_FILE, credentialsDialog,
				FlexoFrame.getActiveFrame(), true, FlexoLocalization.getMainLocalizer());
		if (dialog.getStatus() == Status.VALIDATED) {
			applicationContext.getAdvancedPrefs().setBugReportUser(credentialsDialog.login);
			applicationContext.getAdvancedPrefs().setBugReportPassword(credentialsDialog.password);
			// AdvancedPrefs.save();
			return true;
		}
		return false;
	}
}
