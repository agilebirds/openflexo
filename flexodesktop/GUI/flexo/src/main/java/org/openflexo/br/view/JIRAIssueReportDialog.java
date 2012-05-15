package org.openflexo.br.view;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

import org.openflexo.AdvancedPrefs;
import org.openflexo.ApplicationVersion;
import org.openflexo.Flexo;
import org.openflexo.FlexoCst;
import org.openflexo.br.BugReportManager;
import org.openflexo.components.ProgressWindow;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.toolbox.ZipUtils;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.ws.jira.JIRAClient;
import org.openflexo.ws.jira.JIRAClient.Method;
import org.openflexo.ws.jira.JIRAClient.Progress;
import org.openflexo.ws.jira.UnauthorizedJIRAAccessException;
import org.openflexo.ws.jira.action.SubmitIssue;
import org.openflexo.ws.jira.model.JIRAComponent;
import org.openflexo.ws.jira.model.JIRAIssue;
import org.openflexo.ws.jira.model.JIRAObject;
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
	private final Exception e;

	private File attachFile;

	private boolean membersHaveBeenReplaced;

	public static void newBugReport(Module module) {
		newBugReport(null, module);
	}

	public static void newBugReport(Exception e, Module module) {
		try {
			JIRAIssueReportDialog report = new JIRAIssueReportDialog(e);
			FIBDialog<JIRAIssueReportDialog> dialog = FIBDialog.instanciateAndShowDialog(FIB_FILE, report, FlexoFrame.getActiveFrame(), true,
					FlexoLocalization.getMainLocalizer());
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
					} catch (IOException e1) {
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

	public JIRAIssueReportDialog() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		this(null);
	}

	public JIRAIssueReportDialog(Exception e) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		this.e = e;
		this.project = BugReportManager.getInstance().getOpenFlexoProject();
		issue = new JIRAIssue();
		issue.setIssuetype(project.getIssuetypes().get(0));
		issue.setProject(project);
		sendSystemProperties = true;
		sendScreenshots = false;
		sendLogs = e != null;
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

	public boolean send() throws IOException {
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
			for (int i = 0; i < FlexoFrame.getFrames().length; i++) {
				Frame frame = FlexoFrame.getFrames()[i];
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
		SubmitIssueReport report = new SubmitIssueReport();
		ProgressWindow.instance().showProgressWindow(FlexoLocalization.localizedForKey("submitting_bug_report"), steps);
		try {
			ReportProgress progressAdapter = new ReportProgress();
			JIRAClient client = new JIRAClient(AdvancedPrefs.getBugReportUrl(), AdvancedPrefs.getBugReportUser(),
					AdvancedPrefs.getBugReportPassword());
			String buildid = "build.id = " + FlexoCst.BUILD_ID + "\n";
			if (sendSystemProperties) {
				issue.setSystemProperties(buildid + ToolBox.getSystemProperties(true));
			} else {
				issue.setSystemProperties(buildid);
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
						FlexoProject flexoProject = ModuleLoader.instance().getProject();
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
						for (int i = 0; i < FlexoFrame.getFrames().length; i++) {
							Frame frame = FlexoFrame.getFrames()[i];
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
			} finally {
				getIssue().<JIRAObject> replaceMembersByOriginalMembers();
			}

		} finally {
			ProgressWindow.hideProgressWindow();
		}
		FIBDialog.instanciateAndShowDialog(REPORT_FIB_FILE, report, FlexoFrame.getActiveFrame(), true, FlexoLocalization.getMainLocalizer());
		return !report.hasErrors();
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
		for (Module module : ModuleLoader.instance().availableModules()) {
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
}
