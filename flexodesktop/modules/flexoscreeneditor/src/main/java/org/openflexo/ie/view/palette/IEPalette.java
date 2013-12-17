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
package org.openflexo.ie.view.palette;

/**
 * @author bmangez
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */

import java.awt.Dimension;
import java.util.logging.Logger;

import org.openflexo.ch.FCH;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ie.dm.StyleSheetFolderChanged;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.ProjectClosedNotification;
import org.openflexo.ie.IECst;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.view.palette.FlexoPalette;

/**
 * The palette that contains the dragable elements. Currently draft version.
 * 
 * @author benoit
 */
@Deprecated
public class IEPalette extends FlexoPalette implements FlexoObserver {

	private static final Logger logger = Logger.getLogger(IEPalette.class.getPackage().getName());

	private IEPalettePanel basicPalette;
	private IEPalettePanel imagePalette;
	private IEPalettePanel customWidgetPalette;
	private IEPalettePanel customImagePalette;
	private IEPalettePanel birtPanel;

	/**
	 * Create a palette.
	 * 
	 * @param mainFrame
	 * @throws java.awt.HeadlessException
	 */
	public IEPalette(IEController controller, FlexoProject project) {
		super(controller, project);
		project.addObserver(this);
		setPreferredSize(new Dimension(IECst.DEFAULT_PALETTE_WIDTH, IECst.DEFAULT_PALETTE_HEIGHT));
		FCH.setHelpItem(currentTabbedPane, "ie-palette");
		validate();
		controlPanel = makeControlPanel();
		updateControlButtons();
	}

	public void disposePalettes() {
		if (basicPalette != null) {
			basicPalette.delete();
		}
		if (imagePalette != null) {
			imagePalette.delete();
		}
		if (customWidgetPalette != null) {
			customWidgetPalette.delete();
		}
		if (customImagePalette != null) {
			customImagePalette.delete();
		}
		if (birtPanel != null) {
			birtPanel.delete();
		}
		if (getProject() != null) {
			getProject().deleteObserver(this);
		}
	}

	/**
	 *
	 */
	private IEPalettePanel getCustomImagePalette() {
		if (customImagePalette == null) {
			customImagePalette = new IEImagePalettePanel(this, getProject().getCustomImagePalette(), "images");
		}
		return customImagePalette;
	}

	/**
	 *
	 */
	private IEPalettePanel getCustomWidgetPalette() {
		if (customWidgetPalette == null) {
			customWidgetPalette = new IEPalettePanel(this, getProject().getCustomWidgetPalette(), "widgets");
		}
		return customWidgetPalette;
	}

	/**
	 * @param controller
	 */
	private IEPalettePanel getBasicPalette() {
		if (basicPalette == null) {
			basicPalette = new IEPalettePanel(this, getProject().getBasicPalette(), "basic");
		}
		return basicPalette;
	}

	/**
	 * @param controller
	 */
	private IEPalettePanel getImagePalette() {
		if (imagePalette == null) {
			imagePalette = new IEPalettePanel(this, getProject().getImagePalette(), "icons");
		}
		return imagePalette;
	}

	private IEPalettePanel getBIRTPalette() {
		if (birtPanel == null) {
			birtPanel = new IEPalettePanel(this, getProject().getBIRTPalette(), "Dashboards");
		}
		return birtPanel;
	}

	public boolean currentPaletteIsBasicPalette() {
		return getCurrentPalettePanel() == getBasicPalette();
	}

	private void switchCSS() {
		if (currentTabbedPane == null) {
			return;
		}
		basicPalette.switchCSS();
		imagePalette.switchCSS();
	}

	@Override
	public PaletteTabbedPane makeTabbedPane() {
		PaletteTabbedPane answer = new PaletteTabbedPane();
		answer.add(getBasicPalette());
		answer.add(getImagePalette());
		answer.add(getCustomImagePalette());
		answer.add(getCustomWidgetPalette());
		answer.add(getBIRTPalette());
		return answer;

	}

	@Override
	public boolean handlesPaletteEdition() {
		return false;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getProject()) {
			if (dataModification instanceof StyleSheetFolderChanged) {
				switchCSS();
			} else if (dataModification instanceof ProjectClosedNotification) {
				disposePalettes();
			}
		}
	}
}
