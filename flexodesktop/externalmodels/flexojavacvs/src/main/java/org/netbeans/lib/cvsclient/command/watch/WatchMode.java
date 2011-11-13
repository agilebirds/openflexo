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
package org.netbeans.lib.cvsclient.command.watch;

import org.netbeans.lib.cvsclient.request.CommandRequest;

/**
 * @author Thomas Singer
 * @version Dec 29, 2001
 */
public class WatchMode {
	/**
	 * This is the WatchMode that enables watching.
	 */
	public static final WatchMode ON = new WatchMode("on", // NOI18N
			CommandRequest.WATCH_ON, false);

	/**
	 * This is the WatchMode that disables watching.
	 */
	public static final WatchMode OFF = new WatchMode("off", // NOI18N
			CommandRequest.WATCH_OFF, false);

	/**
	 * This is the WatchMode that adds watching for the specified Watch.
	 */
	public static final WatchMode ADD = new WatchMode("add", // NOI18N
			CommandRequest.WATCH_ADD, true);

	/**
	 * This is the WatchMode that removes watching for the specified Watch.
	 */
	public static final WatchMode REMOVE = new WatchMode("remove", // NOI18N
			CommandRequest.WATCH_REMOVE, true);

	private final String name;
	private final CommandRequest command;
	private final boolean watchOptionAllowed;

	private WatchMode(String name, CommandRequest command, boolean watchOptionAllowed) {
		this.name = name;
		this.command = command;
		this.watchOptionAllowed = watchOptionAllowed;
	}

	/**
	 * Returns the CommandRequest that is used when executing the WatchCommand with this WatchMode.
	 */
	public CommandRequest getCommand() {
		return command;
	}

	/**
	 * Indicated, whether a non-null watch-option is allowed in the WatchCommand.
	 */
	public boolean isWatchOptionAllowed() {
		return watchOptionAllowed;
	}

	/**
	 * Returns the name of this WatchMode ("on", "off", "add", "remove").
	 */
	@Override
	public String toString() {
		return name;
	}
}
