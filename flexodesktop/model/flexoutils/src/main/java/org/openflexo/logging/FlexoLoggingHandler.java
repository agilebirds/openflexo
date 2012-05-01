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

/**
 * Flexo logging handler for dynamic logging
 * 
 * @author sguerin
 */
public class FlexoLoggingHandler extends Handler {

	public FlexoLoggingHandler() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	@Override
	public void publish(java.util.logging.LogRecord record) {
		if (FlexoLoggingManager.instance(this) != null) {
			org.openflexo.logging.LogRecord flexoRecord = new org.openflexo.logging.LogRecord(record, FlexoLoggingManager.instance(this));
			FlexoLoggingManager.instance(this).logRecords.add(flexoRecord, FlexoLoggingManager.instance());
		}
	}

	public void publishUnhandledException(java.util.logging.LogRecord record, Exception e) {
		if (FlexoLoggingManager.instance(this) != null) {
			FlexoLoggingManager.instance(this).logRecords.add(
					new org.openflexo.logging.LogRecord(record, e, FlexoLoggingManager.instance(this)), FlexoLoggingManager.instance());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.logging.Handler#flush()
	 */
	@Override
	public void flush() {
		// nothing special to do : the handler is logging "in memory"
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.logging.Handler#close()
	 */
	@Override
	public void close() throws SecurityException {
		// nothing special to do : the handler is logging "in memory"
	}

}
