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
package org.netbeans.lib.cvsclient.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Handles the logging of communication to and from the server
 * 
 * @author Robert Greig
 * @author Petr Kuzel rewriten to streams
 */
public final class Logger {
	/**
	 * The output stream to use to write communication sent to the server
	 */
	private static OutputStream outLogStream;

	/**
	 * The output stream to use to write communication received from the server
	 */
	private static OutputStream inLogStream;

	/**
	 * The log files path. If the property is set to the constant "system" then it uses System.err, otherwise it tries to create a file at
	 * the specified path
	 */
	private static final String LOG_PROPERTY = "cvsClientLog"; // NOI18N

	/**
	 * Whether we are logging or not
	 */
	private static boolean logging;

	static {
		setLogging(System.getProperty(LOG_PROPERTY));
	}

	public static void setLogging(String logPath) {
		logging = logPath != null;

		try {
			if (logging) {
				if (logPath.equals("system")) { // NOI18N
					outLogStream = System.err;
					inLogStream = System.err;
				} else {
					outLogStream = new BufferedOutputStream(new FileOutputStream(logPath + ".out")); // NOI18N
					inLogStream = new BufferedOutputStream(new FileOutputStream(logPath + ".in")); // NOI18N
				}
			}
		} catch (IOException e) {
			System.err.println("Unable to create log files: " + e); // NOI18N
			System.err.println("Logging DISABLED"); // NOI18N
			logging = false;
			try {
				if (outLogStream != null) {
					outLogStream.close();
				}
			} catch (IOException ex2) {
				// ignore, if we get one here we really are screwed
			}

			try {
				if (inLogStream != null) {
					inLogStream.close();
				}
			} catch (IOException ex2) {
				// ignore, if we get one here we really are screwed
			}
		}
	}

	/**
	 * Log a message received from the server. The message is logged if logging is enabled
	 * 
	 * @param received
	 *            the data received from the server
	 */
	public static void logInput(byte[] received) {
		logInput(received, 0, received.length);
	}

	/**
	 * Log a message received from the server. The message is logged if logging is enabled
	 * 
	 * @param received
	 *            the data received from the server
	 */
	public static void logInput(byte[] received, int offset, int len) {
		if (!logging) {
			return;
		}

		try {
			inLogStream.write(received, offset, len);
			inLogStream.flush();
		} catch (IOException ex) {
			System.err.println("Could not write to log file: " + ex); // NOI18N
			System.err.println("Logging DISABLED."); // NOI18N
			logging = false;
		}
	}

	/**
	 * Log a character received from the server. The message is logged if logging is enabled
	 * 
	 * @param received
	 *            the data received from the server
	 */
	public static void logInput(char received) {
		if (!logging) {
			return;
		}

		try {
			inLogStream.write(received);
			inLogStream.flush();
		} catch (IOException ex) {
			System.err.println("Could not write to log file: " + ex); // NOI18N
			System.err.println("Logging DISABLED."); // NOI18N
			logging = false;
		}
	}

	/**
	 * Log a message sent to the server. The message is logged if logging is enabled
	 * 
	 * @param sent
	 *            the data sent to the server
	 */
	public static void logOutput(byte[] sent) {
		if (!logging) {
			return;
		}

		try {
			outLogStream.write(sent);
			outLogStream.flush();
		} catch (IOException ex) {
			System.err.println("Could not write to log file: " + ex); // NOI18N
			System.err.println("Logging DISABLED."); // NOI18N
			logging = false;
		}
	}
}
