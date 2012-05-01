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
package org.openflexo.logging;

import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * @author sguerin
 * 
 */
public class FlexoLogger extends Logger {

	public FlexoLogger(String name) {
		super(name, null);
	}

	public void unhandledException(Exception e) {
		FlexoLoggingHandler flexoLoggingHandler = getFlexoLoggingHandler();
		if (flexoLoggingHandler != null) {
			flexoLoggingHandler.publishUnhandledException(new java.util.logging.LogRecord(java.util.logging.Level.WARNING,
					"Unhandled exception occured: " + e.getClass().getName()), e);
		} else {
			warning("Unexpected exception occured: " + e.getClass().getName());
		}
	}

	public FlexoLoggingHandler getFlexoLoggingHandler() {
		Handler[] handlers = getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			if (handlers[i] instanceof FlexoLoggingHandler) {
				return (FlexoLoggingHandler) handlers[i];
			}
		}
		return null;
	}
}
