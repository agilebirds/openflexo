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
package org.openflexo.inspector.widget;

import javax.swing.JFileChooser;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FlexoFileChooser;

/**
 * @author sguerin
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class DirectoryEditWidget extends FileEditWidget {

	public DirectoryEditWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
	}

	@Override
	protected void configureFileChooser(FlexoFileChooser chooser) {
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle(title == null ? FlexoLocalization.localizedForKey("select_directory") : FlexoLocalization
				.localizedForKey(title));
		chooser.setFileFilterAsString(filter);
		chooser.setDialogType(mode);
	}

}
