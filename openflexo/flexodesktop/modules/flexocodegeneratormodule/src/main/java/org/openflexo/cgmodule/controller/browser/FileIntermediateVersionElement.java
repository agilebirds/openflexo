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
package org.openflexo.cgmodule.controller.browser;

import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.cg.version.CGFileIntermediateVersion;
import org.openflexo.icon.CGIconLibrary;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconMarker;


public class FileIntermediateVersionElement extends GCBrowserElement
{
	public FileIntermediateVersionElement(CGFileIntermediateVersion fileIntermediateVersion, ProjectBrowser browser, BrowserElement parent)
	{
		super(fileIntermediateVersion, BrowserElementType.FILE_INTERMEDIATE_VERSION, browser,parent);
	}

	@Override
	protected void buildChildrenVector()
	{
	}

	@Override
	public String getName()
	{
			return getIntermediateVersion().getStringRepresentation();
	}

	@Override
	public ImageIcon getBaseIcon()
	{
		ImageIcon returned = FilesIconLibrary.smallIconForFileFormat(getIntermediateVersion().getCGFile().getFileFormat());
		if (returned == null) {
			returned = super.getBaseIcon();
		}
		return returned;
	}

	@Override
	public Icon getIcon()
	{
		ImageIcon returned = getBaseIcon();
		Vector<IconMarker> markers = new Vector<IconMarker>();
		markers.add(CGIconLibrary.INTERMEDIATE_VERSION);
		IconMarker[] markersArray = markers.toArray(new IconMarker[markers.size()]);
		returned = IconFactory.getImageIcon(returned, markersArray);
		return returned;
	}

	public CGFileIntermediateVersion getIntermediateVersion()
	{
		return (CGFileIntermediateVersion)getObject();
	}

    @Override
	protected BrowserElementType getFilteredElementType()
    {
        return BrowserElementType.FILE_RELEASE_VERSION;
    }


}
