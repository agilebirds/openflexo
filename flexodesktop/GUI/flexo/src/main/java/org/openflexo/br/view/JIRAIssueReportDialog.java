package org.openflexo.br.view;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

import org.openflexo.AdvancedPrefs;
import org.openflexo.ApplicationVersion;
import org.openflexo.Flexo;
import org.openflexo.br.BugReportManager;
import org.openflexo.components.ProgressWindow;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.Modules;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.toolbox.ZipUtils;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.ws.jira.JIRAClient;
import org.openflexo.ws.jira.JIRAClient.Method;
import org.openflexo.ws.jira.JIRAClient.Progress;
import org.openflexo.ws.jira.JIRAException;
import org.openflexo.ws.jira.UnauthorizedJIRAAccessException;
import org.openflexo.ws.jira.action.SubmitIssue;
import org.openflexo.ws.jira.model.JIRAComponent;
import org.openflexo.ws.jira.model.JIRAErrors;
import org.openflexo.ws.jira.model.JIRAIssue;
import org.openflexo.ws.jira.model.JIRAObject;
import org.openflexo.ws.jira.model.JIRAPriority;
import org.openflexo.ws.jira.model.JIRAProject;
import org.openflexo.ws.jira.model.JIRAVersion;
import org.openflexo.ws.jira.result.JIRAResult;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class JIRAIssueReportDialog {

	private final class ReportProgress implements Progress {
		int count = 0;

		@Override
		public void setProgress(double percentage) {
			int steps = (int) (percentage * 100);
			steps -= count;
			for (int i = 0; i < steps; i++) {
				ProgressWindow.instance().setSecondaryProgress("");
			}
			count += steps;
		}

		public void resetCount() {
			ProgressWindow.instance().resetSecondaryProgress(100);
			count = 0;
		}

	}

	public static class SubmitIssueReport {

		private String issueLink;

		private List<String> errors;
		private List<String> warnings;

		public SubmitIssueReport() {
			errors = new ArrayList<String>();
			warnings = new ArrayList<String>();
		}

		public boolean hasErrors() {
			return errors.size() > 0;
		}

		public boolean hasWarnings() {
			return warnings.size() > 0;
		}

		public String getIssueLink() {
			return issueLink;
		}

		public void setIssueLink(String issueLink) {
			this.issueLink = issueLink;
		}

		public List<String> getErrors() {
			return errors;
		}

		public List<String> getWarnings() {
			return warnings;
		}

		public void addToErrors(String error) {
			errors.add(error);
		}

		public void addToWarning(String warning) {
			warnings.add(warning);
		}

		public String errorsToString() {
			StringBuilder sb = new StringBuilder();
			for (String e : errors) {
				if (sb.length() > 0) {
					sb.append('\n');
				}
				sb.append(e);
			}
			return sb.toString();
		}

		public String warningsToString() {
			StringBuilder sb = new StringBuilder();
			for (String w : warnings) {
				if (sb.length() > 0) {
					sb.append('\n');
				}
				sb.append(w);
			}
			return sb.toString();
		}

		public String issueLinkHyperlink() {
			return "<html><a href=\"" + issueLink + "\">" + issueLink + "</a></href>";
		}

		public void openIssueLink() {
			ToolBox.openURL(getIssueLink());
		}
	}

	private static final Logger logger = FlexoLogger.getLogger(JIRAIssueReportDialog.class.getPackage().getName());

	public static final File FIB_FILE = new FileResource("Fib/JIRAIssueReportDialog.fib");
	public static final File REPORT_FIB_FILE = new FileResource("Fib/JIRASubmitIssueReportDialog.fib");
	private static final List<JIRAComponent> EMPTY_LIST = new ArrayList<JIRAComponent>(0);

	private JIRAIssue issue;
	private JIRAProject project;

	private boolean sendLogs;
	private boolean sendScreenshots;
	private boolean sendProject;
	private boolean sendSystemProperties;

	private File attachFile;

	private FlexoProject flexoProject;

	public static void newBugReport(FlexoModule module, FlexoProject project) {
		newBugReport(null, module, project);
	}

	public static void newBugReport(Exception e, FlexoModule module, FlexoProject project) {
		try {
			JIRAIssueReportDialog report = new JIRAIssueReportDialog(e);
			report.setFlexoProject(project);
			if (module != null) {
				if (report.getIssue().getIssuetype().getComponentField() != null
						&& report.getIssue().getIssuetype().getComponentField().getAllowedValues() != null) {
					for (JIRAComponent comp : report.getIssue().getIssuetype().getComponentField().getAllowedValues()) {
						if (comp.getId().equals(module.getModule().getJiraComponentID())) {
							report.getIssue().setComponent(comp);
							break;
						}
					}
				}
			}
			FIBDialog<JIRAIssueReportDialog> dialog = FIBDialog.instanciateAndShowDialog(FIB_FILE, report, FlexoFrame.getActiveFrame(),
					true, FlexoLocalization.getMainLocalizer());
			boolean ok = false;
			while (!ok) {
				if (dialog.getStatus() == Status.VALIDATED) {
					try {
						while (AdvancedPrefs.getBugReportUser() == null || AdvancedPrefs.getBugReportUser().trim().length() == 0
								|| AdvancedPrefs.getBugReportPassword() == null
								|| AdvancedPrefs.getBugReportPassword().trim().length() == 0) {
							if (!JIRAURLCredentialsDialog.askLoginPassword()) {
								break;
							}
						}
						ok = dialog.getData().send();
					} catch (MalformedURLException e1) {
						FlexoController.showError(FlexoLocalization.localizedForKey("could_not_send_bug_report") + " " + e.getMessage());
					} catch (UnauthorizedJIRAAccessException e1) {
						if (JIRAURLCredentialsDialog.askLoginPassword()) {
							continue;
						} else {
							break;
						}
					} catch (JIRAException e1) {
						StringBuilder sb = new StringBuilder();
						JIRAErrors errors = e1.getErrors();
						if (errors.getErrorMessages() != null) {
							for (String s : errors.getErrorMessages()) {
								if (sb.length() > 0) {
									sb.append('\n');
								}
								sb.append(s);
							}
						}
						if (errors.getErrors() != null) {
							for (Entry<String, String> e2 : errors.getErrors().entrySet()) {
								if (sb.length() > 0) {
									sb.append('\n');
								}
								sb.append(FlexoLocalization.localizedForKey("field") + " " + FlexoLocalization.localizedForKey(e2.getKey())
										+ " : " + FlexoLocalization.localizedForKey(e2.getValue()));
							}
						}
						FlexoController.notify(FlexoLocalization.localizedForKey("could_not_send_bug_report") + ":\n" + sb.toString());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else {
					break;
				}
				if (!ok) {
					dialog.setVisible(true);
				}
			}
		} catch (JsonSyntaxException e1) {
			e1.printStackTrace();
			FlexoController.showError(FlexoLocalization.localizedForKey("cannot_read_JIRA_project_file"));
		} catch (JsonIOException e1) {
			e1.printStackTrace();
			FlexoController.showError(FlexoLocalization.localizedForKey("cannot_read_JIRA_project_file"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			FlexoController.showError(FlexoLocalization.localizedForKey("cannot_read_JIRA_project_file"));
		}
	}

	private void setFlexoProject(FlexoProject flexoProject) {
		this.flexoProject = flexoProject;
	}

	public JIRAIssueReportDialog() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		this(null);
	}

	public JIRAIssueReportDialog(Exception e) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		this.project = BugReportManager.getInstance().getOpenFlexoProject();
		issue = new JIRAIssue();
		issue.setIssuetype(project.getIssuetypes().get(0));
		issue.setProject(project);
		if (issue.getIssuetype().getPriorityField() != null && issue.getIssuetype().getPriorityField().getAllowedValues() != null) {
			JIRAPriority major = null;
			for (JIRAPriority p : issue.getIssuetype().getPriorityField().getAllowedValues()) {
				if ("Major".equals(p.getName())) {
					major = p;
					break;
				}
				if ("3".equals(p.getId())) {
					major = p;
				}
			}
			issue.setPriority(major);
		}
		sendSystemProperties = false;
		sendScreenshots = false;
		sendLogs = true;
		if (e != null) {
			issue.setStacktrace(e.getClass().getName() + ": " + e.getMessage() + "\n" + ToolBox.getStackTraceAsString(e));
		}
	}

	public JIRAProject getProject() {
		return project;
	}

	public JIRAIssue getIssue() {
		return issue;
	}

	public void setIssue(JIRAIssue issue) {
		this.issue = issue;
	}

	public boolean send() throws Exception {
		JIRAClient client = new JIRAClient(AdvancedPrefs.getBugReportUrl(), AdvancedPrefs.getBugReportUser(),
				AdvancedPrefs.getBugReportPassword());
		final SubmitIssueReport report = new SubmitIssueReport();
		SubmitIssueToJIRA target = new SubmitIssueToJIRA(client, report);
		int steps = target.getNumberOfSteps();
		ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("submitting_bug_report"), steps);
		try {
			boolean submit = true;
			while (submit) {
				target.run();
				if (target.getException() != null) {
					if (target.getException() instanceof SocketTimeoutException) {
						submit = FlexoController.confirm(FlexoLocalization.localizedForKey("could_not_send_incident_so_far_keep_trying")
								+ "? ");
						if (submit) {
							client.setTimeout(client.getTimeout() * 2);// Let's increase time out
						}
					} else {
						throw target.getException();
					}
				} else {
					submit = false;
				}
			}
		} finally {
			ProgressWindow.hideProgressWindow();
		}
		FIBDialog
				.instanciateAndShowDialog(REPORT_FIB_FILE, report, FlexoFrame.getActiveFrame(), true, FlexoLocalization.getMainLocalizer());
		return !report.hasErrors();
	}

	private class SubmitIssueToJIRA implements Runnable {

		private SubmitIssueReport report;
		private Exception exception;

		private final JIRAClient client;

		protected SubmitIssueToJIRA(JIRAClient client, SubmitIssueReport report) {
			super();
			this.client = client;
			this.report = report;
		}

		public int getNumberOfSteps() {
			int steps = 1;
			if (sendProject) {
				steps += 2;
			}
			if (sendLogs) {
				steps++;
			}
			if (attachFile != null) {
				steps++;
			}
			if (sendScreenshots) {
				for (int i = 0; i < Frame.getFrames().length; i++) {
					Frame frame = Frame.getFrames()[i];
					if (frame instanceof FlexoFrame) {
						steps++;
						for (Window w : frame.getOwnedWindows()) {
							if (w instanceof FlexoDialog || w instanceof FIBDialog) {
								steps++;
							}
						}
					}
				}
			}
			return steps;
		}

		@Override
		public void run() {
			ReportProgress progressAdapter = new ReportProgress();
			String buildid = "build.id = " + ApplicationVersion.BUILD_ID + "\n";
			String commitID = "commit.id = " + ApplicationVersion.COMMIT_ID + "\n";
			if (sendSystemProperties) {
				issue.setSystemProperties(buildid + commitID + ToolBox.getSystemProperties(true));
			} else {
				issue.setSystemProperties(buildid + commitID);
			}

			if (getIssue().getIssuetype().getVersionField() != null) {
				List<JIRAVersion> allowedValues = getIssue().getIssuetype().getVersionField().getAllowedValues();
				FlexoVersion flexoVersion = new FlexoVersion(ApplicationVersion.BUSINESS_APPLICATION_VERSION);
				FlexoVersion simpleVersion = new FlexoVersion(flexoVersion.major, flexoVersion.minor, flexoVersion.patch, -1, false, false);
				JIRAVersion selected = null;
				for (JIRAVersion version : allowedValues) {
					if (flexoVersion.equals(version.getName())) {
						selected = version;
						break;
					}
				}
				if (selected == null) {
					for (JIRAVersion version : allowedValues) {
						if (simpleVersion.equals(version.getName())) {
							selected = version;
							break;
						}
					}
				}
				getIssue().setVersion(selected);
			} else {
				getIssue().setVersion(null);
			}
			// Always call make valid before replacing by identity members
			getIssue().makeValid();
			getIssue().<JIRAObject> replaceMembersByIdentityMembers();
			try {
				ProgressWindow.instance().setProgress(FlexoLocalization.localizedForKey("creating_issue"));
				progressAdapter.resetCount();
				JIRAResult submit = client.submit(new SubmitIssue(getIssue()), Method.POST, progressAdapter);
				if (submit.getErrorMessages() != null && submit.getErrorMessages().size() > 0) {
					for (String error : submit.getErrorMessages()) {
						report.addToErrors(error);
					}
				}
				if (submit.getKey() != null) {
					JIRAIssue result = new JIRAIssue();
					result.setKey(submit.getKey());
					report.setIssueLink(AdvancedPrefs.getBugReportUrl() + "/browse/" + submit.getKey());
					if (sendLogs) {
						ProgressWindow.instance().setProgress(FlexoLocalization.localizedForKey("sending_logs"));
						progressAdapter.resetCount();
						try {
							client.attachFilesToIssue(result, progressAdapter, Flexo.getErrLogFile());
						} catch (IOException e) {
							report.addToErrors(FlexoLocalization.localizedForKey("could_not_attach_file") + " "
									+ Flexo.getErrLogFile().getAbsolutePath() + "\n\t" + e.getMessage());
						}
					}
					if (attachFile != null) {
						ProgressWindow.instance().setProgress(
								FlexoLocalization.localizedForKey("sending_file") + " " + attachFile.getAbsolutePath());
						progressAdapter.resetCount();
						try {
							client.attachFilesToIssue(result, progressAdapter, attachFile);
						} catch (IOException e) {
							report.addToErrors(FlexoLocalization.localizedForKey("could_not_attach_file") + " "
									+ attachFile.getAbsolutePath() + "\n\t" + e.getMessage());
						}
					}
					if (sendProject) {
						if (flexoProject != null) {
							File projectDirectory = flexoProject.getProjectDirectory();
							String directoryName = projectDirectory.getName();
							File zipFile = new File(System.getProperty("java.io.tmpdir"), directoryName.substring(0,
									directoryName.length() - 4) + ".zip");
							FileFilter filter = new FileFilter() {

								@Override
								public boolean accept(File pathname) {
									return !pathname.getName().endsWith("~");
								}
							};
							ProgressWindow.instance().setProgress(FlexoLocalization.localizedForKey("compressing_project"));
							ProgressWindow.instance().resetSecondaryProgress(
									FileUtils.countFilesInDirectory(projectDirectory, true, filter));
							try {
								ZipUtils.makeZip(zipFile, projectDirectory, new IProgress() {

									@Override
									public void setSecondaryProgress(String stepName) {
										ProgressWindow.instance().setSecondaryProgress(stepName);
									}

									@Override
									public void setProgress(String stepName) {

									}

									@Override
									public void resetSecondaryProgress(int steps) {
									}

									@Override
									public void hideWindow() {

									}
								}, filter, Deflater.BEST_COMPRESSION);
								try {
									ProgressWindow.instance().setProgress(FlexoLocalization.localizedForKey("sending_project"));
									progressAdapter.resetCount();
									client.attachFilesToIssue(result, progressAdapter, zipFile);
								} catch (IOException e) {
									report.addToErrors(FlexoLocalization.localizedForKey("could_not_attach_project") + " " + e.getMessage());
								}
							} catch (IOException e) {
								report.addToErrors(FlexoLocalization.localizedForKey("could_not_zip_project") + " " + e.getMessage());
							}
						}
					}
					if (sendScreenshots) {
						for (int i = 0; i < Frame.getFrames().length; i++) {
							Frame frame = Frame.getFrames()[i];
							if (frame instanceof FlexoFrame) {
								ProgressWindow.instance().setProgress(
										FlexoLocalization.localizedForKey("sending_screenshot") + " " + frame.getTitle());
								progressAdapter.resetCount();
								attachScreenshotToIssue(client, result, frame, frame.getTitle(), progressAdapter, report);
								for (Window w : frame.getOwnedWindows()) {
									if (w instanceof FlexoDialog || w instanceof FIBDialog) {
										ProgressWindow.instance().setProgress(
												FlexoLocalization.localizedForKey("sending_screenshot") + " " + ((Dialog) w).getTitle());
										progressAdapter.resetCount();
										attachScreenshotToIssue(client, result, w, ((Dialog) w).getTitle(), progressAdapter, report);
									}
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				this.exception = e;
			} catch (JIRAException e) {
				e.printStackTrace();
				this.exception = e;
			} finally {
				getIssue().<JIRAObject> replaceMembersByOriginalMembers();
			}

		}

		public Exception getException() {
			return exception;
		}
	}

	private void attachScreenshotToIssue(JIRAClient client, JIRAIssue result, Window window, String title, Progress progress,
			SubmitIssueReport report) {
		if (window.isVisible() && window.getSize().getWidth() > 0 && window.getSize().getHeight() > 0) {
			try {
				File file = new File(System.getProperty("java.io.tmpdir"), FileUtils.getValidFileName(title + ".png"));
				ImageUtils.saveImageToFile(ImageUtils.createImageFromComponent(window), file, ImageType.PNG);
				client.attachFilesToIssue(result, progress, file);
			} catch (Exception e) {
				report.addToErrors(FlexoLocalization.localizedForKey("could_not_attach_screenshot") + " " + title + "\n\t" + e.getMessage());
				logger.log(Level.SEVERE, "Error when trying to send screenshot: " + title, e);
			}
		}
	}

	public List<JIRAComponent> getAvailableModules() {
		if (getIssue().getIssuetype() == null) {
			return EMPTY_LIST;
		}
		List<JIRAComponent> availableModules = new ArrayList<JIRAComponent>();
		for (Module module : Modules.getInstance().getAvailableModules()) {
			for (JIRAComponent component : getIssue().getIssuetype().getComponentField().getAllowedValues()) {
				if (module.getJiraComponentID().equals(component.getId())) {
					availableModules.add(component);
					break;
				}
			}
		}
		return availableModules;
	}

	public boolean isSendLogs() {
		return sendLogs;
	}

	public void setSendLogs(boolean sendLogs) {
		this.sendLogs = sendLogs;
	}

	public boolean isSendScreenshots() {
		return sendScreenshots;
	}

	public void setSendScreenshots(boolean sendScreenshots) {
		this.sendScreenshots = sendScreenshots;
	}

	public boolean isSendSystemProperties() {
		return sendSystemProperties;
	}

	public void setSendSystemProperties(boolean sendSystemProperties) {
		this.sendSystemProperties = sendSystemProperties;
	}

	public File getAttachFile() {
		return attachFile;
	}

	public void setAttachFile(File attachFile) {
		this.attachFile = attachFile;
	}

	public boolean isSendProject() {
		return sendProject;
	}

	public void setSendProject(boolean sendProject) {
		this.sendProject = sendProject;
	}

	public boolean isValid() {
		return getIssue() != null && getIssue().getIssuetype() != null && StringUtils.isNotEmpty(getIssue().getSummary())
				&& StringUtils.isNotEmpty(getIssue().getDescription());
	}
}
