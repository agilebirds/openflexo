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
package org.openflexo.doceditor.controller.browser;

import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceStatus;
import org.openflexo.icon.DEIconLibrary;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;

public class TOCEntryElement extends DEBrowserElement {

	public TOCEntryElement(TOCEntry entry, ProjectBrowser browser, BrowserElement parent) {
		super(entry, BrowserElementType.TOC_ENTRY, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		Enumeration<TOCEntry> en = ((TOCEntry) getObject()).getSortedTocEntries();
		while (en.hasMoreElements()) {
			addToChilds(en.nextElement());
		}
	}

	@Override
	public boolean isNameEditable() {
		return true;
	}

	@Override
	public Icon getIcon() {
		ImageIcon icon = DEIconLibrary.iconForObject(getEntry());
		if (getEntry().isReadOnly()) {
			icon = IconFactory.getDisabledIcon(icon);
		}
		if (getEntry().getObjectReference() != null && getEntry().getObjectReference().getStatus() != ReferenceStatus.UNRESOLVED
				&& getEntry().getObjectReference().getStatus() != ReferenceStatus.RESOLVED) {
			icon = IconFactory.getImageIcon(icon, IconLibrary.QUESTION);
		}
		return icon;
	}

	@Override
	public String getName() {
		return getEntry().getTitle();
	}

	@Override
	public void setName(String title) throws FlexoException {
		getEntry().setTitle(title);
	}

	public TOCEntry getEntry() {
		return (TOCEntry) getObject();
	}
}
