/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.br.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.openflexo.AdvancedPrefs;
import org.openflexo.FlexoCst;
import org.openflexo.GeneralPreferences;
import org.openflexo.br.BugReport;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.logging.LogRecord;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.UserType;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.toolbox.ToolBox.RequestResponse;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.FlexoController;

/**
 * Utility class used to generate bug reports
 * 
 * @author sguerin
 */
public class NewBugReport extends FlexoDialog {

	private static final Logger logger = Logger.getLogger(NewBugReport.class.getPackage().getName());

	int returned;

	public static final int CANCEL = 0;

	public static final int EMAIL = 1;

	public static final int REGISTER = 2;

	public static final int SAVE = 3;

	public static final int SEND = 4;

	BugReportView _bugReportView;

	JButton saveToFileButton;
	JButton registerButton;
	JButton emailButton;
	JButton sendButton;
	JButton cancelButton;

	public int getStatus() {
		return returned;
	}

	public NewBugReport(BugReport bugReport) {
		super();
		returned = CANCEL;

		setTitle(FlexoLocalization.localizedForKey("bug_reporting"));
		getContentPane().setLayout(new BorderLayout());

		_bugReportView = new BugReportView(bugReport, true, this);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		saveToFileButton = new JButton(FlexoLocalization.localizedForKey("save_to_file"));
		registerButton = new JButton(FlexoLocalization.localizedForKey("register"));
		emailButton = new JButton(FlexoLocalization.localizedForKey("email"));
		sendButton = new JButton(FlexoLocalization.localizedForKey("send_bug"));
		cancelButton = new JButton(FlexoLocalization.localizedForKey("cancel"));
		disableButtons();
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				returned = CANCEL;
				dispose();
			}
		});
		saveToFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				returned = SAVE;
				_bugReportView.updateValues();
			}
		});
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				returned = REGISTER;
				_bugReportView.updateValues();
				dispose();
			}
		});
		emailButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				returned = EMAIL;
				_bugReportView.updateValues();
				dispose();
			}
		});
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				returned = SEND;
				_bugReportView.updateValues();
				if (_bugReportView.getBugReport().isValid())
					dispose();
				else
					FlexoController.notify(FlexoLocalization.localizedForKey("title_and_description_are_mandatory"));
			}
		});
		controlPanel.add(cancelButton);
		if (ModuleLoader.isMaintainerRelease())
			controlPanel.add(saveToFileButton);
		if (ModuleLoader.isMaintainerRelease())
			controlPanel.add(registerButton);
		controlPanel.add(sendButton);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		contentPanel.add(_bugReportView, BorderLayout.CENTER);
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		setModal(true);
		validate();
		pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
		setVisible(true);
	}

	public static void newBugReport(BugReport newBugReport) {
		NewBugReport window = new NewBugReport(newBugReport);
		if (window.getStatus() == SAVE) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return (f.getName().endsWith(".brxml"));
				}

				@Override
				public String getDescription() {
					return FlexoLocalization.localizedForKey("flexo_bug_reports");
				}
			});
			File selectedFile = null;
			File userDirectory = new File(System.getProperty("user.dir"));
			File defaultFile = new File(userDirectory, "NewBugReport.brxml");
			chooser.setSelectedFile(defaultFile);
			int returnVal = chooser.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedFile = chooser.getSelectedFile();
				if (!selectedFile.getName().endsWith(".brxml"))
					selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".brxml");
				newBugReport.saveToFile(selectedFile);
			}
		} else if (window.getStatus() == REGISTER) {
			newBugReport.save();
		} else if (window.getStatus() == EMAIL) {
			sendEMail(newBugReport);
		} else if (window.getStatus() == SEND) {
			String error = sendToDLPM(newBugReport, 30, 3);
			if (error != null) {
				FlexoController.showError(error);
				newBugReport(newBugReport);
			} else {
				FlexoController.notify(FlexoLocalization.localizedForKey("your_report_has_been_sent.") + "\n"
						+ FlexoLocalization.localizedForKey("thank_you."));
			}
		}
	}

	public static void newBugReport(Exception e, Module module) {
		BugReport newBugReport = new BugReport(e, module);
		newBugReport(newBugReport);
	}

	public static void newBugReport(Module module) {
		BugReport newBugReport = new BugReport(module);
		newBugReport(newBugReport);
	}

	public static void newBugReport() {
		BugReport newBugReport = new BugReport(true);
		newBugReport(newBugReport);
	}

	private static final int MAXIMUM_SIZE_DESC_AND_OTHER_INFO = 500000 + 2000;

	private static void sendEMail(BugReport bugReport) {
		String SMTP_HOST = "mail.smtp.host";
		if (GeneralPreferences.getSmtpServer() == null) {
			GeneralPreferences.setSmtpServer(FlexoController.askForString("please_specify_your_smtp_server"));
			FlexoPreferences.savePreferences(true);
		}

		boolean tryAgain = true;
		while (tryAgain) {
			Properties props = new Properties();
			// props.put("mail.smtp.host", "smtp.wanadoo.fr");
			props.put(SMTP_HOST, GeneralPreferences.getSmtpServer());
			Session s = Session.getInstance(props, null);
			if (logger.isLoggable(Level.INFO))
				logger.info("Trying to send mail with " + props.getProperty(SMTP_HOST));
			InternetAddress from;
			try {
				from = new InternetAddress(bugReport.submissionUserName + "@denali.be");
				InternetAddress to = new InternetAddress(FlexoCst.DENALI_SUPPORT_EMAIL);

				MimeMessage message = new MimeMessage(s);
				message.setFrom(from);
				message.addRecipient(Message.RecipientType.TO, to);

				message.setSubject("[" + bugReport.identifier + "] " + bugReport.title);
				message.setText(bugReport.getXMLRepresentation());

				Transport.send(message);
				tryAgain = false;
				FlexoController.notify("bug_report_successfully_sent");

			} catch (javax.mail.NoSuchProviderException e) {
				e.printStackTrace();
				if (logger.isLoggable(Level.FINE))
					logger.fine(e.getMessage());
				String newSMTPServer = FlexoController.askForString("sending_failed_please_specify_your_smtp_server");
				if (newSMTPServer != null) {
					GeneralPreferences.setSmtpServer(newSMTPServer);
					FlexoPreferences.savePreferences(true);
					tryAgain = true;
				} else {
					tryAgain = false;
				}
			} catch (AddressException e) {
				e.printStackTrace();
				tryAgain = false;
				FlexoController.showError("could_not_send_bug_report");
			} catch (MessagingException e) {
				e.printStackTrace();
				tryAgain = false;
				FlexoController.showError("could_not_send_bug_report");
			}
		}

	}

	private static String sendToDLPM(BugReport bugReport, int timeoutInSeconds, int numberOfTries) {
		// ensure we got the url
		if (AdvancedPrefs.getBugReportDirectActionUrl() == null) {
			String urlDialogResponse = FlexoController.askForString("please_specify_the_bug_report_application_url");
			if (urlDialogResponse == null || urlDialogResponse.length() == 0)
				return "Operation cancelled";
			AdvancedPrefs.setBugReportDirectActionUrl(urlDialogResponse);
			FlexoPreferences.savePreferences(true);
		}
		if (AdvancedPrefs.getBugReportDirectActionUrl() == null) {
			return FlexoLocalization.localizedForKey("sorry_you_must_provide_the_bug_report_application_url");
		}
		try {
			new URL(AdvancedPrefs.getBugReportDirectActionUrl());
		} catch (MalformedURLException e1) {
			return FlexoLocalization.localizedForKey("the_url_" + AdvancedPrefs.getBugReportDirectActionUrl() + "_is_not_valid");
		}
		final Hashtable<String, String> param = new Hashtable<String, String>();
		if (bugReport.title == null || bugReport.title.trim().equals(""))
			return FlexoLocalization.localizedForKey("please_specify_a_title");
		param.put("title", bugReport.title + " (" + (bugReport.module == null ? "module not specified" : bugReport.module.getName() + ")"));
		param.put("login", bugReport.submissionUserName == null ? System.getProperty("user.name") : bugReport.submissionUserName);
		if (bugReport.identifier != null)
			param.put("externalKey", bugReport.identifier);

		// garantee null or a non empty and trimmed string on context and descriptop,
		bugReport.context = bugReport.context == null || bugReport.context.trim().length() == 0 ? null : bugReport.context.trim();
		bugReport.description = bugReport.description == null || bugReport.description.trim().length() == 0 ? null : bugReport.description
				.trim();

		if (bugReport.context == null && bugReport.description == null)
			return FlexoLocalization.localizedForKey("please_specify_a_description_or_context");
		StringBuffer incidentDesc = new StringBuffer("");
		if (bugReport.context != null) {
			incidentDesc.append("<h4>CONTEXT</h4>");
			incidentDesc.append(bugReport.context);
		}
		if (bugReport.description != null) {
			if (incidentDesc.length() > 0) {
				incidentDesc.append("<br/>");
			}
			incidentDesc.append("<h4>DESCRIPTION :</h4>");
			incidentDesc.append(bugReport.description);
		}
		if (bugReport.type != null)
			param.put("incidentType", bugReport.type.getKey());
		param.put("desc", incidentDesc.toString());
		int remainingLength = MAXIMUM_SIZE_DESC_AND_OTHER_INFO - incidentDesc.length();
		param.put("impact", transformImpact(bugReport.impact));
		param.put("urgency", transformUrgency(bugReport.urgency));

		StringBuffer otherInfoString = new StringBuffer("");
		otherInfoString.append("build ID=").append(FlexoCst.BUILD_ID).append("\n");
		if (bugReport.technicalDescription != null && !bugReport.technicalDescription.trim().equals("")) {
			otherInfoString.append("COMMENTAIRES :\n");
			otherInfoString.append(bugReport.technicalDescription);
		}
		if (bugReport.stacktrace != null && !bugReport.stacktrace.trim().equals("")) {
			if (otherInfoString.length() > 0) {
				otherInfoString.append("\n");
			}
			otherInfoString.append("STACKTRACE :\n");
			otherInfoString.append(bugReport.stacktrace);
		}
		otherInfoString.append("Look & Feel = ").append(UIManager.getLookAndFeel().getClass().getName()).append("\n");
		Iterator<Object> i = new TreeMap<Object, Object>(System.getProperties()).keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			if ("line.separator".equals(key)) {
				String nl = System.getProperty(key);
				nl = nl.replace("\r", "\\r");
				nl = nl.replace("\n", "\\n");
				otherInfoString.append(key).append(" = ").append(nl).append('\n');
			} else
				otherInfoString.append(key).append(" = ").append(System.getProperty(key)).append('\n');
		}
		remainingLength -= otherInfoString.length();
		for (int counter = FlexoLoggingManager.logRecords.records.size() - 1; counter >= 0; counter--) {
			LogRecord record = FlexoLoggingManager.logRecords.records.get(counter);
			StringBuffer sb = new StringBuffer();
			sb.append(record.level).append(" ");
			sb.append(record.message).append(" ");
			sb.append(record.logger).append(" ");
			sb.append(record.classAsString()).append(" ");
			sb.append(record.methodName).append(" ");
			sb.append(record.sequenceAsString()).append(" ");
			sb.append(record.dateAsString());
			sb.append("\n");
			if (sb.length() < remainingLength) {
				otherInfoString.append(sb);
				remainingLength -= sb.length();
			} else
				break;
		}
		param.put("otherInfo", otherInfoString.toString());

		/*        param.put("foundInReleaseID", FlexoCst.DLPM_RELEASE_VERSION_ID);
		        param.put("dlpmProjectID", FlexoCst.DLPM_PROJECT_ID);*/

		param.put("workpackageID", FlexoCst.DLPM_WORKPACKAGE_ID);
		param.put("foundInReleaseString", FlexoCst.BUSINESS_APPLICATION_VERSION.toString(true));
		final StringBuffer sb = new StringBuffer();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String sendRequest = sendRequest(param);
					if (sendRequest != null)
						sb.append(sendRequest);
				} catch (Exception e) {
					e.printStackTrace();
					sb.append(FlexoLocalization.localizedForKey("an error has occured !\n(error message: " + e.getMessage()));
				}
			}
		}, "Send bug request to DLPM");
		t.start();
		int tries = 0;
		while (t.isAlive() && tries < numberOfTries) {
			try {
				synchronized (t) {
					t.wait(timeoutInSeconds * 1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tries++;
			if (t.isAlive()) {
				if (tries < numberOfTries) {
					if (FlexoController.confirm(FlexoLocalization.localizedForKey("could_not_send_incident_so_far_keep_trying") + "? ("
							+ tries + ")"))
						continue;
				}
				return FlexoLocalization.localizedForKey("send_incident_timeout");
			}
		}
		t.interrupt();
		if (sb.length() > 0)
			return sb.toString();
		return null;
	}

	/**
	 * @param param
	 * @return
	 */
	protected static String sendRequest(Hashtable<String, String> param) {
		String url = AdvancedPrefs.getBugReportDirectActionUrl();
		ToolBox.RequestResponse response;
		try {
			response = ToolBox.getRequest(param, url);
		} catch (UnknownHostException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(url);
				logger.log(Level.SEVERE, "Error while sending bug to url " + url, e);
			}
			return FlexoLocalization.localizedForKey("host") + " \"" + e.getMessage() + "\" "
					+ FlexoLocalization.localizedForKey("cannot_be_reached");
		} catch (IOException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(url);
				logger.log(Level.SEVERE, "Error while sending bug to url " + url, e);
			}
			return FlexoLocalization.localizedForKey("an error has occured !\n(error message: " + e.getMessage());
		}
		if (hasError(response))
			return getErrorMessage(response);
		return null;
	}

	private static String transformImpact(int impact) {
		if (impact == 0)
			return "2";
		if (impact == 1)
			return "1";
		if (impact == 2)
			return "0";
		return "2";
	}

	private static String transformUrgency(int urgency) {
		if (urgency == 0)
			return "3";
		if (urgency == 1)
			return "2";
		if (urgency == 2)
			return "1";
		return "3";
	}

	private static boolean hasError(RequestResponse response) {
		return response.status < 200 || response.status >= 300;
	}

	private static String getErrorMessage(RequestResponse response) {
		return FlexoLocalization.localizedForKey("An error has occured !")
				+ ((ModuleLoader.getUserType() == UserType.DEVELOPER || ModuleLoader.getUserType() == UserType.MAINTAINER) ? response.response
						: "");
	}

	public void disableButtons() {
		if (saveToFileButton != null)
			saveToFileButton.setEnabled(false);
		if (registerButton != null)
			registerButton.setEnabled(false);
		if (emailButton != null)
			emailButton.setEnabled(false);
		if (sendButton != null)
			sendButton.setEnabled(false);
	}

	public void enableButtons() {
		if (saveToFileButton != null)
			saveToFileButton.setEnabled(true);
		if (registerButton != null)
			registerButton.setEnabled(true);
		if (emailButton != null)
			emailButton.setEnabled(true);
		if (sendButton != null)
			sendButton.setEnabled(true);
	}
}
