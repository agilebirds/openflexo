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
package org.openflexo.wysiwyg;

import java.io.File;

public abstract class FlexoWysiwygLight extends FlexoWysiwyg {

	/**
	 * Creates the light version of wysiwyg component without the JMenuBar and without any CSS support. This version removes a set of the
	 * less useful options. This class must implement <code>textChanged(String htmlText)</code> to be concrete.
	 */

	public FlexoWysiwygLight(boolean isViewSourceAvailable) {
		this(null, isViewSourceAvailable);
	}

	/**
	 * Creates the light version of wysiwyg component without the JMenuBar and without any CSS support. This version removes a set of the
	 * less useful options. This class must implement <code>textChanged(String htmlText)</code> to be concrete.
	 * 
	 * @param htmlContent
	 *            if not null, will initialize the wysiwyg with this HTML content.
	 */
	public FlexoWysiwygLight(String htmlContent, boolean isViewSourceAvailable) {
		this(htmlContent, null, isViewSourceAvailable);
	}

	/**
	 * Creates the light version of wysiwyg component without the JMenuBar and with CSS support. This version removes a set of the less
	 * useful options. This class must implement <code>textChanged(String htmlText)</code> to be concrete.
	 * 
	 * @param htmlContent
	 *            if not null, will initialize the wysiwyg with this HTML content.
	 * @param cssFile
	 *            the CSS file to apply on the document.
	 */
	public FlexoWysiwygLight(String htmlContent, File cssFile, boolean isViewSourceAvailable) {

		super(htmlContent, cssFile, isViewSourceAvailable);
		// remove elements
		setMainMenuVisible(false);
		setRemovedMenus("menuTable");
		setRemovedMenuItems("insertTableMenuItem");
		setRemovedToolbarItems("printFileButton, fontsList, fontSizeButton, decreaseIndentButton, increaseIndentButton, subscriptButton, superscriptButton");
		setRemovedPopupMenuItems("insertTableMenuItem, fontPropertiesMenuItem, paragraphPropertiesPopupMenuItem, listPropertiesMenuItem, imagePropertiesMenuItem");
		setPreviewVisible(false);
		setStatusBarVisible(false);
	}

	@Override
	/**
	 * Overridden to remove all table options ,since they are handled by the LaTeX transcriptor for now.
	 */
	protected void initTableToolbar() {
		// override to prevent table toolbar from drawing
	}
}
