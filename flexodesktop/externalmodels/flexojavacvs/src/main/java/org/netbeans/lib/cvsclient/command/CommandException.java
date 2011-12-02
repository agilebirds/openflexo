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
package org.netbeans.lib.cvsclient.command;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.netbeans.lib.cvsclient.util.BundleUtilities;

/**
 * This exception is thrown when an error occurs while executing a command. It is nearly always a container for another exception.
 * 
 * @author Robert Greig
 */
public class CommandException extends Exception {
	private Exception underlyingException;
	private String localizedMessage;
	private String message;

	public CommandException(Exception underlyingException, String localizedMessage) {
		this.underlyingException = underlyingException;
		this.localizedMessage = localizedMessage;
	}

	public CommandException(String message, String localizedMessage) {
		super(message);
		this.message = message;
		this.localizedMessage = localizedMessage;
	}

	public Exception getUnderlyingException() {
		return underlyingException;
	}

	@Override
	public void printStackTrace() {
		if (underlyingException != null) {
			underlyingException.printStackTrace();
		} else {
			super.printStackTrace();
		}
	}

	@Override
	public void printStackTrace(PrintStream stream) {
		if (underlyingException != null) {
			underlyingException.printStackTrace(stream);
		} else {
			super.printStackTrace(stream);
		}
	}

	@Override
	public void printStackTrace(PrintWriter writer) {
		if (underlyingException != null) {
			underlyingException.printStackTrace(writer);
		} else {
			super.printStackTrace(writer);
		}
	}

	@Override
	public String getLocalizedMessage() {
		if (localizedMessage == null) {
			return message;
		}
		return localizedMessage;
	}

	@Override
	public String getMessage() {
		if (message == null) {
			return localizedMessage;
		}
		return message;
	}

	protected static String getBundleString(String key) {
		String value = null;
		try {
			ResourceBundle bundle = BundleUtilities.getResourceBundle(CommandException.class, "Bundle"); // NOI18N
			if (bundle != null) {
				value = bundle.getString(key);
			}
		} catch (MissingResourceException exc) {
		}
		return value;
	}

	public static String getLocalMessage(String key) {
		return getLocalMessage(key, null);
	}

	public static String getLocalMessage(String key, Object[] arguments) {
		String locMessage = CommandException.getBundleString(key);
		if (locMessage == null) {
			return null;
		}
		if (arguments != null) {
			locMessage = MessageFormat.format(locMessage, arguments);
		}
		return locMessage;
	}
}
