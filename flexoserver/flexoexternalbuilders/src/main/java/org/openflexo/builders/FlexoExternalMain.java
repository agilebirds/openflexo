package org.openflexo.builders;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.FlexoProperties;
import org.openflexo.GeneralPreferences;
import org.openflexo.builders.exception.MissingArgumentException;
import org.openflexo.builders.utils.FlexoBuilderListener;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.logging.FlexoLoggingManager.LoggingManagerDelegate;
import org.openflexo.module.Module;
import org.openflexo.module.UserType;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ResourceLocator;
import org.openflexo.toolbox.ToolBox;

public abstract class FlexoExternalMain implements Runnable {

	public static final String CONSOLE_OUTPUT_ENCODING = "UTF-8";

	private static final Logger logger = FlexoLogger.getLogger(FlexoExternalMain.class.getPackage().getName());

	public static final String DEV_ARGUMENT = "Dev";

	public static final String RESOURCE_PATH_ARGUMENT_PREFIX = "-ResourcePath=";

	private static final int MISSING_ARGUMENT = -2;

	public static final int FLEXO_ACTION_FAILED = -3;

	private static final int CLEANUP_EXCEPTION = -4;

	public static final int LOCAL_IO_EXCEPTION = -5;

	public static final int PROJECT_NOT_FOUND = -6;

	public static final int CORRUPTED_PROJECT_EXCEPTION = -7;

	public static final int CLASS_NOT_FOUND = -8;

	public static final int UNEXPECTED_EXCEPTION = -100;

	public static final int UNKNOWN_EXIT = -120;

	public static final int TIMEOUT = -126;

	private volatile int exitCode = 0;

	protected boolean isDev = false;

	private PrintStream consoleStream;

	protected static byte[] mem = new byte[500 * 1024]; // Let's grab 500kb to free in case of an error (especially OutOfMemoryError)

	private boolean notifyOnExit;

	private boolean done;

	private FlexoResourceCenter resourceCenter;

