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
package org.openflexo.fps.controller.browser;

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSObject;
import org.openflexo.fps.CVSStatus;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.dm.FPSDataModification;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.IconMarker;
import org.openflexo.icon.UtilsIconLibrary;


public abstract class FPSBrowserElement extends BrowserElement {

    static final Logger logger = Logger.getLogger(FPSBrowserElement.class.getPackage()
            .getName());
    
	public FPSBrowserElement(FPSObject object, BrowserElementType elementType, ProjectBrowser browser, BrowserElement parent)
	{
		super(object, elementType, browser, parent);
	}
	
	@Override
	public FPSObject getObject()
	{
		return (FPSObject)super.getObject();
	}

	@Override
	public Icon getIcon()
	{
		if (getObject() == null) {
			return null;
		}
		
		ImageIcon returned = getBaseIcon();
		Vector<IconMarker> markers = new Vector<IconMarker>();
		
		if (getObject() instanceof CVSObject) {
			CVSObject cvsObject = (CVSObject)getObject();
			if (cvsObject.getDerivedStatus() == CVSStatus.UpToDate) {
				// No marker to add
			}
			else if (cvsObject.getDerivedStatus() == CVSStatus.LocallyModified) {
				markers.add(UtilsIconLibrary.LEFT_MODIFICATION);
			}
			else if (cvsObject.getDerivedStatus() == CVSStatus.MarkedAsMerged) {
				markers.add(UtilsIconLibrary.LEFT_MODIFICATION);
				markers.add(IconLibrary.MERGE_OK);
			}
			else if (cvsObject.getDerivedStatus() == CVSStatus.LocallyAdded) {
				markers.add(UtilsIconLibrary.LEFT_ADDITION);
			}
			else if (cvsObject.getDerivedStatus() == CVSStatus.LocallyRemoved) {
				markers.add(UtilsIconLibrary.LEFT_REMOVAL);
			}
			else if (cvsObject.getDerivedStatus() == CVSStatus.RemotelyModified) {
				markers.add(UtilsIconLibrary.RIGHT_MODIFICATION);
			}
			else if (cvsObject.getDerivedStatus() == CVSStatus.RemotelyAdded) {
				markers.add(UtilsIconLibrary.RIGHT_ADDITION);
			}
			else if (cvsObject.getDerivedStatus() == CVSStatus.RemotelyRemoved) {
				markers.add(UtilsIconLibrary.RIGHT_REMOVAL);
			}
			else if (cvsObject.getDerivedStatus() == CVSStatus.Conflicting) {
				markers.add(UtilsIconLibrary.CONFLICT);
				if (cvsObject instanceof CVSFile) {
					if ((((CVSFile)cvsObject).getMerge() != null) 
							&& ((CVSFile)cvsObject).getMerge().isResolved()) {
						markers.add(IconLibrary.MERGE_OK);
						//logger.info("Merge for "+((CVSFile)cvsObject).getFileName()+" is resolved");
					}
					/*else {
						logger.info("Merge for "+((CVSFile)cvsObject).getFileName()+" is NOT resolved");
						for (MergeChange c : ((CVSFile)cvsObject).getMerge().getChanges()) {
							logger.info("Change "+c+" isResolved="+c.isResolved());
						}
					}*/
				}
			}
			else {
				markers.add(IconLibrary.QUESTION);
			}
		}
		
		// Get icon with all markers
		IconMarker[] markersArray = markers.toArray(new IconMarker[markers.size()]);
		returned = IconFactory.getImageIcon(returned, markersArray);
		
		if (!isEnabled()) {
			returned = IconFactory.getDisabledIcon(returned);
		}
		return returned;
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

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
         if (_browser != null) {
            if (dataModification instanceof FPSDataModification) {
            	try {
            		refreshWhenPossible();
            	}
            	catch (NullPointerException npe) {
            		// Might happen in MT context, juste ignore it
            	}
            } else {
				super.update(observable, dataModification);
			}
        }
    }

	@Override
	public FPSBrowser getProjectBrowser() 
	{
		return (FPSBrowser)super.getProjectBrowser();
	}


}
