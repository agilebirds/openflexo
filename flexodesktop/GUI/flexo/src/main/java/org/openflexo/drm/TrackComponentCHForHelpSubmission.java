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
package org.openflexo.drm;

import java.awt.Cursor;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.ApplicationContext;
import org.openflexo.action.SubmitDocumentationAction;
import org.openflexo.drm.DocItem;
import org.openflexo.view.FlexoFrame;

public class TrackComponentCHForHelpSubmission extends TrackComponentCH {

	private static final Logger logger = Logger.getLogger(TrackComponentCHForHelpSubmission.class.getPackage().getName());

	public TrackComponentCHForHelpSubmission(FlexoFrame frame, ApplicationContext applicationContext) {
		super(frame, applicationContext);
		frame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void applyTracking(JComponent component) {
		DocItem item = getDocResourceManager().getDocForComponent(focusedComponent);
		if (item != null) {
			SubmitDocumentationAction.actionType.makeNewAction(item, null, getController().getEditor()).doAction();
		}
	}

}
