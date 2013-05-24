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
package org.openflexo.components;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 * 
 */
public class SaveDialog extends JOptionPane {

	private int retval = JOptionPane.CANCEL_OPTION;

	public SaveDialog(Component parent, FlexoProject project) {
		retval = JOptionPane.showConfirmDialog(parent,
				FlexoLocalization.localizedForKey("project_has_unsaved_changes") + ": " + project.getProjectDirectory().getAbsolutePath()
						+ "\n" + FlexoLocalization.localizedForKey("would_you_like_to_save_the_changes?"),
				FlexoLocalization.localizedForKey("exiting_flexo"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	}

	public int getRetval() {
		return retval;
	}

}
