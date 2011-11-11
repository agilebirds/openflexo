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

/**
 * The request for a command. Always a response is expected.
 * 
 * @author Thomas Singer
 */
public class CommandRequest extends Request {

	public static final CommandRequest ADD = new CommandRequest("add\n"); // NOI18N
	public static final CommandRequest ANNOTATE = new CommandRequest("annotate\n"); // NOI18N
	public static final CommandRequest CHECKOUT = new CommandRequest("co\n"); // NOI18N
	public static final CommandRequest COMMIT = new CommandRequest("ci\n"); // NOI18N
	public static final CommandRequest DIFF = new CommandRequest("diff\n"); // NOI18N
	public static final CommandRequest EDITORS = new CommandRequest("editors\n"); // NOI18N
	public static final CommandRequest EXPORT = new CommandRequest("export\n"); // NOI18N
	public static final CommandRequest HISTORY = new CommandRequest("history\n"); // NOI18N
	public static final CommandRequest IMPORT = new CommandRequest("import\n"); // NOI18N
	public static final CommandRequest LOG = new CommandRequest("log\n"); // NOI18N
	public static final CommandRequest NOOP = new CommandRequest("noop\n"); // NOI18N
	public static final CommandRequest RANNOTATE = new CommandRequest("rannotate\n"); // NOI18N
	public static final CommandRequest REMOVE = new CommandRequest("remove\n"); // NOI18N
	public static final CommandRequest RLOG = new CommandRequest("rlog\n"); // NOI18N
	public static final CommandRequest RTAG = new CommandRequest("rtag\n"); // NOI18N
	public static final CommandRequest STATUS = new CommandRequest("status\n"); // NOI18N
	public static final CommandRequest TAG = new CommandRequest("tag\n"); // NOI18N
	public static final CommandRequest UPDATE = new CommandRequest("update\n"); // NOI18N
	public static final CommandRequest WATCH_ADD = new CommandRequest("watch-add\n"); // NOI18N
	public static final CommandRequest WATCH_ON = new CommandRequest("watch-on\n"); // NOI18N
	public static final CommandRequest WATCH_OFF = new CommandRequest("watch-off\n"); // NOI18N
	public static final CommandRequest WATCH_REMOVE = new CommandRequest("watch-remove\n"); // NOI18N
	public static final CommandRequest WATCHERS = new CommandRequest("watchers\n"); // NOI18N

	private final String request;

	private CommandRequest(String request) {
		this.request = request;
	}

	/**
	 * Get the request String that will be passed to the server.
	 */
	@Override
	public String getRequestString() {
		return request;
	}

	/**
	 * Returns true if a response from the server is expected.
	 */
	@Override
	public boolean isResponseExpected() {
		return true;
	}
}