	public FlexoExternalMain() {
		try {
			consoleStream = new PrintStream(System.err, true, CONSOLE_OUTPUT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			consoleStream = new PrintStream(System.err, true);
		}
	}

	protected void init(String[] args) throws MissingArgumentException {
		String resourcePath = null;
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith(RESOURCE_PATH_ARGUMENT_PREFIX)) {
					resourcePath = args[i].substring(RESOURCE_PATH_ARGUMENT_PREFIX.length());
					if (resourcePath.startsWith("\"")) {
						resourcePath = resourcePath.substring(1);
					}
					if (resourcePath.endsWith("\"")) {
						resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
					}
				} else if (args[i].equals(DEV_ARGUMENT)) {
					isDev = true;
				}
			}
		}
		if (resourcePath == null && !isDev) {
			throw new MissingArgumentException(RESOURCE_PATH_ARGUMENT_PREFIX);
		}
		ToolBox.setPlatform();
		if (!isDev) {
			ResourceLocator.resetFlexoResourceLocation(new File(resourcePath));
		}
		UserType.setCurrentUserType(UserType.DEVELOPER);
		FlexoProperties.load();
		initializeLoggingManager();
		if (!isDev) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("PreferredResourcePath is set to " + ResourceLocator.getPreferredResourcePath().getAbsolutePath());
			}
		}
		FlexoObject.initialize(false);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Launching " + getName() + "...");
		}
		GeneralPreferences.setFavoriteModuleName(Module.WKF_MODULE.getName());
	}

	@Override
	public final void run() {
		try {
			doRun();
		} catch (Throwable t) {
			mem = null;
			System.gc();
			t.printStackTrace();
			setExitCodeCleanUpAndExit(UNEXPECTED_EXCEPTION);
		}
	}

	protected abstract void doRun();

	protected abstract String getName();

	protected void cleanUp() {

	}

	public static FlexoLoggingManager initializeLoggingManager() {
		try {
			FlexoProperties properties = FlexoProperties.load();
			return FlexoLoggingManager.initialize(properties.getMaxLogCount(), properties.getIsLoggingTrace(),
					properties.getCustomLoggingFile(), properties.getDefaultLoggingLevel(), new LoggingManagerDelegate() {
						@Override
						public void setMaxLogCount(Integer maxLogCount) {
							FlexoProperties.instance().setMaxLogCount(maxLogCount);
						}

						@Override
						public void setKeepLogTrace(boolean logTrace) {
							FlexoProperties.instance().setIsLoggingTrace(logTrace);
						}

						@Override
						public void setDefaultLoggingLevel(Level lev) {
							String fileName = "SEVERE";
							if (lev == Level.SEVERE) {
								fileName = "SEVERE";
							}
							if (lev == Level.WARNING) {
								fileName = "WARNING";
							}
							if (lev == Level.INFO) {
								fileName = "INFO";
							}
							if (lev == Level.FINE) {
								fileName = "FINE";
							}
							if (lev == Level.FINER) {
								fileName = "FINER";
							}
							if (lev == Level.FINEST) {
								fileName = "FINEST";
							}
							reloadLoggingFile(new FileResource("Config/logging_" + fileName + ".properties").getAbsolutePath());
							FlexoProperties.instance().setLoggingFileName(null);
						}

						@Override
						public void setConfigurationFileName(String configurationFile) {
							reloadLoggingFile(configurationFile);
							FlexoProperties.instance().setLoggingFileName(configurationFile);
						}
					});
		} catch (SecurityException e) {
			logger.severe("cannot read logging configuration file : " + System.getProperty("java.util.logging.config.file")
					+ "\nIt seems the file has read access protection.");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			logger.severe("cannot read logging configuration file : " + System.getProperty("java.util.logging.config.file"));
			e.printStackTrace();
			return null;
		}
	}

	private static boolean reloadLoggingFile(String filePath) {
		logger.info("reloadLoggingFile with " + filePath);
		System.setProperty("java.util.logging.config.file", filePath);
		try {
			LogManager.getLogManager().readConfiguration();
		} catch (SecurityException e) {
			logger.warning("The specified logging configuration file can't be read (not enough privileges).");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			logger.warning("The specified logging configuration file cannot be read.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected void handleActionFailed(FlexoAction<?, ? extends FlexoModelObject, ? extends FlexoModelObject> action, File fileToOpen) {
		if (fileToOpen != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Action " + action.getLocalizedName() + " could not be performed on project at "
						+ fileToOpen.getAbsolutePath());
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Action " + action.getLocalizedName() + " could not be performed");
			}
		}
		reportMessage("Action " + action.getLocalizedName() + " could not be performed");
		if (action.getThrownException() != null) {
			action.getThrownException().printStackTrace();
		}
		setExitCodeCleanUpAndExit(getExitCode() == 0 ? FLEXO_ACTION_FAILED : getExitCode());
	}

	public FlexoResourceCenter getResourceCenter() {
		return resourceCenter;
	}

	public void setResourceCenter(FlexoResourceCenter resourceCenter) {
		this.resourceCenter = resourceCenter;
	}

	protected void handleActionFailed(FlexoAction<?, ? extends FlexoModelObject, ? extends FlexoModelObject> action) {
		handleActionFailed(action, null);
	}

	void build(String[] args) {
		try {
			init(args);
		} catch (MissingArgumentException e) {
			e.printStackTrace();
			cleanUp();
			if (getExitCode() == 0) {
				System.exit(MISSING_ARGUMENT);
			} else {
				System.exit(getExitCode());
			}
		}
		try {
			SwingUtilities.invokeAndWait(this);
			// Don't perform any code after this
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		dumpThreadStackAndExit();
	}

	protected static void dumpThreadStackAndExit() {
		try {
			int size = Thread.activeCount();
			Thread[] t;
			int count;
			do {
				t = new Thread[size];
				count = Thread.enumerate(t);
				size += Thread.activeCount();
			} while (t.length < count);
			for (Thread thread : t) {
				StackTraceElement[] stackTrace = thread.getStackTrace();
				System.err.println(thread.toString());
				for (StackTraceElement e : stackTrace) {
					System.err.println("\tat " + e.getClassName() + "." + e.getMethodName() + "(" + e.getFileName() + ":"
							+ (e.getLineNumber() > 0 ? e.getLineNumber() : "Unknown") + ")");
				}
			}
		} finally {
			System.err.println("Time out reached! Exiting now...");
			System.exit(TIMEOUT);
		}
	}

	public static <A extends FlexoExternalMain> void launch(Class<A> builderClass, String[] args) {
		// The next line is very important for performance purposes. External mains can always be run with the smallest priority because
		// they are not immediate
		final Thread currentThread = Thread.currentThread();
		currentThread.setPriority(Thread.MIN_PRIORITY);
		Thread timeout = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(50 * 60 * 1000);
					// Time-out reached!
					dumpThreadStackAndExit();
				} catch (InterruptedException e) {
					return;// Normal
				}
			}

		}, "Timeout thread");
		timeout.start();
		try {
			A main = builderClass.newInstance();
			try {
				main.build(args);
			} catch (Exception e) {
				mem = null;
				e.printStackTrace();
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("An unexpected exception occured in " + main.getName() + ". Returning now.");
				}
				try {
					main.cleanUp();
				} catch (Exception e1) {
					e1.printStackTrace();
					System.exit(CLEANUP_EXCEPTION);
				}
				System.exit(-1);
			} catch (Throwable e) {
				mem = null;
				e.printStackTrace();
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("An unexpected ERROR occured in " + main.getName() + ". Returning now.");
				}
				try {
					main.cleanUp();
				} catch (Exception e1) {
					e1.printStackTrace();
					System.exit(CLEANUP_EXCEPTION);
				}
				System.exit(-100);
			}
		} catch (InstantiationException e) {
			e.printStackTrace(); // Should not happen!
			System.exit(-125);
		} catch (IllegalAccessException e) {
			e.printStackTrace(); // Should not happen!
			System.exit(-127);
		} finally {
			timeout.interrupt();
		}
	}

	/**
	 * Search recursively and return a project directory in the <code>root</code> directory. The strategy is the following, it first looks
	 * for a file ending with .rmxml in the current directory, if non can be found, it searches within the directory of the current level
	 * and so on.
	 * 
	 * @param root
	 *            - the root directory in which to search.
	 * @return the project directory if a project can be found within the <code>root</code> directory. If several projects are located in
	 *         that directory, the returned value is uncertain (depending on how the method listFiles() returns the sub-directories of
	 *         <code>root</code>).
	 */
	public static File searchProjectDirectory(File root) {
		if (root == null || !root.exists() || !root.isDirectory()) {
			return null;
		}
		Vector<File> founds = new Vector<File>();
		File[] f = root.listFiles();
		for (int i = 0; i < f.length; i++) {
			File file = f[i];
			if (file.isFile() && file.getName().toLowerCase().endsWith(".rmxml")) {
				founds.add(file.getParentFile());
			}
		}
		for (int i = 0; i < f.length; i++) {
			File file = f[i];
			if (file.isDirectory()) {
				File found = searchProjectDirectory(file);
				if (found != null) {
					founds.add(found);
				}
			}
		}
		if (founds.size() == 0) {
			return null;
		} else if (founds.size() == 1) {
			return founds.firstElement();
		} else {
			File ret = founds.firstElement();
			for (File file : founds) {
				if (file.getAbsolutePath().length() < ret.getAbsolutePath().length()) {
					ret = file;
				}
			}
			return ret;
		}
	}

	public void reportMessage(String message) {
		writeToConsole(FlexoBuilderListener.REPORT_START_TAG);
		writeToConsole(message);
		writeToConsole(FlexoBuilderListener.REPORT_END_TAG);
	}

	public void writeToConsole(String s) {
		consoleStream.println(s);
	}

	public int getExitCode() {
		return exitCode;
	}

	private void setExitCode(int exitCode) {
		this.exitCode = exitCode;
		this.done = true;
	}

	public boolean isDone() {
		return done;
	}

	public synchronized void setExitCodeCleanUpAndExit(int exitCode) {
		setExitCode(exitCode);
		try {
			cleanUp();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (isDev) {
			if (notifyOnExit) {
				synchronized (this) {
					notifyAll();
				}
			}

			return;
		}
		System.exit(getExitCode());
	}

	public boolean notifyOnExit() {
		return notifyOnExit;
	}

	public void setNotifyOnExit(boolean notifyOnExit) {
		this.notifyOnExit = notifyOnExit;
	}
}
