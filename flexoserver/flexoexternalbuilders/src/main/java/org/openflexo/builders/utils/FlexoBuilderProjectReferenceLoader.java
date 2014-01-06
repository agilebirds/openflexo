package org.openflexo.builders.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipException;

import org.apache.commons.io.IOUtils;
import org.openflexo.builders.FlexoExternalMain;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoServiceImpl;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.module.ProjectLoader;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ZipUtils;

public class FlexoBuilderProjectReferenceLoader extends FlexoServiceImpl implements FlexoProjectReferenceLoader {

	private String serverURL;
	private String login;
	private String password;
	private final FlexoExternalMain externalMain;
	private final ProjectLoader projectLoader;

	public FlexoBuilderProjectReferenceLoader(FlexoExternalMain externalMain, ProjectLoader projectLoader, String serverURL, String login,
			String password) {
		super();
		this.externalMain = externalMain;
		this.projectLoader = projectLoader;
		this.serverURL = serverURL;
		this.login = login;
		this.password = password;
	}

	@Override
	public void initialize() {
	}

	@Override
	public FlexoProject loadProject(FlexoProjectReference reference, boolean silentlyOnly) {
		FlexoEditor editor = projectLoader.editorForProjectURIAndRevision(reference.getURI(), reference.getRevision());
		if (editor != null) {
			return editor.getProject();
		}
		if (silentlyOnly) {
			return null;
		}
		if (serverURL != null && login != null && password != null) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("login", login);
			param.put("password", password);
			param.put("uri", reference.getURI());
			param.put("revision", String.valueOf(reference.getRevision()));
			param.put("greaterOrEqual", "true");
			StringBuilder paramsAsString = new StringBuilder();
			if (param != null && param.size() > 0) {
				boolean first = true;
				for (Entry<String, String> e : param.entrySet()) {
					paramsAsString.append(first ? "" : "&");
					try {
						paramsAsString.append(URLEncoder.encode(e.getKey(), "UTF-8")).append("=")
								.append(URLEncoder.encode(e.getValue(), "UTF-8"));
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					first = false;
				}

			}
			// Create a URL for the desired page
			URL url;
			try {
				url = new URL(serverURL);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				externalMain.reportMessage("Unable to load " + reference.getName() + " " + reference.getVersion()
						+ " because server URL seems misconfigured: '" + serverURL + "'" + ".\n(" + e.getMessage() + ")");
				return null;
			}
			HttpURLConnection conn;
			try {
				conn = (HttpURLConnection) url.openConnection();
			} catch (IOException e) {
				e.printStackTrace();
				externalMain.reportMessage("Unable to load " + reference.getName() + " " + reference.getVersion()
						+ " because worker cannot access server URL '" + url.toString() + "'" + ".\n(" + e.getMessage() + ")");
				return null;
			}
			try {
				conn.setRequestMethod("POST");
			} catch (ProtocolException e) {
				e.printStackTrace();
				// Should never happen
			}
			conn.setDoOutput(true);
			OutputStreamWriter wr;
			try {
				wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(paramsAsString.toString());
				wr.flush();
			} catch (IOException e) {
				e.printStackTrace();
				externalMain.reportMessage("Unable to load " + reference.getName() + " " + reference.getVersion()
						+ " because worker cannot open server URL '" + url.toString() + "'" + ".\n(" + e.getMessage() + ")");
				return null;
			}
			// Read all the text returned by the server
			int httpStatus;
			try {
				httpStatus = conn.getResponseCode();
			} catch (IOException e) {
				e.printStackTrace();
				externalMain.reportMessage("Unable to load " + reference.getName() + " " + reference.getVersion()
						+ " because worker could not read HTTP response code for server URL '" + url.toString() + "'.\n(" + e.getMessage()
						+ ")");
				return null;
			}
			InputStream inputStream;
			if (httpStatus < 400) {
				try {
					inputStream = conn.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
					externalMain.reportMessage("Unable to load " + reference.getName() + " " + reference.getVersion()
							+ " because worker could not read data for server URL '" + url.toString() + "'.\n(" + e.getMessage() + ")"
							+ "\nStatus: " + httpStatus);
					return null;
				}
				String base = "Projects/" + reference.getName() + "_" + reference.getVersion();
				File file = new File(externalMain.getWorkingDir(), base + ".zip");
				int i = 0;
				while (file.exists()) {
					file = new File(externalMain.getWorkingDir(), base + "-" + i++ + ".zip");
				}
				file.getParentFile().mkdirs();
				try {
					FileUtils.saveToFile(file, inputStream);
				} catch (IOException e) {
					e.printStackTrace();
				}
				base = "ExtractedProjects/" + reference.getName() + "_" + reference.getVersion();
				File extractionDirectory = new File(externalMain.getWorkingDir(), base);
				i = 0;
				while (extractionDirectory.exists()) {
					extractionDirectory = new File(externalMain.getWorkingDir(), base + "-" + i++);
				}
				extractionDirectory.mkdirs();
				try {
					ZipUtils.unzip(file, extractionDirectory);
				} catch (ZipException e) {
					e.printStackTrace();
					externalMain.reportMessage("Unable to load " + reference.getName() + " " + reference.getVersion()
							+ " because worker could not read the zip file returned by server URL '" + url.toString() + "'.\n("
							+ e.getMessage() + ")");
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					externalMain.reportMessage("Unable to load " + reference.getName() + " " + reference.getVersion()
							+ " because worker could not extract the zip file returned by server URL '" + url.toString()
							+ "' to the directory " + extractionDirectory.getAbsolutePath() + ".\n(" + e.getMessage() + ")");
					return null;
				}
				File projectDirectory = FlexoExternalMain.searchProjectDirectory(extractionDirectory);
				if (projectDirectory == null) {
					externalMain.reportMessage("Unable to load " + reference.getName() + " " + reference.getVersion()
							+ " because worker could not find a project in the directory " + extractionDirectory.getAbsolutePath() + ".");
					return null;
				}
				try {
					editor = projectLoader.loadProject(projectDirectory, true);
				} catch (ProjectLoadingCancelledException e) {
					e.printStackTrace();
					externalMain.reportMessage("Unable to load " + reference.getName() + " " + reference.getVersion()
							+ " because project loading was cancelled for directory " + projectDirectory.getAbsolutePath() + ".\n("
							+ e.getMessage() + ")");
					return null;
				} catch (ProjectInitializerException e) {
					e.printStackTrace();
					externalMain.reportMessage("Unable to load " + reference.getName() + " " + reference.getVersion()
							+ " because project loading failed for directory " + projectDirectory.getAbsolutePath() + ".\n("
							+ e.getMessage() + ")");
					return null;

				}
				if (editor != null) {
					return editor.getProject();
				} else {
					externalMain.reportMessage("Unable to load project " + reference.getName() + " " + reference.getVersion()
							+ " located at " + projectDirectory.getAbsolutePath() + ".");
					return null;
				}
			} else {
				inputStream = conn.getErrorStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
				String str;
				StringBuilder reply = new StringBuilder();
				try {
					while ((str = in.readLine()) != null) {
						reply.append(str).append("\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(wr);
					IOUtils.closeQuietly(in);
				}
				if (httpStatus == 404) {
					externalMain.reportMessage("Unable to find " + reference.getName() + " " + reference.getVersion() + "(Revision: "
							+ reference.getRevision() + "). Make sure that you have uploaded that project with that revision");
					return null;
				} else {
					externalMain.reportMessage("Unable to load " + reference.getName() + " " + reference.getVersion()
							+ " because an error occured for server URL '" + url.toString() + "'" + ".\nStatus: " + httpStatus + "\n"
							+ reply.toString());
					return null;
				}
			}
		} else {
			externalMain.reportMessage("Unable to load " + reference.getName() + " " + reference.getVersion()
					+ " because information and credentials are incomplete (server URL=" + serverURL + " login="
					+ (login != null ? "OK" : "null") + " password=" + (password != null ? "OK" : "null"));
			return null;
		}
	}
}
