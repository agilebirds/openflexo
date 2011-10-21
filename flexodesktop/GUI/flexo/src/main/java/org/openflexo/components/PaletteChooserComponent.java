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

import javax.swing.JFileChooser;

import org.openflexo.AdvancedPrefs;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.utils.FlexoFileChooserUtils;

/**
 * Abstract component allowing to choose a Flexo Project
 * 
 * @author sguerin
 */
public abstract class PaletteChooserComponent extends JFileChooser
{

	public PaletteChooserComponent()
	{
		super();
		setCurrentDirectory(AdvancedPrefs.getLastVisitedDirectory());
		setDialogTitle(FlexoLocalization.localizedForKey("select_a_palette_directory"));
		setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		setFileFilter(FlexoFileChooserUtils.PALETTE_FILE_FILTER);
		setFileView(FlexoFileChooserUtils.PALETTE_FILE_VIEW);
	}
}
