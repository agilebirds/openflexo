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
package org.netbeans.lib.cvsclient.request;

import java.io.File;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Notify Entry.java E Sun Nov 11 10:25:40 2001 GMT worker E:\test\admin EUC
 * 
 * @author Thomas Singer
 * @version Nov 14, 2001
 */
public class NotifyRequest extends Request {
	// Constants ==============================================================

	private static final DateFormat DATE_FORMAT;
	private static final String HOST_NAME;

	static {
		DATE_FORMAT = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy z", Locale.US);

		// detect host name
		String hostName = "";
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		HOST_NAME = hostName;
	}

	// Fields =================================================================

	private final String request;

	// Setup ==================================================================

	/**
	 * Creates an NotifyRequest for the specified file. If the specified file is null, a IllegalArgumentException is thrown.
	 */
	public NotifyRequest(File file, String command, String parameters) {
		if (file == null) {
			throw new IllegalArgumentException("File must not be null!");
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append("Notify "); // NOI18N
		buffer.append(file.getName());
		buffer.append('\n');
		buffer.append(command);
		buffer.append('\t');
		buffer.append(DATE_FORMAT.format(new Date()));
		buffer.append('\t');
		buffer.append(HOST_NAME);
		buffer.append('\t');
		buffer.append(file.getParent());
		buffer.append('\t');
		buffer.append(parameters);
		buffer.append('\n');
		this.request = buffer.toString();
	}

	// Implemented ============================================================

	@Override
	public String getRequestString() {
		return request;
	}

	@Override
	public boolean isResponseExpected() {
		return false;
	}
}
