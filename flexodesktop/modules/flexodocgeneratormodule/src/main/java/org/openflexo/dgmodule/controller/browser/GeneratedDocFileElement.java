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

import javax.swing.ImageIcon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.version.CGFileReleaseVersion;
import org.openflexo.foundation.rm.cg.AbstractGeneratedFile;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.file.AbstractCGFile;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.icon.IconMarker;
import org.openflexo.localization.FlexoLocalization;

public class GeneratedDocFileElement extends DGBrowserElement
{
	public GeneratedDocFileElement(CGFile file, ProjectBrowser browser, BrowserElement parent)
	{
		super(file, BrowserElementType.GENERATED_CODE_FILE, browser,parent);
	}

	@Override
	protected void buildChildrenVector()
	{
		if ((getFile()!=null) && (getFile().getRepository()!=null) && getFile().getRepository().getManageHistory()
				&& (getFile().getResource()!=null) && getFile().getResource().isLoaded() && (getFile().getResource().getGeneratedResourceData() instanceof AbstractGeneratedFile)) {
			for (CGFileReleaseVersion releaseVersion :
				((AbstractGeneratedFile)getFile().getResource().getGeneratedResourceData()).getHistory().getReleasesVersion()) {
				addToChilds(releaseVersion);
			}
		}
	}

	@Override
	public String getName()
	{
		String returned = getFile().getFileName()
		+(getFile().isEdited()?"["+FlexoLocalization.localizedForKey("edited")+"]":"");
		if (logger.isLoggable(Level.FINE)) {
			if (getFile() instanceof AbstractCGFile) {
				if (((AbstractCGFile)getFile()).getGenerationException() != null) {
					returned += "[ERROR]";
				}
			}
			returned += "/"+getResource().getGenerationStatus().toString();
			if (getResource().needsGeneration()) {
				returned += "<"+getResource().getNeedsUpdateReason();
			}
		}
		return returned;
	}

	@Override
	public ImageIcon getBaseIcon()
	{
		ImageIcon returned = FilesIconLibrary.smallIconForFileFormat(getFile().getFileFormat());
		if (returned == null) {
			returned = super.getBaseIcon();
		}
		return returned;
	}

	public CGFile getFile()
	{
		return (CGFile)getObject();
	}

	public CGRepositoryFileResource getResource()
	{
		return getFile().getResource();
	}

	@Override
	protected Vector<IconMarker> getMarkers(){
		if(getFile().getMarkedAsDoNotGenerate()){
			Vector<IconMarker> markers = new Vector<IconMarker>();
			markers.add(GeneratorIconLibrary.DO_NOT_GENERATE);
			return markers;
		}
		return super.getMarkers();
	}
}
