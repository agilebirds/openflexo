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
package org.openflexo.sgmodule.view;

import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFile.FileContentEditor;
import org.openflexo.foundation.rm.cg.ASCIIFile;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.WOFile;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.sgmodule.controller.SGController;


public class CodeEditor extends CodeDisplayer implements FileContentEditor
{

	public CodeEditor(GenerationAvailableFileResource resource, SGController controller)
	{
		super(resource,controller);
		_component.setEditable(true);
		setEditedContent(getCGFile());
	}

	@Override
	public String getEditedContentForKey(String contentKey) 
	{
		return _component.getEditedContentForKey(contentKey);
	}

	@Override
	public void setEditedContent(CGFile file) 
	{
		_component.setEditedContent(file);
	}

	@Override
	protected CodeDisplayerComponent buildComponent()
	{
		_component = null;
		
		if (getResourceData() instanceof ASCIIFile) {
			_component = new ASCIIFileCodePanel(ContentSource.CONTENT_ON_DISK);
		}
		else if (getResourceData() instanceof WOFile) {
			_component = new WOComponentCodePanel(ContentSource.CONTENT_ON_DISK);
		}
		return _component;
	}
	
}
