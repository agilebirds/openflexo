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
package org.openflexo.dgmodule.controller.browser;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GenerationStatusModification;
import org.openflexo.foundation.cg.dm.CGFileWritenOnDisk;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.IconMarker;
import org.openflexo.icon.UtilsIconLibrary;


public abstract class DGBrowserElement extends BrowserElement {
	// GPO: DGBrowserElement does not extends DEBrowserElement because they are
	// different browsers (even though, they have some objects from the model in
	// common, they are not displayed the same way at all). 

    protected static final Logger logger = Logger.getLogger(DGBrowserElement.class.getPackage().getName());

	public DGBrowserElement(CGObject object, BrowserElementType elementType, ProjectBrowser browser, BrowserElement parent)
	{
		super(object, elementType, browser, parent);
	}

	@Override
    public Icon getIcon()
	{
		if (getObject() == null) {
			return null;
		}

		ImageIcon returned = getBaseIcon();

		Vector<IconMarker> markers = getMarkers();
		// Get icon with all markers
		IconMarker[] markersArray = markers.toArray(new IconMarker[markers.size()]);
		returned = IconFactory.getImageIcon(returned, markersArray);

		if (!isEnabled()) {
			returned = IconFactory.getDisabledIcon(returned);
		}
		return returned;
	}

	@Override
	public CGObject getObject() {
		return (CGObject) super.getObject();
	}
	
	protected Vector<IconMarker> getMarkers(){

		Vector<IconMarker> markers = new Vector<IconMarker>();

		if (getObject().needsModelReinjection()) {
			markers.add(GeneratorIconLibrary.NEEDS_MODEL_REINJECTION);
		}
		GenerationStatus status = getObject().getGenerationStatus();
		if (status == GenerationStatus.GenerationAdded) {
			markers.add(UtilsIconLibrary.LEFT_ADDITION);
		}
		else if (status == GenerationStatus.GenerationRemoved) {
			markers.add(UtilsIconLibrary.LEFT_REMOVAL);
		}
		else if (status == GenerationStatus.GenerationModified) {
			markers.add(UtilsIconLibrary.LEFT_MODIFICATION);
		}
		else if (status == GenerationStatus.OverrideScheduled) {
			markers.add(UtilsIconLibrary.LEFT_MODIFICATION);
		}
		else if (status == GenerationStatus.DiskModified) {
			markers.add(UtilsIconLibrary.RIGHT_MODIFICATION);
		}
		else if (status == GenerationStatus.DiskRemoved) {
			markers.add(UtilsIconLibrary.RIGHT_REMOVAL);
		}
		else if (status == GenerationStatus.ConflictingUnMerged) {
			markers.add(UtilsIconLibrary.CONFLICT);
		}
		else if (status == GenerationStatus.ConflictingMarkedAsMerged) {
			markers.add(UtilsIconLibrary.LEFT_MODIFICATION);
		}

		if (getObject().hasGenerationErrors()) {
			markers.add(IconLibrary.ERROR);
		}

		if (getObject().needsRegeneration()) {
			markers.add(GeneratorIconLibrary.NEEDS_REGENERATE);
		}

		if ((getObject() instanceof CGFile)
				&& (((CGFile)getObject()).getResource() != null)
				&& (((CGFile)getObject()).getResource().getGenerator() != null)) {
			/*if (((CGFile)getObject()).getResource().getGenerator().needsGeneration())
				markers.add(NEEDS_REGENERATE);*/
			if (((CGFile) getObject()).getResource().getGenerator().hasFormattingException()) {
				markers.add(IconLibrary.WARNING);
			}
		}

		if (logger.isLoggable(Level.FINE)) {
			if (getObject() instanceof CGFile) {
				String ims = "";
				for (IconMarker marker : markers) {
					ims += " "+marker.getID();
				}
				logger.fine("File "+((CGFile)getObject()).getFileName()+" status "+status+" ims="+ims);
			}
		}
		return markers;
	}
	public ImageIcon getBaseIcon()
	{
		return getElementType().getIcon();
	}

	@Override
    public boolean isSelectable()
	{
		return isEnabled();
	}

	public boolean isEnabled()
	{
		return getObject().isEnabled();
	}

    /**
     * Overrides update
     * @see org.openflexo.components.browser.BrowserElement#update(org.openflexo.foundation.FlexoObservable, org.openflexo.foundation.DataModification)
     */
    @Override
    public void update(FlexoObservable observable, DataModification dataModification)
    {
        if ((dataModification instanceof GenerationStatusModification) || (dataModification instanceof CGFileWritenOnDisk)) {
        	updateViewWhenPossible();
            return;
        }
        super.update(observable, dataModification);
    }

}
