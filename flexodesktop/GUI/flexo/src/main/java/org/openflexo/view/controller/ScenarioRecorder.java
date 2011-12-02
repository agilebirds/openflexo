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
package org.openflexo.view.controller;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoGUIAction;

public class ScenarioRecorder {

	private static final Logger logger = Logger.getLogger(ScenarioRecorder.class.getPackage().getName());

	public static boolean ENABLE = false;

	private Vector<FlexoAction> _actionHistory;

	public ScenarioRecorder() {
		_actionHistory = new Vector<FlexoAction>();
	}

	public void registerDoneAction(FlexoAction action) {
		if (logger.isLoggable(Level.FINE)) {
			logger.info("registerDoneAction " + action);
		}
		if (action instanceof FlexoGUIAction) {
			// Ignore
		} else {
			_actionHistory.add(action);
		}
		if (logger.isLoggable(Level.FINE)) {
			debug();
		}
	}

	private void debug() {
		logger.info("ScenarioRecorder: ");
		int i = 0;
		for (FlexoAction a : _actionHistory) {
			logger.info("" + i + " : " + a);
			i++;
		}
	}

}
