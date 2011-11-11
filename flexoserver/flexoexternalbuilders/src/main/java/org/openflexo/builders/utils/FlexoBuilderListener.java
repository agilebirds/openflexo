package org.openflexo.builders.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.builders.FlexoExternalMain;

import org.openflexo.logging.FlexoLogger;

public class FlexoBuilderListener implements Runnable {

	private static final Logger logger = FlexoLogger.getLogger(FlexoBuilderListener.class.getPackage().getName());

	public static final String MAIN_STEP_START_TAG = "<MAIN_STEP>";
	public static final String MAIN_STEP_END_TAG = "</MAIN_STEP>";
	public static final String SUB_STEP_START_TAG = "<SUB_STEP>";
	public static final String SUB_STEP_END_TAG = "</SUB_STEP>";
	public static final String MAIN_STEP_COUNT_START_TAG = "<MAIN_STEP_COUNT>";
	public static final String MAIN_STEP_COUNT_END_TAG = "</MAIN_STEP_COUNT>";
	public static final String SUB_STEP_COUNT_START_TAG = "<SUB_STEP_COUNT>";
	public static final String SUB_STEP_COUNT_END_TAG = "</SUB_STEP_COUNT>";

	public static final String REPORT_START_TAG = "<FLEXO_REPORT>";
	public static final String REPORT_END_TAG = "</FLEXO_REPORT>";
	public static final String TOCS_START_TAG = "<TOCS>";
	public static final String TOCS_END_TAG = "</TOCS>";
	public static final String CONFLICTING_RESSOURCES_START_TAG = "<CONFLICTING_RESOURCES>";
	public static final String CONFLICTING_RESSOURCES_END_TAG = "</CONFLICTING_RESOURCES>";

	private static final String STACKTRACE_LINE_REG_EXP = "\\s+at [A-Za-z0-9.$_]+\\([A-Za-z0-9.$_]+?\\.java:[0-9]+\\)";

	private InputStream input;

	private StringBuilder stacktraces;

	// private StringBuilder logs;

	private boolean logAsException = false;

	private Vector<String> messages;

	private Vector<String> conflictingResources;

	private String xmlTOCRepositoriesDescription;

	private int exitCode = -1;

	private File logFile = null;
	private boolean writeToConsole = true;

	private Writer writer;

	public FlexoBuilderListener(String prefix, InputStream is, boolean writeToConsole) {
		this.input = is;
		this.writeToConsole = writeToConsole;
		this.messages = new Vector<String>();
		stacktraces = new StringBuilder();
		try {
			logFile = File.createTempFile(prefix + "LogFile", ".log");
		} catch (IOException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot create temporary file for logs! " + e.getMessage());
			}
		}
	}

	public void run() {
		boolean isReportingMessage = false;
		boolean isReportingTOCRepositories = false;
		boolean isReportingConflictingResources = false;
		StringBuilder tmpMessage = null;
		StringBuilder xmlTocRepositories = null;
		conflictingResources = new Vector<String>();
		InputStreamReader is;
		try {
			is = new InputStreamReader(input, FlexoExternalMain.CONSOLE_OUTPUT_ENCODING);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			is = new InputStreamReader(input);
		}
		BufferedReader reader = new BufferedReader(is);
		writer = null;
		if (logFile != null) {
			try {
				writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(logFile, true)), "UTF-8");
			} catch (FileNotFoundException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("FileNotFound: Cannot open build log file " + logFile.getAbsolutePath() + ": " + e.getMessage());
				}
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("IOException: Cannot open build log file " + logFile.getAbsolutePath() + ": " + e.getMessage());
				}
			}
		}
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.equals(REPORT_START_TAG)) {
					tmpMessage = new StringBuilder();
					isReportingMessage = true;
				} else if (line.equals(REPORT_END_TAG)) {
					isReportingMessage = false;
					messages.add(tmpMessage.toString());
				} else if (line.equals(TOCS_START_TAG)) {
					xmlTocRepositories = new StringBuilder();
					isReportingTOCRepositories = true;
				} else if (line.equals(TOCS_END_TAG)) {
					isReportingTOCRepositories = false;
					xmlTOCRepositoriesDescription = xmlTocRepositories.toString();
				} else if (line.equals(CONFLICTING_RESSOURCES_START_TAG)) {
					isReportingConflictingResources = true;
				} else if (line.equals(CONFLICTING_RESSOURCES_END_TAG)) {
					isReportingConflictingResources = false;
				} else {
					if (isReportingMessage) {
						tmpMessage.append(line).append("\n");
					} else if (isReportingTOCRepositories) {
						xmlTocRepositories.append(line).append("\n");
					} else if (isReportingConflictingResources) {
						conflictingResources.add(line);
					} else if (line.indexOf("Exception:") > -1) {
						logAsException = true;
						stacktraces.append(line).append("\n");
					} else if (logAsException && line.matches(STACKTRACE_LINE_REG_EXP)) {
						stacktraces.append(line).append("\n");
					} else if (line.trim().length() == 0) {
						;
					} else {
						if (logAsException) {
							stacktraces.append("--------------- END OF STACKTRACE ---------------\n");
						}
						logAsException = false;
					}
					if (writeToConsole) {
						System.err.println(line);
					}
					appendLineToLog(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					if (logger.isLoggable(Level.SEVERE)) {
						logger.severe("Could not close writer! " + e.getMessage());
					}
				}
			}
		}
	}

	public void appendLineToLog(String line) throws IOException {
		if (writer != null) {
			writer.write(line);
			writer.write('\n');
		}
	}

	public boolean hasStacktraces() {
		return stacktraces.length() > 0;
	}

	public String getStacktraces() {
		if (logAsException) {
			stacktraces.append("--------------- END OF STACKTRACE ---------------\n");
		}
		logAsException = false;
		return hasStacktraces() ? stacktraces.toString() : null;
	}

	/*public String getLogs() {
		return logs.toString();
	}*/

	public static void main(String[] args) {
		String s = "\tat org.openflexo.server.FlexoDocGeneratorMain$Coucou.main(FlexoDocGeneratorMain.java:166)";
		System.out.println(s.matches(STACKTRACE_LINE_REG_EXP));
	}

	public Vector<String> getMessages() {
		return messages;
	}

	public String getXMLTOCRepositoriesDescription() {
		return xmlTOCRepositoriesDescription;
	}

	public boolean hasTOCRepositoriesDescription() {
		return xmlTOCRepositoriesDescription != null && xmlTOCRepositoriesDescription.length() > 0;
	}

	public Vector<String> getConflictingResources() {
		return conflictingResources;
	}

	public int getExitCode() {
		return exitCode;
	}

	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	public File getLogFile() {
		return logFile;
	}
}
