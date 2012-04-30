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
package org.openflexo.fib;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.logging.FlexoLoggingViewer;

public class LoggingViewerEDITOR {

	private static final Logger logger = FlexoLogger.getLogger(LoggingViewerEDITOR.class.getPackage().getName());

	public static void main(String[] args) {
		try {
			FlexoLoggingManager.initialize();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FlexoLoggingManager.setKeepLogTrace(true);
		FlexoLoggingManager.setLogCount(-1);
		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return makeArray(new FlexoLoggingViewer(FlexoLoggingManager.logRecords));
			}

			@Override
			public File getFIBFile() {
				return FlexoLoggingViewer.LOGGING_VIEWER_FIB;
			}
		};
		editor.launch();
	}
}
