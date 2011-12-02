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
package org.openflexo.velocity;

import java.util.logging.Level;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

public class VelocityLogger implements LogChute {

	/**
	 * Overrides init
	 * 
	 * @see org.apache.velocity.runtime.log.LogSystem#init(org.apache.velocity.runtime.RuntimeServices)
	 */
	@Override
	public void init(RuntimeServices rs) throws Exception {

	}

	/**
	 * Overrides logVelocityMessage
	 * 
	 * @see org.apache.velocity.runtime.log.LogSystem#logVelocityMessage(int, java.lang.String)
	 */
	public void logVelocityMessage(int level, String message) {
		switch (level) {
		case DEBUG_ID:
			if (FlexoVelocity.logger.isLoggable(Level.FINE)) {
				FlexoVelocity.logger.fine("Velocity debug: " + message);
			}
			break;
		case INFO_ID:
			if (FlexoVelocity.logger.isLoggable(Level.INFO)) {
				FlexoVelocity.logger.info("Velocity info: " + message);
			}
			break;
		case WARN_ID:
			if (FlexoVelocity.logger.isLoggable(Level.WARNING)) {
				FlexoVelocity.logger.warning("Velocity warning: " + message);
			}
			break;
		case ERROR_ID:
			if (FlexoVelocity.logger.isLoggable(Level.SEVERE)) {
				FlexoVelocity.logger.severe("Velocity severe: " + message);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean isLevelEnabled(int level) {
		switch (level) {
		case DEBUG_ID:
			return FlexoVelocity.logger.isLoggable(Level.FINE);
		case INFO_ID:
			return FlexoVelocity.logger.isLoggable(Level.FINE);
		case WARN_ID:
			return FlexoVelocity.logger.isLoggable(Level.WARNING);
		case ERROR_ID:
			return FlexoVelocity.logger.isLoggable(Level.SEVERE);
		default:
			break;
		}
		return true;
	}

	@Override
	public void log(int level, String message) {
		logVelocityMessage(level, message);
	}

	@Override
	public void log(int level, String message, Throwable t) {
		switch (level) {
		case DEBUG_ID:
			if (FlexoVelocity.logger.isLoggable(Level.FINE)) {
				FlexoVelocity.logger.log(Level.FINE, "Velocity debug: " + message, t);
			}
			break;
		case INFO_ID:
			if (FlexoVelocity.logger.isLoggable(Level.FINE)) {
				FlexoVelocity.logger.log(Level.FINE, "Velocity info: " + message, t);
			}
			break;
		case WARN_ID:
			if (FlexoVelocity.logger.isLoggable(Level.WARNING)) {
				FlexoVelocity.logger.log(Level.WARNING, "Velocity warning: " + message, t);
			}
			break;
		case ERROR_ID:
			if (FlexoVelocity.logger.isLoggable(Level.SEVERE)) {
				FlexoVelocity.logger.log(Level.SEVERE, "Velocity severe: " + message, t);
			}
			break;
		default:
			break;
		}
	}

}