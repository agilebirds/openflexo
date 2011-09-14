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

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFolder;
import org.openflexo.foundation.cg.CGSymbolicDirectory;


public class GeneratedDocSymbDirElement extends DGBrowserElement
{
	public GeneratedDocSymbDirElement(CGSymbolicDirectory dir, ProjectBrowser browser, BrowserElement parent)
	{
		super(dir, BrowserElementType.GENERATED_CODE_SYMB_DIR, browser,parent);
	}

	@Override
	protected void buildChildrenVector()
	{
		for (CGFolder folder : getSymbDir().getSortedSubFolders()) {
			addToChilds(folder);
		}
		for (CGFile file : getSymbDir().getSortedFiles()) {
			addToChilds(file);
		}
	}

	@Override
	public String getName()
	{
		return getSymbDir().getName()+" - ["+getSymbDir().getDirectory().getRelativePath()+"]";
	}

	public CGSymbolicDirectory getSymbDir()
	{
		return (CGSymbolicDirectory)getObject();
	}
}
